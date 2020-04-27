package com.example.annotationlib.write;

import com.example.annotationlib.ViewBind;
import com.example.annotationlib.interfaces.AdapterWriter;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.JavaFileObject;

public abstract class AbsWriter implements AdapterWriter {
    ProcessingEnvironment mProcessingEnvironment;
    Filer mFiler;

    public AbsWriter(ProcessingEnvironment processingEnvironment) {
        mProcessingEnvironment = processingEnvironment;
        mFiler = processingEnvironment.getFiler();
    }

    @Override
    public void generate(Map<String, List<VariableElement>> map) {
        Set<Map.Entry<String, List<VariableElement>>> entries = map.entrySet();
        for (Map.Entry<String, List<VariableElement>> entry : entries) {
            List<VariableElement> variableElements = entry.getValue();
            if (variableElements == null || variableElements.size() == 0) {
                continue;
            }

            InjectorInfo injectorInfo = createInjectorInfo(variableElements.get(0));
            Writer writer = null;
            JavaFileObject javaFileObject;
            try {
                javaFileObject = mFiler.createSourceFile(injectorInfo.getClassFullPath());
                // 写入package，import，class已经findviewbyid代码段
                writer = javaFileObject.openWriter();
                generateImport(writer, injectorInfo);
                for (VariableElement variableElement : variableElements) {
                    writeField(writer, variableElement,injectorInfo);
                }
                writerEnd(writer);
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }

    protected abstract void writerEnd(Writer writer) throws IOException;

    protected abstract void writeField(Writer writer, VariableElement variableElement, InjectorInfo injectorInfo) throws IOException;

    protected abstract void generateImport(Writer writer, InjectorInfo injectorInfo) throws IOException;


    private InjectorInfo createInjectorInfo(VariableElement variableElement) {
        TypeElement typeElement = (TypeElement) variableElement.getEnclosingElement();
        String packageName = mProcessingEnvironment.getElementUtils().getPackageOf(typeElement).getQualifiedName().toString();
        String className = typeElement.getSimpleName().toString();
        return new InjectorInfo(packageName, className);
    }

    public static class InjectorInfo {
        public String packageName;
        public String className;
        public String newClassName;

        public InjectorInfo(String packageName, String className) {
            this.packageName = packageName;
            this.className = className;
            this.newClassName = className + ViewBind.SUFFIX;
        }

        public String getClassFullPath() {
            return packageName + File.separator + newClassName;
        }
    }

}
