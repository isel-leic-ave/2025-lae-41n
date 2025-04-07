package isel.lae.li41n

enum class Weekdays {
    Monday,
    Tuesday,
    Wednesday,
    Thursday,
    Friday,
    Saturday,
    Sunday
}

fun main() {
    val monday = "Monday"
    //Weekdays.values().forEach { weekday -> println(weekday.name) }
    var javaClass: Class<*> = Weekdays::class.java
    var m: Any = javaClass.enumConstants?.find { c -> c.toString() == monday }!!
    println(m::class)

}