package com.joanet.pizzalgustmobile

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Actividad para obtener y mostrar la lista de pizzas disponibles en la aplicación.
 */

class GetAllPizzasActivity :  AppCompatActivity() {

    private lateinit var rvPizzaList: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pizza_list)

        rvPizzaList = findViewById(R.id.rv_pizzaList)
        rvPizzaList.layoutManager = LinearLayoutManager(this)

        fetchPizzas()
    }

    /**
     * Método privado para obtener la lista de pizzas del servidor y mostrarlas en el RecyclerView.
     */
    private fun fetchPizzas() {
        val sharedPref = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val authToken = sharedPref.getString("authToken", null)
        if (authToken != null) {
            val apiService = RetrofitClient.createApiService()
            val call = apiService.getAllPizzas(GetAllPizzasRequest(authToken))

            call.enqueue(object : Callback<GetAllPizzas> {
                override fun onResponse(call: Call<GetAllPizzas>, response: Response<GetAllPizzas>) {
                    if (response.isSuccessful) {
                        val pizzas = response.body()?.pizzas ?: emptyList()
                        rvPizzaList.adapter = PizzaListAdapter(pizzas)
                    } else {
                        Toast.makeText(this@GetAllPizzasActivity, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<GetAllPizzas>, t: Throwable) {
                    Toast.makeText(this@GetAllPizzasActivity, "Error en la red: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(this, "No se encontró el token de autenticación.", Toast.LENGTH_SHORT).show()
        }
    }


}









