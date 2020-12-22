package com.coodev.app_apt.compiler;

import com.coodev.app_apt.annotation.ViewInjector;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

public class ElementPrint {
    public static boolean ENABLE = true;

    public static void print(Element element) {
        if (!ENABLE || element == null) {
            return;
        }

        switch (element.getKind()) {
            case PACKAGE:
                printPackage((PackageElement) element);
                break;
            case CLASS:
                printClass((TypeElement) element);
                break;
            case FIELD:
                printField((VariableElement) element);
                break;
            case METHOD:
                printMethod((ExecutableElement) element);
                break;
        }

    }

    private static void printMethod(ExecutableElement element) {
        final String methodName = element.getSimpleName().toString();
    }

    private static void printField(VariableElement element) {
        final String fieldName = element.getSimpleName().toString();
        final int value = element.getAnnotation(ViewInjector.class).value();
        String className = null;
        String packageName = null;
        final Element classElement = element.getEnclosingElement();
        if (classElement.getKind() == ElementKind.CLASS) {
            TypeElement typeElement = (TypeElement) classElement;
            className = typeElement.getQualifiedName().toString();
            PackageElement packageElement = (PackageElement) classElement.getEnclosingElement();
            packageName = packageElement.getQualifiedName().toString();
        }

        System.out.println(String.format("[packageName:%s,class:%s,fileName:%s,value:%s]"
                , packageName, className, fieldName, value));
    }

    private static void printClass(TypeElement element) {
        final String className = element.getQualifiedName().toString();
        String packageName = null;
        final Element enclosingElement = element.getEnclosingElement();
        if (enclosingElement.getKind() == ElementKind.PACKAGE) {
            PackageElement packageElement = (PackageElement) enclosingElement;
            packageName = packageElement.getQualifiedName().toString();
        }
        System.out.println(String.format("[packageName:%s,class:%s]"
                , packageName, className));
    }

    private static void printPackage(PackageElement element) {
        final String packageName = element.getQualifiedName().toString();

    }


}
