package com.example.annotationlib;


import com.example.annotationlib.annontation.ViewInjector;
import com.example.annotationlib.impl.ViewInjectHandler;
import com.example.annotationlib.interfaces.AnnotationHandler;
import com.google.auto.service.AutoService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;

@AutoService(ViewInjector.class)
public class ViewInjectorProcessor extends AbstractProcessor {
    public static final String TAG = ViewInjectorProcessor.class.getSimpleName();


    List<AnnotationHandler> mAnnotationHandlerList = new ArrayList<>();

    Map<String, List<VariableElement>> map = new HashMap<>();

    ProcessUtils mProcessUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mProcessUtils = new ProcessUtils(processingEnvironment);
        mAnnotationHandlerList.add(new ViewInjectHandler());
        mProcessUtils.getMessager().printMessage(Diagnostic.Kind.WARNING, "init success");
        System.out.println("init success");
    }


    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> supportSet = new HashSet<>();
        supportSet.add(ViewInjector.class.getCanonicalName());
        return supportSet;
    }



    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        for (AnnotationHandler annotationHandler : mAnnotationHandlerList) {
            annotationHandler.attachProcessingEnv(mProcessUtils.getProcessingEnvironment());
            map.putAll(annotationHandler.handleAnnotation(roundEnvironment));
        }
        return true;
    }
}
