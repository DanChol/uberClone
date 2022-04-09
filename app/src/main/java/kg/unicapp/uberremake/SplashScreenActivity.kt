package kg.unicapp.uberremake

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.firebase.ui.auth.AuthMethodPickerLayout
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import java.util.*
import java.util.concurrent.TimeUnit

class SplashScreenActivity : AppCompatActivity() {

    companion object{
        private val LOGIN_REQUEST_CODE = 7171
    }

    private lateinit var providers: List<AuthUI.IdpConfig>
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var listener: FirebaseAuth.AuthStateListener

    private lateinit var database: FirebaseDatabase
    private lateinit var driverInfoRef: DatabaseReference


    override fun onStart() {
        super.onStart()
        delaySplashScreen()
    }

    private fun delaySplashScreen() {
        Completable.timer(3, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
            .subscribe(
                {
                    firebaseAuth.addAuthStateListener (listener)
                }
            )
    }

    override fun onStop() {
        super.onStop()
        if (firebaseAuth != null && listener != null ) firebaseAuth.removeAuthStateListener(listener)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        initObj()

    }

    private fun initObj() {
        database = FirebaseDatabase.getInstance()
        driverInfoRef = database.getReference(Comm.DRIVER_INFO_REFERENCE)

        providers = Arrays.asList(
            AuthUI.IdpConfig.PhoneBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        firebaseAuth = FirebaseAuth.getInstance()
        listener = FirebaseAuth.AuthStateListener { myFirebaseAuth ->
            val user = myFirebaseAuth.currentUser
            if (user != null)
                Toast.makeText(this@SplashScreenActivity, "Добро пожаловать, " +user.uid, Toast.LENGTH_SHORT).show()
            else

                showLoginLayout()
        }
    }

    private fun showLoginLayout() {
        val authMetodPickerLayout = AuthMethodPickerLayout.Builder(R.layout.activity_sign)
            .setPhoneButtonId(R.id.phone_btn)
            .setGoogleButtonId(R.id.google_btn)
            .build()

        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAuthMethodPickerLayout(authMetodPickerLayout)
                .setTheme(R.style.LoginTheme)
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(false)
                .build(),
            LOGIN_REQUEST_CODE
        )
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LOGIN_REQUEST_CODE){
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK){
                val user = FirebaseAuth.getInstance().currentUser
            }
            else
                Toast.makeText(this@SplashScreenActivity, "" +response!!.error!!.message, Toast.LENGTH_SHORT).show()

        }
    }
}