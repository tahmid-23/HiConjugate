package com.github.tahmid_23.hiconjugate

import com.github.tahmid_23.hiconjugate.network.ConjugationRequester
import io.ktor.client.*
import kotlinx.coroutines.runBlocking
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun lolUnitTest() {
        val client = HttpClient()
        val requestor = ConjugationRequester(client)

        runBlocking {
            requestor.getVerb("penser")
        }
    }
}