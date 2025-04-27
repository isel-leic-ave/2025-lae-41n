package isel.lae.li41n.mapper.mappers

import isel.lae.li41n.mapper.*
import isel.lae.li41n.mapper.domain.*

class MapperDynCourseExtWithTeacherToCourseInternalWithTeacher() : Mapper<CourseExternalWithTeacher, CourseInternalWithTeacher> {
    private val teacherMapper: Mapper<TeacherExternal, TeacherInternal> =
        loadDynamicMapper(TeacherExternal::class.java, TeacherInternal::class.java)

    override fun mapTo(src: CourseExternalWithTeacher): CourseInternalWithTeacher {
        return CourseInternalWithTeacher(
            src.name,
            src.semester,
            src.programme,
            teacherMapper.mapTo(src.teacher)
        )
    }
}