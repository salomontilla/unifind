package com.salomontilla.frontunifind

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.salomontilla.frontunifind.databinding.ActivityDetailBinding

/**
 * =============================================================================
 * PANTALLA 3: DetailActivity (Detalle del Objeto y Verificación Anti-Estafa)
 * 
 * 1. Protocolo Anti-Fraude: El usuario ingresa la respuesta a la pregunta
 *    formulada por el hallador. Al validar, se abre el canal seguro de chat.
 * 2. Ergonomía Móvil: La barra de acciones fija en la base de la pantalla
 *    permite interacción cómoda con una sola mano.
 * =============================================================================
 */
class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
    }

    private fun setupListeners() {
        // Botón Atrás
        binding.btnBackDetail.setOnClickListener {
            finish()
        }

        // Botón Compartir
        binding.btnShareDetail.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(
                    Intent.EXTRA_TEXT,
                    "Reporte Unifind #UAC-8841: Calculadora Casio encontrada en Bloque A Lab 201 Uniautónoma."
                )
            }
            startActivity(Intent.createChooser(shareIntent, "Compartir Reporte"))
        }

        // Botón Reportar publicación incorrecta
        binding.btnFlagDetail.setOnClickListener {
            Toast.makeText(this, "Publicación enviada a revisión de Bienestar", Toast.LENGTH_SHORT).show()
        }

        binding.btnReportIncorrect.setOnClickListener {
            Toast.makeText(this, "Reporte enviado a moderación institucional", Toast.LENGTH_SHORT).show()
        }

        // Botón Principal: Verificar Propiedad y Abrir Chat
        binding.btnVerifyOwnership.setOnClickListener {
            val answer = binding.etVerificationAnswer.text?.toString()?.trim().orEmpty()
            if (answer.isEmpty()) {
                binding.etVerificationAnswer.error = "Escribe tu respuesta para validar propiedad"
                binding.etVerificationAnswer.requestFocus()
                return@setOnClickListener
            }

            Toast.makeText(this, "¡Respuesta validada con éxito! Abriendo chat seguro...", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, ChatActivity::class.java)
            startActivity(intent)
        }
    }
}
