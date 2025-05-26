package pt.isel.lae.li41n

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import pt.isel.generator.fetchManyCpsLazy
import pt.isel.generator.fetchManySuspendLazy
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

val URL1 = "https://api.chucknorris.io/jokes/random"
fun main() {
    //callFetchManyAsCps()
    //callfetchManyCpsLazy()
    callfetchManySuspendLazy()
//    runBlocking {
//        callFetchManyAsSuspend()
//    }



}

suspend fun callFetchManyAsSuspend() {
    var fetchManyHandle =  ::fetchManyCps as (suspend (String, String, String)-> List<String>)
    var result = fetchManyHandle(URL1, URL1, URL1)
    println("Result from fetchManyCps called as suspend ${result.joinToString("\n")}")
}

fun callFetchManyAsCps() {
    fetchManyCps(
        URL1, URL1, URL1,
        object : Continuation<Iterable<String>> {
            override val context: CoroutineContext = EmptyCoroutineContext
            override fun resumeWith(result: Result<Iterable<String>>) {
//                var res = result.getOrThrow()
//                val iter = res.iterator()
//                val response1 = iter.next()
//                println(response1)

                //println("Result from fetchManyCps ${result.getOrThrow().joinToString("\n")}")
            }
        })

    runBlocking {
        delay(5000)
    }
}

fun callfetchManyCpsLazy() {
    val seq = fetchManyCpsLazy(URL1, URL1, URL1)
    val iter = seq.iterator()
    iter.hasNext()
    val content1 = iter.next()
    println(content1)

}

fun callfetchManySuspendLazy() {
    val seq = fetchManySuspendLazy(URL1, URL1, URL1)
    val iter = seq.iterator()
    iter.hasNext()
    val content1 = iter.next()
    println(content1)
}
