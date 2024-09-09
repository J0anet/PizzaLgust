package com.joanet.pizzalgustmobile

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Actividad para crear nuevos usuarios.
 */
class CreateUsersActivity : AppCompatActivity() {

    private lateinit var etUserName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etFirstName: EditText
    private lateinit var etLastName: EditText
    private lateinit var etPassword: EditText
    private lateinit var addButton: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)

        etUserName = findViewById(R.id.etUpdateUserName)
        etEmail = findViewById(R.id.etUpdateEmail)
        etFirstName = findViewById(R.id.etUpdateName)
        etLastName = findViewById(R.id.etUpdateLastName)
        etPassword = findViewById(R.id.etPassword)
        addButton = findViewById(R.id.add_button)

        addButton.setOnClickListener {
            val userName = etUserName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val firstName = etFirstName.text.toString().trim()
            val lastName = etLastName.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (userName.isEmpty() || email.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || password.isEmpty()) {
                Toast.makeText(
                    this@CreateUsersActivity,
                    "Por Favor completa todos los campos",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                createUser(userName, email, firstName, lastName, password)
            }

        }
    }

    /**
     * Método para crear un nuevo usuario.
     *
     * @param userName El nombre de usuario del nuevo usuario.
     * @param email El correo electrónico del nuevo usuario.
     * @param firstName El nombre del nuevo usuario.
     * @param lastName El apellido del nuevo usuario.
     * @param password La contraseña del nuevo usuario.
     */

    private fun createUser(
        userName: String,
        email: String,
        firstName: String,
        lastName: String,
        password: String
    ) {


        val apiService = RetrofitClient.createApiService()

        val model = CreateUser(userName, email, firstName, lastName, password)

        val call = apiService.createUser(model)

        call.enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful) {
                    // Aquí se muestra el mensaje de éxito del servidor
                    val userResponse = response.body()
                    Toast.makeText(
                        this@CreateUsersActivity,
                        userResponse?.msg ?: "Usuario creado con éxito",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.i("CreateUser", "Respuesta del servidor: ${userResponse?.msg}")
                } else {
                    // Aquí se maneja el caso de respuesta no exitosa, mostrando el cuerpo del error
                    val errorBody = response.errorBody()?.string()
                    Toast.makeText(
                        this@CreateUsersActivity,
                        "Error al crear usuario: $errorBody",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("CreateUser", "Error al crear usuario: $errorBody")
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                // Aquí se maneja el caso de fallo en la llamada, como un error de red
                Toast.makeText(
                    this@CreateUsersActivity,
                    "Error en la red: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("CreateUser", "Error en la red: ${t.message}")
            }
        })
    }
}
