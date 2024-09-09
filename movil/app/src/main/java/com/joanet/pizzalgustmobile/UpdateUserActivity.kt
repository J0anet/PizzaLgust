package com.joanet.pizzalgustmobile

import android.content.Context
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/**
 * Actividad para actualizar la información del usuario.
 * Permite al usuario modificar su nombre de usuario, correo electrónico,
 * nombre y apellido.
 */
class UpdateUserActivity : AppCompatActivity() {

    private lateinit var etUpdateUserName: EditText
    private lateinit var etUpdateEmail: EditText
    private lateinit var etUpdateName: EditText
    private lateinit var etUpdateLastName: EditText
    private lateinit var btnSave: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_user)

        etUpdateUserName = findViewById(R.id.etUpdateUserName)
        etUpdateEmail = findViewById(R.id.etUpdateEmail)
        etUpdateName = findViewById(R.id.etUpdateName)
        etUpdateLastName = findViewById(R.id.etUpdateLastName)
        btnSave = findViewById(R.id.btnSave)

        btnSave.setOnClickListener {
            val userName = etUpdateUserName.text.toString().trim().takeIf { it.isNotEmpty() }
            val email = etUpdateEmail.text.toString().trim().takeIf { it.isNotEmpty() }
            val firstName = etUpdateName.text.toString().trim().takeIf { it.isNotEmpty() }
            val lastName = etUpdateLastName.text.toString().trim().takeIf { it.isNotEmpty() }

            val sharedPref = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
            val authToken = sharedPref.getString("authToken", null)
            if (authToken != null) {
                updateUser(authToken, userName, email, firstName, lastName)
            } else {
                Toast.makeText(this, "No se encontró el token de autenticación.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Actualiza la información del usuario en el servidor.
     *
     * @param authToken El token de autenticación del usuario.
     * @param userName El nuevo nombre de usuario.
     * @param email El nuevo correo electrónico.
     * @param firstName El nuevo nombre.
     * @param lastName El nuevo apellido.
     */

    private fun updateUser(
        authToken: String,
        userName: String?,
        email: String?,
        firstName: String?,
        lastName: String?
    ) {
        val apiService = RetrofitClient.createApiService()

        val fieldsToUpdate = mutableMapOf<String, String?>().apply {
            userName?.let { put("user_name", it) }
            email?.let { put("email", it) }
            firstName?.let { put("first_name", it) }
            lastName?.let { put("last_name", it) }
        }

        if (fieldsToUpdate.isNotEmpty()) {
            val updateUser = UpdateUser(
                authToken,
                fieldsToUpdate["user_name"],
                fieldsToUpdate["email"],
                fieldsToUpdate["first_name"],
                fieldsToUpdate["last_name"]
            )

            val call = apiService.updateUser(updateUser)

            call.enqueue(object : Callback<UpdateResponse> {
                override fun onResponse(
                    call: Call<UpdateResponse>,
                    response: Response<UpdateResponse>
                ) {
                    if (response.isSuccessful) {
                        val updateResponse = response.body()
                        Toast.makeText(
                            this@UpdateUserActivity,
                            updateResponse?.msg ?: "Usuario actualizado con éxito",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Toast.makeText(
                            this@UpdateUserActivity,
                            "Error al actualizar usuario: $errorBody",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<UpdateResponse>, t: Throwable) {
                    Toast.makeText(
                        this@UpdateUserActivity,
                        "Error en la red: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        } else {
            Toast.makeText(
                this@UpdateUserActivity,
                "No hay cambios para actualizar",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}

