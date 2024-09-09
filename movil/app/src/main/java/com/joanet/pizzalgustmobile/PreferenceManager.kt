import android.content.Context
import android.content.SharedPreferences

/**
 * Singleton que gestiona las preferencias compartidas de la aplicación.
 */
object PreferenceManager {
    private const val PREF_NAME = "MyAppPrefs"
    private const val AUTH_TOKEN_KEY = "AuthToken"

    /**
     * Inicializa el objeto PreferenceManager.
     *
     * @param context El contexto de la aplicación.
     */
    fun init(context: Context) {
        getSharedPreferences(context)
    }


    /**
     * Obtiene el objeto SharedPreferences para la aplicación.
     *
     * @param context El contexto de la aplicación.
     * @return El objeto SharedPreferences.
     */
    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }


    /**
     * Guarda el token de autenticación en las preferencias compartidas.
     *
     * @param context El contexto de la aplicación.
     * @param authToken El token de autenticación que se va a guardar.
     */
    fun saveAuthToken(context: Context, authToken: String) {
        val editor = getSharedPreferences(context).edit()
        editor.putString(AUTH_TOKEN_KEY, authToken)
        editor.apply()
    }

    /**
     * Obtiene el token de autenticación almacenado en las preferencias compartidas.
     *
     * @param context El contexto de la aplicación.
     * @return El token de autenticación almacenado.
     */
    fun getAuthToken(context: Context): String {
        return getSharedPreferences(context).getString(AUTH_TOKEN_KEY, "") ?: ""
    }

    /**
     * Elimina el token de autenticación de las preferencias compartidas.
     *
     * @param context El contexto de la aplicación.
     */
    fun clearAuthToken(context: Context) {
        val editor = getSharedPreferences(context).edit()
        editor.remove(AUTH_TOKEN_KEY)
        editor.apply()
    }
}
