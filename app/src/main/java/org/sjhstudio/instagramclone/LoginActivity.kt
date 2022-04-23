package org.sjhstudio.instagramclone

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.messaging.FirebaseMessaging
import org.sjhstudio.instagramclone.MyApplication.Companion.auth
import org.sjhstudio.instagramclone.MyApplication.Companion.firestore
import org.sjhstudio.instagramclone.MyApplication.Companion.userId
import org.sjhstudio.instagramclone.MyApplication.Companion.userUid
import org.sjhstudio.instagramclone.databinding.ActivityLoginBinding
import org.sjhstudio.instagramclone.util.BaseActivity
import java.lang.Exception

class LoginActivity: BaseActivity() {

    private lateinit var binding: ActivityLoginBinding

    private var googleSignInClient: GoogleSignInClient? = null  // google
    private var callbackManager: CallbackManager? = null    // facebook

    override fun onStart() {
        super.onStart()
        moveMainActivity(auth?.currentUser) // 자동로그인
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        // Facebook callback
        callbackManager = CallbackManager.Factory.create()

        // listener
        binding.signBtn.setOnClickListener { signInAndSignUp() }
        binding.googleLoginBtn.setOnClickListener { googleLogin() }
        binding.facebookLoginBtn.setOnClickListener { facebookLogin() }
    }

    /**
     * Google sign-in
     */
    private fun googleLogin() {
        // First. make GoogleSignInClient using GoogleSignInOptions
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.google_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Second. request signInIntent and set firebase auth using result
        val signInIntent = googleSignInClient?.signInIntent
        googleSignInResult.launch(signInIntent)
    }

    /**
     * Facebook sign-in
     */
    private fun facebookLogin() {
        callbackManager?.let { cm ->
            LoginManager.getInstance().apply {
                logInWithReadPermissions(
                    this@LoginActivity,
                    cm,
                    listOf("email", "public_profile")
                )

                registerCallback(callbackManager, object: FacebookCallback<LoginResult> {
                    override fun onSuccess(result: LoginResult) {
                        println("xxx FacebookCallback : onSuccess()")
                        handleFacebookAccessToken(result.accessToken)
                    }

                    override fun onCancel() { println("xxx FacebookCallback : onCancel()") }

                    override fun onError(error: FacebookException) { println("xxx FacebookCallback : onError()") }
                })
            }
        }
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
                        Snackbar.make(binding.signBtn, task.exception?.message ?: "회원가입 중 문제가 발생했습니다.", 1000).show()
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
                    Snackbar.make(binding.signBtn, task.exception?.message ?: "로그인 오류가 발생했습니다.", 1000).show()
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
                    Snackbar.make(binding.signBtn, task.exception?.message ?: "구글 로그인 에러", 1000).show()
                }
            }
    }

    /**
     * Firebase auth sign-in with credential
     * (with Facebook Access Token)
     */
    private fun handleFacebookAccessToken(token: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(token.token)

        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    // Login(Facebook)
                    moveMainActivity(task.result?.user)
                } else {
                    // Error
                    Snackbar.make(binding.signBtn, task.exception?.message ?: "Facebook 로그인 에러", 1000).show()
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
            userUid = auth?.currentUser?.uid
            userId = auth?.currentUser?.email
            requestToken()
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }
    }

    /**
     * Request token
     * (로그인마다 토큰생성 -> Firestore에 저장)
     */
    private fun requestToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if(!task.isSuccessful) {
                println("xxx Failed to request token")
                return@addOnCompleteListener
            }

            val token = task.result
            val map = mutableMapOf<String, Any>()
            map["pushToken"] = token
            firestore?.collection("pushtokens")
                ?.document(userUid!!)
                ?.set(map)
        }
    }

}