package engine.expressions;

import engine.calculation.functions.*;
import org.parboiled.BaseParser;
import org.parboiled.Parboiled;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;
import org.parboiled.annotations.SuppressSubnodes;
import org.parboiled.buffers.InputBuffer;
import org.parboiled.errors.ActionException;
import org.parboiled.errors.InvalidInputError;
import org.parboiled.errors.ParseError;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.*;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Oleksiy Pylypenko
 * Date: 2/9/13
 * Time: 11:24 AM
 */
public class ParboiledExpressionParser implements ExpressionParser {

    @Retention(RetentionPolicy.RUNTIME)
    private @interface Clause {
        ClauseType value();
    }
    @BuildParseTree
    private static class Parser extends BaseParser<Object> {
        public Parser() {
        }

        @Override
        protected Rule fromStringLiteral(String string) {
            return string.endsWith(" ") ?
                    Sequence(
                            String(string.substring(0, string.length() - 1)),
                            WhiteSpace()) :
                    String(string);
        }

        Rule WhiteSpace() {
            return ZeroOrMore(AnyOf(" \t\f\n\r"));
        }
        @Clause(ClauseType.EQUATIONS)
        public Rule Equations() {
            Var<List<Equation>> eqs = new Var<List<Equation>>(new ArrayList<Equation>());

            return Sequence(
                    Equation(),
                    eqs.get().add(pop(Equation.class)),

                    WhiteSpace(),
                    Optional(Equations(),
                            eqs.get().addAll(pop(List.class))),

                    push(eqs.get())
            );
        }
        @Clause(ClauseType.EQUATION)
        public Rule Equation() {
            Var<String> op = new Var<String>();
            return Sequence(Expression(),
                    FirstOf("=", "<=", ">=", "<", ">"),
                    op.set(match()),
                    WhiteSpace(),
                    Expression(),
                    swap() && push(new Equation(pop(Function.class),
                            Equation.Type.byOperator(op.get()),
                            pop(Function.class)
                    )));
        }

        public <T> T pop(Class<T> clazz) {
            Object obj = pop();
            if (obj == null) {
                return null;
            }
            if (clazz.isAssignableFrom(obj.getClass())) {
                return clazz.cast(obj);
            } else {
                throw new RuntimeException("Bad type matching. Requested: " + clazz + ", actual: " + obj.getClass());
            }
        }

        @Clause(ClauseType.EXPRESSION)
        public Rule Expression() {
            Var<Character> op = new Var<Character>();
            return Sequence(
                    Term(),
                    ZeroOrMore(AnyOf("+-"),
                            op.set(matchedChar()),
                            WhiteSpace(),
                            Term(),
                            swap() && push(
                                    op.get() == '+' ?
                                            new Addition(pop(Function.class), pop(Function.class)) :
                                            new Subtraction(pop(Function.class), pop(Function.class)))));
        }

        @Clause(ClauseType.TERM)
        public Rule Term() {
            Var<Character> op = new Var<Character>();
            return Sequence(
                    Factor(),
                    ZeroOrMore(AnyOf("*/"),
                            op.set(matchedChar()),
                            WhiteSpace(),
                            Factor(),
                            swap() && push(
                                    op.get() == '*' ?
                                            new Multiplication(pop(Function.class), pop(Function.class)) :
                                            new Division(pop(Function.class), pop(Function.class)))));
        }

        @Clause(ClauseType.FACTOR)
        public Rule Factor() {
            return FirstOf(Constant(), MathFunc(), Variable(), Parents());
        }


        @Clause(ClauseType.MATH_FUNCTION)
        public Rule MathFunc() {
            StringVar type = new StringVar();
            Var<List<Function>> arguments = new Var<List<Function>>();
            return Sequence(
                    OneOrMore(CharRange('a', 'z')),
                    type.set(matchOrDefault("")),
                    WhiteSpace(),
                    "( ", Arguments(), ") ",
                    arguments.set(pop(List.class)),
                    push(mathFuncConstruct(type, arguments)));

        }

        protected MathFunction mathFuncConstruct(StringVar typeVar, Var<List<Function>> argsVar) {
            String type = typeVar.get();
            List<Function> list = argsVar.get();
            Function[] args = list.toArray(new Function[list.size()]);

            MathFunction.Type funcType = getMathFuncType(type, args);
            if (funcType == null) {
                throw new ActionException("there is no function matching name '" + type +
                        "' and " + argsVar.get().size() + " argument(s)");
            }
            return new MathFunction(funcType,args);
        }

        protected MathFunction.Type getMathFuncType(String name, Function[] args) {
            for (MathFunction.Type type : MathFunction.Type.values()) {
                if (type.getInExpressionName().equals(name)
                        && type.getArgumentsCount() == args.length) {
                    return type;
                }
            }
            return null;
        }

        @Clause(ClauseType.ARGUMENTS)
        public Rule Arguments() {
            Var<List<Function>> arguments = new Var<List<Function>>(new ArrayList<Function>());

            return Sequence(
                    Expression(),
                    arguments.get().add(pop(Function.class)),

                    Optional(", ",
                            Arguments(),
                            arguments.get().addAll(pop(List.class))),

                    push(arguments.get())
            );
        }

