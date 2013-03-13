package engine.expressions;

import engine.calculation.functions.*;
import org.parboiled.BaseParser;
import org.parboiled.Parboiled;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;
import org.parboiled.annotations.SuppressSubnodes;
import org.parboiled.buffers.InputBuffer;
import org.parboiled.errors.InvalidInputError;
import org.parboiled.errors.ParseError;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.MatcherPath;
import org.parboiled.support.ParsingResult;
import org.parboiled.support.Position;
import org.parboiled.support.Var;

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
    private static class Parser extends BaseParser<Function> {
        public Parser() {
        }

        @Clause(ClauseType.FUNCTION)
        public Rule Function() {
            Var<Character> op = new Var<Character>();
            return Sequence(
                    Term(),
                    ZeroOrMore(AnyOf("+-"),
                            op.set(matchedChar()),
                            Term(),
                            swap() && push(
                                    op.get() == '+' ?
                                            new Addition(pop(), pop()) :
                                            new Subtraction(pop(), pop()))));
        }

        @Clause(ClauseType.TERM)
        public Rule Term() {
            Var<Character> op = new Var<Character>();
            return Sequence(
                    Factor(),
                    ZeroOrMore(AnyOf("*/"),
                            op.set(matchedChar()),
                            Factor(),
                            swap() && push(
                                    op.get() == '*' ?
                                            new Multiplication(pop(), pop()) :
                                            new Division(pop(), pop()))));
        }

        @Clause(ClauseType.FACTOR)
        public Rule Factor() {
            return FirstOf(Constant(), Variable(), Parents());
        }

        @Clause(ClauseType.PARENTS)
        public Rule Parents() {
            return Sequence('(', Function(), ')');
        }

        @Clause(ClauseType.CONSTANT)
        public Rule Constant() {
            return Sequence(
                    DecimalFloat(),
                    push(new Constant(
                            Double.parseDouble(
                                    matchOrDefault("0"))))
            );
        }

        @Clause(ClauseType.VARIABLE)
        public Rule Variable() {
            return Sequence(
                    OneOrMore(CharRange('a','z')),
                    push(new Variable(matchOrDefault(""))));
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
        Rule rule = ruleMap.get(type);
        if (rule == null) {
            throw new IllegalArgumentException("clause method not found. bad clause type: " + type);
        }

        if (finalize) {
            rule = parser.Finalize(rule);
        }

        ReportingParseRunner<Function> runner;
        runner = new ReportingParseRunner<Function>(rule);

        ParsingResult<Function> result = runner.run(expression);

        checkForErrors(result);

        return result.resultValue;
    }

    private void checkForErrors(ParsingResult<Function> result) throws ParsingException {
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
    public Function parseFunction(String expression) throws ParsingException {
        return (Function) parse(ClauseType.FUNCTION, expression, true);
    }

    @Override
    public Equation parseEquation(String expression) throws ParsingException {
        return null;
    }
}
