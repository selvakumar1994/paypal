package com.example.test.myapplication

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText

import com.paypal.android.sdk.payments.PayPalConfiguration
import com.paypal.android.sdk.payments.PayPalPayment
import com.paypal.android.sdk.payments.PayPalService
import com.paypal.android.sdk.payments.PaymentActivity
import com.paypal.android.sdk.payments.PaymentConfirmation

import org.json.JSONException

import java.math.BigDecimal

class MainActivity : AppCompatActivity() {
    private var buttonPay: Button? = null
    private var editTextAmount: EditText? = null
    private var paymentAmount: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        buttonPay = findViewById<View>(R.id.buttonPay) as Button
        editTextAmount = findViewById<View>(R.id.editTextAmount) as EditText
        val intent = Intent(this, PayPalService::class.java)
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config)
        startService(intent)
        buttonPay!!.setOnClickListener { getPayment() }
    }

    public override fun onDestroy() {
        stopService(Intent(this, PayPalService::class.java))
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        //If the result is from paypal
        if (requestCode == PAYPAL_REQUEST_CODE) {

            //If the result is OK i.e. user has not canceled the payment
            if (resultCode == Activity.RESULT_OK) {
                //Getting the payment confirmation
                val confirm = data.getParcelableExtra<PaymentConfirmation>(PaymentActivity.EXTRA_RESULT_CONFIRMATION)

                //if confirmation is not null
                if (confirm != null) {
                    //Getting the payment details
                    var paymentDetails: String? = null
                    try {
                        paymentDetails = confirm.toJSONObject().toString(4)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                    Log.i("paymentExample", paymentDetails)

                    //Starting a new activity for the payment details and also putting the payment details with intent
                    startActivity(Intent(this, ConfirmationActivity::class.java)
                            .putExtra("PaymentDetails", paymentDetails)
                            .putExtra("PaymentAmount", paymentAmount))

                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("paymentExample", "The user canceled.")
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i("paymentExample", "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.")
            }
        }
    }

    private fun getPayment() {

        paymentAmount = editTextAmount!!.text.toString()

        //Creating a paypalpayment
        val payment = PayPalPayment(BigDecimal(paymentAmount.toString()), "USD", "Test mode by rajesh",
                PayPalPayment.PAYMENT_INTENT_SALE)

        //Creating Paypal Payment activity intent
        val intent = Intent(this, PaymentActivity::class.java)

        //putting the paypal configuration to the intent
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config)

        //Puting paypal payment to the intent
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment)

        //Starting the intent activity for result
        //the request code will be used on the method onActivityResult
        startActivityForResult(intent, PAYPAL_REQUEST_CODE)

    }

    companion object {
        val PAYPAL_REQUEST_CODE = 123
        private val config = PayPalConfiguration()
                .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
                .clientId(PayPalConfig.PAYPAL_CLIENT_ID)
    }
}
