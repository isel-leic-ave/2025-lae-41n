package isel.lae.li41n.mapper

import isel.lae.li41n.mapper.domain.*
import isel.lae.li41n.mapper.mappers.MapperDynCourseExtToCourseInternalJava
import isel.lae.li41n.mapper.mappers.MapperDynCourseExtWithTeacherToCourseInternalWithTeacher
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class CourseExtWithTeacherToCourseInternalWithTeacherTests {
    companion object {
        @JvmStatic
        fun mappers() =
            listOf<Mapper<CourseExternalWithTeacher, CourseInternalWithTeacher>>(
//                SimpleMapperReflect(CourseExternalWithTeacher::class, CourseInternalWithTeacher::class) as Mapper<CourseExternalWithTeacher, CourseInternalWithTeacher>,
//                MapperDynCourseExtWithTeacherToCourseInternalWithTeacher(),
                loadDynamicMapper(CourseExternalWithTeacher::class, CourseInternalWithTeacher::class)
            )

    }

    val courseExternalWithTeacher = CourseExternalWithTeacher("LAE", 4, "LEIC", TeacherExternal("Luis Falcão", "luis.falcao@isel.pt"))

    @ParameterizedTest
    @MethodSource("mappers")
    fun `Should Map ExternalCourse To InternalCourse`(mapper: Mapper<CourseExternalWithTeacher, CourseInternalWithTeacher>) {
        var cIntWithTeacher = mapper.mapTo(courseExternalWithTeacher)
        assertNotNull(cIntWithTeacher)
        assertEquals(CourseInternalWithTeacher::class, cIntWithTeacher::class)

        val cIntWTeacher = cIntWithTeacher as CourseInternalWithTeacher

        assertNotNull(cIntWTeacher.teacher)

        val teacherExt = courseExternalWithTeacher.teacher
        val teacherInt = cIntWTeacher.teacher
        assertEquals(teacherExt.name, teacherInt.teacherName)
        assertEquals(teacherExt.email, teacherInt.email)
    }
}