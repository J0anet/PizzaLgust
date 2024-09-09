package com.joanet.pizzalgustmobile



import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * Adaptador para la lista de pizzas.
 * Este adaptador se encarga de mostrar la lista de pizzas en un RecyclerView.
 *
 * @property pizzas La lista de pizzas a mostrar.
 */
class PizzaListAdapter(private val pizzas: List<Pizza>) : RecyclerView.Adapter<PizzaListAdapter.PizzaViewHolder>() {

    /**
     * Crea una nueva instancia de PizzaViewHolder.
     *
     * @param parent El ViewGroup padre en el que se añadirá la nueva vista.
     * @param viewType El tipo de vista de la nueva vista.
     * @return Una nueva instancia de PizzaViewHolder.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PizzaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pizza, parent, false)
        return PizzaViewHolder(view)
    }

    /**
     * Enlaza los datos de la pizza en la posición especificada con la vista correspondiente.
     *
     * @param holder El PizzaViewHolder que representa la vista de la pizza.
     * @param position La posición de la pizza en la lista.
     */
    override fun onBindViewHolder(holder: PizzaViewHolder, position: Int) {
        val pizza = pizzas[position]
        holder.bind(pizza)
    }

    /**

     * Obtiene el número total de pizzas en la lista.
     *
     * @return El número total de pizzas.
     */
    override fun getItemCount(): Int = pizzas.size

    /**
     * Clase que representa la vista de cada elemento de pizza en el RecyclerView.
     *
     * @property itemView La vista que representa el elemento de pizza.
     */
    class PizzaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.tvPizzaName)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.tvPizzaDescription)
        private val priceTextView: TextView = itemView.findViewById(R.id.tvPizzaPrice)

        fun bind(pizza: Pizza) {
            nameTextView.text = "Nombre: \n${pizza.name}"
            descriptionTextView.text = "Descripción: \n${pizza.description}"
            priceTextView.text = "Precio: \n${pizza.price}"
        }
    }
}

