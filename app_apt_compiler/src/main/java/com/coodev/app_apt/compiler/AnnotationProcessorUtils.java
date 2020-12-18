package com.coodev.app_apt.compiler;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

public class AnnotationProcessorUtils {
    private Elements mElementUtils;
    private Filer mFiler;
    private Messager mMessager;
    private Types mTypeUtils;

    public AnnotationProcessorUtils(Elements elementUtils, Filer filer, Messager messager, Types typeUtils) {
        mElementUtils = elementUtils;
        mFiler = filer;
        mMessager = messager;
        mTypeUtils = typeUtils;
    }

    public Elements getElementUtils() {
        return mElementUtils;
    }


    public Filer getFiler() {
        return mFiler;
    }


    public Messager getMessager() {
        return mMessager;
    }


    public Types getTypeUtils() {
        return mTypeUtils;
    }

}
