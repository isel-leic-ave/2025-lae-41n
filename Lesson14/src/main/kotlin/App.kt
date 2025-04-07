import java.io.File
import java.io.FileOutputStream
import java.lang.classfile.ClassFile
import java.lang.classfile.ClassFile.ACC_PUBLIC
import java.lang.classfile.ClassFile.ACC_STATIC
import java.lang.classfile.CodeBuilder
import java.lang.constant.ClassDesc
import java.lang.constant.ConstantDescs.*
import java.lang.constant.MethodTypeDesc
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.functions

class App {}
private val resourcePath =
    App::class.java.getResource("/")?.path




fun main() {
    genereteClassFileMyApp()
}

fun genereteClassFileMyApp() {
    var bytes = ClassFile.of().build(
        ClassDesc.of("MyClassFileApp"))
        {
            classBuilder ->
                classBuilder.withFlags(ACC_PUBLIC)
                .withMethod("add", MethodTypeDesc.of(CD_int, listOf(CD_int, CD_int)),ACC_PUBLIC or ACC_STATIC) {
                    mb ->
                        mb.withCode {
                            cb -> cb
                                    .iload(0)
                                    .iload(1)
                                    .iadd()
                                    .ireturn()
                        }

                }
        }
    val file = File(resourcePath, "MyClassFileApp.class")
    //file.parentFile.mkdirs() // Create directories if they do not exist

    FileOutputStream(file).write(bytes)

    App::class.java.classLoader
        .loadClass("MyClassFileApp")
        .kotlin
        .also { klass ->
            val add = klass.functions.first { it.name == "add" }
            println(add.call(2, 5)) // prints 67895
        }
}

fun generateBarClass() {
    val bytes =
        ClassFile.of().build(ClassDesc.of("pt.isel.Bar")) { clb ->
            clb
                .withFlags(ACC_PUBLIC)
                .withMethod(INIT_NAME, MTD_void, ACC_PUBLIC) { mb ->
                    mb.withCode { cob: CodeBuilder ->
                        cob
                            .aload(0)
                            .invokespecial(CD_Object, INIT_NAME, MTD_void)
                            .return_()
                    }
                }.withMethod("foo", MethodTypeDesc.of(CD_int), ACC_PUBLIC) { mb ->
                    mb.withCode { cob: CodeBuilder ->
                        cob
                            .ldc(clb.constantPool().intEntry(67895))
                            .ireturn()
                    }
                }
        }
    val file = File(resourcePath, "pt/isel/Bar.class")
    file.parentFile.mkdirs() // Create directories if they do not exist

    FileOutputStream(file).write(bytes)

    App::class.java.classLoader
        .loadClass("pt.isel.Bar")
        .kotlin
        .also { klass ->
            val obj = klass.createInstance()
            val foo = klass.functions.first { it.name == "foo" }
            println(foo.call(obj)) // prints 67895
        }
}