package pt.isel.generator

import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.resume

interface Yieldable<T> {
    suspend fun yield(item: T)
}

fun <T> buildSequence(block: suspend Yieldable<T>.() -> Unit): Sequence<T> {
    return object : Sequence<T> {
        override fun iterator(): Iterator<T> {
            return SequenceBuilderIterator(block)
        }
    }
}

enum class SequenceState { NotReady, Ready, Done, Failed}

private class SequenceBuilderIterator<T>(block: suspend Yieldable<T>.() -> Unit) :
    Yieldable<T>,
    Iterator<T>,
    Continuation<Unit>
{

    private var nextItem: T? = null
    private var nextStep: Continuation<Unit>? = null
    private var finish: Result<Unit>? = null
    private var state = SequenceState.NotReady

    override val context = EmptyCoroutineContext

    @Suppress("UNCHECKED_CAST")
    private val blockHandle = block as (Yieldable<T>.(Continuation<Unit>) -> Any)

    private fun tryAdvance() {
        if(state == SequenceState.NotReady) {
            nextStep
                ?.resume(Unit)
                ?: blockHandle(this)
        }
    }

    override fun resumeWith(result: Result<Unit>) {
        state =
            if(result.isSuccess) {
                SequenceState.Done
            } else {
                finish = result
                SequenceState.Failed
            }
    }

    override fun hasNext(): Boolean {
        tryAdvance()
        return state == SequenceState.Ready
    }

    override fun next(): T {
        tryAdvance()
        finish?.getOrThrow()
        if(state != SequenceState.Ready) throw NoSuchElementException()
        state = SequenceState.NotReady
        return nextItem as T
    }

    override suspend fun yield(item: T) = yieldHandle(item)

    @Suppress("UNCHECKED_CAST")
    private val yieldHandle = ::yieldCps as (suspend(T) -> Unit)

    fun yieldCps(item: T, cont: Continuation<Unit>) : Any{
        nextItem = item
        nextStep = cont
        state = SequenceState.Ready
        /*
         * It means that suspend function did suspend the execution
         * and will not return any result immediately.
         */
        return COROUTINE_SUSPENDED
    }
}

