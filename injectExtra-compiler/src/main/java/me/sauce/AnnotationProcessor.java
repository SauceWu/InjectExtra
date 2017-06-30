package me.sauce;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.kotlinpoet.KotlinFile;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;


@AutoService(Processor.class)//自动生成 javax.annotation.processing.IProcessor 文件
@SupportedSourceVersion(SourceVersion.RELEASE_8)//java版本支持
public class AnnotationProcessor extends AbstractProcessor {


    private Types mTypeUtils;
    private Elements mElementUtils;
    private Filer mFiler;
    private Messager mMessager;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotationsTypes = new LinkedHashSet<>();
        annotationsTypes.add(InjectExtra.class.getCanonicalName());
        return annotationsTypes;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }


    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mTypeUtils = processingEnv.getTypeUtils();
        mElementUtils = processingEnv.getElementUtils();
        mFiler = processingEnv.getFiler();
        mMessager = processingEnv.getMessager();
        mMessager.printMessage(Diagnostic.Kind.NOTE, "java");
    }

    private Map<String, ExtraAPKT> mExtraAnnotatedClassMap = new HashMap<>();

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        mExtraAnnotatedClassMap.clear();
        try {
            processBindExtra(roundEnv);
        } catch (IllegalArgumentException e) {
            return true; // stop process
        }

        for (ExtraAPKT annotatedClass : mExtraAnnotatedClassMap.values()) {
            try {
                annotatedClass.generateFinder(mFiler);
            } catch (IOException e) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Unable to write binding for type %s: %s" + e.getMessage());
            }
        }
        return true;
    }


    private void processBindExtra(RoundEnvironment roundEnv) throws IllegalArgumentException {
        for (Element element : roundEnv.getElementsAnnotatedWith(InjectExtra.class)) {
            ExtraAPKT annotatedClass = getExtraAnnotatedClass(element);
            BindExtraField field = new BindExtraField(element);
            annotatedClass.addField(field);
        }
    }

    private ExtraAPKT getExtraAnnotatedClass(Element element) {
        TypeElement classElement = (TypeElement) element.getEnclosingElement();
        String fullClassName = classElement.getQualifiedName().toString();
        mMessager.printMessage(Diagnostic.Kind.NOTE, fullClassName);
        ExtraAPKT annotatedClass;
        if (mExtraAnnotatedClassMap.containsKey(fullClassName)) {
            annotatedClass = mExtraAnnotatedClassMap.get(fullClassName);
        } else {
            annotatedClass = new ExtraAPKT(classElement, mElementUtils);
            mExtraAnnotatedClassMap.put(fullClassName, annotatedClass);
        }
        return annotatedClass;
    }


}
