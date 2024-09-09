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
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Actividad para actualizar los datos de un usuario desde administrador.
 */
class AdminUpdateActivity : AppCompatActivity() {

    private lateinit var etUpdateUserName: EditText
    private lateinit var etUpdateEmail: EditText
    private lateinit var etUpdateName: EditText
    private lateinit var etUpdateLastName: EditText
    private lateinit var etUserId: EditText
    private lateinit var btnSave: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_admin)

        etUpdateUserName = findViewById(R.id.etUpdateUserNameAdmin)
        etUpdateEmail = findViewById(R.id.etUpdateEmailAdmin)
        etUpdateName = findViewById(R.id.etUpdateNameAdmin)
        etUpdateLastName = findViewById(R.id.etUpdateLastNameAdmin)
        etUserId = findViewById(R.id.etUpdateUserIdAdmin)
        btnSave = findViewById(R.id.btnSaveAdmin)

        btnSave.setOnClickListener {
            val userName = etUpdateUserName.text.toString().trim().takeIf { it.isNotEmpty() }
            val email = etUpdateEmail.text.toString().trim().takeIf { it.isNotEmpty() }
            val firstName = etUpdateName.text.toString().trim().takeIf { it.isNotEmpty() }
            val lastName = etUpdateLastName.text.toString().trim().takeIf { it.isNotEmpty() }
            val userId = etUserId.text.toString().trim().takeIf { it.isNotEmpty() }

            val sharedPref = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
            val authToken = sharedPref.getString("authToken", null)
            if (authToken != null) {
                updateUser(authToken, userId, userName, email, firstName, lastName)
            } else {
                Toast.makeText(this, "No se encontró el token de autenticación.", Toast.LENGTH_SHORT).show()
            }
        }
    }
/**
 * Método para actualizar los datos del usuario desde administrador.
 *
 * @param authToken El token de autenticación del usuario.
 * @param userId El ID del usuario a actualizar.
 * @param userName El nuevo nombre de usuario.
 * @param email El nuevo correo electrónico.
 * @param firstName El nuevo nombre.
 * @param lastName El nuevo apellido.
 * */

    private fun updateUser(
        authToken: String,
        userId: String?,
        userName: String?,
        email: String?,
        firstName: String?,
        lastName: String?
    ) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:5002/") // Asegúrate de que esta es la URL base correcta
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        val fieldsToUpdate = mutableMapOf<String, String?>().apply {
            userName?.let { put("user_name", it) }
            email?.let { put("email", it) }
            firstName?.let { put("first_name", it) }
            lastName?.let { put("last_name", it) }
        }

        if (fieldsToUpdate.isNotEmpty() && userId != null) {
            val updateUser = AdminUpdateUser(
                authToken,
                userId,
                fieldsToUpdate["user_name"],
                fieldsToUpdate["email"],
                fieldsToUpdate["first_name"],
                fieldsToUpdate["last_name"]
            )

            val call = apiService.updateAdminUser(updateUser)

            call.enqueue(object : Callback<AdminUpdateResponse> {
                override fun onResponse(
                    call: Call<AdminUpdateResponse>,
                    response: Response<AdminUpdateResponse>
                ) {
                    if (response.isSuccessful) {
                        val updateResponse = response.body()
                        Toast.makeText(
                            this@AdminUpdateActivity,
                            updateResponse?.msg ?: "Usuario actualizado con éxito",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Toast.makeText(
                            this@AdminUpdateActivity,
                            "Error al actualizar usuario: $errorBody",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<AdminUpdateResponse>, t: Throwable) {
                    Toast.makeText(
                        this@AdminUpdateActivity,
                        "Error en la red: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        } else {
            Toast.makeText(
                this@AdminUpdateActivity,
                "No hay cambios para actualizar o falta el ID del usuario",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}