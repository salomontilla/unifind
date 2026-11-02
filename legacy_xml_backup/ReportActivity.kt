package com.salomontilla.frontunifind

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.salomontilla.frontunifind.databinding.ActivityReportBinding

/**
 * =============================================================================
 * PANTALLA 4: ReportActivity (Publicar Reporte de Hallazgo o Pérdida)
 * 
 * Conceptos Clave para Sustentación:
 * 1. Wizard de Formulario por Pasos: Datos generales, atributos, punto seguro
 *    de custodia institucional y pregunta confidencial anti-estafa.
 * 2. Toggle Dinámico: Alterna visualmente entre 'Perdí un Objeto' y 'Encontré un Objeto'.
 * =============================================================================
 */
class ReportActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReportBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
    }

    private fun setupListeners() {
        binding.btnBackReport.setOnClickListener {
            finish()
        }

        // Toggle Perdí vs Encontré
        binding.btnOptionLost.setOnClickListener {
            Toast.makeText(this, "Modo: Reportar Objeto Perdido", Toast.LENGTH_SHORT).show()
        }

        binding.btnOptionFound.setOnClickListener {
            Toast.makeText(this, "Modo: Reportar Objeto Encontrado", Toast.LENGTH_SHORT).show()
        }

        // Botón Publicar en la Comunidad
        binding.btnPublishReport.setOnClickListener {
            val title = binding.etReportTitle.text?.toString()?.trim().orEmpty()
            if (title.isEmpty()) {
                binding.etReportTitle.error = "Ingresa un título descriptivo"
                binding.etReportTitle.requestFocus()
                return@setOnClickListener
            }

            Toast.makeText(
                this,
                "¡Reporte publicado exitosamente en la Red Uniautónoma!",
                Toast.LENGTH_LONG
            ).show()
            finish()
        }

        // Botón Guardar Borrador
        binding.btnSaveDraft.setOnClickListener {
            Toast.makeText(this, "Borrador guardado localmente", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
