package me.sauce;

import com.google.auto.service.AutoService;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
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
        annotationsTypes.add(APIService.class.getCanonicalName());
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
    }

    private Map<String, ExtraAnnotationProcessor> mExtraAnnotatedClassMap = new HashMap<>();
    private Map<String, ApiServiceAnnotatedProcessor> mApiAnnotatedClassMap = new HashMap<>();

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        mExtraAnnotatedClassMap.clear();

        try {
            processBindExtra(roundEnv);
            processApiService(roundEnv);
        } catch (IllegalArgumentException e) {
            return true; // stop process
        }
        for (ApiServiceAnnotatedProcessor annotatedClass : mApiAnnotatedClassMap.values()) {
            try {
                annotatedClass.generateFinder().writeTo(mFiler);
            } catch (IOException e) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Unable to write binding for type %s: %s" + e.getMessage());
            }
        }
        for (ExtraAnnotationProcessor annotatedClass : mExtraAnnotatedClassMap.values()) {
            try {
                annotatedClass.generateFinder().writeTo(mFiler);
            } catch (IOException e) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Unable to write binding for type %s: %s" + e.getMessage());
            }
        }
        return true;
    }

    private void processApiService(RoundEnvironment roundEnv)  throws IllegalArgumentException{
        for (Element element : roundEnv.getElementsAnnotatedWith(APIService.class)) {
            ApiServiceAnnotatedProcessor annotatedClass = getApiAnnotatedClass(element);
            ApiServiceField field = new ApiServiceField(element);
            annotatedClass.addField(field);
        }
    }

    private void processBindExtra(RoundEnvironment roundEnv) throws IllegalArgumentException {
        for (Element element : roundEnv.getElementsAnnotatedWith(InjectExtra.class)) {
            ExtraAnnotationProcessor annotatedClass = getExtraAnnotatedClass(element);
            BindExtraField field = new BindExtraField(element);
            annotatedClass.addField(field);
        }
    }

    private ExtraAnnotationProcessor getExtraAnnotatedClass(Element element) {
        TypeElement classElement = (TypeElement) element.getEnclosingElement();
        String fullClassName = classElement.getQualifiedName().toString();
        ExtraAnnotationProcessor annotatedClass;
        if (mExtraAnnotatedClassMap.containsKey(fullClassName)) {
            annotatedClass = mExtraAnnotatedClassMap.get(fullClassName);
        } else {
            annotatedClass = new ExtraAnnotationProcessor(classElement, mElementUtils);
            mExtraAnnotatedClassMap.put(fullClassName, annotatedClass);
        }
        return annotatedClass;
    }
    private ApiServiceAnnotatedProcessor getApiAnnotatedClass(Element element) {
        TypeElement classElement = (TypeElement) element.getEnclosingElement();
        String fullClassName = classElement.getQualifiedName().toString();
        ApiServiceAnnotatedProcessor annotatedClass;
        if (mApiAnnotatedClassMap.containsKey(fullClassName)) {
            annotatedClass = mApiAnnotatedClassMap.get(fullClassName);
        } else {
            annotatedClass = new ApiServiceAnnotatedProcessor(classElement, mElementUtils);
            mApiAnnotatedClassMap.put(fullClassName, annotatedClass);
        }
        return annotatedClass;
    }
}
