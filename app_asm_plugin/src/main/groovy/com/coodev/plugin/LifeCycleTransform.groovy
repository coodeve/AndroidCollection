package com.coodev.plugin

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import com.coodev.asm.LifecycleClassVisitor
import groovy.io.FileType
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes

/**
 * 辅助类:{@link TransformManager}
 */
public class LifeCycleTransform extends Transform {
    def NAME = "transformClassesWithLifeCycleForBigData"

    @Override
    String getName() {
        return NAME
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.PROJECT_ONLY
    }

    /**
     * 是否需要增量编译
     * @return
     */
    @Override
    boolean isIncremental() {
        return false
    }

    /**
     * inputs :中是传过来的输入流，其中有两种格式，一种是 jar 包格式，一种是 directory（目录格式）
     * outputProvider: 获取到输出目录，最后将修改的文件复制到输出目录，这一步必须做，否则编译会报错
     * @param transformInvocation
     * @throws TransformException* @throws InterruptedException* @throws IOException
     */
    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation)
        // 获取到的所有的class文件
        def inputs = transformInvocation.inputs
        def outputProvider = transformInvocation.outputProvider
        inputs.each { input ->
            // 处理jar,所依赖的项目
            handleJar(input.jarInputs);
            // 处理源码(以源码方式参与编译的所有目录结构及目录下的所有文件,比如自己写的MainActivity.class,
            // 以及一些自动生成的类,比如R.class.BuildConfig.class)
            handleSource(outputProvider, input.directoryInputs);
        }
    }


    def handleJar(Collection<JarInput> inputJars) {

    }

    def handleSource(TransformOutputProvider outputProvider, Collection<DirectoryInput> directoryInputs) {
        directoryInputs.each { directoryInput ->
            def targetFile = directoryInput.file
            if (targetFile) {
                targetFile.traverse(type: FileType.FILES, nameFilter: ~/.*\.class/) { file ->
                    println "find class : $file.name"
                    // 以下使用ASM处理字节码
                    // 对字节码进行读取和解析
                    def classReader = new ClassReader(file.bytes)
                    def classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
                    def lifecycleClassVisitor = new LifecycleClassVisitor(classWriter)
                    classReader.accept(lifecycleClassVisitor, ClassReader.EXPAND_FRAMES)
                    def classBytes = classWriter.toByteArray()
                    def fileOutputStream = new FileOutputStream(file.getPath())
                    fileOutputStream.write(classBytes)
                    fileOutputStream.close();
                }

                def dest = outputProvider.getContentLocation(
                        directoryInput.name,
                        directoryInput.contentTypes,
                        directoryInput.scopes,
                        Format.DIRECTORY);
                FileUtils.copyDirectory(directoryInput.file,dest)
            }

        }
    }
}
