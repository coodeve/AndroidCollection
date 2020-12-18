package com.coodev.app_apt.compiler;

import com.coodev.app_apt.compiler.handler.AnnotationHandler;
import com.coodev.app_apt.compiler.handler.GetSetHandler;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

/**
 * 自定义注解处理器
 */
@SupportedAnnotationTypes("com.coodev.app_apt.compiler.annotation.*")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class CooAnnotationProcessor extends AbstractProcessor {

    private AnnotationProcessorUtils mAnnotationProcessorUtils = null;
    /**
     * 注解处理器集合
     */
    private List<AnnotationHandler> mAnnotationHandlers = new LinkedList<>();

    private void registerAnnotationHandler() {
        mAnnotationHandlers.add(new GetSetHandler());
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mAnnotationProcessorUtils = new AnnotationProcessorUtils(
                processingEnvironment.getElementUtils(),
                processingEnvironment.getFiler(),
                processingEnvironment.getMessager(),
                processingEnvironment.getTypeUtils());
        registerAnnotationHandler();
        System.out.println("CooAnnotationProcessor init success");
        mAnnotationProcessorUtils.getMessager().printMessage(Diagnostic.Kind.NOTE, "CooAnnotationProcessor init success #Message");
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        for (AnnotationHandler annotationHandler : mAnnotationHandlers) {
            annotationHandler.attachProcessorEnv(mAnnotationProcessorUtils);
            annotationHandler.handleAnnotation(roundEnvironment);
        }
        return true;
    }
}
