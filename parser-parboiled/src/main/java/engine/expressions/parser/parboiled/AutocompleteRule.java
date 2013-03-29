package engine.expressions.parser.parboiled;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
* User: Oleksiy Pylypenko
* Date: 3/27/13
* Time: 5:29 PM
*/
@Retention(RetentionPolicy.RUNTIME)
@interface AutocompleteRule {
    String[] value();
}
