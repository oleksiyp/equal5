package engine.expressions.parser.parboiled;

import engine.calculation.Arguments;
import engine.calculation.functions.*;
import engine.expression.parser.AbstractExpressionParserTest;
import engine.expressions.Equation;
import engine.expressions.Function;
import engine.expressions.parser.ClauseType;
import engine.expressions.parser.ParsingException;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static junit.framework.Assert.assertEquals;

/**
 * User: Oleksiy Pylypenko
 * Date: 2/9/13
 * Time: 12:49 PM
 */
public class ParboiledExpressionParserTest extends AbstractExpressionParserTest<ParboiledExpressionParser> {
    public ParboiledExpressionParserTest() {
        super(new ParboiledExpressionParser());
    }
}
