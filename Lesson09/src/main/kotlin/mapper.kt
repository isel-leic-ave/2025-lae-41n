import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.primaryConstructor


var typeNamePropMap: MutableMap<KClass<*>, Map<String, KProperty<*>>> = emptyMap<KClass<*>, Map<String, KProperty<*>>>().toMutableMap()

fun mapper(src: Any, dstRep: KClass<*>): Any? {
    val primaryConstructor = dstRep.primaryConstructor
    val srcClass = src::class
    val srcMemberProperties = srcClass.declaredMemberProperties
    val srcPropMap = typeNamePropMap.getOrElse(srcClass,
        { srcMemberProperties.associateBy { it.name }.also { typeNamePropMap[srcClass] = it } })


    return primaryConstructor?.callBy(primaryConstructor?.parameters?.associateWith {
            param -> srcPropMap[param.name]?.call(src)
    }!!)


}