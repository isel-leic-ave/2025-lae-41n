

fun getValuesEager(): Iterable<Int> {
    val res = mutableListOf<Int>()
    println("Adding 1")
    res.add(1)
    println("Adding 2")
    res.add(2)
    println("Adding 3")
    res.add(3)
    println("Adding 4")
    res.add(4)
    println("Adding 5")
    res.add(5)
    return res
}

fun getValuesLazy(): Sequence<Int> {
    return sequence<Int> {
        yield(1)
        println("*Adding 1")
        yield(2)
        println("*Adding 2")
        yield(3)
        println("*Adding 3")
        yield(4)
        println("*Adding 4")
        yield(5)
        println("*Adding 5")
    }

}


fun getValuesLazyWithLoop() : Sequence<Int> {
    return sequence<Int> {
        for (i in 0..5) {
            yield(i)
            println("*Adding ${i + 1}")
        }
    }
}

fun <T>showValues(seq: Iterator<T>, numElemsToShow: Int = -1) {
    var count = numElemsToShow
    for (i in seq) {
        if(numElemsToShow != -1 && count-- == 0)
            break
        println(i)
    }
}


fun main() {
    val eagerValues = getValuesEager()
    val lazyValues = getValuesLazy()
    val lazyValuesLoop = getValuesLazyWithLoop()

    println("Eager values")
    showValues(eagerValues.iterator(), 2)
    println("*Lazy values")
    showValues(lazyValues.iterator(), 2)
    println("*Lazy values loop")
    showValues(lazyValuesLoop.iterator(), 2)
}


