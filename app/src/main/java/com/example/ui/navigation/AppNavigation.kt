package com.example.ui.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.WelcomeScreen
import com.example.ui.screens.CreateCharacterScreen
import com.example.ui.screens.CreateGroupChatScreen
import com.example.ui.screens.SettingsScreen

import com.example.ui.screens.GroupChatScreen
import com.example.ui.screens.PersonaViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavType
import androidx.navigation.navArgument

object Routes {
    const val WELCOME = "welcome"
    const val HOME = "home"
    const val PROFILE = "profile"
    const val CREATE_CHARACTER = "create_character"
    const val CREATE_GROUP_CHAT = "create_group_chat"
    const val SETTINGS = "settings"
    const val CHARACTER_SETTINGS = "character_settings/{characterId}"
    const val CHAT = "chat/{chatId}"
    fun createChatRoute(chatId: Int) = "chat/$chatId"
    fun createCharacterSettingsRoute(characterId: Int) = "character_settings/$characterId"
}

@Composable
fun AppNavigation() {
    val context = LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("PersonaPrefs", Context.MODE_PRIVATE) }
    val initialUsername = sharedPrefs.getString("username", "") ?: ""
    
    val startDestination = if (initialUsername.isEmpty()) Routes.WELCOME else Routes.HOME
    val navController = rememberNavController()
    
    NavHost(
        navController = navController, 
        startDestination = startDestination,
        enterTransition = { 
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, animationSpec = tween(400)) 
        },
        exitTransition = { 
            fadeOut(animationSpec = tween(400)) 
        },
        popEnterTransition = { 
            fadeIn(animationSpec = tween(400)) 
        },
        popExitTransition = { 
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, animationSpec = tween(400)) 
        }
    ) {
        composable(Routes.WELCOME) {
            WelcomeScreen(
                onContinue = { name ->
                    sharedPrefs.edit().putString("username", name).apply()
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.WELCOME) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.HOME) {
            HomeScreen(
                onNavigateToProfile = {
                    navController.navigate(Routes.PROFILE)
                },
                onNavigateToSettings = {
                    navController.navigate(Routes.SETTINGS)
                },
                onNavigateToCreate = {
                    navController.navigate(Routes.CREATE_CHARACTER)
                },
                onNavigateToCreateGroupChat = {
                    navController.navigate(Routes.CREATE_GROUP_CHAT)
                },
                onNavigateToChat = { chatId ->
                    navController.navigate(Routes.createChatRoute(chatId))
                }
            )
        }
        composable(Routes.PROFILE) {
            val currentName = sharedPrefs.getString("username", "User") ?: "User"
            val viewModel: PersonaViewModel = viewModel()
            ProfileScreen(
                username = currentName,
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToSettings = {
                    navController.navigate(Routes.SETTINGS)
                }
            )
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(Routes.CREATE_CHARACTER) {
            CreateCharacterScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(Routes.CREATE_GROUP_CHAT) {
            CreateGroupChatScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onGroupChatCreated = { chatId ->
                    navController.popBackStack()
                    navController.navigate(Routes.createChatRoute(chatId))
                }
            )
        }
        composable(
            route = Routes.CHARACTER_SETTINGS,
            arguments = listOf(navArgument("characterId") { type = NavType.IntType })
        ) { backStackEntry ->
            val characterId = backStackEntry.arguments?.getInt("characterId") ?: 0
            com.example.ui.screens.CharacterSettingsScreen(
                characterId = characterId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onDeleteComplete = {
                    navController.popBackStack(Routes.HOME, inclusive = false)
                }
            )
        }
        composable(
            route = Routes.CHAT,
            arguments = listOf(navArgument("chatId") { type = NavType.IntType })
        ) { backStackEntry ->
            val chatId = backStackEntry.arguments?.getInt("chatId") ?: 0
            GroupChatScreen(
                chatId = chatId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToCharacterSettings = { id ->
                    navController.navigate(Routes.createCharacterSettingsRoute(id))
                },
                onDeleteComplete = {
                    navController.popBackStack(Routes.HOME, inclusive = false)
                }
            )
        }
    }
}
