package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.navigation.AppNavigation
import com.example.ui.profile.ProfileViewModel
import com.example.ui.profile.ProfileViewModelFactory
import com.example.ui.theme.AppTheme
import com.google.firebase.analytics.FirebaseAnalytics

class MainActivity : ComponentActivity() {
  private lateinit var firebaseAnalytics: FirebaseAnalytics

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    // Initialize Firebase Analytics
    firebaseAnalytics = FirebaseAnalytics.getInstance(this)
    
    enableEdgeToEdge()
    setContent {
      val viewModel: ProfileViewModel = viewModel(factory = ProfileViewModelFactory(this))
      val isDarkMode by viewModel.isDarkMode.collectAsState()
      val darkTheme = isDarkMode ?: isSystemInDarkTheme()
      
      AppTheme(darkTheme = darkTheme) {
          AppNavigation()
      }
    }
  }
}
