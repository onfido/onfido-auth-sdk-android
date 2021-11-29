package com.onfido.sampleapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.onfido.authentication.contract.OnfidoAuthentication
import com.onfido.authentication.contract.OnfidoAuthenticationConfig
import com.onfido.authentication.contract.OnfidoAuthenticationImpl
import java.util.*


class MainActivity : Activity() {

    companion object {
        private const val ONFIDO_AUTH_REQUEST_CODE = 300
    }

    private lateinit var onfidoAuth: OnfidoAuthentication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // TODO: Call your backend to get `sdkToken` https://github.com/onfido/onfido-android-sdk#41-sdk-token
        val sdkToken = "YOUR_SDK_TOKEN"

        val config = OnfidoAuthenticationConfig.builder(this@MainActivity)
            .withSdkToken(sdkToken)
            .withRetryCount(2)
//            .withLocale(Locale.GERMAN)
            .withUserConsentScreen()
            .build()

        onfidoAuth = OnfidoAuthenticationImpl(this)

        findViewById<Button>(R.id.launchAuthButton).setOnClickListener {
            onfidoAuth.startActivityForResult(this, ONFIDO_AUTH_REQUEST_CODE, config)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ONFIDO_AUTH_REQUEST_CODE) {
            onfidoAuth.handleActivityResult(
                resultCode,
                data,
                object : OnfidoAuthentication.ResultListener {
                    override fun onUserCompleted(authResult: OnfidoAuthentication.AuthenticationResult) {
                        val result =
                            "token: " + authResult.token + " verified: " + authResult.verified
                        showToast(result)
                    }

                    override fun onUserExited(exitCode: OnfidoAuthentication.ExitCode) {
                        showToast("onUserExited: " + exitCode.name)
                    }

                    override fun onError(exception: OnfidoAuthentication.AuthException) {
                        showToast("onError: " + exception.localizedMessage)
                    }
                })
        }
    }

    private fun showToast(result: String) {
        Toast.makeText(this@MainActivity, result, Toast.LENGTH_LONG).show()
    }
}