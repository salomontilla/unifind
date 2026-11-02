package com.salomontilla.frontunifind

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.salomontilla.frontunifind.databinding.ActivityChatBinding

/**
 * =============================================================================
 * PANTALLA 6: ChatActivity (Chat Privado y Coordinación de Entrega Kime)
 * 
 * Conceptos Clave para Sustentación:
 * 1. Mensajería Segura: Validación previa de identidad institucional antes
 *    de habilitar el canal de conversación.
 * 2. Cierre del Ciclo de Vida: El botón 'Confirmar Entrega de Objeto' despliega
 *    el modal de valoración [RatingBottomSheetDialog].
 * =============================================================================
 */
class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
    }

    private fun setupListeners() {
        binding.btnBackChat.setOnClickListener {
            finish()
        }

        // Botón Confirmar Entrega de Objeto -> Abre Pantalla 7 (Modal de Valoración)
        binding.btnConfirmDeliveryChat.setOnClickListener {
            val ratingDialog = RatingBottomSheetDialog()
            ratingDialog.show(supportFragmentManager, RatingBottomSheetDialog.TAG)
        }

        // Botón Enviar Mensaje
        binding.btnSendMessage.setOnClickListener {
            val message = binding.etChatMessage.text?.toString()?.trim().orEmpty()
            if (message.isNotEmpty()) {
                binding.etChatMessage.text?.clear()
                Toast.makeText(this, "Mensaje seguro enviado", Toast.LENGTH_SHORT).show()
            }
        }

        // Botones de Adjuntos
        binding.btnAttachCamera.setOnClickListener {
            Toast.makeText(this, "Adjuntar evidencia fotográfica", Toast.LENGTH_SHORT).show()
        }

        binding.btnAttachLocation.setOnClickListener {
            Toast.makeText(this, "Ubicación fijada: Cafetería Bloque A", Toast.LENGTH_SHORT).show()
        }
    }
}
