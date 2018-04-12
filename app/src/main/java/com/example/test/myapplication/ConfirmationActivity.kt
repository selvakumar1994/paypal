package com.example.test.myapplication

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast

import org.json.JSONException
import org.json.JSONObject

class ConfirmationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirmation)


        val intent = intent


        try {
            var jsonDetails: JSONObject? = null
            try {
                jsonDetails = JSONObject(intent.getStringExtra("PaymentDetails"))
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            //Displaying payment details
            showDetails(jsonDetails!!.getJSONObject("response"), intent.getStringExtra("PaymentAmount"))
        } catch (e: JSONException) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }

    }

    @Throws(JSONException::class)
    private fun showDetails(jsonDetails: JSONObject, paymentAmount: String) {
        //Views
        val textViewId = findViewById(R.id.paymentId) as TextView
        val textViewStatus = findViewById(R.id.paymentStatus) as TextView
        val textViewAmount = findViewById(R.id.paymentAmount) as TextView

        //Showing the details from json object
        textViewId.text = jsonDetails.getString("id")
        textViewStatus.text = jsonDetails.getString("state")
        textViewAmount.text = "$paymentAmount USD"
    }
}
