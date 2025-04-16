/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package isel.lae.li41n.mapper

class CourseExt(val name: String, val semester: Int,val  programme: String) {}


class CourseInternal @JvmOverloads constructor(
    val name: String,
    val semester: Int,
    val programme: String,
    val numStudents: Int = -1,
) {}






class AnotherCourseExternal(
    @PropName("name")
    val courseName: String,
    val semester: Int,
    val programme: String,
) {}

class TeacherInternal(@PropName("name") val teacherName: String, val email: String){ }
class TeacherExternal(val name: String, val email: String){ }

class CourseInternalWithTeacher(
    val name: String,
    val semester: Int,
    val programme: String,
    val teacher: TeacherInternal
) {}

class CourseExternalWithTeacher(
    val name: String,
    val semester: Int,
    val programme: String,
    val teacher: TeacherExternal
) {}



fun mapCourseExtToCourseInternal(courseExt: CourseExt): CourseInternal {
    return CourseInternal(courseExt.name, courseExt.semester, courseExt.programme)
}

fun mapCourseInternalToCourseExternal(courseInt: CourseInternal): CourseExt {
    return CourseExt(courseInt.name, courseInt.semester, courseInt.programme)
}



