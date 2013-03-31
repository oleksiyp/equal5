package engine.expressions.parser.antlr;

import engine.expressions.parser.ClauseType;
import engine.expressions.parser.ExpressionParser;
import engine.expressions.parser.ParsingException;
import engine.expressions.parser.SyntaxError;
import engine.expressions.parser.auto_complete.AutocompletionParser;
import org.antlr.runtime.*;
import org.antlr.runtime.tree.RewriteEarlyExitException;

import java.util.List;
import java.util.Map;

/**
 * User: Oleksiy Pylypenko
 * Date: 3/30/13
 * Time: 10:39 AM
 */
public class AntlrExpressionParser implements ExpressionParser {

    @Override
    public Object parse(ClauseType clause, String expression) throws ParsingException {
        CommonTokenStream tokens = lexer(expression);
        ParserRuleReturnScope ruleReturn = parser(clause, tokens);
        return builder(clause, ruleReturn);
    }

    private CommonTokenStream lexer(String expression) throws ParsingException {
        EqualLexer lex = new EqualLexer(new ANTLRStringStream(expression));
        CommonTokenStream tokens = new CommonTokenStream(lex);
        tokens.fill();
        List<SyntaxError> errors = lex.getSyntaxErrors();
        if (!errors.isEmpty()) {
            throw new ParsingException(errors);
        }
        return tokens;
    }

    private ParserRuleReturnScope parser(ClauseType clause, CommonTokenStream tokens) throws ParsingException {
        EqualParser parser = new EqualParser(tokens);
        ParserRuleReturnScope ruleReturn = null;

        try {
            ruleReturn = clause.accept(parser);
            parser.theEnd();
        } catch (RecognitionException e) {
            if (parser.getSyntaxErrors().isEmpty()) {
                throw new RuntimeException("parser failed but no errors recorded", e);
            }
        } catch (RewriteEarlyExitException e) {
            parser
                    .getSyntaxErrors()
                    .add(new SyntaxError(1, 1, 0, 1, "bad input"));
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
        List<SyntaxError> errors = parser.getSyntaxErrors();
        if (!errors.isEmpty()) {
            throw new ParsingException(errors);
        }
        return ruleReturn;
    }

    private Object builder(ClauseType clause, ParserRuleReturnScope ruleReturn) throws ParsingException {
        AntlrExpressionBuilder builder = new AntlrExpressionBuilder();
        Object result = null;
        try {
            result = builder.build(clause, ruleReturn);
        } catch (ExpressionBuilderFailure failure) {
            if (builder.getErrors().isEmpty()) {
                throw new RuntimeException("parser failed but no errors recorded", failure);
            }
        }
        if (!builder.getErrors().isEmpty()) {
            throw new ParsingException(builder.getErrors());
        }
        return result;
    }

    @Override
    public AutocompletionParser createAutocompletionParser() {
        return null;
    }

    @Override
    public void setKnownConstants(Map<String, Double> knownConstants) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setVarList(List<String> varList) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Map<String, Double> getKnownConstants() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<String> getVarList() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
