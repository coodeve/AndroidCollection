package com.coodev.asm;


import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class LifecycleMethodVisitor extends MethodVisitor {
    private String className;
    private String methodName;

    public LifecycleMethodVisitor(MethodVisitor methodVisitor, String className, String name) {
        super(Opcodes.ASM7, methodVisitor);
        this.className = className;
        this.methodName = name;
    }

    @Override
    public void visitCode() {
        super.visitCode();
        System.out.println("LifecycleMethodVisitor visitCode");
        mv.visitLdcInsn("TAG");
        mv.visitLdcInsn(className + "--->" + methodName);
        mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                "andorid/util/Log",
                "i",
                "(Ljava/lang/String;Ljava/lang/String;)I",
                false);
        mv.visitInsn(Opcodes.POP);

    }
}
