package engine.expressions.parser;

import engine.expressions.ClauseType;

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
