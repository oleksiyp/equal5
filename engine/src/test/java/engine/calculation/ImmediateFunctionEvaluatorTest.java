package engine.calculation;

import engine.calculation.functions.*;
import engine.expressions.Name;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * User: Oleksiy Pylypenko
 * At: 2/8/13  3:32 PM
 */
public class ImmediateFunctionEvaluatorTest {
    private FunctionEvaluator asEvaluator;

    @Before
    public void setUp() throws Exception {
        asEvaluator = new ImmediateFunctionEvaluator();
    }

    @Test
    public void testOk() throws Exception {
        Arguments args = new Arguments() {
            @Override
            public Name[] getArguments() {
                return new Name[0];
            }

            @Override
            public double getValue(Name name) {
                return 0;
            }
        };

        assertEquals(5, asEvaluator.calculate(new Constant(5), args), 1e-6);
        assertEquals(5, asEvaluator.calculate(
                new Addition(new Constant(3), new Constant(2)), args
        ), 1e-6);
        assertEquals(6, asEvaluator.calculate(
                new Multiplication(new Constant(3), new Constant(2)), args
        ), 1e-6);
        assertEquals(1, asEvaluator.calculate(
                new Subtraction(new Constant(3), new Constant(2)), args
        ), 1e-6);
        assertEquals(1.5, asEvaluator.calculate(
                new Division(new Constant(3), new Constant(2)), args
        ), 1e-6);
        assertEquals(9, asEvaluator.calculate(
                new Power(new Constant(3), new Constant(2)), args
        ), 1e-6);
    }

    @Test(expected = UnknownArgumentUsedException.class)
    public void testFail() throws Exception {
        Arguments args = new Arguments() {
            @Override
            public Name[] getArguments() {
                return new Name[] { new Name("y") };
            }

            @Override
            public double getValue(Name name) {
                if (name.getSymbols().equals("y")) {
                    throw new UnknownArgumentUsedException(name);
                }
                return 0;
            }
        };
        asEvaluator.calculate(new Variable(new Name("y")), args);
    }

}
