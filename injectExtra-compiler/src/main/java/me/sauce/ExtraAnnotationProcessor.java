package me.sauce;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * Created by sauce on 2017/3/8.
 * Version 1.0.0
 */
public class ExtraAnnotationProcessor {

    public TypeElement mClassElement;
    public List<BindExtraField> mFields;
    public Elements mElementUtils;
    private static final ClassName UI_THREAD =
            ClassName.get("android.support.annotation", "UiThread");

    public ExtraAnnotationProcessor(TypeElement classElement, Elements mElementUtils) {
        this.mClassElement = classElement;
        this.mFields = new ArrayList<>();
        this.mElementUtils = mElementUtils;

    }

    public void addField(BindExtraField field) {
        mFields.add(field);
    }

    public JavaFile generateFinder() {

        MethodSpec.Builder injectMethodBuilder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(UI_THREAD)
                .addParameter(TypeName.get(mClassElement.asType()), "target");
        for (BindExtraField field : mFields) {
            // find views
            injectMethodBuilder.addStatement("target.$N = ($T)(target.getIntent().getExtras().get($S))", field.getFieldName(),
                    ClassName.get(field.getFieldType()), field.getKey());
        }


        // generate whole class
        TypeSpec finderClass = TypeSpec.classBuilder(mClassElement.getSimpleName() + "_ExtraBinding")
                .addModifiers(Modifier.PUBLIC)
                .addMethod(injectMethodBuilder.build())
                .build();

        String packageName = mElementUtils.getPackageOf(mClassElement).getQualifiedName().toString();

        return JavaFile.builder(packageName, finderClass).build();
    }
}
