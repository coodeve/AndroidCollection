package com.example.annotationlib;

import java.util.Map;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

public class ProcessUtils {
    private Elements mElementUtils;
    private Filer mFiler;
    private Types mTypeUtils;
    private Messager mMessager;
    private Map<String, String> mOptions;
    private ProcessingEnvironment mProcessingEnvironment;

    public ProcessUtils(ProcessingEnvironment processingEnv) {
        this.mProcessingEnvironment = processingEnv;
        mElementUtils = processingEnv.getElementUtils();
        mFiler = processingEnv.getFiler();
        mTypeUtils = processingEnv.getTypeUtils();
        mMessager = processingEnv.getMessager();
        mOptions = processingEnv.getOptions();
    }

    public ProcessingEnvironment getProcessingEnvironment() {
        return mProcessingEnvironment;
    }

    public Elements getElementUtils() {
        return mElementUtils;
    }

    public Filer getFiler() {
        return mFiler;
    }

    public Types getTypeUtils() {
        return mTypeUtils;
    }

    public Messager getMessager() {
        return mMessager;
    }

    public Map<String, String> getOptions() {
        return mOptions;
    }
}
