package isel.lae.li41n.mapper.mappers

import isel.lae.li41n.mapper.Mapper
import isel.lae.li41n.mapper.domain.CourseExt
import isel.lae.li41n.mapper.domain.CourseInternal

class MapperDynCourseExtToCourseInternalKotlin() : Any(), Mapper<CourseExt, CourseInternal> {
    override fun mapTo(courseExt: CourseExt): CourseInternal {
        return CourseInternal(courseExt.name, courseExt.semester, courseExt.programme)
    }
}