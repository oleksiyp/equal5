package engine.calculation.vector;

import com.google.common.base.Stopwatch;
import engine.calculation.Arguments;
import engine.calculation.evaluator.ImmediateFunctionEvaluator;
import engine.calculation.functions.*;
import engine.calculation.vector.fillers.VectorFiller;
import engine.expressions.Function;
import engine.expressions.ParboiledExpressionParser;
import engine.expressions.ParsingException;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

/**
 * User: Oleksiy Pylypenko
 * At: 3/12/13  7:47 PM
 */
public class VectorMachineEvaluatorTest {
    private ConcurrentVectorEvaluator ve;
    private static final int N_SAMPLES = 100;
    private static final int SIZE = 2000 * 2000; // 4 mega-pixels
    private static final Random RND = new Random(232);
    private static final double EPSILON = 1e-6;

    @Before
    public void setUp() throws Exception {
        ve = new VectorMachineEvaluator();
    }

    @Test
    public void testCalculate() throws Exception {
        SomeKindOfArguments args = arguments("x", 1, "y", 2, "z", 3, "w", 4);

        check(args, "10*y+x");

        check(args, "5");
        check(args, "4+y");
        check(args, "z*y+x");
        // x^2+y^2-25
        check(args, new Subtraction(
                new Addition(
                        new Power(new Variable("x"), new Constant(2)),
                        new Power(new Variable("y"), new Constant(2))),
                new Constant(25))
        );

        check(args, "(x+1)*(x+1)+(y+1)*(y+1)-25");
        check(args, "z*x+x/y-y","x/y","z*x","x*x","y*x","y*z", "x*x");
        check(args,
                "(x+1)*(x+1)+(y+1)*(y+1)-25",
                "(x+1)*(x+1)+(y+1)*(y+1)-14",
                "(x+1)*(x+1)+(y+1)*(y+1)-90",
                "(x+1)*(x+1)+(y+1)*(y+1)-32",
                "(x+1)*(x+1)+(y+1)*(y+1)-15"
        );
        check(args,
                "(x+1)*(x+1)+(y+1)*(y+1)+(z+1)*(z+1)+(w+1)*(w+1)-25",
                "(x+1)*(x+1)+(y+1)*(y+1)+(z+1)*(z+1)+(w+1)*(w+1)-14",
                "(x+1)*(x+1)+(y+1)*(y+1)+(z+1)*(z+1)+(w+1)*(w+1)-90",
                "(x+1)*(x+1)+(y+1)*(y+1)+(z+1)*(z+1)+(w+1)*(w+1)-32",
                "(x+1)*(x+1)+(y+1)*(y+1)+(z+1)*(z+1)+(w+1)*(w+1)-15"
        );
    }

    private void check(SomeKindOfArguments args, String ...expressions) {
        Function[]functions = new Function[expressions.length];
        try {
            for (int i = 0; i < expressions.length; i++) {
                functions[i] = new ParboiledExpressionParser().parseFunction(expressions[i]);
            }
        } catch (ParsingException e) {
            throw new RuntimeException(e);
        }
        check(args, functions);
    }

    private void check(SomeKindOfArguments args, Function ...functions) {
        check(false, args, functions);
        check(true, args, functions);
    }

    private void check(boolean concurrent, SomeKindOfArguments args, Function ...functions) {
        if (Runtime.getRuntime().availableProcessors() == 1 && concurrent) {
            System.out.println("Skipping concurrent run on singe processor machine!");
            return;
        }
        int concurrency = concurrent ? Runtime.getRuntime().availableProcessors() : 1;

        ve.setSize(SIZE);
        ve.setFunctions(functions);

        Stopwatch sw = new Stopwatch().start();
        ve.prepare();
        double prepTime = sw.stop().elapsedTime(TimeUnit.MICROSECONDS) / 1000.0;

        sw.reset().start();
        double[][] results;
        try {
            System.out.println("Function: " + Arrays.toString(functions));
            System.out.println("Processing operations:");
            System.out.println("Concurrency: " + concurrency);
            ve.setTimeReporter(new TimeReporter() {
                @Override
                public synchronized void report(String operation, int size, double ms, int nRunner) {
                    double s = ms / 10000;
                    double mops = size;
                    mops /= s;
                    mops /= 1000000000L;
                    System.out.printf("%s for %.2f GigaOp/s in %.2f ms on Runner#%d%n", operation, mops, ms, nRunner);
                }
            });

            results = ve.calculate(args);
        } finally {
            double time = sw.stop().elapsedTime(TimeUnit.MICROSECONDS) / 1000.0;
            System.out.printf("Total time is %.2f ms and preparation is %.2f ms%n", time, prepTime);
            System.out.println();
        }

        ImmediateFunctionEvaluator evaluator = new ImmediateFunctionEvaluator();

        for (int i = 0; i < N_SAMPLES; i++) {
            int nVal = RND.nextInt(SIZE);

            args.setOffset(nVal);

            for (int j = 0; j < functions.length; j++)
            {
                double actual = results[j][nVal];
                double expected = evaluator.calculate(functions[j], args);

                assertEquals("Sample #" + nVal + " in vector calculation of '" + functions[j] + "'",
                        expected, actual, EPSILON);
            }
        }
    }



    private SomeKindOfArguments arguments(Object ...args) {
        final Map<String, Double> map = new HashMap<String, Double>();
        for (int i = 0; i+1 < args.length; i+=2) {
            String name = (String) args[i];
            Double val = ((Number) args[i+1]).doubleValue();
            map.put(name, val);
        }
        return new SomeKindOfArguments(map);
    }

    private static class SomeKindOfArguments implements VectorArguments, Arguments {
        private final Map<String, Double> map;

        public SomeKindOfArguments(Map<String, Double> map) {
            this.map = map;
        }

        @Override
        public String[] getArgumentNames() {
            Set<String> set = map.keySet();
            return set.toArray(new String[set.size()]);
        }

        private int offset = 0;

        public void setOffset(int offset) {
            this.offset = offset;
        }

        @Override
        public double getValue(String name) {
            double off = offset;
            off /= 1000;
            return off + map.get(name);
        }

        @Override
        public VectorFiller getVectorFiller(final String argument) {
            return new VectorFiller() {
                @Override
                public void fill(double[] vector) {
                    for (int i = 0; i < vector.length; i++) {
                        double off = i;
                        off /= 1000;
                        vector[i] = map.get(argument) + off;
                    }
                }
            };
        }
    }
}
