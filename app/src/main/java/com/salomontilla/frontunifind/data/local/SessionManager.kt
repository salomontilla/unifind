package com.salomontilla.frontunifind.data.local

import android.content.Context
import android.content.SharedPreferences
import com.salomontilla.frontunifind.data.model.UserDto

/**
 * =============================================================================
 * SessionManager: Gestor Local de Sesión y Token de Autenticación (SharedPreferences)
 * HU-11: Token de Sesión y Persistencia Segura
 * =============================================================================
 */
object SessionManager {

    private const val PREF_NAME = "unifind_session_prefs"

    private const val KEY_TOKEN = "session_token"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_USER_EMAIL = "user_email"
    private const val KEY_USER_NAME = "user_name"
    private const val KEY_STUDENT_CODE = "student_code"
    private const val KEY_USER_ROLE = "user_role"
    private const val KEY_HONOR_POINTS = "honor_points"
    private const val KEY_IS_LOGGED_IN = "is_logged_in"

    private var prefs: SharedPreferences? = null

    fun init(context: Context) {
        if (prefs == null) {
            prefs = context.applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        }
    }

    private fun requirePrefs(): SharedPreferences {
        return prefs ?: throw IllegalStateException("SessionManager no ha sido inicializado. Llama a SessionManager.init(context) primero.")
    }

    /**
     * Guarda la sesión del usuario autenticado de forma atómica en disco.
     */
    fun saveSession(user: UserDto, token: String? = null) {
        val p = prefs ?: return
        p.edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, true)
            putString(KEY_USER_ID, user.realId)
            putString(KEY_USER_EMAIL, user.institutionalEmail)
            putString(KEY_USER_NAME, user.displayName)
            putString(KEY_STUDENT_CODE, user.studentCode)
            putString(KEY_USER_ROLE, user.role)
            putInt(KEY_HONOR_POINTS, user.honorPoints)
            if (token != null) {
                putString(KEY_TOKEN, token)
            } else if (!p.contains(KEY_TOKEN)) {
                putString(KEY_TOKEN, "bearer-uac-${user.studentCode.ifBlank { "token" }}")
            }
            apply()
        }
    }

    fun getToken(): String? {
        return prefs?.getString(KEY_TOKEN, null)
    }

    fun getUser(): UserDto? {
        val p = prefs ?: return null
        if (!isLoggedIn()) return null
        val id = p.getString(KEY_USER_ID, "") ?: ""
        val email = p.getString(KEY_USER_EMAIL, "") ?: ""
        val name = p.getString(KEY_USER_NAME, null)
        val code = p.getString(KEY_STUDENT_CODE, "") ?: ""
        val role = p.getString(KEY_USER_ROLE, "student") ?: "student"
        val points = p.getInt(KEY_HONOR_POINTS, 200)

        val resolvedId = when {
            id.isNotBlank() && !id.startsWith("user-") -> id
            code == "GUARD-001" -> "670c1f1e1c9d440000a10003"
            code == "ADMIN-001" -> "670c1f1e1c9d440000a10004"
            else -> "670c1f1e1c9d440000a10001"
        }

        return UserDto(
            id = resolvedId,
            name = name ?: "Carlos Andrés Muñoz",
            institutionalEmail = email.ifBlank { "carlos.munoz@uniautonoma.edu.co" },
            studentCode = code.ifBlank { "2023-1001" },
            role = role,
            honorPoints = points
        )
    }

    fun isLoggedIn(): Boolean {
        return prefs?.getBoolean(KEY_IS_LOGGED_IN, false) ?: false
    }

    fun clearSession() {
        prefs?.edit()?.clear()?.apply()
    }
}
