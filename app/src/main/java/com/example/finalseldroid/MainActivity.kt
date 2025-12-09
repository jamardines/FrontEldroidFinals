package com.example.finalseldroid

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat




class MainActivity : AppCompatActivity() {

    private val products = mutableListOf<Product>()
    private val itemViews = mutableListOf<View>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.menu_page)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val tpriceText = findViewById<TextView>(R.id.totalPriceText)
        val contentLayout = findViewById<GridLayout>(R.id.contentLayout)

        // Fetch products from backend (example with hardcoded for now)
        fetchProducts()

        // Dynamically create item views
        products.forEach { product ->
            val itemView = layoutInflater.inflate(R.layout.itemview, contentLayout, false)


            //default size of item view
            val params = GridLayout.LayoutParams().apply {
                width = 0 // 0dp so weight works
                height = GridLayout.LayoutParams.WRAP_CONTENT
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f) // equal weight for columns
                setMargins(8, 8, 8, 8)
            }
            itemView.layoutParams = params
            contentLayout.addView(itemView)
            itemViews.add(itemView)
        }

        updateUI()

        // Setup button listeners for each dynamic item
        products.forEachIndexed { index, product ->
            val itemView = itemViews[index]
            val decBtn = itemView.findViewById<Button>(R.id.decreaseBtn0)
            val incBtn = itemView.findViewById<Button>(R.id.increaseBtn0)
            val qntyEdit = itemView.findViewById<EditText>(R.id.quantityEdit0)
            val stockText = itemView.findViewById<TextView>(R.id.stockText0)

            decBtn.setOnClickListener {
                if (product.quantity > 0) {
                    product.quantity--
                    updateUI()
                }
            }

            incBtn.setOnClickListener {
                if (product.quantity < product.stock) {
                    product.quantity++
                    updateUI()
                } else {
                    Toast.makeText(this, "Out of Stock", Toast.LENGTH_SHORT).show()
                }
            }

            qntyEdit.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    val input = qntyEdit.text.toString().toIntOrNull() ?: 0
                    if (input > product.stock) {
                        AlertDialog.Builder(this)
                            .setTitle("Error")
                            .setMessage("Quantity cannot exceed stock (${product.stock})")
                            .setPositiveButton("OK", null)
                            .show()
                        product.quantity = 0
                    } else {
                        product.quantity = input
                    }
                    updateUI()
                }
            }
        }

        val addBtn = findViewById<Button>(R.id.Addbutton)
        addBtn.setOnClickListener { showOrderDialog() }

    }

    private fun fetchProducts() {
        // TODO: Replace with backend API call
        products.clear()
        products.addAll(
            listOf(
                Product(1, "Avocado", 10.0, R.drawable.avocado, 0, 5),
                Product(2, "Chicken Curry", 20.0, R.drawable.avocado, 0, 3),
                Product(3, "Salad", 30.0, R.drawable.avocado, 0, 10),
                Product(4, "Avocado", 10.0, R.drawable.avocado, 0, 20),
                Product(4, "Burger", 25.0, R.drawable.avocado, 0, 8),
                Product(5, "Pizza", 50.0, R.drawable.avocado, 0, 6),
                Product(6, "Pasta", 35.0, R.drawable.avocado, 0, 12),
                Product(7, "Sushi", 40.0, R.drawable.avocado, 0, 7),
                Product(8, "Ice Cream", 15.0, R.drawable.avocado, 0, 20),
                Product(9, "Cake", 45.0, R.drawable.avocado, 0, 9)
                // Add more dynamically from backend
            )
        )
    }

    private fun updateUI() {
        val tpriceText = findViewById<TextView>(R.id.totalPriceText)
        products.forEachIndexed { index, product ->
            val itemView = itemViews[index]
            val qntyEdit = itemView.findViewById<EditText>(R.id.quantityEdit0)
            val stockText = itemView.findViewById<TextView>(R.id.stockText0)
            val priceText = itemView.findViewById<TextView>(R.id.menuPrice)
            val nameText = itemView.findViewById<TextView>(R.id.menuName)

            priceText.text = "₱${product.price}"
            nameText.text = product.name
            qntyEdit.setText(product.quantity.toString())
            stockText.text = if (product.stock - product.quantity > 0)
                "Stock: ${product.stock - product.quantity}" else "Out of Stock"
        }

        val total = products.sumOf { it.price * it.quantity }
        tpriceText.text = "₱%.2f".format(total)
    }

    private fun showOrderDialog() {
        val orderedProducts = products.filter { it.quantity > 0 }
        if (orderedProducts.isEmpty()) {
            AlertDialog.Builder(this)
                .setTitle("No items")
                .setMessage("Please select at least one item before adding to order.")
                .setPositiveButton("OK", null)
                .show()
            return
        }

        val dialog = Dialog(this)
        dialog.setContentView(R.layout.confirmationorder)
        dialog.setCancelable(true)

        val window = dialog.window
        window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.85).toInt(),
            LinearLayout.LayoutParams.WRAP_CONTENT
        )


        val backBtn = dialog.findViewById<ImageButton>(R.id.buttonBack)
        backBtn.setOnClickListener { dialog.dismiss() }

        val orderItemsContainer = dialog.findViewById<LinearLayout>(R.id.orderItemsContainer)
        val totalPriceText = dialog.findViewById<TextView>(R.id.totalPrice)

        var total = 0.0
        orderedProducts.forEach { product ->
            val tv = TextView(this)
            tv.text = "${product.name} x${product.quantity} - ₱${product.price * product.quantity}"
            tv.textSize = 16f
            orderItemsContainer.addView(tv)
            total += product.price * product.quantity
        }
        totalPriceText.text = "₱%.2f".format(total)

        val confirmBtn = dialog.findViewById<Button>(R.id.buttonConfirmOrder)
        confirmBtn.setOnClickListener {
            orderedProducts.forEach { it.stock -= it.quantity }
            products.forEach { it.quantity = 0 }
            updateUI()
            dialog.dismiss()
            Toast.makeText(this, "Order placed successfully!", Toast.LENGTH_SHORT).show()
        }

        dialog.show()
    }
}
