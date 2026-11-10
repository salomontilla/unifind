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

            // 3. Probar creación de reporte express (Found)
            val createRes = UniFindRepository.createFoundExpress(
                com.salomontilla.frontunifind.data.model.CreateFoundExpressRequest(
                    title = "Objeto Prueba Unitaria Directa",
                    category = "Electrónica",
                    custodyType = "PORTERIA",
                    campusZone = "Biblioteca Central",
                    locationReference = "Piso 1 Cubículos",
                    photoUrl = "https://images.unsplash.com/photo-1587145820266-a5951ee6f620"
                )
            )
            // 4. Probar Login como Vigilante (Guard) y consulta de pendientes
            val guardLoginRes = UniFindRepository.login("andres.salazar@uniautonoma.edu.co", "GUARD-001")
            assertTrue(guardLoginRes.isSuccess)
            val guardUser = guardLoginRes.getOrNull()
            assertNotNull(guardUser)
            assertTrue("El rol debe ser guard", guardUser!!.isGuard)

            val guardPendingRes = UniFindRepository.getPendingModerationItems()
            assertTrue(guardPendingRes.isSuccess)
            val guardPending = guardPendingRes.getOrNull()
            assertNotNull(guardPending)
            println("GUARD PENDING ITEMS: count=${guardPending!!.size}")
            assertTrue("Debe tener items pendientes de moderación en portería", guardPending.isNotEmpty())
        }
    }
}
