package isel.lae.li41n.mapper

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class MapperTests {
    val courseInternal = CourseInternal("LAE", 4, "LEIC", 80)
    val courseExternal = CourseExt("LAE", 4, "LEIC")
    val anotherCourseExternal = AnotherCourseExternal("LAE", 4, "LEIC")

    @Test
    fun `Should Map ExternalCourse To InternalCourse`() {
        var cInternal: Any? = mapTo(courseExternal, CourseInternal::class)
        var cInternal1: Any? = mapTo(courseExternal, CourseInternal::class)

        assertNotNull(cInternal)
        assertEquals(CourseInternal::class, cInternal::class)

        val ci: CourseInternal = cInternal as CourseInternal
        assertEquals(courseExternal.name, ci.name)
        assertEquals(courseExternal.semester, ci.semester)
        assertEquals(courseExternal.programme, ci.programme)
        assertEquals(-1, ci.numStudents)
    }


    @Test
    fun `Should Map InternalCourse To ExternalCourse`() {
        var cExt = mapTo(courseInternal, CourseExt::class)
        assertNotNull(cExt)
        assertEquals(CourseExt::class, cExt::class)

        val ce = cExt as CourseExt
        assertEquals(courseInternal.name, ce.name)
        assertEquals(courseInternal.semester, ce.semester)
        assertEquals(courseInternal.programme, ce.programme)
    }

    @Test
    fun `Should Map CourseInternal To AnotherCourseExternal`() {
        val anCoruseExt = mapTo(courseInternal, AnotherCourseExternal::class)
        assertNotNull(anCoruseExt)
        assertEquals(AnotherCourseExternal::class, anCoruseExt::class)

        val ace = anCoruseExt as AnotherCourseExternal
        assertEquals(courseInternal.name, ace.courseName)
        assertEquals(courseInternal.semester, ace.semester)
        assertEquals(courseInternal.programme, ace.programme)
    }




    @Test
    fun `Should Map CourseExt To AnotherCourseExternal`() {
        var anCExt = mapTo(courseExternal, AnotherCourseExternal::class)
        assertNotNull(anCExt)
        assertEquals(AnotherCourseExternal::class, anCExt::class)

        val ce = anCExt as AnotherCourseExternal
        assertEquals(anotherCourseExternal.courseName, ce.courseName)
        assertEquals(anotherCourseExternal.semester, ce.semester)
        assertEquals(anotherCourseExternal.programme, ce.programme)
    }

    @Test
    fun `Should Map CourseExternalWithTeacher To CourseInternalWithTeacher`() {
        val courseExternalWithTeacher = CourseExternalWithTeacher("LAE", 4, "LEIC", TeacherExternal("Luis Falcão", "luis.falcao@isel.pt"))
        var cIntWithTeacher = mapTo(courseExternalWithTeacher, CourseInternalWithTeacher::class)
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