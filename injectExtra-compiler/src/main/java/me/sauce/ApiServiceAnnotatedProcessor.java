package me.sauce;


import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

/**
 * Created by sauce on 2017/5/15.
 * Version 1.0.0
 */
public class ApiServiceAnnotatedProcessor {
    public TypeElement mClassElement;
    public List<ApiServiceField> mFields = new ArrayList<>();
    public Elements mElementUtils;

    public ApiServiceAnnotatedProcessor(TypeElement classElement, Elements mElementUtils) {
        this.mClassElement = classElement;
        this.mElementUtils = mElementUtils;
    }


    public void addField(ApiServiceField field) {
        mFields.add(field);
    }

    public JavaFile generateFinder() {
        String packageName = mElementUtils.getPackageOf(mClassElement).getQualifiedName().toString();
        // generate whole class
        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(mClassElement.getSimpleName() + "_DataResponse")
                .addModifiers(Modifier.PUBLIC);

        for (ApiServiceField field :mFields) {
            for (Element e : field.getElements()) {
                ExecutableElement executableElement = (ExecutableElement) e;
                MethodSpec.Builder methodBuilder =
                        MethodSpec.methodBuilder(e.getSimpleName().toString())
                                .addModifiers(PUBLIC, STATIC);

                if (TypeName.get(executableElement.getReturnType()).toString().contains("DataArr")) {//返回列表数据
                    methodBuilder.returns(ClassName.get("rx", "Observable"));
                    Map<String, Object> params = new HashMap<>();
                    methodBuilder.addParameter(params.getClass(), "param");
                    ClassName apiUtil = ClassName.get("com.base.util", "ApiUtil");
                    ClassName C = ClassName.get("com", "C");
                    CodeBlock.Builder blockBuilder = CodeBlock.builder();
                    int len = executableElement.getParameters().size();
                    for (int i = 0; i < len; i++) {
                        VariableElement ep = executableElement.getParameters().get(i);
                        boolean isLast = i == len - 1;
                        String split = (isLast ? "" : ",");
                        switch (ep.getSimpleName().toString()) {
                            case "include":
                                blockBuilder.add("$L.getInclude(param)" + split, apiUtil);
                                break;
                            case "where":
                                blockBuilder.add("$L.getWhere(param)" + split, apiUtil);
                                break;
                            case "skip":
                                blockBuilder.add("$L.getSkip(param)" + split, apiUtil);
                                break;
                            case "limit":
                                blockBuilder.add("$L.PAGE_COUNT" + split, C);
                                break;
                            case "order":
                                blockBuilder.add("$L._CREATED_AT" + split, C);
                                break;
                        }
                    }
                    methodBuilder.addStatement(
                            "return $T.getInstance()" +
                                    ".service.$L($L)" +
                                    ".compose($T.io_main())"
                            , ClassName.get("com.api", "Api")
                            , e.getSimpleName().toString()
                            , blockBuilder.build().toString()
                            , ClassName.get("com.base.util.helper", "RxSchedulers"));
                    classBuilder.addMethod(methodBuilder.build());
                } else {//返回普通数据
                    methodBuilder.returns(TypeName.get(executableElement.getReturnType()));
                    String paramsString = "";
                    for (VariableElement ep : executableElement.getParameters()) {
                        methodBuilder.addParameter(TypeName.get(ep.asType()), ep.getSimpleName().toString());
                        paramsString += ep.getSimpleName().toString() + ",";
                    }
                    methodBuilder.addStatement(
                            "return $T.getInstance()" +
                                    ".service.$L($L)" +
                                    ".compose($T.io_main())"
                            , ClassName.get("com.api", "Api")
                            , e.getSimpleName().toString()
                            , paramsString.substring(0, paramsString.length() - 1)
                            , ClassName.get("com.base.util.helper", "RxSchedulers"));
                    classBuilder.addMethod(methodBuilder.build());
                }
            }
        }


        return JavaFile.builder(packageName, classBuilder.build()).build();
    }

    private boolean isSubtypeOfType(TypeMirror typeMirror, String otherType) {
        if (isTypeEqual(typeMirror, otherType)) {
            return true;
        }
        if (typeMirror.getKind() != TypeKind.DECLARED) {
            return false;
        }
        DeclaredType declaredType = (DeclaredType) typeMirror;
        List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
        if (typeArguments.size() > 0) {
            StringBuilder typeString = new StringBuilder(declaredType.asElement().toString());
            typeString.append('<');
            for (int i = 0; i < typeArguments.size(); i++) {
                if (i > 0) {
                    typeString.append(',');
                }
                typeString.append('?');
            }
            typeString.append('>');
            if (typeString.toString().equals(otherType)) {
                return true;
            }
        }
        Element element = declaredType.asElement();
        if (!(element instanceof TypeElement)) {
            return false;
        }
        TypeElement typeElement = (TypeElement) element;
        TypeMirror superType = typeElement.getSuperclass();
        if (isSubtypeOfType(superType, otherType)) {
            return true;
        }
        for (TypeMirror interfaceType : typeElement.getInterfaces()) {
            if (isSubtypeOfType(interfaceType, otherType)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isTypeEqual(TypeMirror typeMirror, String otherType) {
        return otherType.equals(typeMirror.toString());
    }

}
