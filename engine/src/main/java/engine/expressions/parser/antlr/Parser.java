package engine.expressions.parser.antlr;

import engine.expressions.parser.ClauseTypeVisitor;
import engine.expressions.parser.SyntaxError;
import org.antlr.runtime.*;

import java.util.ArrayList;
import java.util.List;

/**
 * HACK: this class is injected in ANTLR hierarchy
 * this is due 'superClass=BaseEqualParser;' is only working for parsers not lexers
 * User: Oleksiy Pylypenko
 * Date: 3/30/13
 * Time: 4:51 PM
 */
abstract class Parser
        extends org.antlr.runtime.Parser implements ClauseTypeVisitor<ParserRuleReturnScope> {
    private final List<SyntaxError> syntaxErrors = new ArrayList<SyntaxError>();

    public Parser(TokenStream input) {
        super(input);
    }

    public Parser(TokenStream input, RecognizerSharedState state) {
        super(input, state);
    }

    public void displayRecognitionError(String[] tokenNames,
                                        RecognitionException ex)
    {
        String msg = getErrorMessage(ex, tokenNames);

        syntaxErrors.add(new SyntaxError(
                ex.line, ex.charPositionInLine,
                ex.index, ex.index + 1,
                msg));
    }

    public void reset() {
        syntaxErrors.clear();
        super.reset();
    }

    public List<SyntaxError> getSyntaxErrors() {
        return syntaxErrors;
    }

    @Override
    protected Object getMissingSymbol(IntStream input,
                                      RecognitionException e,
                                      int expectedTokenType,
                                      BitSet follow)
    {
        String tokenText;
        if ( expectedTokenType==Token.EOF ) tokenText = "<missing EOF>";
        else tokenText = "<missing "+getTokenNames()[expectedTokenType]+">";
        CommonToken t = new CommonToken(expectedTokenType, tokenText);
        Token current = ((TokenStream)input).LT(1);
        if ( current.getType() == Token.EOF ) {
            Token tok = ((TokenStream)input).LT(-1);
            if (tok != null) {
                current = tok;
            }
        }
        t.setLine(current.getLine());
        t.setCharPositionInLine(current.getCharPositionInLine());
        t.setChannel(DEFAULT_TOKEN_CHANNEL);
        t.setInputStream(current.getInputStream());
        return t;
    }
}
