package isel.lae.li41n.mapper

class MapperDynCourseExtWithTeacherToCourseInternalWithTeacher() : Any(), Mapper<CourseExternalWithTeacher, CourseInternalWithTeacher> {
    override fun mapTo(courseExt: CourseExternalWithTeacher): CourseInternalWithTeacher {
        var name = courseExt.name;
        var semester = courseExt.semester;
        var programme = courseExt.programme

        var mapperTeachers = loadDynamicMapper(TeacherExternal::class.java, TeacherInternal::class.java)
        var teacherInternal = mapperTeachers.mapTo(courseExt.teacher)


        return CourseInternalWithTeacher(name, semester, programme, teacherInternal)
    }
}