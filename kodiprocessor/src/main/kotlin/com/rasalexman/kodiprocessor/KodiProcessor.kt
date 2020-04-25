package com.rasalexman.kodiprocessor

import com.google.auto.service.AutoService
import com.rasalexman.kodi.core.IKodiModule
import com.rasalexman.kodi.core.KodiModule
import com.rasalexman.kodiannotation.KodiSingle
import com.squareup.kotlinpoet.*
import java.io.File
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic
import kotlin.reflect.KClass

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor::class)
class SingleAnnotationProcessor : AbstractProcessor() {

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(KodiSingle::class.java.name)
    }

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latest()

    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]
        roundEnv.getElementsAnnotatedWith(KodiSingle::class.java)
                .forEach {
                    if (it.kind != ElementKind.CLASS) {
                        processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "Only classes can be annotated")
                        return true
                    }
                    processAnnotation(it)
                }
        return false
    }

    private fun processAnnotation(element: Element) {
        val className = element.simpleName.toString()
        //val inheritance: KClass<*>? = singleAnnotation?.bindingTo
        //println("-----> inheritance = $inheritance")
        val annotation = element.getAnnotation(KodiSingle::class.java) as KodiSingle
        println("-----> annotation = $annotation")
        //println("-----> bindingTo = ${annotation.bindingTo}")
        val pack = processingEnv.elementUtils.getPackageOf(element).toString()

        val fileName = "${className}Module"
        val fileBuilder= FileSpec.builder(pack, fileName)
                .addImport("com.rasalexman.kodi.core", "bind")
                .addImport("com.rasalexman.kodi.core", "with")
                .addImport("com.rasalexman.kodi.core", "single")
                .addImport("com.rasalexman.kodi.core", "instance")
                .addImport("com.rasalexman.kodi.core", "kodiModule")
        val classBuilder = TypeSpec.classBuilder(fileName)

        var initializer = "kodiModule { bind<$className>() with single { $className("

        var classProperty = ""
        var propCount = 0
        for (enclosed in element.enclosedElements) {
            if (enclosed.kind == ElementKind.FIELD) {
                val property = enclosed.simpleName.toString()
                classProperty += (if(propCount > 0) "," else "") + "$property = instance()"
                propCount++
            }
        }
        initializer += "$classProperty) }"
        initializer += "}"

        val moduleProperty = PropertySpec.builder("${className.toLowerCase()}Module", IKodiModule::class)
                .initializer(initializer).build()
        val companion = TypeSpec.companionObjectBuilder().addProperty(moduleProperty).build()

        val file = fileBuilder.addType(classBuilder.addType(companion).build()).build()
        val kaptKotlinGeneratedDir = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]
        val createdFile = File(kaptKotlinGeneratedDir)
        file.writeTo(createdFile)
    }

}
