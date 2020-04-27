package com.example.annotationlib.impl;


import com.example.annotationlib.annontation.ViewInjector;
import com.example.annotationlib.interfaces.AnnotationHandler;

import java.lang.annotation.AnnotationTypeMismatchException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;

public class ViewInjectHandler implements AnnotationHandler {
    private ProcessingEnvironment mProcessingEnvironment;

    @Override
    public void attachProcessingEnv(ProcessingEnvironment processingEnvironment) {
        mProcessingEnvironment = processingEnvironment;
    }

    @Override
    public Map<String, List<VariableElement>> handleAnnotation(RoundEnvironment roundEnvironment) {
        Map<String, List<VariableElement>> map = new HashMap<>();
        Set<? extends Element> elementsAnnotatedWith = roundEnvironment.getElementsAnnotatedWith(ViewInjector.class);
        for (Element element : elementsAnnotatedWith) {
            ElementKind kind = element.getKind();
            if (kind != ElementKind.FIELD) {
                // 不是字段的不进行处理
                throw new AnnotationTypeMismatchException(null, "Only Field can be @ViewInjector");
            }

            VariableElement filedType = (VariableElement) element;
            String fullClassName = getParentClassName(filedType);
            mProcessingEnvironment.getMessager().printMessage(Diagnostic.Kind.WARNING, "className:" + fullClassName);
            List<VariableElement> variableElements = map.get(fullClassName);
            if (variableElements == null) {
                variableElements = new ArrayList<>();
            }

            variableElements.add(filedType);
            map.put(fullClassName, variableElements);
        }
        return map;
    }

    private String getParentClassName(VariableElement variableElement) {
        TypeElement typeElement = (TypeElement) variableElement.getEnclosingElement();
        String packageName = mProcessingEnvironment.getElementUtils().getPackageOf(typeElement).getQualifiedName().toString();
        return packageName + "." + typeElement.getSimpleName().toString();
    }
}
