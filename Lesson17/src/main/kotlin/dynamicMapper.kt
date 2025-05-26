package isel.lae.li41n.mapper

import java.io.File
import java.lang.classfile.ClassBuilder
import java.lang.classfile.ClassFile
import java.lang.classfile.ClassFile.*
import java.lang.classfile.CodeBuilder
import java.lang.constant.ClassDesc
import java.lang.constant.ConstantDescs.*
import java.lang.constant.MethodTypeDesc
import java.net.URLClassLoader
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty1
import kotlin.reflect.KType
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.javaGetter

private const val PACKAGE_NAME: String = "isel.lae.li41n.mapper"
private val packageFolder = PACKAGE_NAME.replace(".", "/")

private val root =
    SimpleMapperReflect::class.java
        .getResource("/")
        ?.toURI()
        ?.path
        ?: "${System.getProperty("user.dir")}/"

private val rootLoader = URLClassLoader(arrayOf(File(root).toURI().toURL()))

private val mappers = mutableMapOf<Pair<KClass<*>, KClass<*>>, Mapper<Any, Any>>()

fun <S : Any, D : Any> loadDynamicMapper(
    srcDomainKlass: Class<S>, dstDomainKlass: Class<D>
) = loadDynamicMapper<S, D>(srcDomainKlass.kotlin, dstDomainKlass.kotlin)

fun <S : Any, D : Any> loadDynamicMapper(
    srcDomainKlass: KClass<S>, dstDomainKlass: KClass<D>
) = mappers.getOrPut(Pair(srcDomainKlass, dstDomainKlass)) {
    buildMapperClassfile(srcDomainKlass, dstDomainKlass)
        .constructors
        .first()
        .call() as Mapper<Any, Any>
} as Mapper<S, D>

private fun <S : Any, D: Any> buildMapperClassfile(srcDomainClass: KClass<S>, dstDomainClass: KClass<D>): KClass<*> {
    val className = "MyGeneratedMapperDyn${srcDomainClass.simpleName}To${dstDomainClass.simpleName}"
    buildMapperClassFile(className, srcDomainClass, dstDomainClass)
    return rootLoader
        .loadClass("$PACKAGE_NAME.$className")
        .kotlin
}

private data class MappingDynamic(val srcRep: KClass<*>, val dstRep: KClass<*>, val mapperCD: ClassDesc) {
    val paramToProp: List<Pair<KParameter, KProperty1<Any, Any?>>>
    val srcDescriptor = srcRep.descriptor()
    val dstDescriptor = dstRep.descriptor()
    init {
        paramToProp =
            dstRep.primaryConstructor!!.parameters
                .filter { !it.isOptional }
                .map { param ->
                    val propName = param.findAnnotation<PropName>()?.n ?: param.name
                    val prop = srcRep.declaredMemberProperties.find { it.name == propName } as KProperty1<Any, Any?>
                    Pair(param, prop)
                }
    }
}
/**
 * Generates a byte array representing a dynamically created
 * class that extends RepositoryReflect, and then saves it to the
 * corresponding class file.
 */
fun <S: Any, D : Any> buildMapperClassFile(
    className: String,
    srcRep: KClass<S>,
    dstRep: KClass<D>,
) {
    val mapperCD: ClassDesc = ClassDesc.of("${PACKAGE_NAME}.${className}")
    val mappingDyn = MappingDynamic(srcRep, dstRep, mapperCD)
    val bytes =
        ClassFile.of().build(mapperCD) { clb ->
            clb
                .withFlags(ACC_PUBLIC)
                .withInterfaceSymbols(Mapper::class.descriptor())
                .createConstructor(mappingDyn)
                .createMapToObject(mappingDyn)
                .createMapToStronglyTyped(mappingDyn)
        }
        File("${root}${packageFolder}", "${className}.class").also {
            it.parentFile.mkdirs() // Create directories if they do not exist
        }.writeBytes(bytes)
}




private fun ClassBuilder.createConstructor(mappingDyn: MappingDynamic): ClassBuilder {
    val complexParams = mappingDyn.paramToProp.filter { (param, prop) -> param.type.isComplex() }
    complexParams.forEach() { (param, prop) ->

        withField(param.MapperName(), Mapper::class.descriptor()) {
            it.withFlags(ACC_PRIVATE or ACC_FINAL)
        }
    }

    withMethod(INIT_NAME, MTD_void, ACC_PUBLIC) { mb ->
        mb.withCode { cob: CodeBuilder ->
            cob
                .aload(0)
                .invokespecial(CD_Object, INIT_NAME, MTD_void)
                .setMappers(complexParams, mappingDyn)
                .return_()
        }
    }
    return this
}

private fun KParameter.MapperName(): String? =
    "${this.name}Mapper"


