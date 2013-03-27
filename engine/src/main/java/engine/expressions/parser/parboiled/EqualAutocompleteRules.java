package engine.expressions.parser.parboiled;

import engine.expressions.parser.auto_complete.Completion;
import engine.expressions.parser.auto_complete.CompletionType;
import org.parboiled.support.MatcherPath;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;

/**
* User: Oleksiy Pylypenko
* Date: 3/27/13
* Time: 5:28 PM
*/
class EqualAutocompleteRules {
    private final List<Completion> variants = new ArrayList<Completion>();
    private final String expressionStart;

    EqualAutocompleteRules(String expressionStart) {
        this.expressionStart = expressionStart;
    }

    public List<Completion> getVariants() {
        return variants;
    }

    @AutocompleteRule({"*/*/*/Name/MathFunc/**","*/Name/MathFunc/**"})
    protected void functionName() {
        String prefix = EqualParboiledParser.extractFinalId(expressionStart);
        variants.add(new Completion(CompletionType.FUNCTION_NAME, prefix));
    }

    @AutocompleteRule({"'('/Parentheses/**", "'('/MathFunc/**"})
    protected void openBracket() {
        variants.add(new Completion(CompletionType.OPEN_BRACKET));
    }

    @AutocompleteRule({"')'/Parentheses/**", "')'/MathFunc/**"})
    protected void closeBracket() {
        variants.add(new Completion(CompletionType.CLOSE_BRACKET));
    }

    @AutocompleteRule({"*/*/*/Name/Variable/**","*/Name/Variable/**"})
    protected void variableName() {
        String prefix = EqualParboiledParser.extractFinalId(expressionStart);
        variants.add(new Completion(CompletionType.VARIABLE_NAME, prefix));
    }

    @AutocompleteRule("Operator/OperatorFactor/**")
    protected void factorOperator() {
        variants.add(new Completion(CompletionType.FACTOR_OPERATOR));
    }

    @AutocompleteRule("Operator/OperatorTerm/**")
    protected void termOperator() {
        variants.add(new Completion(CompletionType.TERM_OPERATOR));
    }

    @AutocompleteRule({"*/*/EqualitySign/**", "*/EqualitySign/**"})
    protected void equalitySign() {
        Matcher matcher = ParboiledAutocompletionParser.LESS_OR_GREATER_IN_THE_END_PATTERN.matcher(expressionStart);
        if (matcher.find()) {
            variants.add(new Completion(CompletionType.EQUALITY_SIGN, matcher.group(1)));
        } else {
            variants.add(new Completion(CompletionType.EQUALITY_SIGN));
        }
    }

    @AutocompleteRule("*/*/DecimalFloat/**")
    protected void number() {
        variants.add(new Completion(CompletionType.NUMBER));
    }

    static class Rule {
        private final AutocompleteRule annotation;
        private final Method method;

        Rule(AutocompleteRule annotation, Method method) {
            this.annotation = annotation;
            this.method = method;
        }

        private static class RuleMatcher {
            private final String[] pattern;

            private RuleMatcher(String[] pattern) {
                this.pattern = pattern;
            }

            private boolean recursiveMatch(int pos, MatcherPath path) {
                if (pos >= pattern.length) {
                    return path == null;
                }
                if (path == null) {
                    return false;
                }
                String value = pattern[pos];
                if ("*".equals(value)) {
                    return recursiveMatch(pos + 1, path.parent);
                } else if ("**".equals(value)) {
                    // recursiveMatch end of expression
                    if (recursiveMatch(pos + 1, null)) {
                        return true;
                    }
                    // iterate
                    while (path != null) {
                        if (recursiveMatch(pos + 1, path)) {
                            return true;
                        }
                        path = path.parent;
                    }
                } else if (value.equals(path.element.matcher.getLabel())) {
                    return recursiveMatch(pos + 1, path.parent);
                }
                return false;
            }

            public boolean match(MatcherPath matcherPath) {
                return recursiveMatch(0, matcherPath);
            }
        }

        public boolean match(MatcherPath path) {
            String[] arr = annotation.value();
            for (String value : arr) {
                if (new Rule.RuleMatcher(value.split("/"))
                        .match(path)) {
                    return true;
                }
            }
            return false;
        }

        public void execute(EqualAutocompleteRules executor) {
            try {
                method.invoke(executor);
            } catch (Exception e) {
                throw new RuntimeException("rule execution problem", e);
            }
        }
    }

    private static List<Rule> rules = new ArrayList<Rule>();
    static {
        for (Method method : EqualAutocompleteRules.class.getDeclaredMethods()) {
            if (method.isAnnotationPresent(AutocompleteRule.class)) {
                rules.add(new Rule(
                        method.getAnnotation(AutocompleteRule.class),
                        method));
            }
        }
    }

    public void run(List<MatcherPath> matcherPaths) {
        Set<MatcherPath> worked = new HashSet<MatcherPath>();
        for (Rule rule : rules) {
            for (MatcherPath path : matcherPaths) {
                if (rule.match(path)) {
                    rule.execute(this);
                    worked.add(path);
                    break;
                }
            }
            for (MatcherPath p2 : matcherPaths) {
                if (rule.match(p2)) {
                    worked.add(p2);
                }
            }
        }
        ArrayList<MatcherPath> paths = new ArrayList<MatcherPath>(matcherPaths);
        paths.remove(worked);
        for (MatcherPath path : paths) {
            System.out.println(path);
        }
    }
}
