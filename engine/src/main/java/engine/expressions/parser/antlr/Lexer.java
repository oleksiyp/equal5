package engine.expressions.parser.antlr;

import engine.expressions.parser.SyntaxError;
import org.antlr.runtime.*;

import java.util.ArrayList;
import java.util.List;

/**
 * HACK: this class is injected in ANTLR hierarchy
 * User: Oleksiy Pylypenko
 * Date: 3/30/13
 * Time: 4:51 PM
 */
abstract class Lexer
        extends org.antlr.runtime.Lexer {
    private final List<SyntaxError> syntaxErrors = new ArrayList<SyntaxError>();

    protected Lexer() {
    }

    protected Lexer(CharStream input) {
        super(input);
    }

    protected Lexer(CharStream input, RecognizerSharedState state) {
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
}
