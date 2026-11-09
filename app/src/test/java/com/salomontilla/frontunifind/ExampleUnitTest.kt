package com.salomontilla.frontunifind

import com.salomontilla.frontunifind.data.model.CreateFoundExpressRequest
import com.salomontilla.frontunifind.data.model.ItemDto
import com.salomontilla.frontunifind.data.network.KtorClient
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import org.junit.Test
import org.junit.Assert.*

class ExampleUnitTest {
    @Test
    fun testObjectSerializationInPost() = runBlocking {
        val testClient = HttpClient(CIO) {
            install(ContentNegotiation) {
                json(KtorClient.jsonConfig)
            }
            defaultRequest {
                url("http://localhost:3000/api/v1/")
                contentType(ContentType.Application.Json)
                header("Accept", "application/json")
            }
        }

        val req = CreateFoundExpressRequest(
            title = "Test Verificación Completa",
            type = "found",
            category = "Electrónica",
            custodyType = "PORTERIA",
            campusZone = "Biblioteca Central",
            locationReference = "Piso 2",
            photoUrl = "https://images.unsplash.com/photo-1587145820266-a5951ee6f620",
            postedBy = "670c1f1e1c9d440000a10001"
        )

        val resp = testClient.post("items") {
            setBody(req)
        }
        assertEquals(201, resp.status.value)
        val text = resp.bodyAsText()
        val el = KtorClient.jsonConfig.parseToJsonElement(text)
        assertTrue(el is JsonObject && el.containsKey("data"))
        val item = KtorClient.jsonConfig.decodeFromJsonElement<ItemDto>((el as JsonObject)["data"]!!)
        assertNotNull(item.code)
        assertEquals("pending_review", item.status)
    }
}
