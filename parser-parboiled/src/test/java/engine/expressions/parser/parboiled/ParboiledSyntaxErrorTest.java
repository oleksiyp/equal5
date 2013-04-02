package engine.expressions.parser.parboiled;

import engine.expression.parser.AbstractSyntaxErrorTest;
import engine.expressions.parser.ClauseType;
import engine.expressions.parser.ParsingException;
import engine.expressions.parser.SyntaxError;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.fail;

/**
 * User: Oleksiy Pylypenko
 * Date: 3/23/13
 * Time: 10:32 PM
 */
public class ParboiledSyntaxErrorTest extends AbstractSyntaxErrorTest<ParboiledExpressionParser> {
    public ParboiledSyntaxErrorTest() {
        super(new ParboiledExpressionParser());
    }
}
