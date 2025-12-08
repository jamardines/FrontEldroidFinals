package com.example.finalseldroid

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ReceiptDialog : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.confirmationorder) //receipt XML


        val backBtn = findViewById<ImageButton>(R.id.buttonBack)
        backBtn.setOnClickListener {
            finish() // just close this activity and go back
        }

        // Handle Confirm button
        val confirmButton = findViewById<Button>(R.id.buttonConfirmOrder)
        confirmButton.setOnClickListener {
            Toast.makeText(this, "Order placed successfully!", Toast.LENGTH_SHORT).show()
            finish() // close this activity
        }


    }
}