        @Clause(ClauseType.PARENTS)
        public Rule Parents() {
            return Sequence("( ", Expression(), ") ");
        }

        @Clause(ClauseType.CONSTANT)
        public Rule Constant() {
            return Sequence(
                    DecimalFloat(),
                    push(new Constant(
                            Double.parseDouble(
                                    matchOrDefault("0")))),
                    WhiteSpace()
            );
        }

        @Clause(ClauseType.VARIABLE)
        public Rule Variable() {
            return Sequence(
                    OneOrMore(CharRange('a','z')),
                    push(new Variable(matchOrDefault(""))),
                    WhiteSpace()
            );
        }

        @SuppressSubnodes
        @Clause(ClauseType.DECIMAL_FLOAT)
        public Rule DecimalFloat() {
            return FirstOf(
                    Sequence(OneOrMore(Digit()),
                            Optional(FirstOf(
                                Sequence('.', ZeroOrMore(Digit()), Optional(Exponent())),
                                Exponent()))),
                    Sequence('.', OneOrMore(Digit()), Optional(Exponent()))
            );
        }

        @Clause(ClauseType.EXPONENT)
        public Rule Exponent() {
            return Sequence(AnyOf("eE"), Optional(AnyOf("+-")), OneOrMore(Digit()));
        }

        @Clause(ClauseType.DIGIT)
        public Rule Digit() {
            return CharRange('0', '9');
        }

        public Rule Finalize(Rule rule) {
            return Sequence(rule, EOI);
        }

        public Rule WhiteSpaceStart(Rule rule) {
            return Sequence(WhiteSpace(), rule);
        }
    }

    private static final Parser parser = Parboiled.createParser(Parser.class);
    private static final Map<ClauseType, Rule> ruleMap = new HashMap<ClauseType, Rule>();
    static {

        for (Method method : Parser.class.getMethods()) {
            if (method.isAnnotationPresent(Clause.class)) {
                Clause clause = method.getAnnotation(Clause.class);
                try {
                    ruleMap.put(clause.value(), (Rule) method.invoke(parser));
                } catch (Exception e) {
                    throw new RuntimeException("clause method invocation problem", e);
                }
            }
        }
    }

    public Object parse(ClauseType type,
                        String expression,
                        boolean finalize) throws ParsingException {
        if (type == null) {
            throw new IllegalArgumentException("bad clause type(null)");
        }
        if (expression == null) {
            throw new IllegalArgumentException("expression");
        }
        Rule rule = ruleMap.get(type);
        if (rule == null) {
            throw new IllegalArgumentException("clause method not found. bad clause type: " + type);
        }

        if (finalize) {
            rule = parser.Finalize(rule);
        }

        rule = parser.WhiteSpaceStart(rule);

        ReportingParseRunner<ParsableObject> runner;
        runner = new ReportingParseRunner<ParsableObject>(rule);

        ParsingResult<ParsableObject> result = runner.run(expression);

        checkForErrors(result);

        return result.resultValue;
    }

    private void checkForErrors(ParsingResult<?> result) throws ParsingException {
        List<ParseError> errors = result.parseErrors;
        if (!errors.isEmpty()) {
            List<ParsingException.SyntaxError> strErrors;
            strErrors = new ArrayList<ParsingException.SyntaxError>();

            for (ParseError error : errors) {
                strErrors.add(createSyntaxError(error));
            }
            throw new ParsingException(strErrors);
        }
    }

    private ParsingException.SyntaxError createSyntaxError(ParseError error) {
        String message = error.getErrorMessage();
        InputBuffer buf = error.getInputBuffer();
        Position pos = buf.getPosition(error.getStartIndex());
        int line = pos.line;
        int col = pos.column;
        if (message == null) {
            String badExpr = nearChars(error, buf);
            message = "syntax error near \"" + badExpr + "\"";
            if (error instanceof InvalidInputError) {
                InvalidInputError iiError = (InvalidInputError) error;
                List<MatcherPath> matchers = iiError.getFailedMatchers();
                message += ", failed matchers: " + matchers;
            }
        }

        boolean oneLiner = buf.getLineCount() <= 1;

        return new ParsingException.SyntaxError(
                oneLiner,
                line,
                col,
                error.getStartIndex(),
                error.getEndIndex(),
                message);
    }

    private String nearChars(ParseError error, InputBuffer buf) {
        int around = 5;
        String sLeft = buf.extract(error.getStartIndex() - around,
                error.getStartIndex());
        if (error.getStartIndex() > around) {
            sLeft = "..." + sLeft;
        }
        String sCenter = buf.extract(error.getStartIndex(), error.getEndIndex());
        String sRight = buf.extract(error.getEndIndex(), error.getEndIndex() + around + 1);
        if (sRight.length() == around + 1) {
            sRight = sRight.substring(0, around);
            sRight = sRight + "...";
        }
        return sLeft + ">>>" + sCenter + "<<<" + sRight;
    }

    @Override
    public Function parseExpression(String expression) throws ParsingException {
        return (Function) parse(ClauseType.EXPRESSION, expression, true);
    }

    @Override
    public Equation[] parseEquations(String expression) throws ParsingException {
        List<Equation> lst = (List<Equation>) parse(ClauseType.EQUATIONS, expression, true);
        return lst.toArray(new Equation[lst.size()]);
    }
}
