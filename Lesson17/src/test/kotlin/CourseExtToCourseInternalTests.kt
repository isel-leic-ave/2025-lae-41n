package isel.lae.li41n.mapper

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class CourseExtToCourseInternalTests {
    companion object {
        @JvmStatic
        fun mappers() =
            listOf<Mapper<CourseExt, CourseInternal>>(
//                SimpleMapperReflect(CourseExt::class, CourseInternal::class) as Mapper<CourseExt, CourseInternal>,
//                MapperDynCourseExtToCourseInternalJava(),
                loadDynamicMapper(CourseExt::class, CourseInternal::class)
            )

    }

    val courseExternal = CourseExt("LAE", 4, "LEIC")

    @ParameterizedTest
    @MethodSource("mappers")
    fun `Should Map ExternalCourse To InternalCourse`(mapper: Mapper<CourseExt, CourseInternal>) {
        var cInternal: Any? = mapper.mapTo(courseExternal)

        assertNotNull(cInternal)
        assertEquals(CourseInternal::class, cInternal::class)

        val ci: CourseInternal = cInternal as CourseInternal
        assertEquals(courseExternal.name, ci.name)
        assertEquals(courseExternal.semester, ci.semester)
        assertEquals(courseExternal.programme, ci.programme)
        assertEquals(-1, ci.numStudents)
    }
}