package isel.lae.li41n.mapper

import kotlin.reflect.*
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.primaryConstructor


@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class PropName(val n: String)

data class MapperTypes(val srcRep: KClass<*>, val dstRep: KClass<*>)
data class MapperReflection(val constructor: KFunction<Any>, val paramToProp: Map<KParameter, (Any)-> Any?>)





open class SimpleMapperReflect(val srcRep: KClass<*>, val dstRep: KClass<*>) : Mapper<Any, Any> {
    companion object {
        private val mappers: MutableMap<MapperTypes, SimpleMapperReflect> = mutableMapOf()
        fun getMapperFor(src: KClass<*>, dst: KClass<*>): SimpleMapperReflect {
            val mt = MapperTypes(src, dst)
            return mappers.getOrPut(mt) {
                SimpleMapperReflect(src, dst)
            }
        }
    }
    val mapperReflection = MapperReflection(
        dstRep.primaryConstructor!!,
        dstRep.primaryConstructor!!.parameters
            .filter { !it.isOptional }
            .associateWith { param ->
                val propName = param.findAnnotation<PropName>()?.n ?: param.name
                val prop: KProperty1<Any, Any?> = srcRep.declaredMemberProperties.find { it.name == propName } as KProperty1<Any, Any?>
                getParamValueGetter(prop, param)
            }
    )



    override fun mapTo(src: Any): Any {
        val srcRep: KClass<*> = src::class

        val arguments: Map<KParameter, Any?> = mapperReflection.paramToProp.mapValues { (param, valueGetter) ->
            valueGetter(src)
        }
        return mapperReflection.constructor.callBy(arguments)
    }

    inline fun getParamValueGetter(prop: KProperty1<Any, Any?>, param: KParameter): (Any) -> Any? {
        val propValueGetter: (Any) -> Any? = { src ->  prop.call(src) }
        return when (param.type.classifier) {
        //return when (prop.returnType.classifier) {
            Int::class -> propValueGetter
            String::class -> propValueGetter

            else -> {
                { src  ->
                    val value = propValueGetter(src)
                    if(value != null) {
                        val mapper: SimpleMapperReflect = getMapperFor(value::class, param.type.classifier as KClass<*>)
                        mapper.mapTo(value)
                    } else
                        null
                }

            }

        }
    }


}

interface Mapper<S, D> {
    fun mapTo(src: S): D;
}

