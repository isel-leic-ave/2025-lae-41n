package pt.isel.lae.li41n

import org.junit.jupiter.api.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

/*
 * Located on testes resources
 */
private const val weatherPath = "q-Lisbon_format-csv_date-2020-05-08_enddate-2020-06-11.csv"

class TestWeatherTemperatures {
    fun loadNaiveCsv(): List<Weather> =
        ClassLoader
            .getSystemResource(weatherPath)
            .openStream()
            .reader()
            .readLines()                    // List<String>
            .filter { !it.startsWith('#') } // Filter comments
            .drop(1)                        // Skip line: Not available
            .filterIndexed { index, _ -> index % 2 != 0 } // Filter hourly info
            .map { it.fromCsvToWeather() }  // List<Weather>
            .toList()
    private val weatherData = loadNaiveCsv()


    @Test
    fun checkData() {
        weatherData
            .forEach() { println(it) }
    }

    @Test
    fun countDistinctDescriptionsInCloudyDays() {
        var count = 0
        val distinctDescr = weatherData         // Iterable<Weather>
            //.asSequence()
            .filter {
                ++count
                it.weatherDesc.contains("cloudy", true)
            }   // Iterable<Weather>
            .asSequence()
            .map {
                ++count
                it.weatherDesc
            }    // Iterable<String>
            .distinct()
            //.count()
            //.forEach { println(it) }
        println(count)
        //assertEquals(2, distinctDescr)
    }

    @Test
    fun countDistinctDescriptionsInRainyDaysMapFilter() {
        var iters = 0
        val size =
            weatherData
                .eagerMap {
                    iters++
                    it.weatherDesc
                }.eagerFilter {
                    iters++
                    it.lowercase().contains("rain")
                }.eagerDistinct()
                .count()
        assertEquals(5, size)
        assertEquals(70, iters)
    }

    @Test
    fun countDistinctDescriptionsInRainyDaysFilterMap() {
        var iters = 0
        val size =
            weatherData
                .eagerFilter {
                    iters++
                    it.weatherDesc.lowercase().contains("rain")
                }.eagerMap {
                    iters++
                    it.weatherDesc
                }.eagerDistinct()
                .count()
        assertEquals(5, size)
        assertEquals(48, iters)
    }

    @Test
    fun firstDescriptionInWindyDays() {
        var iters = 0
        val desc =
            weatherData
                .filter {  // List<Weather>
                    iters++
                    it.windspeedKmph > 22
                }
                .eagerMap {    // List<String>
                    iters++
                    it.weatherDesc
                }
            .first()
        assertEquals("Light rain shower", desc)
        assertEquals(37, iters)
    }

    @Test
    fun firstDescriptionInWindyDaysAsSequenceWithoutTerminalOperation() {
        var iters = 0
        val desc =
            weatherData
                .asSequence()  // Sequence<Weather>
                .filter {      // Sequence<Weather>
                    iters++
                    it.windspeedKmph > 22
                }.lazyMap {        // Sequence<String>
                    iters++
                    it.weatherDesc
                }
                .first()
        // assertEquals("Light rain shower", desc)
        /**
         * NO Processing WITHOUT a terminal operation
         */
        assertEquals(0, iters)
    }

    @Test
    fun firstDescriptionInWindyDaysAsSequence() {
        var iters = 0
        val desc =
            weatherData
                .asSequence()
                .filter {
                    iters++
                    it.windspeedKmph > 22
                }.lazyMap {
                    iters++
                    it.weatherDesc
                }.first()
        assertEquals("Light rain shower", desc)
        assertEquals(5, iters)
    }

    @Test
    fun countDistinctDescriptionsInRainyDaysFilterMapLazy() {
        var iters = 0
        val size =
            weatherData
                .asSequence()
                .filter {
                    iters++
                    it.weatherDesc.lowercase().contains("rain")
                }.lazyMap {
                    iters++
                    it.weatherDesc
                }.lazyDistinct()
                .onEach { println(it) }
                .count()
        assertEquals(5, size)
        assertEquals(48, iters)
    }


    @Test
    fun testFilterLazy() {
        val seq =
            listOf(1, 2, 3, 4, 5, null,  6, 7, 8, 9)
                .eagerFilter { it != null && it % 2 == 0 }

        var iter = seq.iterator()
        iter.hasNext()
        iter.hasNext()
        iter.hasNext()
        assertEquals(2,iter.next())
        assertEquals(4,iter.next())
        iter.hasNext()
        assertEquals(6,iter.next())
        iter.hasNext()
        iter.hasNext()
        iter.hasNext()
        iter.hasNext()
        assertEquals(8,iter.next())


    }


    @Test
    fun testDistinctSimple() {
        val seq =
            sequenceOf(1, 2, 3, 4, 5, 6, 7, 8, 9)
                .lazyDistinct()
        val iter = seq.iterator()
        assertEquals(1, iter.next())
        iter.hasNext()
        iter.hasNext()
        iter.hasNext()
        assertEquals(2, iter.next())
        iter.hasNext()
        assertEquals(3, iter.next())
        assertEquals(4, iter.next())
        iter.hasNext()
        iter.hasNext()
        iter.hasNext()
        assertEquals(5, iter.next())
    }

    @Test
    fun testDistinctOnNulls() {
        val actual =
            sequenceOf(1, 2, 4, 2, 2, 5, null, null, 3, 7, 9, null, null)
                .lazyDistinct()

        assertContentEquals(
            sequenceOf(1, 2, 4, 5, null, 3, 7, 9),
            actual,
        )
    }
}
