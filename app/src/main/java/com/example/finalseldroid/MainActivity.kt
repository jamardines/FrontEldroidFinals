package com.example.finalseldroid

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

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

        // 9 item views
        val itemViews = listOf(
            findViewById<View>(R.id.item1),
            findViewById<View>(R.id.item2),
            findViewById<View>(R.id.item3),
            findViewById<View>(R.id.item4),
            findViewById<View>(R.id.item5),
            findViewById<View>(R.id.item6),
            findViewById<View>(R.id.item7),
            findViewById<View>(R.id.item8),
            findViewById<View>(R.id.item9)
        )

        // list of products
        val products = listOf(
            Product(1, "Avocado", 10.0, R.drawable.avocado, quantity = 0, stock = 5),
            Product(2, "Chicken Curry", 20.0, R.drawable.avocado, quantity = 0, stock = 3),
            Product(3, "Salad", 30.0, R.drawable.avocado, quantity = 0, stock = 10),
            Product(4, "Avocado", 10.0, R.drawable.avocado, quantity = 0, stock = 20),
            Product(5, "Avocado", 10.0, R.drawable.avocado, quantity = 0, stock = 8),
            Product(6, "Avocado", 10.0, R.drawable.avocado, quantity = 0, stock = 4),
            Product(7, "Avocado", 10.0, R.drawable.avocado, quantity = 0, stock = 9),
            Product(8, "Avocado", 10.0, R.drawable.avocado, quantity = 0, stock = 13),
            Product(9, "Avocado", 10.0, R.drawable.avocado, quantity = 0, stock = 27),

        )

        // initialize UI
        updateUI(products, itemViews, tpriceText)

        products.forEachIndexed { index, product ->
            val itemView = itemViews[index]
            val decBtn = itemView.findViewById<Button>(R.id.decreaseBtn0)
            val incBtn = itemView.findViewById<Button>(R.id.increaseBtn0)
            val qntyEdit = itemView.findViewById<EditText>(R.id.quantityEdit0)
            val stockText = itemView.findViewById<TextView>(R.id.stockText0)

            // decrease quantity
            decBtn.setOnClickListener {
                if (product.quantity > 0) {
                    product.quantity--
                    updateUI(products, itemViews, tpriceText)
                }
            }

            // increase quantity
            incBtn.setOnClickListener {
                if (product.quantity < product.stock) {
                    product.quantity++
                    updateUI(products, itemViews, tpriceText)
                } else {
                    print("Out of Stock")
                }
            }

            // when user edits quantity manually
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
                    updateUI(products, itemViews, tpriceText)
                }
            }
        }

        val addBtn = findViewById<Button>(R.id.Addbutton)
        addBtn.setOnClickListener {
            val orderedProducts = products.filter { it.quantity > 0 }

            if (orderedProducts.isEmpty()) {
                AlertDialog.Builder(this)
                    .setTitle("No items")
                    .setMessage("Please select at least one item before adding to order.")
                    .setPositiveButton("OK", null)
                    .show()
                return@setOnClickListener
            }

            // TODO: connect to backend to fetch updated stock before showing dialog
            // GET

            val dialog = Dialog(this)
            dialog.setContentView(R.layout.confirmationorder)
            dialog.setCancelable(true)

            val backBtn = dialog.findViewById<ImageButton>(R.id.buttonBack)
            backBtn.setOnClickListener {
                dialog.dismiss()
            }

            val orderItemsContainer = dialog.findViewById<LinearLayout>(R.id.orderItemsContainer)
            val totalPriceText = dialog.findViewById<TextView>(R.id.totalPrice)

            val window = dialog.window
            window?.setLayout(
                (resources.displayMetrics.widthPixels * 0.85).toInt(), // 85% of screen width
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

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

                // TODO: send orderedProducts to backend to save order
                // POST

                Toast.makeText(this, "Order placed successfully!", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
                products.forEach { it.quantity = 0 }
                updateUI(products, itemViews, tpriceText)

                // TODO: optionally, call backend to update stock after order is placed
                // PATCH /products/{id}/stock
            }

            dialog.show()
        }

        // TODO: if you want to fetch product list from backend instead of hardcoding,
        // you can call API here and populate `products` list
        // Example: GET /products -> update products list and call updateUI()
    }

    // private function only in item view
    private fun updateUI(products: List<Product>, itemViews: List<View>, tpriceText: TextView) {
        products.forEachIndexed { index, product ->
            val itemView = itemViews[index]
            val qntyEdit = itemView.findViewById<EditText>(R.id.quantityEdit0)
            val stockText = itemView.findViewById<TextView>(R.id.stockText0)
            val priceText = itemView.findViewById<TextView>(R.id.menuPrice)
            val nameText = itemView.findViewById<TextView>(R.id.menuName)

            priceText.text = "₱${product.price}"
            nameText.text = product.name
            qntyEdit.setText(product.quantity.toString())
            stockText.text =
                if (product.stock - product.quantity > 0) "Stock: ${product.stock - product.quantity}" else "Out of Stock"
        }

        val total = products.sumOf { it.price * it.quantity }
        tpriceText.text = "₱%.2f".format(total)

    }

}
