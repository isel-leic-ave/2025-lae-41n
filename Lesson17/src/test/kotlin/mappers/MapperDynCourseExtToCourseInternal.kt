package isel.lae.li41n.mapper.mappers

import isel.lae.li41n.mapper.Mapper
import isel.lae.li41n.mapper.domain.CourseExt
import isel.lae.li41n.mapper.domain.CourseInternal

class MapperDynCourseExtToCourseInternalKotlin() : Any(), Mapper<CourseExt, CourseInternal> {
    override fun mapTo(src: CourseExt): CourseInternal {
        return CourseInternal(src.name, src.semester, src.programme)
    }
}