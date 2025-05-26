package pt.isel.generator

import pt.isel.lae.li41n.fetchCps
import java.util.NoSuchElementException
import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext

fun fetchManyCpsLazy(url1: String, url2: String, url3: String) : Sequence<String> {
    return object : Sequence<String> {
        override fun iterator() = FetchManyCpsLazy(url1, url2, url3)
    }
}

class FetchManyCpsLazy(
    private val url1: String,
    private val url2: String,
    private val url3: String
)
    : Continuation<String?>, Iterator<String>
{
    var state = 0
    var nextItem: String? = null

    override val context = EmptyCoroutineContext

    override fun resumeWith(result: Result<String?>) {
        nextItem = result.getOrThrow()
    }
    private fun block() {
        when(state++) {
            0 -> fetchCps(url1, this)
            1 -> fetchCps(url2, this)
            2 -> fetchCps(url3, this)
            else -> throw NoSuchElementException()
        }
    }

    override fun hasNext() = state <= 2

    override fun next(): String {
        block()
        return nextItem as String
    }
}
