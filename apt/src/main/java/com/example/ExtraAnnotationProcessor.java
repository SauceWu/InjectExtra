package com.example;

import com.squareup.javapoet.MethodSpec;

import javax.annotation.processing.RoundEnvironment;

import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

/**
 * Created by sauce on 2017/3/8.
 * Version 1.0.0
 */
public class ExtraAnnotationProcessor {
    public static void process(RoundEnvironment roundEnv, AnnotationProcessor annotationProcessor) {
        MethodSpec.Builder methodBuilder1 = MethodSpec.methodBuilder("")
                .addJavadoc("@此方法由apt自动生成")
                .addModifiers(PUBLIC, STATIC);


    }
}
