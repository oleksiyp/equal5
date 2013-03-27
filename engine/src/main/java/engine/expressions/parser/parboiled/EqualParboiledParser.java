package engine.expressions.parser.parboiled;

import engine.expressions.parser.ClauseType;
import org.parboiled.BaseParser;
import org.parboiled.Parboiled;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;
import org.parboiled.annotations.Label;
import org.parboiled.annotations.SuppressSubnodes;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
* User: Oleksiy Pylypenko
* Date: 3/24/13
* Time: 2:05 AM
*/
@BuildParseTree
class EqualParboiledParser extends BaseParser<Object> {
    public static final EqualParboiledParser INSTANCE = Parboiled.createParser(EqualParboiledParser.class);

    public static final Pattern EXTRACT_FINAL_ID_PATTERN = Pattern.compile("([a-z][a-z\\d]*)$");

    private final Map<ClauseType, Rule> ruleMap = new HashMap<ClauseType, Rule>();

    EqualParboiledParser() {
        initRuleMap();
    }

    private void initRuleMap() {
        for (Method method : EqualParboiledParser.class.getMethods()) {
            if (method.isAnnotationPresent(Clause.class)) {
                Clause clause = method.getAnnotation(Clause.class);
                try {
                    ClauseType type = clause.value();
                    Rule rule = (Rule) method.invoke(this);
                    ruleMap.put(type, rule);
                } catch (Exception e) {
                    throw new RuntimeException("clause method invocation problem", e);
                }
            }
        }
    }

    public Map<ClauseType, Rule> getRuleMap() {
        return ruleMap;
    }

    @Clause(ClauseType.EQUATIONS)
    public Rule Equations() {
        return Sequence(
                Equation(),
                ZeroOrMore(
                        CompulsoryWhiteSpace(),
                        Equation()));
    }

    @Clause(ClauseType.EQUATION)
    public Rule Equation() {
        return Sequence(
                Expression(),
                FirstOf("=", "<=", ">=", "<", ">").label("EqualitySign"),
                WhiteSpace(),
                Expression());
    }

    @Clause(ClauseType.EXPRESSION)
    public Rule Expression() {
        return Sequence(
                Term(),

                ZeroOrMore(Sequence(
                        AnyOf("+-").label("Operator"),
                        WhiteSpace(),
                        Term()).label("OperatorTerm")
                ).label("Tail"));
    }

    @Clause(ClauseType.TERM)
    public Rule Term() {
        return Sequence(
                Factor(),

                ZeroOrMore(
                        Sequence(
                                AnyOf("*/").label("Operator"),
                                WhiteSpace(),
                                Factor()).label("OperatorFactor")
                ).label("Tail"));
    }

    @Clause(ClauseType.FACTOR)
    public Rule Factor() {
        return FirstOf(
                Constant(),
                MathFunc(),
                Variable(),
                Parentheses());
    }

    @Clause(ClauseType.MATH_FUNCTION)
    public Rule MathFunc() {
        return Sequence(
                Identifier().label("Name"),
                WhiteSpace(),

                "(",
                WhiteSpace(),

                Arguments(),

                ")",
                WhiteSpace());


    }

    @Clause(ClauseType.ARGUMENTS)
    public Rule Arguments() {
        return Optional(
                Expression(),
                ZeroOrMore(
                        Sequence(

                                ",",
                                WhiteSpace(),

                                Expression()).label("CommaExpression")
                ).label("Tail")
        );
    }

    @Clause(ClauseType.PARENTHESES)
    public Rule Parentheses() {
        return Sequence(
                "(",
                WhiteSpace(),

                Expression(),

                ")",
                WhiteSpace());
    }

    @Clause(ClauseType.CONSTANT)
    @SuppressSubnodes
    public Rule Constant() {
        return Sequence(
                DecimalFloat(),
                WhiteSpace());
    }

    @Clause(ClauseType.VARIABLE)
    public Rule Variable() {
        return Sequence(
                Identifier().label("Name"),
                WhiteSpace());
    }

    @SuppressSubnodes
    public Rule Identifier() {
        return Sequence(
                CharRange('a', 'z'),
                ZeroOrMore(
                        FirstOf(
                                CharRange('a', 'z'),
                                CharRange('0', '9')
                        )
                )
        );
    }

    public static String extractFinalId(String expression) {
        Matcher matcher = EXTRACT_FINAL_ID_PATTERN.matcher(expression);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }

    @SuppressSubnodes
    @Clause(ClauseType.DECIMAL_FLOAT)
    public Rule DecimalFloat() {
        return FirstOf(
                Sequence(OneOrMore(Digit()),
                        Optional(FirstOf(Sequence('.', ZeroOrMore(Digit()),
                                Optional(Exponent())), Exponent()))),
                Sequence('.',
                        OneOrMore(Digit()),
                        Optional(Exponent()))
        );
    }

    @Clause(ClauseType.EXPONENT)
    public Rule Exponent() {
        return Sequence(AnyOf("eE"),
                Optional(AnyOf("+-")),
                OneOrMore(Digit()));
    }

    @Clause(ClauseType.DIGIT)
    public Rule Digit() {
        return CharRange('0', '9');
    }

    Rule WhiteSpace() {
        return ZeroOrMore(AnyOf(" \t\f\n\r")).suppressSubnodes().suppressNode();
    }

    @Label("WhiteSpace")
    Rule CompulsoryWhiteSpace() {
        return OneOrMore(AnyOf(" \t\f\n\r")).suppressSubnodes().suppressNode();
    }

    public Rule WholeSentence(Rule rule) {
        return Sequence(WhiteSpace(), rule, EOI);
    }

}
