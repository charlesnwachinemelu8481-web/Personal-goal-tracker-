package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.AddEditGoalScreen
import com.example.ui.screens.CompletedGoalsScreen
import com.example.ui.screens.GoalDetailScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.GoldSuccess
import com.example.ui.theme.MilestoneTheme
import com.example.ui.theme.PurplePrimary
import com.example.ui.viewmodel.MilestoneViewModel

enum class MainTab(val titleRes: Int, val selectedIcon: ImageVector, val unselectedIcon: ImageVector) {
    HOME(R.string.nav_home, Icons.Filled.Flag, Icons.Outlined.Flag),
    COMPLETED(R.string.nav_completed, Icons.Filled.EmojiEvents, Icons.Outlined.EmojiEvents),
    SETTINGS(R.string.nav_settings, Icons.Filled.Settings, Icons.Outlined.Settings)
}

sealed class AppScreen {
    data class Main(val tab: MainTab = MainTab.HOME) : AppScreen()
    data class GoalDetail(val goalId: Long) : AppScreen()
    data class AddEditGoal(val goalId: Long? = null) : AppScreen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: MilestoneViewModel = viewModel()
            val themePreference by viewModel.settingsRepository.themePreference.collectAsState()

            val isDarkTheme = when (themePreference) {
                "Dark (#0F0D17)" -> true
                "Light (#FAFAFA)" -> false
                else -> isSystemInDarkTheme()
            }

            MilestoneTheme(darkTheme = isDarkTheme) {
                MilestoneApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MilestoneApp(
    viewModel: MilestoneViewModel,
    modifier: Modifier = Modifier
) {
    var currentScreen by remember { mutableStateOf<AppScreen>(AppScreen.Main(MainTab.HOME)) }
    var currentTab by remember { mutableStateOf(MainTab.HOME) }

    // Back handling for custom navigation stack
    BackHandler(enabled = currentScreen !is AppScreen.Main || currentTab != MainTab.HOME) {
        when (val screen = currentScreen) {
            is AppScreen.GoalDetail -> {
                currentScreen = AppScreen.Main(currentTab)
            }
            is AppScreen.AddEditGoal -> {
                if (screen.goalId != null) {
                    currentScreen = AppScreen.GoalDetail(screen.goalId)
                } else {
                    currentScreen = AppScreen.Main(currentTab)
                }
            }
            is AppScreen.Main -> {
                if (currentTab != MainTab.HOME) {
                    currentTab = MainTab.HOME
                }
            }
        }
    }

    val isMainTabScreen = currentScreen is AppScreen.Main

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            if (isMainTabScreen) {
                NavigationBar(
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .testTag("bottom_nav_bar"),
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    MainTab.entries.forEach { tab ->
                        val isSelected = currentTab == tab
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                currentTab = tab
                                currentScreen = AppScreen.Main(tab)
                            },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                    contentDescription = stringResource(tab.titleRes)
                                )
                            },
                            label = {
                                Text(
                                    text = stringResource(tab.titleRes),
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = PurplePrimary,
                                selectedTextColor = PurplePrimary,
                                indicatorColor = PurplePrimary.copy(alpha = 0.12f),
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        AnimatedContent(
            targetState = currentScreen,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "screenTransition",
            modifier = Modifier.padding(innerPadding)
        ) { screen ->
            when (screen) {
                is AppScreen.Main -> {
                    when (currentTab) {
                        MainTab.HOME -> {
                            HomeScreen(
                                viewModel = viewModel,
                                onNavigateToGoalDetail = { goalId ->
                                    currentScreen = AppScreen.GoalDetail(goalId)
                                },
                                onNavigateToAddGoal = {
                                    currentScreen = AppScreen.AddEditGoal(null)
                                }
                            )
                        }
                        MainTab.COMPLETED -> {
                            CompletedGoalsScreen(
                                viewModel = viewModel,
                                onNavigateToGoalDetail = { goalId ->
                                    currentScreen = AppScreen.GoalDetail(goalId)
                                },
                                onExploreActiveGoals = {
                                    currentTab = MainTab.HOME
                                    currentScreen = AppScreen.Main(MainTab.HOME)
                                }
                            )
                        }
                        MainTab.SETTINGS -> {
                            SettingsScreen(viewModel = viewModel)
                        }
                    }
                }

                is AppScreen.GoalDetail -> {
                    GoalDetailScreen(
                        goalId = screen.goalId,
                        viewModel = viewModel,
                        onNavigateBack = {
                            currentScreen = AppScreen.Main(currentTab)
                        },
                        onNavigateToEditGoal = { editId ->
                            currentScreen = AppScreen.AddEditGoal(editId)
                        }
                    )
                }

                is AppScreen.AddEditGoal -> {
                    AddEditGoalScreen(
                        goalId = screen.goalId,
                        viewModel = viewModel,
                        onNavigateBack = {
                            if (screen.goalId != null) {
                                currentScreen = AppScreen.GoalDetail(screen.goalId)
                            } else {
                                currentScreen = AppScreen.Main(currentTab)
                            }
                        },
                        onGoalSaved = { savedId ->
                            currentScreen = AppScreen.GoalDetail(savedId)
                        }
                    )
                }
            }
        }
    }
}
