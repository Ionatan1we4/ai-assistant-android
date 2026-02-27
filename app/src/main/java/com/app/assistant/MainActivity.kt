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
}
