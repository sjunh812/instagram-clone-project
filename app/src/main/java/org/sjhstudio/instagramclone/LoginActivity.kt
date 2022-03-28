package org.sjhstudio.instagramclone

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import org.sjhstudio.instagramclone.databinding.ActivityLoginBinding
import java.lang.Exception

class LoginActivity: BaseActivity() {

    private lateinit var binding: ActivityLoginBinding

    private var auth: FirebaseAuth? = null
    private var googleSignInClient: GoogleSignInClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        auth = FirebaseAuth.getInstance()

        binding.signBtn.setOnClickListener {
            signInAndSignUp()
        }

        binding.googleLoginBtn.setOnClickListener {
            googleLogin()
        }
    }

    /**
     * Google sign-in
     */
    private fun googleLogin() {
        // first. make GoogleSignInClient using GoogleSignInOptions
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.google_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // second. request signInIntent and set firebase auth using result
        val signInIntent = googleSignInClient?.signInIntent
        googleSignInResult.launch(signInIntent)
    }

    /**
     * Firebase auth sign-in and sign-up
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
     * Firebase auth sign-in
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

    /**
     * Firebase auth sign-in with credential
     * (with Google)
     */
    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    // Login(Google)
                    moveMainActivity(task.result?.user)
                } else {
                    // Error
                    Snackbar.make(binding.signBtn, task.exception?.message ?: "구글로그인 에러", 1000).show()
                }
            }
    }

    private val googleSignInResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { ar ->
        println("xxx googleSignInResult!! : ${ar.resultCode}")
        if(ar.resultCode == RESULT_OK) {
            ar.data?.let {
                val result = Auth.GoogleSignInApi.getSignInResultFromIntent(it)
                result?.let { r ->
                    if(r.isSuccess) {
                        firebaseAuthWithGoogle(r.signInAccount)
                    }
                }
            }
        } else {
            println("xxx googleSignInResult error")
        }
    }

    private fun moveMainActivity(user: FirebaseUser?) {
        if(user != null) {
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }
    }
}