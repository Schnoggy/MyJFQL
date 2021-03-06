package de.jokergames.jfql.server.controller;

import de.jokergames.jfql.server.util.Method;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Janick
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)

public @interface ControllerHandler {

    Method method() default Method.GET;

    int status() default 200;

    String path();

}
