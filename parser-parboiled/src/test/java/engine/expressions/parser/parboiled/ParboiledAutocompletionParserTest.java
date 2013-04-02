package engine.expressions.parser.parboiled;

import engine.expression.parser.AbstractAutocompletionParserTest;
import engine.expressions.parser.ClauseType;
import engine.expressions.parser.auto_complete.Completion;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.fail;

/**
 * User: Oleksiy Pylypenko
 * Date: 3/27/13
 * Time: 1:23 PM
 */
public class ParboiledAutocompletionParserTest extends AbstractAutocompletionParserTest<ParboiledAutocompletionParser> {
    public ParboiledAutocompletionParserTest() {
        super(new ParboiledAutocompletionParser());
    }
}
