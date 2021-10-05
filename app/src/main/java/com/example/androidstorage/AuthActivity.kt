package com.example.androidstorage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast

class AuthActivity : AppCompatActivity() {
    lateinit var uidEditText: EditText
    lateinit var pwdEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        uidEditText = findViewById(R.id.useridE)
        pwdEditText = findViewById(R.id.passwordE)
    }

    fun submitClick(view: View) {
        val userName = uidEditText.text.toString()
        val password = pwdEditText.text.toString()

        if(userName.isNotEmpty() && password.isNotEmpty()){
            // save these credentials
            val pref = getSharedPreferences("credentials", MODE_PRIVATE)      // made our data private

            val editor = pref.edit()       // get editor for our data
            editor.putString("userid", userName)    // we put data in file using editor
            editor.putString("password", password)     //(keyname_in_form_of_string, data_that_we_want_to_store)
            editor.commit()   // this is how our data is physically is returning to a file

            Toast.makeText(this, "Credentials are stored", Toast.LENGTH_SHORT).show()
        }
        else
            Toast.makeText(this, "Please enter all data", Toast.LENGTH_SHORT).show()
    }
}