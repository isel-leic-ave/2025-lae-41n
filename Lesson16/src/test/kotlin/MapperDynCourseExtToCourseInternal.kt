package isel.lae.li41n.mapper

class MapperDynCourseExtToCourseInternalKotlin() : Any(), Mapper<CourseExt, CourseInternal> {
    override fun mapTo(courseExt: CourseExt): CourseInternal {
        return CourseInternal(courseExt.name, courseExt.semester, courseExt.programme)
    }
}