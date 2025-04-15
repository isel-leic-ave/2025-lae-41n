package isel.lae.li41n.mapper

import java.io.File
import java.lang.classfile.ClassBuilder
import java.lang.classfile.ClassFile
import java.lang.classfile.ClassFile.ACC_PUBLIC
import java.lang.classfile.CodeBuilder
import java.lang.constant.ClassDesc
import java.lang.constant.ConstantDescs.*
import java.lang.constant.MethodTypeDesc
import java.net.URLClassLoader
import kotlin.reflect.KClass
import kotlin.reflect.KType

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
    val className = "GeneratedMapperDyn${srcDomainClass.simpleName}To${dstDomainClass.simpleName}"
    buildMapperClassFile(className, srcDomainClass, dstDomainClass)
    return rootLoader
        .loadClass("$PACKAGE_NAME.$className")
        .kotlin
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


    val mapperCD = ClassDesc.of("${PACKAGE_NAME}.${className}")
    val bytes =
        ClassFile.of().build(mapperCD) { clb ->
            clb
                .withFlags(ACC_PUBLIC)
                .withInterfaceSymbols(ClassDesc.of(PACKAGE_NAME, "Mapper"))
                .createConstructor()
                .createMapToObject(srcRep, dstRep, mapperCD)
                .createMapToStronglyTyped(srcRep, dstRep, mapperCD)
        }
        File("${root}${packageFolder}", "${className}.class").also {
            it.parentFile.mkdirs() // Create directories if they do not exist
        }.writeBytes(bytes)
}




private fun ClassBuilder.createConstructor() : ClassBuilder {
    withMethod(INIT_NAME, MTD_void, ACC_PUBLIC) { mb ->
        mb.withCode { cob: CodeBuilder ->
            cob
                .aload(0)
                .invokespecial(CD_Object, INIT_NAME, MTD_void)
                .return_()
        }
    }
    return this
}

private fun ClassBuilder.createMapToObject(srcRep: KClass<*>, dstRep: KClass<*>, mapperCD: ClassDesc): ClassBuilder {
    val methodName = "mapTo"
    withMethod(methodName, MethodTypeDesc.of(CD_Object, listOf(CD_Object)), ACC_PUBLIC) {
            mb ->
        mb.withCode {
               it.aload(0)
                   .aload(1)
                   .checkcast(srcRep.descriptor())
                   .invokevirtual(mapperCD, methodName, MethodTypeDesc.of(dstRep.descriptor(), listOf(srcRep.descriptor())))
                   .areturn()
        }
    }
    return this
}

private fun ClassBuilder.createMapToStronglyTyped(srcRep: KClass<*>, dstRep: KClass<*>, mapperCD: ClassDesc): ClassBuilder {
    val methodName = "mapTo"
    val srcDescriptor = srcRep.descriptor()
    val dstDescriptor = dstRep.descriptor()
    withMethod(methodName, MethodTypeDesc.of(dstRep.descriptor(), listOf(srcRep.descriptor())), ACC_PUBLIC) {
            mb ->
        mb.withCode {
            it
                .aload(1)
                .invokevirtual(srcDescriptor, "getName", MethodTypeDesc.of(CD_String))
                .astore(2)
                .aload(1)
                .invokevirtual(srcDescriptor, "getSemester", MethodTypeDesc.of(CD_int))
                .istore(3)
                .aload(1)
                .invokevirtual(srcDescriptor, "getProgramme", MethodTypeDesc.of(CD_String))
                .astore(4)
                .new_(dstDescriptor)
                .dup()
                .aload(2)
                .iload(3)
                .aload(4)
                .iconst_m1()
                .invokespecial(dstDescriptor, INIT_NAME , MethodTypeDesc.of(CD_void, CD_String, CD_int, CD_String, CD_int))
                .areturn()
        }
    }
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
