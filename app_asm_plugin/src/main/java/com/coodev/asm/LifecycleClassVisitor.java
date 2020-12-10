package com.coodev.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.Locale;

public class LifecycleClassVisitor extends ClassVisitor {
    private String className;
    private String superClassName;

    public LifecycleClassVisitor(ClassVisitor classVisitor) {
        super(Opcodes.ASM7, classVisitor);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        className = name;
        superClassName = superName;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        final MethodVisitor methodVisitor = cv.visitMethod(access, name, descriptor, signature, exceptions);
        String targetDescriptor = "(Landroid/os/Bundle;)V";
        String targetName = "onCreate";
        if (Opcodes.ACC_PROTECTED == access && targetName.equals(name) && targetDescriptor.equals(descriptor)) {
            System.out.println(String.format(Locale.getDefault(), "[access = %d ,name = %s,descriptor = %s,signature = %s],", access, name, descriptor, signature));
            return new LifecycleMethodVisitor(methodVisitor, className, name);
        }
        return methodVisitor;
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
    }
}
