package com.salomontilla.frontunifind

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.salomontilla.frontunifind.data.local.SessionManager
import com.salomontilla.frontunifind.data.model.CreateFoundExpressRequest
import com.salomontilla.frontunifind.data.model.CreateLostDetailedRequest
import com.salomontilla.frontunifind.data.network.KtorClient
import com.salomontilla.frontunifind.data.repository.UniFindRepository
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    @Test
    fun testCreateReportFromMobileAppToDatabase() = runBlocking {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        SessionManager.init(appContext)

        val expressTimestamp = System.currentTimeMillis() % 10000
        val expressTitle = "Mochila Institucional con Cuadernos Móvil #$expressTimestamp"
        val expressReq = CreateFoundExpressRequest(
            title = expressTitle,
            type = "found",
            category = "Ropa y Accesorios",
            custodyType = "PORTERIA",
            campusZone = "Portería Principal",
            locationReference = "Dejada en ventanilla de portería",
            brandAndColor = "Mochila Totto Negra con bordado UAC",
            photoUrl = "https://images.unsplash.com/photo-1553062407-98eeb64c6a62",
            postedBy = "670c1f1e1c9d440000a10001"
        )

        val expressResult = UniFindRepository.createFoundExpress(expressReq)
        println("INSTRUMENTED TEST EXPRESS RESULT: isSuccess=${expressResult.isSuccess}")
        if (expressResult.isFailure) {
            println("FAILURE EXCEPTION: ${expressResult.exceptionOrNull()?.message}")
            expressResult.exceptionOrNull()?.printStackTrace()
        }
        assertTrue("El reporte express debe guardarse exitosamente en el backend", expressResult.isSuccess)
        val createdExpress = expressResult.getOrNull()
        assertNotNull(createdExpress)
        println("CREATED ITEM IN DB: code=${createdExpress?.code}, title=${createdExpress?.title}, status=${createdExpress?.status}")
        assertTrue("El código generado debe comenzar con UAC-", createdExpress!!.code.startsWith("UAC-"))
        assertEquals("pending_review", createdExpress.status)

        // 2. Reporte Móvil 2: Modo Detallado Pérdida (Dueño Estudiante)
        val lostTitle = "Carné Estudiantil y Billetera Móvil #$expressTimestamp"
        val lostReq = CreateLostDetailedRequest(
            title = lostTitle,
            type = "lost",
            category = "Documentos / Carnés",
            distinguishingMarks = "Billetera de cuero con sticker de Sistemas y firma institucional",
            brandAndColor = "Cuero Marrón",
            campusZone = "Biblioteca Central",
            locationReference = "Cubículos individuales Piso 2",
            photoUrl = null,
            postedBy = "670c1f1e1c9d440000a10001"
        )

        val lostResult = UniFindRepository.createLostDetailed(lostReq)
        assertTrue("El reporte detallado de pérdida debe guardarse en el backend", lostResult.isSuccess)
        val createdLost = lostResult.getOrNull()
        assertNotNull(createdLost)
        println("CREATED LOST ITEM IN DB: code=${createdLost?.code}, title=${createdLost?.title}, status=${createdLost?.status}")
        assertTrue("El código generado debe comenzar con UAC-", createdLost!!.code.startsWith("UAC-"))
        assertEquals("pending_review", createdLost.status)
    }
}
