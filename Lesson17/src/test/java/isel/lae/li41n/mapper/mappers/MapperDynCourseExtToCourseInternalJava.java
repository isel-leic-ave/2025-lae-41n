package isel.lae.li41n.mapper.mappers;

import isel.lae.li41n.mapper.Mapper;
import isel.lae.li41n.mapper.domain.CourseExt;
import isel.lae.li41n.mapper.domain.CourseInternal;

public class MapperDynCourseExtToCourseInternalJava extends Object implements Mapper<CourseExt, CourseInternal> {
    public MapperDynCourseExtToCourseInternalJava() {
        super();
    }
    @Override
    public CourseInternal mapTo(CourseExt src) {
        String name = src.getName();
        int semester = src.getSemester();
        String programme = src.getProgramme();

        return new CourseInternal(name, semester, programme, -1);
    }
}
