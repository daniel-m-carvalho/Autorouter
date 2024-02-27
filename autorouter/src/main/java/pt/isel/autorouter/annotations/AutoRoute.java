package pt.isel.autorouter.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import pt.isel.autorouter.ArRetType;
import pt.isel.autorouter.ArVerb;

@Target(ElementType.METHOD) //Only applicable to methods
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoRoute {
    String value();
    ArVerb method() default ArVerb.GET;
    ArRetType type() default ArRetType.OBJECT;
}

