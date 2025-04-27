package isel.lae.li41n.mapper;

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
