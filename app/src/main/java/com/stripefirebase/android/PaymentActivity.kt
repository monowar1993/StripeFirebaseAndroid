package com.stripefirebase.android

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.functions.FirebaseFunctions
import com.stripe.android.model.Token
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_payment.*

class PaymentActivity : AppCompatActivity() {

    companion object {
        const val TAG = "PaymentActivity"

        const val EXTRA_TOKEN_ID_KEY = "token_id"
    }

    private lateinit var cardTokenId: String

    private val firebaseFunctions by lazy { FirebaseFunctions.getInstance() }

    private val progressDialog by lazy { SpotsDialog.Builder().setContext(this).build() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)
        setSupportActionBar(toolbar)

        supportActionBar?.let { actionBar ->
            actionBar.setHomeButtonEnabled(true)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        cardTokenId = intent.getStringExtra(EXTRA_TOKEN_ID_KEY)

        tvToken.text = ("Your card token ID - %s. Use this token to make payment on stripe. Stripe Charge API is being used here.").format(cardTokenId)

        btnRetrieveToken.setOnClickListener { retrieveToken() }

        btnPay.setOnClickListener { pay() }
    }

    private fun retrieveToken() {
        progressDialog.show()
        val data = hashMapOf(
            "token_id" to cardTokenId
        )
        firebaseFunctions.getHttpsCallable("retrieveStripeToken").call(data).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val httpsCallableResult = task.result
                val responseData = httpsCallableResult?.data as Map<*, *>
                Log.d(TAG, responseData.toString())
                Log.d(TAG, responseData["token"].toString())
                val token = Token.fromString(responseData["token"].toString())
                Log.d(TAG, token?.id)
                Log.d(TAG, token?.card?.last4)
                progressDialog.dismiss()
            } else {
                task.exception?.printStackTrace()
                progressDialog.dismiss()
            }
        }
    }

    private fun pay() {
        progressDialog.show()
        val data = hashMapOf(
            "token_id" to cardTokenId,
            "amount" to 10 * 100,
            "currency" to "usd",
            "description" to "Firebase Example"
        )
        firebaseFunctions.getHttpsCallable("stripeChargeCallable").call(data).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val httpsCallableResult = task.result
                val responseData = httpsCallableResult?.data as Map<*, *>
                Log.d(TAG, responseData.toString())
                Log.d(TAG, responseData["charge"].toString())
                progressDialog.dismiss()
            } else {
                task.exception?.printStackTrace()
                progressDialog.dismiss()
            }
        }
    }
}
