package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GoalCategory
import com.example.model.GoalWithMilestones
import com.example.ui.components.CategoryTag
import com.example.ui.components.CircularProgressRing
import com.example.ui.components.FilterChipItem
import com.example.ui.components.LinearGradientProgressBar
import com.example.ui.components.MotivationalBanner
import com.example.ui.theme.GoldSuccess
import com.example.ui.theme.PinkAccent
import com.example.ui.theme.PurplePrimary
import com.example.ui.viewmodel.MilestoneViewModel

@Composable
fun HomeScreen(
    viewModel: MilestoneViewModel,
    onNavigateToGoalDetail: (Long) -> Unit,
    onNavigateToAddGoal: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeGoals by viewModel.filteredActiveGoals.collectAsState()
    val allActiveGoals by viewModel.activeGoals.collectAsState()
    val completedGoals by viewModel.completedGoals.collectAsState()
    val selectedCategory by viewModel.selectedCategoryFilter.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val showQuotes by viewModel.settingsRepository.showMotivationalQuotes.collectAsState()

    var showSearchBar by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddGoal,
                containerColor = PurplePrimary,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier
                    .testTag("add_goal_fab")
                    .padding(bottom = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Goal",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Milestone",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = PurplePrimary
                        )
                        Text(
                            text = "Conquer your summit, step by step",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(
                        onClick = {
                            showSearchBar = !showSearchBar
                            if (!showSearchBar) viewModel.updateSearchQuery("")
                        },
                        modifier = Modifier.testTag("toggle_search_button")
                    ) {
                        Icon(
                            imageVector = if (showSearchBar) Icons.Default.Clear else Icons.Default.Search,
                            contentDescription = "Search goals",
                            tint = PurplePrimary
                        )
                    }
                }
            }

            // Search Bar (if visible)
            if (showSearchBar) {
                item {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.updateSearchQuery(it) },
                        placeholder = { Text("Search your active goals...") },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = PurplePrimary)
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PurplePrimary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("home_search_input")
                    )
                }
            }

            // Motivational Banner
            if (showQuotes) {
                item {
                    val avgProgress = if (allActiveGoals.isNotEmpty()) {
                        allActiveGoals.map { it.progressPercentage }.average().toInt()
                    } else 0
                    MotivationalBanner(
                        activeGoalsCount = allActiveGoals.size,
                        completedGoalsCount = completedGoals.size,
                        averageProgress = avgProgress
                    )
                }
            }

            // Category Filter Chips
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    item {
                        FilterChipItem(
                            label = "All Goals (${allActiveGoals.size})",
                            isSelected = selectedCategory == null,
                            onClick = { viewModel.selectCategoryFilter(null) },
                            modifier = Modifier.testTag("filter_all")
                        )
                    }
                    items(GoalCategory.entries) { category ->
                        val count = allActiveGoals.count {
                            it.goal.category.equals(category.displayName, ignoreCase = true)
                        }
                        FilterChipItem(
                            label = "${category.displayName} ($count)",
                            emoji = category.emoji,
                            isSelected = selectedCategory.equals(category.displayName, ignoreCase = true),
                            onClick = {
                                if (selectedCategory.equals(category.displayName, ignoreCase = true)) {
                                    viewModel.selectCategoryFilter(null)
                                } else {
                                    viewModel.selectCategoryFilter(category.displayName)
                                }
                            },
                            modifier = Modifier.testTag("filter_${category.name.lowercase()}")
                        )
                    }
                }
            }

            // Active Goals List Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (selectedCategory != null) "$selectedCategory Goals" else "Active Goals",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${activeGoals.size} active",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Goal Cards or Empty State
            if (activeGoals.isEmpty()) {
                item {
                    EmptyGoalsState(onAddGoal = onNavigateToAddGoal)
                }
            } else {
                items(activeGoals, key = { it.goal.id }) { goalItem ->
                    GoalCard(
                        goalItem = goalItem,
                        onClick = { onNavigateToGoalDetail(goalItem.goal.id) },
                        modifier = Modifier.testTag("goal_card_${goalItem.goal.id}")
                    )
                }
            }

            // Bottom Spacer for FAB
            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }
}

@Composable
fun GoalCard(
    goalItem: GoalWithMilestones,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val goal = goalItem.goal
    val percent = goalItem.progressPercentage
    val daysLeft = goalItem.daysLeft()
    val deadlineLabel = goalItem.deadlineLabel()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Top Row: Category Tag, Emoji Badge, Days Left Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = goal.emoji, fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    CategoryTag(categoryName = goal.category)
                }

                // Deadline Badge
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (daysLeft <= 7) Color(0xFFFEE2E2) else MaterialTheme.colorScheme.surfaceVariant
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        modifier = Modifier.size(13.dp),
                        tint = if (daysLeft <= 7) Color(0xFFDC2626) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = deadlineLabel,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = if (daysLeft <= 7) Color(0xFFDC2626) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Title & Description
            Text(
                text = goal.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (goal.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = goal.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress Bar Forward Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = PurplePrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${goalItem.completedMilestones} of ${goalItem.totalMilestones} milestones",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = "$percent%",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = PurplePrimary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Animated Gradient Progress Bar
            LinearGradientProgressBar(
                progress = goalItem.progressFraction,
                height = 10.dp,
                isCompleted = goalItem.goal.isCompleted
            )
        }
    }
}

@Composable
fun EmptyGoalsState(
    onAddGoal: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "🏔️", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "No Active Goals Found",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Every great summit begins with your first milestone. Create a new goal today!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(20.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(PurplePrimary)
                    .clickable(onClick = onAddGoal)
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "+ Create First Goal",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
