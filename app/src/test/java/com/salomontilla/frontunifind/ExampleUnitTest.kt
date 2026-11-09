package com.salomontilla.frontunifind

import com.salomontilla.frontunifind.data.network.KtorClient
import com.salomontilla.frontunifind.data.repository.UniFindRepository
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.Assert.*

class ExampleUnitTest {
    @Test
    fun testRealRepositoryCalls() {
        runBlocking {
            KtorClient.host = "127.0.0.1"

            // 1. Probar carga de items pendientes de moderación (Guard)
            val pendingRes = UniFindRepository.getPendingModerationItems()
            println("PENDING RES: isSuccess=${pendingRes.isSuccess}, count=${pendingRes.getOrNull()?.size}")
            assertTrue(pendingRes.isSuccess)
            val pending = pendingRes.getOrNull()
            assertNotNull(pending)
            assertTrue("Debe contener objetos pendientes de moderación en el backend", pending!!.isNotEmpty())
            pending.forEach {
                println(" - [${it.code}] ${it.title} (${it.status})")
            }

            // 2. Probar carga de feed activo (Student)
            val feedRes = UniFindRepository.getItems()
            println("FEED RES: isSuccess=${feedRes.isSuccess}, count=${feedRes.getOrNull()?.size}")
            assertTrue(feedRes.isSuccess)
        }
    }
}
