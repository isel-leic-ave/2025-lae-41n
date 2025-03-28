package lesson03

import java.util.Enumeration
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.KTypeProjection
import kotlin.reflect.full.createType
import kotlin.reflect.typeOf

class Stack<T> //where T : Comparable<T>, T: Enumeration<T>
{
    val values = emptyList<Any>().toMutableList()
    fun push(obj: T) {
        values.add(obj as Any)
    }

    fun pop(): T? {
        return values.removeLast() as T
    }
}

//class GenericClass<A, B>
//
//fun inspectGenericType(type: KType) {
//    println("Type: ${type}")
//    val arguments = type.arguments
//    if (arguments.isNotEmpty()) {
//        println("Type arguments:")
//        arguments.forEach { arg ->
//            println(" - ${arg.type}")
//        }
//    } else {
//        println("No type arguments.")
//    }
//}


fun main() {
//    val stackInt = StackInt()
//    stackInt.push(1);
//    //stackInt.push("SLB");
//    val i: Int? = stackInt.pop()


//    stackInt.push(2)
//    val pop: Int? = stackInt.pop()


    val stackInt = Stack<Int>()
    val stackInt1 = Stack<Int>()

    stackInt.push(2)
//    val a = "abc" as Any
//    val i = a as Int
//    stackInt.push(i)
    val i1 = stackInt.pop()

    //val pop: Int? = stackInt.pop()
    val stackString = Stack<String>()




    println("String type parameters: ${String::class.typeParameters}")

    val stackGenericType = Stack::class
//
//
println("Stack type parameters: ${Stack::class.typeParameters}")
//
val kClassStackInt: KClass<out Stack<Int>> = Stack<Int>()::class
println("Stack<Int> type parameters: ${kClassStackInt.typeParameters}")
val kTypeStackInt: KType = typeOf<Stack<Int>>()
println(kTypeStackInt.arguments)

val kTypeStackString: KType = typeOf<Stack<String>>()
println(kTypeStackString.arguments)



//    //println("Stack<Int> type parameters: ${kClassStackInt}")
//
//
//
//    // Create an instance of a specific type
//    val instanceType = //typeOf<List<String>>();
//        GenericClass::class.createType(
//            listOf(
//                KTypeProjection.invariant(String::class.createType()),
//                KTypeProjection.invariant(Int::class.createType())
//            )
//        )
//    inspectGenericType(instanceType)
}
//
//
//fun inspectGenericType(type: KType) {
//    println("Type: ${type}")
//    val arguments = type.arguments
//    if (arguments.isNotEmpty()) {
//        println("Type arguments:")
//        arguments.forEach { arg ->
//            println(" - ${arg.type}")
//        }
//    } else {
//        println("No type arguments.")
//    }
//}
//
//fun main() {
//    // Example of a generic class
//    class GenericClass<A, B>
//
//    // Create an instance of a specific type
//    val instanceType = GenericClass::class.createType(listOf(String::class.createType(), Int::class.createType()))
//    inspectGenericType(instanceType)
//}
