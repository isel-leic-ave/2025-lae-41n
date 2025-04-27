
package isel.lae.li41n.mapper

import isel.lae.li41n.mapper.domain.CourseExt
import isel.lae.li41n.mapper.domain.CourseInternal
import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit

@BenchmarkMode(Mode.AverageTime) // Measure execution time per operation
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
open class MapperBench {
    val courseInternal = CourseInternal("LAE", 4, "LEIC")
    val courseExternal = CourseExt("LAE", 4, "LEIC")
    val mapperReflection = SimpleMapperReflect.getMapperFor(CourseInternal::class, CourseExt::class)
    val mapperDynamic = loadDynamicMapper(CourseInternal::class, CourseExt::class)

    @Benchmark
    fun benchmarkManualMapper() = mapCourseInternalToCourseExternal(courseInternal)

    @Benchmark
    fun benchmarkReflectionMapper() = mapperReflection.mapTo(courseInternal)

    @Benchmark
    fun benchmarkDynamicMapper() = mapperDynamic.mapTo(courseInternal)
}

fun mapCourseExtToCourseInternal(courseExt: CourseExt): CourseInternal {
    return CourseInternal(courseExt.name, courseExt.semester, courseExt.programme)
}

fun mapCourseInternalToCourseExternal(courseInt: CourseInternal): CourseExt {
    return CourseExt(courseInt.name, courseInt.semester, courseInt.programme)
}
