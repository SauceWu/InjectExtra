package me.sauce.injectextra_kotlin_compiler

import com.google.auto.service.AutoService
import me.sauce.InjectExtra
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Types
import javax.tools.Diagnostic
import javax.tools.Diagnostic.Kind.NOTE

@AutoService(Processor::class)
class AnnotationProcessor : AbstractProcessor() {
    private lateinit var types: Types
    private lateinit var messager: Messager
    private lateinit var filer: Filer

    @Synchronized override fun init(env: ProcessingEnvironment?) {
        super.init(env)
        this.types = env!!.typeUtils
        this.messager = env.messager
        this.filer = env.filer
        messager.printMessage(NOTE, "kotlin")
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latestSupported()
    }

    override fun getSupportedAnnotationTypes(): Set<String> {
        return setOf(InjectExtra::class.java.canonicalName)
    }

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment?): Boolean {
        val factoryTypes = roundEnv!!.getElementsAnnotatedWith(InjectExtra::class.java)

        factoryTypes.toSet()
                .map { types.asElement(it.asType()) as TypeElement }
                .forEach { write(it) }
        return true
    }

    fun write(element: TypeElement) {
        val name = element.qualifiedName.toString()
        messager.printMessage(Diagnostic.Kind.NOTE, name)

    }
}
