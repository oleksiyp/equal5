package engine;

import junit.framework.TestSuite;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * User: Oleksiy Pylypenko
 * Date: 3/5/13
 * Time: 9:01 AM
 */
@RunWith(Theories.class)
public class UseCasesTest {

    @Theory
    public void testUseCases(EqualUseCase useCase) throws Exception {
        System.out.println(useCase.equations);
    }

    private static EqualUseCase []useCases;

    @DataPoints
    public static EqualUseCase[] getUseCases() {
        return useCases;
    }

    @BeforeClass
    public static void initUseCases() {
        try {
            useCases = EqualUseCase.read(UseCasesTest.class.getResource("use_cases.txt"));
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            throw new AssertionError("loading problem", ex);
        }
    }

    private static class EqualUseCase {
        private final int number;
        private final String description;
        private final String equations;

        private EqualUseCase(int number, String description, String equations) {
            this.number = number;
            this.description = description;
            this.equations = equations;
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
                    String str = scanner.next("\\d+[!\\.]");
                    boolean skip = false;
                    if (str.substring(str.length() - 1).equals("!")) {
                        skip = true;
                    }
                    str = str.substring(0, str.length() - 1);
                    int t = Integer.parseInt(str);
                    if (i != t) {
                        System.err.println("Mismatch in use-case numbering " + i + " " + t);
                    }

                    String line = scanner.nextLine();
                    line = line.replaceAll("^\\.", "");
                    line = line.trim();
                    String description = line;
                    if (skip) {
                        System.err.println("Skipping use case: " + line);
                    }

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
                        useCaseList.add(new EqualUseCase(i, description, equations));
                    }
                }

                return useCaseList.toArray(new EqualUseCase[useCaseList.size()]);
            } finally {
                scanner.close();
            }
        }
    }
}