private fun CodeBuilder.setMappers(
    fieldNamesParamAndProps: List<Pair<KParameter, KProperty1<Any, Any?>>>,
    mappingDyn: MappingDynamic
): CodeBuilder {
    fieldNamesParamAndProps.forEach { (param, prop) ->
        aload(0)
        ldc(prop.returnType.descriptor())
        ldc(param.type.descriptor())
        invokestatic(
            ClassDesc.of("${PACKAGE_NAME}.DynamicMapperKt"),
            "loadDynamicMapper",
            MethodTypeDesc.of(Mapper::class.descriptor(), Class::class.descriptor(), Class::class.descriptor())
        )
        .putfield(mappingDyn.mapperCD, param.MapperName(), Mapper::class.descriptor())
    }

    return this
}

private fun ClassBuilder.createMapToObject(mappingDyn: MappingDynamic): ClassBuilder {
    val methodName = "mapTo"
    withMethod(methodName, MethodTypeDesc.of(CD_Object, listOf(CD_Object)), ACC_PUBLIC) {
            mb ->
        mb.withCode {
               it.aload(0)
                   .aload(1)
                   .checkcast(mappingDyn.srcRep.descriptor())
                   .invokevirtual(mappingDyn.mapperCD, methodName, MethodTypeDesc.of(mappingDyn.dstRep.descriptor(), listOf(mappingDyn.srcRep.descriptor())))
                   .areturn()
        }
    }
    return this
}


private fun ClassBuilder.createMapToStronglyTyped(mappingDyn: MappingDynamic): ClassBuilder {
    val methodName = "mapTo"
    withMethod(methodName, MethodTypeDesc.of(mappingDyn.dstRep.descriptor(), listOf(mappingDyn.srcRep.descriptor())), ACC_PUBLIC) {
        mb -> mb.withCode {

            it
            .new_(mappingDyn.dstDescriptor)
            .dup()
            .loadPropValues(mappingDyn)
            .invokespecial(
                mappingDyn.dstDescriptor,
                INIT_NAME ,
                MethodTypeDesc.of(
                    CD_void,
                    mappingDyn.paramToProp.map { it.first.type.descriptor() }.toList())
            )
            .areturn()
        }
    }
    return this
}

private fun CodeBuilder.loadPropValues(mappingDyn: MappingDynamic): CodeBuilder {
    mappingDyn.paramToProp.forEach {
        if(it.second.returnType.isComplex()) {
            loadComplexValue(it, mappingDyn);
        } else {
            loadPropValue(it.second, mappingDyn)
        }
    }
    return this
}

private fun CodeBuilder.loadPropValue(prop: KProperty1<Any, Any?>, mappingDyn: MappingDynamic) {
    aload(1)
    invokevirtual(
        mappingDyn.srcDescriptor,
        "${prop.javaGetter?.name}",
        MethodTypeDesc.of(prop.returnType.descriptor())
    )
}

private fun CodeBuilder.loadComplexValue(paramAndProp: Pair<KParameter, KProperty1<Any, Any?>>, mappingDyn: MappingDynamic): CodeBuilder {
    aload(0)
   .getfield(mappingDyn.mapperCD, paramAndProp.first.MapperName(), Mapper::class.descriptor())

//    ldc(paramAndProp.second.returnType.descriptor())
//    ldc(paramAndProp.first.type.descriptor())
//    invokestatic(
//        ClassDesc.of("${PACKAGE_NAME}.DynamicMapperKt"),
//        "loadDynamicMapper",
//        MethodTypeDesc.of(Mapper::class.descriptor(), Class::class.descriptor(), Class::class.descriptor())
//    )
    loadPropValue(paramAndProp.second, mappingDyn)
    invokeinterface(
        Mapper::class.descriptor(),
        "mapTo",
        MethodTypeDesc.of(CD_Object, CD_Object)
    )
    checkcast(paramAndProp.first.type.descriptor())

    return this
}
/**
 * Returns a ClassDesc of the type descriptor of the given KClass.
 */
fun KClass<*>.descriptor(): ClassDesc =
    if (this.java.isPrimitive) {
        when (this) {
            Char::class -> CD_char
            Short::class -> CD_short
            Int::class -> CD_int
            Long::class -> CD_long
            Float::class -> CD_float
            Double::class -> CD_double
            Boolean::class -> CD_boolean
            else -> {
                throw IllegalStateException("No primitive type for ${this.qualifiedName}!")
            }
        }
    } else {
        ClassDesc.of(this.java.name)
    }

/**
 * Returns a ClassDesc of the type descriptor of the given KType.
 */
fun KType.descriptor(): ClassDesc {
    val klass = this.classifier as KClass<*>
    return klass.descriptor()
}

fun KType.isComplex(): Boolean {
    val klass = this.classifier as KClass<*>
    return !(klass.java.isPrimitive || klass.java.isEnum || klass.java == String::class.java)
}


