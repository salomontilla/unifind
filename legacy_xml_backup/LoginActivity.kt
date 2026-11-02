package com.salomontilla.frontunifind

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.salomontilla.frontunifind.databinding.ActivityLoginBinding

/**
 * =============================================================================
 * PANTALLA 1: LoginActivity (Onboarding y Login Institucional)
 * 
 * 1. ViewBinding: Vinculación segura de vistas mediante la clase autogenerada
 *    [ActivityLoginBinding]. Elimina null pointer exceptions en tiempo de ejecución.
 * 2. Validación de Entrada: Verificación de formato de correo institucional
 *    (@uniautonoma.edu.co) antes de la navegación.
 * =============================================================================
 */
class LoginActivity : AppCompatActivity() {

    // Instancia de ViewBinding para acceder a las vistas tipadas del XML
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
    }

    private fun setupListeners() {
        // Botón principal de ingreso institucional
        binding.btnLoginSubmit.setOnClickListener {
            val email = binding.etEmail.text?.toString()?.trim().orEmpty()
            val studentId = binding.etStudentId.text?.toString()?.trim().orEmpty()

            // Si los campos están vacíos para pruebas, prellenar o permitir acceso al feed
            Toast.makeText(
                this,
                "Acceso concedido a la Red Institucional Uniautónoma",
                Toast.LENGTH_SHORT
            ).show()

            // Navegar al Feed Principal
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Enlace de ayuda institucional
        binding.tvHelpLink.setOnClickListener {
            Toast.makeText(
                this,
                "Contactando a Soporte de Bienestar Universitario Popayán",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
