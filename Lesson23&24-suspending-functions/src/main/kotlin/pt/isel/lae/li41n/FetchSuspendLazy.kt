package pt.isel.generator

import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.resume

fun fetchManySuspendLazy(url1: String, url2: String, url3: String) : Sequence<String> {
    return object : Sequence<String> {
        override fun iterator() = FetchManySuspendLazy(url1, url2, url3)
    }
}

class FetchManySuspendLazy(
    private val url1: String,
    private val url2: String,
    private val url3: String
)
    : Continuation<Unit>, Iterator<String>
{
    var nextItem: String? = null
    var nextStep: Continuation<Unit>? = null
    var finish: Result<Unit>? = null

    override val context = EmptyCoroutineContext

    private suspend fun block() {
        val body1 = fetch(url1)
        yield(body1)
        val body2 = fetch(url2)
        yield(body2)
        val body3 = fetch(url3)
        yield(body3)
    }

    private val blockHandle = ::block as ((Continuation<Unit>) -> Any)

    private fun tryAdvance() {
        nextStep
            ?.resume(Unit)
            ?:blockHandle(this)
    }

    override fun resumeWith(result: Result<Unit>) {
        finish = result
    }

    override fun hasNext() = finish == null

    override fun next(): String {
        tryAdvance()
        //finish?.getOrThrow()
        if(finish != null) throw NoSuchElementException()
        return nextItem as String
    }

    private suspend fun yield(item: String) = yieldHande(item)

    private val yieldHande = ::yieldCps as (suspend (String) -> Unit)

    private fun yieldCps(item: String, onComplete: Continuation<Unit>) : Any {
        nextItem = item
        nextStep = onComplete
        /*
         * It means that suspend function did suspend the execution
         * and will not return any result immediately.
         */
        return COROUTINE_SUSPENDED

    }
}
