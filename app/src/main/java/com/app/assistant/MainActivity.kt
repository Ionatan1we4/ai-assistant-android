package com.app.assistant

import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.play.core.install.model.ActivityResult
import com.google.android.play.core.appupdate.AppUpdateOptions
import android.app.KeyguardManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.app.assistant.ui.screen.SetupUI
import com.app.assistant.viewmodel.MainViewModel
import com.app.assistant.viewmodel.MainViewModelFactory

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels{
        MainViewModelFactory(application, intent.getBooleanExtra("speak", false))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        allowOnLockScreen()
        // Inițializare Firebase Auth
        auth = Firebase.auth

// Configurare Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

// Verifică utilizator
        val currentUser = auth.currentUser
        if (currentUser == null) {
            setContent {
                LoginScreen(
                    onSignInClick = { signInWithGoogle() }
                )
            }
            return
        }
        private fun signInWithGoogle() {
            googleSignInLauncher.launch(googleSignInClient.signInIntent)
        }

        private fun firebaseAuthWithGoogle(idToken: String) {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            auth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d("Auth", "success")
                        recreate()
                    } else {
                        Log.w("Auth", "failure", task.exception)
                    }
                }
        }
        val appUpdateManager = AppUpdateManagerFactory.create(this)
    
    val updateLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            Log.d("Update", "Update flow started successfully")
        } else {
            Log.d("Update", "Update flow failed: ${result.resultCode}")
        }
    }
    
    appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
        if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
            && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
        ) {
            appUpdateManager.startUpdateFlowForResult(
                appUpdateInfo,
                updateLauncher,
                AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE).build()
            )
        }
    }
        enableEdgeToEdge()
        setContent {
            SetupUI(viewModel)
        }
    }

    private fun allowOnLockScreen() {
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            Log.w("Auth", "Google sign in failed", e)
        }
    }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) { // Android 8.1+
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }

        try {
            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                if (keyguardManager.isKeyguardLocked) {
                    keyguardManager.requestDismissKeyguard(this, null)
                }
            }
        }catch (ex: Exception){
            ex.message?.let { Log.d("Exception Occurred", it) }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.shutdownResources()
    }
    @Composable
    fun LoginScreen(onSignInClick: () -> Unit) {
        Box(
            modifier = Modifier
               .fillMaxSize()
               .background(Color(0xFFF8F9FF)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Knowledge of AI",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF8A5CF5)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Autentifică-te pentru a continua",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = onSignInClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF8A5CF5)
                    ),
                    shape = RoundedCornerShape(50.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_google),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Autentificare cu Google", color = Color.White)
                }
            }
        }
    }
