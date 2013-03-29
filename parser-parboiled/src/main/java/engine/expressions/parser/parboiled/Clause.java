package engine.expressions.parser.parboiled;

import engine.expressions.parser.ClauseType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
* User: Oleksiy Pylypenko
* Date: 3/24/13
* Time: 2:05 AM
*/
@Retention(RetentionPolicy.RUNTIME)
@interface Clause {
    ClauseType value();
}
