package com.joanet.pizzalgustmobile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/**
 * Actividad que representa la pantalla de usuario.
 */
class UserActivity : AppCompatActivity() {

    private lateinit var btnSalir2: Button
    private lateinit var tvFirstName2: TextView
    private lateinit var tvMessage2: TextView
    private lateinit var btnUpdate: ImageView
    //private lateinit var btnPizzas: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_user)

        btnSalir2 = findViewById(R.id.btnSalirUser)
        tvFirstName2 = findViewById(R.id.tvFirstNameUser)
        tvMessage2 = findViewById(R.id.tvMessageUser)
        btnUpdate = findViewById(R.id.updateUser)
        //btnPizzas = findViewById(R.id.pizza)

        // Obtiene los datos pasados desde la actividad anterior
        val authToken = intent.getStringExtra("authToken")
        val firstName = intent.getStringExtra("firstName")
        val message = intent.getStringExtra("message")

        // Muestra los datos recibidos en las vistas correspondientes
        tvFirstName2.text = "Hola $firstName!"
        tvMessage2.text = message


        // Configura el listener para el botón de salir
        btnSalir2.setOnClickListener {
            authToken?.let { token ->
                logoutUser(token)
            } ?: run {
                Toast.makeText(this, "Error: Token no encontrado", Toast.LENGTH_SHORT).show()
            }
        }
        btnUpdate.setOnClickListener {
            val intent = Intent(this@UserActivity, UpdateUserActivity::class.java)
            startActivity(intent)
        }
        /*btnPizzas.setOnClickListener {
            val intent = Intent(this@UserActivity, UserPizzasActivity::class.java)
            startActivity(intent)
        }*/
    }




    /**
     * Método para realizar el logout del usuario.
     *
     * @param token El token de autenticación del usuario.
     */
    private fun logoutUser(token: String) {
        Log.d("DEBUG", "Iniciando logout con token: $token")

        // Configura la instancia de Retrofit
        val logoutService = RetrofitClient.createLogoutService()

        // Realiza la llamada al servicio de logout
        val call = logoutService.logout(Logout(token, ""))

        // Maneja la respuesta de la llamada asíncrona
        call.enqueue(object : Callback<Logout> {
            override fun onResponse(call: Call<Logout>, response: Response<Logout>) {
                if (response.isSuccessful) {
                    val responseData = response.body()
                    responseData?.let {
                        Toast.makeText(this@UserActivity, it.msg, Toast.LENGTH_SHORT).show()
                        Log.d("DEBUG", "Logout exitoso: ${it.msg}")

                    }
                    // Redirige a la actividad de inicio de sesión
                    val intent = Intent(this@UserActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // Muestra un mensaje de error si la respuesta no fue exitosa
                    Toast.makeText(
                        this@UserActivity,
                        "Error en el logout: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("ERROR", "Error en el logout: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Logout>, t: Throwable) {
                // Muestra un mensaje de error en caso de fallo en la llamada de logout
                Toast.makeText(
                    this@UserActivity,
                    "Fallo en la llamada de logout: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("ERROR", "Fallo en la llamada de logout: ${t.message}")
            }
        })
    }
}
