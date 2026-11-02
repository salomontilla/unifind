package com.salomontilla.frontunifind

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.salomontilla.frontunifind.databinding.ActivityMessagesBinding

/**
 * =============================================================================
 * PANTALLA 5: MessagesActivity (Bandeja de Entrada de Mensajes y Custodias)
 * 
 * Conceptos Clave para Sustentación:
 * 1. Gestión de Estados: Conversaciones activas con badges dinámicos.
 * 2. Insets Handling: Padding dinámico para la barra de navegación del sistema.
 * 3. Badge en BottomNav: Notificador de mensajes pendientes.
 * =============================================================================
 */
class MessagesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMessagesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessagesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWindowInsets()
        setupListeners()
        setupBottomNavigation()
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.bottomNavigationMessages) { view, insets ->
            val navBars = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            view.updatePadding(bottom = navBars.bottom)
            insets
        }
    }

    private fun setupListeners() {
        // Al tocar la conversación 1 (Calculadora Casio), abrir el Chat Privado
        binding.cardMessageItem1.setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            startActivity(intent)
        }

        binding.cardMessageItem2.setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            startActivity(intent)
        }

        binding.cardMessageItem3.setOnClickListener {
            // Caso ya devuelto: abrir modal de valoración
            val ratingDialog = RatingBottomSheetDialog()
            ratingDialog.show(supportFragmentManager, RatingBottomSheetDialog.TAG)
        }

        // Enlace Mapa de Puntos Seguros
        binding.btnViewSafePoints.setOnClickListener {
            Toast.makeText(
                this,
                "Puntos Seguros: Cafetería Bloque A, Bienestar La Casona y Portería Principal",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigationMessages.selectedItemId = R.id.nav_messages

        val badge = binding.bottomNavigationMessages.getOrCreateBadge(R.id.nav_messages)
        badge.isVisible = true
        badge.number = 2
        badge.backgroundColor = ContextCompat.getColor(this, R.color.brand_blue_primary)
        badge.badgeTextColor = ContextCompat.getColor(this, R.color.white)

        binding.bottomNavigationMessages.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_objects -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.nav_report -> {
                    val intent = Intent(this, ReportActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_messages -> true
                R.id.nav_honor -> {
                    val ratingDialog = RatingBottomSheetDialog()
                    ratingDialog.show(supportFragmentManager, RatingBottomSheetDialog.TAG)
                    true
                }
                R.id.nav_admin -> {
                    Toast.makeText(this, "Panel de Moderación Institucional", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
    }
}
