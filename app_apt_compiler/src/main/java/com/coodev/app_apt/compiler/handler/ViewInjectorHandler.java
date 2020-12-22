package com.coodev.app_apt.compiler.handler;

import com.coodev.app_apt.annotation.ViewInjector;
import com.coodev.app_apt.compiler.AnnotationProcessorUtils;
import com.coodev.app_apt.compiler.ElementPrint;

import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;

/**
 * {@link ViewInjector}
 */
public class ViewInjectorHandler implements AnnotationHandler {
    private AnnotationProcessorUtils mAnnotationProcessorUtils;

    @Override
    public void attachProcessorEnv(AnnotationProcessorUtils envUtils) {
        mAnnotationProcessorUtils = envUtils;
    }

    @Override
    public void handleAnnotation(RoundEnvironment roundEnvironment) {
        final Set<? extends Element> getSetAnnotations = roundEnvironment.getElementsAnnotatedWith(ViewInjector.class);
        for (Element element : getSetAnnotations) {
            ElementPrint.print(element);
        }
    }
}
