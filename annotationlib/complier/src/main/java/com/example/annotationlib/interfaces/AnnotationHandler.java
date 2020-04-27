package com.example.annotationlib.interfaces;

import java.util.List;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.VariableElement;

public interface AnnotationHandler {

    void attachProcessingEnv(ProcessingEnvironment processingEnvironment);

    Map<String, List<VariableElement>> handleAnnotation(RoundEnvironment roundEnvironment);
}
