package org.sjhstudio.instagramclone

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import org.sjhstudio.instagramclone.databinding.ActivityLoginBinding
import java.lang.Exception

class LoginActivity: BaseActivity() {

    private lateinit var binding: ActivityLoginBinding
    private var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        auth = FirebaseAuth.getInstance()

        binding.signBtn.setOnClickListener {
            signInAndSignUp()
        }
    }

    /**
     * Firebase sign-in and sign-up
     */
    fun signInAndSignUp() {
        try {
            auth?.createUserWithEmailAndPassword(binding.emailEt.text.toString().trim(), binding.pwEt.text.toString())
                ?.addOnCompleteListener { task ->
                    if(task.isSuccessful) {
                        // Creating user account
                        moveMainActivity(task.result?.user)
                    } else if(task.exception?.message?.contains("The email address is already in use by another account.") == true) {
                        // Login if user has account
                        signIn()
                    } else {
                        // Error
                        Snackbar.make(binding.signBtn, task.exception?.message ?: "회원가입 에러", 1000).show()
                    }
                }
        } catch(e: Exception) {
            e.printStackTrace()
            if(e.message?.contains("Given String is empty or null") == true) {
                Snackbar.make(binding.signBtn, "이메일 또는 비밀번호를 입력해주세요.", 1000).show()
            }
        }
    }

    /**
     * Firebase signIn
     */
    fun signIn() {
        auth?.signInWithEmailAndPassword(binding.emailEt.text.toString().trim(), binding.pwEt.text.toString())
            ?.addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    // Login
                    moveMainActivity(task.result?.user)
                } else {
                    // Error
                    Snackbar.make(binding.signBtn, task.exception?.message ?: "로그인 에러", 1000).show()
                }
            }
    }

    fun moveMainActivity(user: FirebaseUser?) {
        if(user != null) {
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }
    }
}