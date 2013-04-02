package engine.expressions.parser.antlr;

import engine.expressions.parser.ClauseType;
import engine.expressions.parser.ClauseTypeVisitor;
import engine.expressions.parser.auto_complete.AutocompletionParser;
import engine.expressions.parser.auto_complete.Completion;
import engine.expressions.parser.auto_complete.CompletionType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: Oleksiy Pylypenko
 * Date: 3/30/13
 * Time: 10:40 AM
 */
public class AntlrAutocompletionParser implements AutocompletionParser {
    public static final Pattern EXTRACT_FINAL_ID_PATTERN = Pattern.compile("([a-z][a-z\\d]*)$");

    @Override
    public List<Completion> completeExpression(ClauseType clause, String expressionStart) {
        Matcher matcher = EXTRACT_FINAL_ID_PATTERN.matcher(expressionStart);
        if (matcher.find()) {
            String prefix = matcher.group(1);
            return Arrays.asList(
                    new Completion(CompletionType.VARIABLE_NAME, prefix),
                    new Completion(CompletionType.FUNCTION_NAME, prefix));
        }
        return Collections.emptyList();
    }
}
