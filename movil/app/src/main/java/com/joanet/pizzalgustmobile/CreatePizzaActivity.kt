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
 * Esta clase representa la actividad de creación de pizzas en la aplicación PizzaLgust.
 * Permite a los usuarios crear nuevas pizzas proporcionando información como el nombre, el precio y la descripción.
 */
class CreatePizzaActivity : AppCompatActivity() {

    private lateinit var etPizzaName: EditText
    private lateinit var etPrice: EditText
    private lateinit var etDescription: EditText
    private lateinit var btnAddPizza: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_pizzas)

        etPizzaName = findViewById(R.id.etPizzaName)
        etPrice = findViewById(R.id.et_Price)
        etDescription = findViewById(R.id.etDescription)
        btnAddPizza = findViewById(R.id.addPizza1)

        btnAddPizza.setOnClickListener {
            val pizzaName = etPizzaName.text.toString().trim()
            val price = etPrice.text.toString().toFloatOrNull()
            val description = etDescription.text.toString().trim()

            if (pizzaName.isNotEmpty() && price != null && description.isNotEmpty()) {
                val sharedPref = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
                val authToken = sharedPref.getString("authToken", null)
                if (authToken != null) {
                    createPizza(authToken, pizzaName, price, description)
                } else {
                    Toast.makeText(this, "Por favor, inicie sesión primero.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Todos los campos son obligatorios y el precio debe ser un número.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Método privado para enviar una solicitud de creación de pizza al servidor.
     *
     * @param token Token de autenticación del usuario.
     * @param name Nombre de la pizza.
     * @param price Precio de la pizza.
     * @param descr Descripción de la pizza.
     */
    private fun createPizza(token: String, name: String, price: Float, descr: String) {


        val createPizzaRequest = CreatePizza(token, name, price, descr)
        val call = RetrofitClient.createApiService().createPizza(createPizzaRequest)

        call.enqueue(object : Callback<ResponseCreatePizza> {
            override fun onResponse(call: Call<ResponseCreatePizza>, response: Response<ResponseCreatePizza>) {
                if (response.isSuccessful) {
                    val createResponse = response.body()
                    Toast.makeText(
                        this@CreatePizzaActivity,
                        createResponse?.msg ?: "Pizza creada con éxito",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    val errorBody = response.errorBody()?.string()
                    Toast.makeText(
                        this@CreatePizzaActivity,
                        "Error al crear pizza: $errorBody",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ResponseCreatePizza>, t: Throwable) {
                Toast.makeText(
                    this@CreatePizzaActivity,
                    "Error en la red: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}
