package com.example.annotationlib;

import com.example.annotationlib.adapter.InjectAdapter;

import java.util.HashMap;
import java.util.Map;

public class ViewBind {
    public static final String SUFFIX = "$InjectAdapter";

    private static Map<Class<?>, InjectAdapter<?>> sClassInjectAdapterMap = new HashMap<>();




}
