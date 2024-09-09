package com.joanet.pizzalgustmobile

import PreferenceManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Actividad principal de la aplicación.
 * Esta actividad maneja el inicio de sesión de los usuarios.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnEntrar: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var tvRegistration: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etEmail = findViewById(R.id.etLogin)
        etPassword = findViewById(R.id.etPassword)
        btnEntrar = findViewById(R.id.btnEntrar1)
        progressBar = findViewById(R.id.idLoadingPB)
        tvRegistration = findViewById(R.id.tvRegistration)

        btnEntrar.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT)
                    .show()
            } else {
                progressBar.visibility = View.VISIBLE
                login(email, password)
            }
        }

        tvRegistration.setOnClickListener{
            val intent = Intent(this@MainActivity,CreateUsersActivity::class.java)
            startActivity(intent)
        }
        PreferenceManager.init(applicationContext)
    }


    /**
     * Método para iniciar sesión en la aplicación.
     * Realiza una llamada a la API para autenticar al usuario.
     * @param email El correo electrónico del usuario.
     * @param password La contraseña del usuario.
     */
    private fun login(email: String, password: String) {

        val apiService = RetrofitClient.createApiService()

        val model = Login(email, password, "", "", "", "", false, "","")

        val call = apiService.getUserData(model)

        call.enqueue(object : Callback<Login> {
            override fun onResponse(call: Call<Login>, response: Response<Login>) {
                if (response.isSuccessful) {
                    val dataModel = response.body()
                    if (dataModel?.token.isNullOrEmpty()) {
                        Toast.makeText(this@MainActivity, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                    } else {
                        val sharedPref = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
                        with(sharedPref.edit()) {
                            putString("authToken", dataModel?.token)
                            apply()
                        }

                        Toast.makeText(this@MainActivity, "¡Correcto!", Toast.LENGTH_SHORT).show()
                        Log.i("INFO","¡Correcto! Token recibido: ${dataModel?.token}")
                        Log.i("INFO","¡dame nombre: ${dataModel?.first_name}")
                        Log.i("INFO","¡Correcto! isadmin: ${dataModel?.is_admin}")
                        Log.i("INFO","¡Correcto! usertype: ${dataModel?.user_type}")


                        val gson = Gson()
                        val jsonString = gson.toJson(dataModel)
                        Log.d("JSON Response", jsonString)

                        // Verifica si el usuario es un administrador o no
                        if (dataModel?.is_admin == true) {
                            val intent = Intent(this@MainActivity, AdminActivity::class.java)
                            intent.putExtra("authToken", dataModel.token)
                            intent.putExtra("firstName", dataModel.first_name)
                            intent.putExtra("message", dataModel.msg)

                            startActivity(intent)
                        } else {
                            val intent = Intent(this@MainActivity, UserActivity::class.java)
                            intent.putExtra("authToken", dataModel?.token)
                            intent.putExtra("firstName", dataModel?.first_name)
                            intent.putExtra("message", dataModel?.msg)

                            startActivity(intent)
                        }
                        finish()
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
                progressBar.visibility = View.GONE
            }

            override fun onFailure(call: Call<Login>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
            }
        })
    }
}
