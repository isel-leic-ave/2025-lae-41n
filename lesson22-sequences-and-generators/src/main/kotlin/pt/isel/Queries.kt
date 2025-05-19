package pt.isel.lae.li41n

fun <T, R> Iterable<T>.eagerMap(transform: (T) -> R): Iterable<R> {
    val destination = mutableListOf<R>()
     for (item in this)
        destination.add(transform(item))
//    val iter = this.iterator()
//    while (iter.hasNext()) {
//        destination.add(transform(iter.next()))
//    }
    return destination
}

fun <T> Iterable<T>.eagerFilter(predicate: (T) -> Boolean): Iterable<T> {
    val destination = mutableListOf<T>()
    for (item in this) {
        if (predicate(item)) {
            destination.add(item)
        }
    }
    return destination
}




fun <T> Iterable<T>.eagerDistinct(): Iterable<T> {
    val destination = mutableSetOf<T>()
    for (item in this) {
        destination.add(item)
    }
    return destination
}

fun <T> Iterable<T>.eagerLast(): T {
    val iterPrev = this.iterator()
    if(iterPrev.hasNext())
        throw NoSuchElementException()
    var value: T = iterPrev.next()
    while (iterPrev.hasNext()) {
        value = iterPrev.next()
    }
    return value
}

fun <T> Iterable<T>.eagerLast(predicate: (T) -> Boolean): T {
    val iterPrev = this.iterator()
    var value: T? = null
    while (iterPrev.hasNext()) {
        val current = iterPrev.next()
        if(predicate(current)) {
            value = current
        }
    }
    if(value == null)
        throw NoSuchElementException()
    return value
}

fun <T, R> Iterable<T>.lazyMap(transform: (T) -> R): Iterable<R> {
    val prevIterable = this
    return object : Iterable<R> {
        override fun iterator(): Iterator<R> {
            val prevIterator = prevIterable.iterator()
            return object: Iterator<R> {
                override fun hasNext(): Boolean = prevIterator.hasNext()

                override fun next(): R = transform(prevIterator.next())
            }
        }
    }
}

fun <T> Sequence<T>.lazyFilter(predicate: (T) -> Boolean): Sequence<T> {
    val prevIterable = this
    return object : Sequence<T> {
        override fun iterator(): Iterator<T> {
            val prevIterator = prevIterable.iterator()
            return object: Iterator<T> {
                var value: T? = null
                var hasValue = false
                override fun hasNext(): Boolean {
                    if(hasValue)
                        return true

                    while (prevIterator.hasNext()) {
                        val next: T = prevIterator.next()
                        if(predicate(next)) {
                            value = next
                            hasValue = true
                            return true
                        }
                    }
                    return false
                }

                override fun next(): T  {
                    if(hasNext()) {
                        val ret = value as T
                        hasValue = false
                        return ret
                    }
                    throw NoSuchElementException()
                }
            }
        }
    }
}


fun <T> Sequence<T>.lazyFilterWithSequence(predicate: (T) -> Boolean): Sequence<T> {
    return sequence<T> {
        for (item in this@lazyFilterWithSequence) {
            if (predicate(item)) {
                this.yield(item)
            }
        }
    }
}

fun <T, R> Sequence<T>.lazyMap(transform: (T) -> R): Sequence<R> =
    object : Sequence<R> {
        override fun iterator(): Iterator<R> =
            object : Iterator<R> {
                val iter = this@lazyMap.iterator()

                override fun hasNext() = iter.hasNext()

                override fun next() = transform(iter.next())
            }
    }

/**
 * SUPPORTS Sequences with null elements
 */
fun <T> Sequence<T?>.lazyDistinct(): Sequence<T?> {
    return object : Sequence<T?> {
        override fun iterator(): Iterator<T?> =
            object : Iterator<T?> {
                val iterPrev = this@lazyDistinct.iterator()
                val returnedObjects = mutableSetOf<T?>()
                var value: T? = null
                var hasValue = false
                override fun hasNext(): Boolean {
                    if (hasValue) return true
                    while (iterPrev.hasNext()) {
                        val next = iterPrev.next()
                        if (!returnedObjects.contains(next)) {
                            returnedObjects.add(next)
                            value = next
                            hasValue = true
                            return true
                        }
                    }
                    return false
                }

                override fun next(): T? {
                    if (hasNext()) {
                        val ret = value
                        hasValue = false
                        return ret
                    }
                    throw NoSuchElementException()
                }
            }
    }
}

fun <T> Iterable<T>.myForEach(action: (T) -> Unit ): Unit {
    for (item in this) {
        action(item)
    }
}

fun <T> Iterable<T>.myCount(): Int {
    var count = 0
    for (item in this) {
        count++
    }
    return count
}


fun <T>Sequence<T>.interleaveLazy(other: Sequence<T>): Sequence<T> {
    TODO()
}


fun <T> Sequence<T>.suspFilter(predicate: (T) -> Boolean): Sequence<T> {
    TODO()
}


fun <T> Sequence<T>.lazyConcat(other: Sequence<T>): Sequence<T> {
    return object : Sequence<T> {
        override fun iterator(): Iterator<T> {
            return object: Iterator<T> {
                var prevIter = this@lazyConcat.iterator()
                var prevOtherIter = other.iterator()
                var inFirstSequence = prevIter.hasNext()

                override fun hasNext(): Boolean {
                    inFirstSequence = prevIter.hasNext()
                    return inFirstSequence || prevOtherIter.hasNext()
                }
                override fun next(): T {
                    val iter = if (inFirstSequence) prevIter else prevOtherIter
                    return iter.next()
                }
            }
        }

    }
}

fun <T> Sequence<T>.suspConcat(other: Sequence<T>): Sequence<T> {
    return sequence {
        for (item in this@suspConcat) {
            yield(item)
        }
        for (item in other) {
            yield(item)
        }
    }
}

fun <T : Any?> Sequence<T>.suspCollapse(): Sequence<T> {
    return sequence {
        val iter = this@suspCollapse.iterator()
        if(iter.hasNext()) {
            var last = iter.next()
            yield(last)
            while(iter.hasNext()) {
                var newVal = iter.next()
                if(newVal != last) {
                    last = newVal
                    yield(newVal)
                }
            }
        }
    }
}

fun <T> Sequence<T>.suspDistinct(): Sequence<T> {
    TODO()
}

public fun <T, R, V> Sequence<T>.suspZip(other: Sequence<R>, transform: (a: T, b: R) -> V): Sequence<V> {
    return sequence {
        val iterThis = this@suspZip.iterator()
        val iterOther = other.iterator()

        while(iterThis.hasNext() && iterOther.hasNext()) {
            yield(transform(iterThis.next(), iterOther.next()))
        }
    }
}


