package engine.expressions.parser.parboiled;

import engine.expressions.parser.ClauseType;
import org.parboiled.BaseParser;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;
import org.parboiled.annotations.SuppressSubnodes;

/**
* User: Oleksiy Pylypenko
* Date: 3/24/13
* Time: 2:05 AM
*/
@BuildParseTree
class EqualParboiledParser extends BaseParser<Object> {
    public EqualParboiledParser() {
    }

    @Clause(ClauseType.EQUATIONS)
    public Rule Equations() {
        return Sequence(
                Equation(),
                ZeroOrMore(
                    WhiteSpace(),
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

                "( ",

                Arguments(),

                ") ");

    }

    @Clause(ClauseType.ARGUMENTS)
    public Rule Arguments() {
        return Optional(
                Expression(),
                ZeroOrMore(
                        Sequence(
                                ", ",
                                Expression()).label("CommaExpression")
                ).label("Tail")
        );
    }

    @Clause(ClauseType.PARENTHESES)
    public Rule Parentheses() {
        return Sequence("( ",
                Expression(),
                ") ");
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
                WhiteSpace()
        );
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

    @Override
    protected Rule fromStringLiteral(String string) {
        return string.endsWith(" ") ?
                Sequence(
                        String(string.substring(0, string.length() - 1)),
                        WhiteSpace()) :
                String(string);
    }

    Rule WhiteSpace() {
        return ZeroOrMore(AnyOf(" \t\f\n\r")).suppressNode();
    }

    public Rule WholeSentence(Rule rule) {
        return Sequence(WhiteSpace(), rule, EOI);
    }

}
