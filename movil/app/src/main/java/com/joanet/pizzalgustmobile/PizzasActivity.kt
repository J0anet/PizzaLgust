package com.joanet.pizzalgustmobile

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * Actividad principal que muestra las opciones para agregar una nueva pizza o ver la lista de pizzas.
 */
class PizzasActivity : AppCompatActivity() {

    private lateinit var btnAddPizza: FloatingActionButton
    private lateinit var btnListPizza: FloatingActionButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pizzas)

        btnAddPizza = findViewById(R.id.addPizza)
        btnListPizza = findViewById(R.id.findPizzas)

        btnAddPizza.setOnClickListener {
            val intent = Intent(this@PizzasActivity, CreatePizzaActivity::class.java)
            startActivity(intent)
        }
        btnListPizza.setOnClickListener{
            val intent = Intent(this@PizzasActivity,GetAllPizzasActivity::class.java)
            startActivity(intent)
        }
    }

}

