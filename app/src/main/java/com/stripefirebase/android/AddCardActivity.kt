package com.stripefirebase.android

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.stripe.android.Stripe
import com.stripe.android.TokenCallback
import com.stripe.android.model.Card
import com.stripe.android.model.Token
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_add_card.*

class AddCardActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "AddCardActivity"
    }

    private val stripe by lazy { Stripe(applicationContext, BuildConfig.STRIPE_PUBLISHABLE_KEY) }

    private val progressDialog by lazy { SpotsDialog.Builder().setContext(this).build() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_card)

        setSupportActionBar(toolbar)
        supportActionBar?.let { actionBar ->
            actionBar.setHomeButtonEnabled(true)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        btnGenerateToken.setOnClickListener { view ->
            val card = cardWidget.card
            if (card == null) {
                Snackbar.make(view, "Invalid Card Data", Snackbar.LENGTH_LONG).show()
            } else {
                createToken(card)
            }
        }
    }

    private fun createToken(card: Card) {
        progressDialog.show()
        stripe.createToken(card, object : TokenCallback {
            override fun onSuccess(token: Token) {
                progressDialog.dismiss()
                Log.d(TAG, "Token Id - ${token.id}")
                startActivity(Intent(this@AddCardActivity, PaymentActivity::class.java).apply {
                    putExtra(PaymentActivity.EXTRA_TOKEN_ID_KEY, token.id)
                })
            }

            override fun onError(error: Exception) {
                progressDialog.dismiss()
                error.printStackTrace()
                Snackbar.make(layoutRoot, error.toString(), Snackbar.LENGTH_LONG).show()
            }
        })
    }
}
