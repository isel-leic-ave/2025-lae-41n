package pt.isel.lae.li41n

import kotlinx.coroutines.future.await
import kotlinx.coroutines.runBlocking
import java.lang.Thread.sleep
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.net.http.HttpResponse.BodyHandlers.*
import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


private val httpClient: HttpClient = HttpClient.newHttpClient()
private val requestBuilder = HttpRequest.newBuilder()
private fun request(url: String) = requestBuilder.uri(URI.create(url)).build()

val URL = "https://api.chucknorris.io/jokes/random"

fun main() {
    println("#### Call synchronous fetch")
    callFetchSynchronous()
    sleep(1000)
    println("#### Call suspend fetch")
    callFetchSuspend()
    sleep(1000)
    println("#### Call a suspend function as CPS")
    callFetchSuspendAsCps()
    sleep(1000)
    println("#### Call a CPS function as suspending function")
    runBlocking {
        callFetchCpsAsSuspend()
    }
}

fun callFetchSuspend() {
    runBlocking {
        var res = fetchSuspend(URL)
        println("Fetch suspend result is ${res}")
    }

}

fun callFetchSynchronous() {
    val res = fetch(URL)
    println("fetch result is ${res}")
}

fun fetch(path: String): String {
    println("Fetching $path")
    return URI(path).toURL().readText()
}


suspend fun fetchSuspend(url: String): String {
    println("Fetching $url")
    return httpClient
        .sendAsync(request(url), ofString())
        .thenApply(HttpResponse<String>::body)
        .await()
}

fun fetchCps(path: String, onComplete: Continuation<String>) : Any {
    println("Fetching $path")
    try {
        val body = URI(path).toURL().readText()
        onComplete.resume(body)
//        httpClient
//            .sendAsync(request(path), ofString())
//            .thenApply(HttpResponse<String>::body)
//            .thenAccept { body -> onComplete.resume(body) }
    } catch(err: Throwable) {
        onComplete.resumeWithException(err)
    }
    /*
     * It means execution was suspended and will not return any result immediately.
     * !!! It makes sense with the asynchronous httpClient call !!!
     */
    return COROUTINE_SUSPENDED
}

fun callFetchSuspendAsCps() {
    val fetchHandler = ::fetchSuspend as (String, Continuation<String>) -> Any
    fetchHandler(URL, object : Continuation<String> {
        override val context = EmptyCoroutineContext
        override fun resumeWith(result: Result<String>) {
            println(result.getOrThrow())
        }
    })
}

//fun fetchCps(path: String, onComplete: Continuation<String>) : Any {
suspend fun callFetchCpsAsSuspend() {
    val fetchCpsHandler = ::fetchCps as (suspend (String) -> String)
    val result = fetchCpsHandler(URL)
    println(result)
}