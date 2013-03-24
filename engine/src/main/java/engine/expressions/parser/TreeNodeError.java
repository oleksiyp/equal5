package engine.expressions.parser;

import org.parboiled.Node;
import org.parboiled.buffers.InputBuffer;
import org.parboiled.errors.ParseError;

/**
 * User: Oleksiy Pylypenko
 * Date: 3/24/13
 * Time: 11:23 AM
 */
public class TreeNodeError implements ParseError {
    private final InputBuffer inputBuffer;
    private final int startIndex;
    private final int endIndex;
    private final String errorMessage;
    private final Node<Object> node;

    public TreeNodeError(InputBuffer inputBuffer,
                         int startIndex, int endIndex,
                         Node<Object> node,
                         String errorMessage) {
        this.inputBuffer = inputBuffer;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.node = node;
        this.errorMessage = errorMessage;
    }

    public TreeNodeError(InputBuffer inputBuffer,
                         Node<Object> node,
                         String errorMessage) {
        this.inputBuffer = inputBuffer;
        this.startIndex = node.getStartIndex();
        this.endIndex = node.getEndIndex();
        this.node = node;
        this.errorMessage = errorMessage;
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
}
