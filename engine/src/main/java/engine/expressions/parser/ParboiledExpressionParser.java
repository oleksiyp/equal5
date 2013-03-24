package engine.expressions.parser;

import engine.expressions.*;
import org.parboiled.Parboiled;
import org.parboiled.Rule;
import org.parboiled.buffers.InputBuffer;
import org.parboiled.errors.BasicParseError;
import org.parboiled.errors.InvalidInputError;
import org.parboiled.errors.ParseError;
import org.parboiled.parserunners.RecoveringParseRunner;
import org.parboiled.support.*;

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
    private static final EqualParboiledParser parser = Parboiled.createParser(EqualParboiledParser.class);
    private static final Map<ClauseType, Rule> ruleMap = new HashMap<ClauseType, Rule>();
    static {

        for (Method method : EqualParboiledParser.class.getMethods()) {
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
                        String expression) throws ParsingException {
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

        rule = parser.WholeSentence(rule);

        RecoveringParseRunner<Object> runner;
        runner = new RecoveringParseRunner<Object>(rule);

        ParsingResult<Object> result = runner.run(expression);

        ArrayList<ParseError> list = new ArrayList<ParseError>(result.parseErrors);

        ExpressionBuilder builder = new ExpressionBuilder(result.inputBuffer);
        Object res = null;
        try {
            res = builder.build(type, result.parseTreeRoot);
        } catch (ParsingFailureException e) {
            // skip
        }

        list.addAll(builder.getErrors());
        checkForErrors(list);
        return res;
    }

    private void checkForErrors(List<ParseError> errorList) throws ParsingException {
        if (!errorList.isEmpty()) {
            List<SyntaxError> strErrors;
            strErrors = new ArrayList<SyntaxError>();

            for (ParseError error : errorList) {
                strErrors.add(createSyntaxError(error));
            }
            throw new ParsingException(strErrors);
        }
    }

    private SyntaxError createSyntaxError(ParseError error) {
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


        int delta = 0;
        if (error instanceof BasicParseError) {
            delta = ((BasicParseError)error).getIndexDelta();
        }

        return new SyntaxError(
                oneLiner,
                line,
                col,
                error.getStartIndex() - delta,
                error.getEndIndex() - delta,
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
        return (Function) parse(ClauseType.EXPRESSION, expression);
    }

    @Override
    public Equation[] parseEquations(String expression) throws ParsingException {
        return (Equation[]) parse(ClauseType.EQUATIONS, expression);
    }
}
