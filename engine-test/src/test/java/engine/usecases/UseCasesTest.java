package engine.usecases;

import engine.calculation.*;
import engine.calculation.evaluator.FunctionEvaluator;
import engine.calculation.evaluator.ImmediateFunctionEvaluator;
import engine.calculation.vector.VectorEvaluator;
import engine.calculation.vector.VectorMachineEvaluator;
import engine.calculation.vector.implementations.VectorMachineBuilder;
import engine.expressions.Equation;
import engine.expressions.parser.ClauseType;
import engine.expressions.parser.ExpressionParser;
import engine.calculation.drawables.DrawToImage;
import engine.calculation.drawables.Drawable;
import engine.calculation.drawables.RectRange;
import engine.expressions.parser.antlr.AntlrExpressionParser;
import org.junit.BeforeClass;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.fail;

/**
 * Look use_cases.txt in src/test/resources
 *
 * User: Oleksiy Pylypenko
 * Date: 3/5/13
 * Time: 9:01 AM
 */
@RunWith(Theories.class)
public class UseCasesTest {
    public static final int PROCESSORS = Runtime.getRuntime().availableProcessors();
    private final static int MAX_CONCURRENCY = PROCESSORS;
    private static final File DIR = new File("test_images");
    static {
        DIR.mkdirs();
    }
    private ExecutorService executor = Executors.newFixedThreadPool(MAX_CONCURRENCY);
    private ViewportSize size = new ViewportSize(1000, 1000);

    static class Header {
        static {
            System.out.println("Look '" + DIR + "' directory for image results");
            System.out.println("P - number of processors");
            System.out.printf("%10s", "BASIC");
            for (int c = 1; c <= PROCESSORS; c++) {
                System.out.printf("%10s", String.format("V P=%d", c));
                System.out.printf("%10s", String.format("V2 P=%d", c));
            }
            System.out.println();
        }

    }

    @Theory
    public void testUseCase(EqualUseCase useCase) throws Exception {
        new Header();
        StringTokenizer tokenizer = new StringTokenizer(useCase.bounds);
        ViewportBounds bounds = new ViewportBounds(
                Double.parseDouble(tokenizer.nextToken()),
                Double.parseDouble(tokenizer.nextToken()),
                Double.parseDouble(tokenizer.nextToken()),
                Double.parseDouble(tokenizer.nextToken()));

        String eqs = useCase.equations.trim().replaceAll("\n\r?", ",");

        System.out.println(eqs);

        ExpressionParser parser = new AntlrExpressionParser();
        Equation []equations = (Equation[]) parser.parse(ClauseType.EQUATIONS, eqs);

        CalculationParameters params = new CalculationParameters(bounds, size, 0.0,
                equations);

        long []times = new long[1 + PROCESSORS * 2];
        int i = 0;
        times[i++] = calculateAndWrite(params,
                createBasicEngine(),
                String.format("test%03db.png",
                useCase.number));
        for (int c = 1; c <= PROCESSORS; c++) {
            times[i++] = calculateAndWrite(params,
                    createVectorEngine(c),
                    String.format("test%03dv.png",
                    useCase.number));

            times[i++] = calculateAndWrite(params,
                    createVectorEngine2(4),
                    String.format("test%03dv2.png",
                    useCase.number));
        }
        long min = Long.MAX_VALUE;
        for (long t : times) {
            if (t < min) {
                min = t;
            }
        }

        for (long t : times) {
            String str = t + " ms";
            if (t == min) {
                str = "*" + str + "*";
            }
            System.out.printf("%10s", str);
        }
        System.out.println();
        for (long t : times) {
            double idx = min;
            idx /= t;
            idx *= 100;
            System.out.printf("%9.1fp", idx);
        }
        System.out.println();
    }

    private long calculateAndWrite(CalculationParameters params, CalculationEngine engine, String filename) throws IOException {
        engine.calculate(params);
        long time = System.currentTimeMillis();
        CalculationResults results = engine.calculate(params);
        time = System.currentTimeMillis() - time;
        RectRange range = RectRange.fromViewportSize(size);
        DrawToImage drawToImage = new DrawToImage(range);
        for (Drawable drawable : results.getDrawables()) {
            drawToImage.draw(range, drawable);
        }
        drawToImage.writePng(new File(DIR, filename), range);
        return time;
    }

    private CalculationEngine createBasicEngine() {
        FunctionEvaluator evaluator = new ImmediateFunctionEvaluator();
        return new BasicCalculationEngine(evaluator);
    }

    private CalculationEngine createVectorEngine(int concurrency) {
        VectorMachineBuilder builder = new VectorMachineBuilder();
        builder.setConcurrency(concurrency, executor);
        VectorEvaluator evaluator = new VectorMachineEvaluator(builder);
        return new VectorCalculationEngine(evaluator);
    }

    private CalculationEngine createVectorEngine2(int concurrency) {
        VectorMachineBuilder builder = new VectorMachineBuilder();
        builder.setConcurrency(concurrency, executor);
        VectorEvaluator evaluator = new VectorMachineEvaluator(builder);
        return new VectorCalculationEngine2(evaluator);
    }

    private static EqualUseCase []useCases;

    @DataPoints
    public static EqualUseCase[] getUseCases() {
        return useCases;
    }

    @BeforeClass
    public static void initUseCases() {
        useCases = EqualUseCase.read(UseCasesTest.class.getResource("use_cases.txt"));
    }

    private static class EqualUseCase {
        private final int number;
        private final String description;
        private final String equations;
        private final String bounds;

        private EqualUseCase(int number, String description, String equations, String bounds) {
            this.number = number;
            this.description = description;
            this.equations = equations;
            this.bounds = bounds;
        }

        public static EqualUseCase[] read(URL resource) {
            Scanner scanner;
            try {
                if (resource == null) {
                    throw new FileNotFoundException("resource not found");
                }
                scanner = new Scanner(resource.openStream());
            } catch (IOException e) {
                throw new RuntimeException("failed to read use-cases", e);
            }
            try {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    if (line.trim().equals("------------------------")) {
                        break;
                    }
                }
                if (!scanner.hasNextLine()) {
                    return new EqualUseCase[0];
                }

                List<EqualUseCase> useCaseList = new ArrayList<EqualUseCase>();
                for (int i = 1; scanner.hasNext(); i++) {
                    boolean skip = false;
                    String line = scanner.nextLine().trim();
                    if (line.startsWith("!")) {
                        line = line.substring(1);
                        skip = true;
                    }
                    line = line.replaceAll("^\\.", "");
                    line = line.trim();
                    String description = line;
                    if (skip) {
                        System.err.println("Skipping use case: " + line);
                    }

                    String bounds = scanner.nextLine().trim();

                    StringBuilder builder = new StringBuilder();
                    while (scanner.hasNext()) {
                        line = scanner.nextLine();
                        if (line.trim().isEmpty()) {
                            break;
                        }

                        builder.append(line);
                        builder.append("\n");
                    }
                    String equations = builder.toString();

                    if (!skip) {
                        useCaseList.add(new EqualUseCase(i, description, equations, bounds));
                    }
                }

                return useCaseList.toArray(new EqualUseCase[useCaseList.size()]);
            } finally {
                scanner.close();
            }
        }
    }
}
