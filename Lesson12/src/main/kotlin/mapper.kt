package isel.lae.li41n.mapper

import kotlin.reflect.*
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.primaryConstructor


@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class PropName(val n: String)

var typeNamePropMap: MutableMap<Pair<KClass<*>, KClass<*>>, Pair<KFunction<Any>, Map<KParameter, (Any) -> Any?>>> = mutableMapOf()

fun mapTo(src: Any, dstRep: KClass<*>): Any {
    val srcRep: KClass<*> = src::class

    val srcDestMapping: Pair<KFunction<Any>, Map<KParameter, (Any) -> Any?>> =
        typeNamePropMap.getOrElse(Pair(srcRep, dstRep))
        {
        println("### Obtaining map for $srcRep, $dstRep ")
        val primaryConstructor: KFunction<Any> = dstRep.primaryConstructor!!


        val mapKParamProp: Map<KParameter, (Any) -> Any?> =
            primaryConstructor.parameters
                .filter { !it.isOptional }
                .associateWith { param ->
                    val propName = param.findAnnotation<PropName>()?.n ?: param.name
                    val prop: KProperty1<Any, Any?> = srcRep.declaredMemberProperties.find { it.name == propName } as KProperty1<Any, Any?>
                    getParamValueGetter(prop, param)
                }
        val pair = Pair(primaryConstructor, mapKParamProp)
        typeNamePropMap[Pair(srcRep, dstRep)] = pair
        pair
    }

    val (primaryConstructor, mapKParamProp) = srcDestMapping

    val arguments: Map<KParameter, Any?> = mapKParamProp.mapValues { (param, propGetter) ->
        propGetter(src)
        //getParamValue(src, prop, param)
    }
    return primaryConstructor.callBy(arguments)
}

val KInt = Int::class
val KString = String::class

inline fun getParamValueGetter(prop: KProperty1<Any, Any?>,param: KParameter) : (Any) -> Any? {
    val valueGetter: (Any) -> Any? = { src ->  prop.call(src)}

    return when (param.type.classifier) {
    //return when (prop.returnType.classifier) {
        KInt, KString -> valueGetter
        else -> { src ->
            var value = valueGetter(src)
            if (value != null)
                mapTo(value, param.type.classifier as KClass<*>)
            else
                null
        }

    }
//    return when (prop.returnType.classifier) {
//        Int::class -> value
//        String::class -> value
//        else -> if (value != null) mapTo(value, param.type.classifier as KClass<*>) else null
//    }
}
