package engine.expressions.parser.parboiled;

import engine.expressions.parser.ClauseType;
import engine.expressions.parser.auto_complete.*;
import org.parboiled.Rule;
import org.parboiled.errors.InvalidInputError;
import org.parboiled.errors.ParseError;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.MatcherPath;
import org.parboiled.support.ParsingResult;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * User: Oleksiy Pylypenko
 * Date: 3/27/13
 * Time: 12:34 PM
 */
public class ParboiledAutocompletionParser implements AutocompletionParser {

    public static final Pattern LESS_OR_GREATER_IN_THE_END_PATTERN = Pattern.compile("(<|>)$");

    @Override
    public List<Completion> completeExpression(ClauseType clause,
                                                      String expressionStart) {
        if (clause == null) {
            throw new IllegalArgumentException("clause");
        }
        if (expressionStart == null) {
            throw new IllegalArgumentException("expression");
        }

        EqualParboiledParser epp = EqualParboiledParser.INSTANCE;
        Rule rule = epp.getRuleMap().get(clause);
        if (rule == null) {
            throw new UnsupportedOperationException("clause '" + clause + "' is not handled");
        }

        rule = epp.WholeSentence(rule);

        ReportingParseRunner<Object> runner;
        runner = new ReportingParseRunner<Object>(rule);
        ParsingResult<Object> result = runner.run(expressionStart + "$");

        List<Completion> variants = new ArrayList<Completion>();

        for (ParseError error : result.parseErrors) {
            if (error.getStartIndex() != expressionStart.length()) {
                continue;
            }
            if (error instanceof InvalidInputError) {
                InvalidInputError iie = (InvalidInputError) error;
                EqualAutocompleteRules executor = new EqualAutocompleteRules(expressionStart);
                List<MatcherPath> matchers = iie.getFailedMatchers();
                executor.run(matchers);
                variants.addAll(executor.getVariants());
            }
        }

        return variants;
    }

}
