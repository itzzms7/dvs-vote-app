package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.DvsErrorNotification
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.MainVotingScreen
import com.example.ui.screens.ReceiptScreen
import com.example.ui.theme.DvsBackground
import com.example.ui.theme.DvsTheme
import com.example.util.DvsSoundManager
import com.example.viewmodel.VoterScreen
import com.example.viewmodel.VoterViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DvsTheme {
                DvsAppRoot()
            }
        }
    }
}

@Composable
fun DvsAppRoot(viewModel: VoterViewModel = viewModel()) {
    val context = LocalContext.current
    val soundManager = remember(context) { DvsSoundManager(context) }
    DisposableEffect(soundManager) {
        viewModel.soundManager = soundManager
        onDispose {
            soundManager.release()
            viewModel.soundManager = null
        }
    }

    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // Handle back button logically per screen
    BackHandler(enabled = state.currentScreen != VoterScreen.LOGIN) {
        when {
            state.showConfirmPopup -> viewModel.dismissVoteConfirmation()
            state.showProfilePopup -> viewModel.dismissProfile()
            state.showWalletPopup -> viewModel.dismissWalletPopup()
            state.currentScreen == VoterScreen.RECEIPT -> viewModel.backToHomeFromReceipt()
            state.currentScreen == VoterScreen.VOTING -> viewModel.signOut()
        }
    }

    Scaffold(
        containerColor = DvsBackground,
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                // Refresh 30-minute inactivity timer on any user touch event
                detectTapGestures(onTap = { viewModel.recordActivity() })
            }
            .testTag("dvs_app_root")
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(DvsBackground)
        ) {
            // Three primary screens
            when (state.currentScreen) {
                VoterScreen.LOGIN -> LoginScreen(state = state, viewModel = viewModel)
                VoterScreen.VOTING -> MainVotingScreen(state = state, viewModel = viewModel)
                VoterScreen.RECEIPT -> ReceiptScreen(state = state, viewModel = viewModel)
            }

            // Unified small error notification component placed at the top
            DvsErrorNotification(
                message = state.errorMessage,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
            )
        }
    }
}
