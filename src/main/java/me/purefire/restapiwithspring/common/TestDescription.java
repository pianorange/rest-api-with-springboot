package me.purefire.restapiwithspring.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom Annotation For Description of TestClass
 *
 * Retention 얼마나 오래 지속시키느냐
 * RetentionPolicy.SOURCE
 * RetentionPolicy.RunTime
 * RetentionPolicy.Class
 */
@Target(ElementType.METHOD)
//how long
@Retention(RetentionPolicy.SOURCE)
public @interface TestDescription {

    String value();

}
