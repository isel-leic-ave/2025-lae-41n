
package isel.lae.li41n.mapper

import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit

@BenchmarkMode(Mode.AverageTime) // Measure execution time per operation
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
open class MapperBench {
    val courseInternal = CourseInternal("LAE", 4, "LEIC")
    val courseExternal = CourseExt("LAE", 4, "LEIC")

    @Benchmark
    fun benchmarkManualMapper() = mapCourseInternalToCourseExternal(courseInternal)

    @Benchmark
    fun benchmarkReflectionMapper() = mapTo(courseInternal, CourseExt::class)
}

fun mapCourseExtToCourseInternal(courseExt: CourseExt): CourseInternal {
    return CourseInternal(courseExt.name, courseExt.semester, courseExt.programme)
}

fun mapCourseInternalToCourseExternal(courseInt: CourseInternal): CourseExt {
    return CourseExt(courseInt.name, courseInt.semester, courseInt.programme)
}
