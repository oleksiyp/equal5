package engine.expressions;

import org.parboiled.buffers.InputBuffer;
import org.parboiled.errors.ParseError;
import org.parboiled.support.MatcherPath;

/**
 * User: Oleksiy Pylypenko
 * Date: 3/23/13
 * Time: 9:30 PM
 */
public class SimpleActionError implements ParseError {

    private final InputBuffer inputBuffer;
    private final int startIndex;
    private final int endIndex;
    private final String errorMessage;
    private final MatcherPath matcherPath;

    public SimpleActionError(InputBuffer inputBuffer,
                             int startIndex,
                             int endIndex,
                             String errorMessage,
                             MatcherPath matcherPath) {
        this.inputBuffer = inputBuffer;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.errorMessage = errorMessage;
        this.matcherPath = matcherPath;
    }

    @Override
    public InputBuffer getInputBuffer() {
        return inputBuffer;
    }

    @Override
    public int getStartIndex() {
        return startIndex;
    }

    @Override
    public int getEndIndex() {
        return endIndex;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }

    public MatcherPath getMatcherPath() {
        return matcherPath;
    }
}
