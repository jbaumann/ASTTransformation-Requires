package de.scrum_master.groovy_ast

import org.codehaus.groovy.transform.GroovyASTTransformationClass

import java.lang.annotation.Retention
import java.lang.annotation.Target

import static java.lang.annotation.ElementType.METHOD
import static java.lang.annotation.RetentionPolicy.SOURCE

@Retention(SOURCE)
@Target([METHOD])
@GroovyASTTransformationClass(classes = [RequiresTransformation])
@interface Requires {
  String value() default "true";
}

