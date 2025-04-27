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
        //val mapper = MapperDynCourseExtToCourseInternal()
        val mapper = loadDynamicMapper(CourseExt::class, CourseInternal::class);
        //val mapper = SimpleMapperReflect.getMapperFor(CourseExt::class, CourseInternal::class)
        var cInternal: Any? = mapper.mapTo(courseExternal)

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
        val mapper = SimpleMapperReflect.getMapperFor(CourseInternal::class, CourseExt::class)
        var cExt = mapper.mapTo(courseInternal)
        assertNotNull(cExt)
        assertEquals(CourseExt::class, cExt::class)

        val ce = cExt as CourseExt
        assertEquals(courseInternal.name, ce.name)
        assertEquals(courseInternal.semester, ce.semester)
        assertEquals(courseInternal.programme, ce.programme)
    }

    @Test
    fun `Should Map CourseInternal To AnotherCourseExternal`() {
        val mapper = SimpleMapperReflect.getMapperFor(CourseInternal::class, AnotherCourseExternal::class)
        val anCoruseExt = mapper.mapTo(courseInternal)
        assertNotNull(anCoruseExt)
        assertEquals(AnotherCourseExternal::class, anCoruseExt::class)

        val ace = anCoruseExt as AnotherCourseExternal
        assertEquals(courseInternal.name, ace.courseName)
        assertEquals(courseInternal.semester, ace.semester)
        assertEquals(courseInternal.programme, ace.programme)
    }




    @Test
    fun `Should Map CourseExt To AnotherCourseExternal`() {
        val mapper = SimpleMapperReflect.getMapperFor(CourseExt::class, AnotherCourseExternal::class)
        var anCExt = mapper.mapTo(courseExternal)
        assertNotNull(anCExt)
        assertEquals(AnotherCourseExternal::class, anCExt::class)

        val ce = anCExt as AnotherCourseExternal
        assertEquals(anotherCourseExternal.courseName, ce.courseName)
        assertEquals(anotherCourseExternal.semester, ce.semester)
        assertEquals(anotherCourseExternal.programme, ce.programme)
    }

    @Test
    fun `Should Map CourseExternalWithTeacher To CourseInternalWithTeacher`() {
        val mapper = SimpleMapperReflect.getMapperFor(CourseExternalWithTeacher::class, CourseInternalWithTeacher::class)

        val courseExternalWithTeacher = CourseExternalWithTeacher("LAE", 4, "LEIC", TeacherExternal("Luis Falcão", "luis.falcao@isel.pt"))
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