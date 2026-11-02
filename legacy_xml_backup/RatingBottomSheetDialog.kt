package com.salomontilla.frontunifind

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.salomontilla.frontunifind.databinding.BottomSheetRatingBinding

/**
 * =============================================================================
 * PANTALLA 7: RatingBottomSheetDialog (Modal de Valoración y Confirmación)
 * 
 * Conceptos Clave para Sustentación:
 * 1. BottomSheetDialogFragment: Patrón de diseño recomendado en Android para
 *    modales anclados en la parte inferior, con soporte de gestos de deslizamiento.
 * 2. Gamificación e Impacto Institucional: Reconocimiento por Puntos de Honor
 *    y actualización del ranking estudiantil en campus.
 * =============================================================================
 */
class RatingBottomSheetDialog : BottomSheetDialogFragment() {

    private var _binding: BottomSheetRatingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetRatingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Botón Cerrar
        binding.btnCloseRating.setOnClickListener {
            dismiss()
        }

        // Botón Finalizar y Cerrar Reporte
        binding.btnFinishReport.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "¡Valoración guardada! +100 Puntos de Honor acreditados.",
                Toast.LENGTH_LONG
            ).show()
            dismiss()
        }

        // Enlace Constancia PDF
        binding.btnDownloadReceipt.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "Generando constancia institucional de devolución en PDF...",
                Toast.LENGTH_SHORT
            ).show()
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "RatingBottomSheetDialog"
    }
}
