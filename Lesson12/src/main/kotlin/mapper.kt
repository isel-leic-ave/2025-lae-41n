package isel.lae.li41n.mapper

import kotlin.reflect.*
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.primaryConstructor


@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class PropName(val n: String)

var typeNamePropMap: MutableMap<KClass<*>, Map<String, KProperty<*>>> = emptyMap<KClass<*>, Map<String, KProperty<*>>>().toMutableMap()

fun mapTo(src: Any, dstRep: KClass<*>): Any? {
    val primaryConstructor: KFunction<Any>? = dstRep.primaryConstructor
    val srcClass: KClass<*> = src::class
    val srcMemberProperties: Collection<KProperty1<*, *>> = srcClass.declaredMemberProperties
    val srcPropMap: Map<String, KProperty<*>> = typeNamePropMap.getOrElse(srcClass,
        { srcMemberProperties.associateBy { it.name }.also { typeNamePropMap[srcClass] = it } })


    return primaryConstructor?.callBy(primaryConstructor?.parameters?.filter { !it.isOptional }?.associateWith {
        param ->
            val propName = param.findAnnotation<PropName>()?.n ?: param.name
            getParamValue(src, srcPropMap[propName], param)
    }!!)
}

fun getParamValue(src: Any, prop: KProperty<*>?, param: KParameter) : Any? {
    val propKClass: KClass<*>? = prop?.returnType?.classifier as? KClass<*>
    var value: Any? = prop?.call(src)

    return when (propKClass) {
        Int::class -> value
        String::class -> value
        else -> if (value != null) mapTo(value, param.type?.classifier as KClass<*>) else value
    }
}
