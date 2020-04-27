package com.example.annotationlib.write;

import com.example.annotationlib.annontation.ViewInjector;

import java.io.IOException;
import java.io.Writer;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.VariableElement;

public class DefaultWriter extends AbsWriter {

    public DefaultWriter(ProcessingEnvironment processingEnvironment) {
        super(processingEnvironment);
    }

    @Override
    protected void writerEnd(Writer writer) throws IOException {
        writer.write(" }");
        writer.write("\n");
        writer.write("}");
    }

    @Override
    protected void writeField(Writer writer, VariableElement variableElement, InjectorInfo injectorInfo) throws IOException {
        ViewInjector annotation = variableElement.getAnnotation(ViewInjector.class);
        String simpleName = variableElement.getSimpleName().toString();
        writer.write("  target." + simpleName + " = ViewFinder.findViewById(target," + annotation.value() + ")");
        writer.write("\n");
    }

    @Override
    protected void generateImport(Writer writer, InjectorInfo injectorInfo) throws IOException {
        writer.write("package " + injectorInfo.packageName + " ;");
        writer.write("\n\n");
        writer.write("import com.example.annotationlib.adapter.InjectAdapter ;");
        writer.write("\n");
        writer.write("import com.example.annotationlib.adapter.utils.ViewFinder ;");
        writer.write("\n");
        writer.write("public class " + injectorInfo.newClassName + " implements InjectAdapter<" + injectorInfo.className + ">{");
        writer.write("\n");
        writer.write("public void inject(" + injectorInfo.className + " target){");
        writer.write("\n");
    }
}
