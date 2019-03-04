package no.hacking.here

import android.content.Context
import android.os.Bundle
import android.view.View
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity;
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val userNameEditText: EditText by lazy {
        findViewById<EditText>(R.id.userNameEditView)
    }

    private val passwordEditText: EditText by lazy {
        findViewById<EditText>(R.id.passwordEditView)
    }

    private val goButton: Button by lazy {
        findViewById<Button>(R.id.goButton)
    }

    private val db: UserDatabase by lazy {
        UserDatabase(this)
    }

    private val alert: AlertDialog.Builder by lazy {
        AlertDialog.Builder(this)
    }

    private lateinit var userName: String
    private lateinit var password: String
    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        try {
            db.storeUser(0, "admin", "password")
            handleUserActions()
        } catch (ex: Exception) {
            showError(ex)
        }
    }

    private fun handleUserActions() {
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Boop", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        userNameEditText.onFocusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                try {
                    userName = userNameEditText.text.toString()
                    var message = "$userName not found,\nwould you like to create an account?"
                    if (db.userExists(userName)) {
                        message = "Welcome back $userName!"
                    }
                    alertUser(message, "Hello,")
                } catch (ex: Exception) {
                    showError(ex)
                }
            }
        }

        goButton.setOnClickListener { view ->
            try {
                dismissKeyboard()
                password = passwordEditText.text.toString()
                passwordEditText.text.clear()

                if (db.passwordisCorrect(userName, password)) {
                    alertUser("Logging in now", "You did it!")
                } else {
                    alertUser("Wrong password, please try again.", "Whoops")
                }
            } catch (ex: Exception) {
                showError(ex)
            }
        }
    }

    private fun dismissKeyboard() {
        (getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)
            ?.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    private fun showError(exception: Exception) {
        exception.printStackTrace()
        alertUser(exception.localizedMessage)
    }

    private fun alertUser(message: String,
                          title: String = "Sorry...\n Failed with error:",
                          buttonText: String = "Try again") {
        alert.setTitle(title)
            .setMessage("$message\n")
            .setNeutralButton(buttonText, null)
            .show()
    }
}
