package com.coodev.app_apt.compiler.handler;

import com.coodev.app_apt.compiler.AnnotationProcessorUtils;

import javax.annotation.processing.RoundEnvironment;

public interface AnnotationHandler {
    void attachProcessorEnv(AnnotationProcessorUtils envUtils);

    void handleAnnotation(RoundEnvironment roundEnvironment);
}
