package me.sauce.rxbusC;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

/**
 * Created by sauce on 2017/3/8.
 * Version 1.0.0
 */
public class BusAnnotationProcessor {

    public TypeElement mClassElement;
    public List<BindBusField> mFields;
    public Elements mElementUtils;
    public static final String RXBUS_TYPE = "me.sauce.rxBus.RxBus";

    public BusAnnotationProcessor(TypeElement classElement, Elements mElementUtils) {
        this.mClassElement = classElement;
        this.mFields = new ArrayList<>();
        this.mElementUtils = mElementUtils;

    }

    public void addField(BindBusField field) {
        mFields.add(field);
    }

    public JavaFile generateFinder() {
        TypeMirror typeMirror = mClassElement.asType();

        MethodSpec.Builder injectMethodBuilder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(TypeName.get(typeMirror), "target");

        injectMethodBuilder.addStatement("$N rxBus =$N.getInstance();", RXBUS_TYPE, RXBUS_TYPE);

        for (BindBusField field : mFields) {
            // find views
            injectMethodBuilder.addCode("rxBus.toObservable($L)", field.getTag());
            if (field.getParameters().size() == 0)
                injectMethodBuilder.addCode(".subscribe(o ->target.$N());", field.getFieldName());
            else
                injectMethodBuilder.addCode(".subscribe(o ->target.$N($L));", field.getFieldName(), "o");


        }


        TypeSpec finderClass = TypeSpec.classBuilder(mClassElement.getSimpleName() + "_BusManager")
                .addModifiers(Modifier.PUBLIC)
                .addMethod(injectMethodBuilder.build())
                .build();

        String packageName = mElementUtils.getPackageOf(mClassElement).getQualifiedName().toString();

        return JavaFile.builder(packageName, finderClass).build();
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
