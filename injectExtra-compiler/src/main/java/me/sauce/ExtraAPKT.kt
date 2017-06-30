package me.sauce


import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.TypeName.Companion.asTypeName
import java.util.ArrayList
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeKind
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.Elements

/**
 * Created by sauce on 2017/6/30.
 */
class ExtraAPKT(classElement: TypeElement, var mElementUtils: Elements) {

    var mClassElement: TypeElement = classElement
    var mFields: MutableList<BindExtraField> = ArrayList()
    val UI_THREAD = ClassName("android.support.annotation", "UiThread")
    val INTENT_TYPE = ClassName("android.content", "Intent")
    val BUNDLE_TYPE = ClassName("android.os", "Bundle")


    fun addField(field: BindExtraField) {
        mFields.add(field)
    }

    fun generateFinder(mFiler: Filer) {
        val typeMirror = mClassElement.asType()

        val injectMethodBuilder = FunSpec.constructorBuilder()
                .addModifiers(KModifier.PUBLIC)
                .addParameter("target", typeMirror.asTypeName(), KModifier.VARARG)
                .addAnnotation(UI_THREAD)

        if (isSubtypeOfType(typeMirror, "android.app.Activity")) {
            injectMethodBuilder.addStatement("%T intent = target.getIntent()", INTENT_TYPE)
            injectMethodBuilder.addStatement("if(intent ==null) return")
            injectMethodBuilder.addStatement("%T bundle = intent.getExtras()", BUNDLE_TYPE)


        } else
            injectMethodBuilder.addStatement("%T bundle = target.getArguments()", BUNDLE_TYPE)
        injectMethodBuilder.addStatement("if(bundle ==null) return")

        for (field in mFields) {
            if (field.fieldName.toString() == "intent") {
                injectMethodBuilder.addStatement("target.%N =intent", field.fieldName)

            } else {

                injectMethodBuilder.addStatement("Object %N = bundle.get(%S)", field.fieldName, field.key)

                injectMethodBuilder.addStatement("if(%N!=null)\ntarget.%N = (%T)%N", field.fieldName, field.fieldName, field.fieldType, field.fieldName)
            }
        }


        // generate whole class
        val finderClass = TypeSpec.classBuilder(mClassElement.simpleName.toString() + "_ExtraBinding")
                .addModifiers(KModifier.PUBLIC)
                .addFun(injectMethodBuilder.build())
                .addSuperinterface(ClassName("java.io", "Serializable"))
                .build()


        val packageName = mElementUtils.getPackageOf(mClassElement).qualifiedName.toString()

        val kotlin = KotlinFile.builder(packageName, mClassElement.simpleName.toString() + "_ExtraBinding")
                .addType(finderClass)
                .build()
        kotlin.writeTo(mFiler.createSourceFile(packageName+ mClassElement.simpleName.toString() + "_ExtraBinding",finderClass.funSpecs).openWriter())
    }


    private fun isSubtypeOfType(typeMirror: TypeMirror, otherType: String): Boolean {
        if (isTypeEqual(typeMirror, otherType)) {
            return true
        }
        if (typeMirror.kind != TypeKind.DECLARED) {
            return false
        }
        val declaredType = typeMirror as DeclaredType
        val typeArguments = declaredType.typeArguments
        if (typeArguments.size > 0) {
            val typeString = StringBuilder(declaredType.asElement().toString())
            typeString.append('<')
            for (i in typeArguments.indices) {
                if (i > 0) {
                    typeString.append(',')
                }
                typeString.append('?')
            }
            typeString.append('>')
            if (typeString.toString() == otherType) {
                return true
            }
        }
        val element = declaredType.asElement() as? TypeElement ?: return false
        val typeElement = element
        val superType = typeElement.superclass
        if (isSubtypeOfType(superType, otherType)) {
            return true
        }
        for (interfaceType in typeElement.interfaces) {
            if (isSubtypeOfType(interfaceType, otherType)) {
                return true
            }
        }
        return false
    }

    private fun isTypeEqual(typeMirror: TypeMirror, otherType: String): Boolean {
        return otherType == typeMirror.toString()
    }

}

