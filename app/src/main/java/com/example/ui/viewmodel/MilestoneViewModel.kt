package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.GoalRepository
import com.example.data.MilestoneDatabase
import com.example.data.SettingsRepository
import com.example.model.Goal
import com.example.model.GoalWithMilestones
import com.example.model.MilestoneItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MilestoneViewModel(application: Application) : AndroidViewModel(application) {
    private val database = MilestoneDatabase.getDatabase(application, viewModelScope)
    private val repository = GoalRepository(database.goalDao())
    val settingsRepository = SettingsRepository(application)

    val activeGoals: StateFlow<List<GoalWithMilestones>> = repository.activeGoals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val completedGoals: StateFlow<List<GoalWithMilestones>> = repository.completedGoals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allGoals: StateFlow<List<GoalWithMilestones>> = repository.allGoals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedCategoryFilter = MutableStateFlow<String?>(null)
    val selectedCategoryFilter: StateFlow<String?> = _selectedCategoryFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val filteredActiveGoals: StateFlow<List<GoalWithMilestones>> = combine(
        activeGoals,
        _selectedCategoryFilter,
        _searchQuery
    ) { goals, category, query ->
        goals.filter { item ->
            val matchesCategory = category == null || item.goal.category.equals(category, ignoreCase = true)
            val matchesQuery = query.isBlank() || item.goal.title.contains(query, ignoreCase = true) ||
                    item.goal.description.contains(query, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedGoalId = MutableStateFlow<Long?>(null)
    val selectedGoalId: StateFlow<Long?> = _selectedGoalId.asStateFlow()

    private val _showCelebration = MutableStateFlow(false)
    val showCelebration: StateFlow<Boolean> = _showCelebration.asStateFlow()

    fun selectCategoryFilter(category: String?) {
        _selectedCategoryFilter.value = category
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectGoal(goalId: Long?) {
        _selectedGoalId.value = goalId
    }

    fun triggerCelebration() {
        viewModelScope.launch {
            _showCelebration.value = true
            kotlinx.coroutines.delay(3200)
            _showCelebration.value = false
        }
    }

    fun saveGoal(
        id: Long = 0,
        title: String,
        description: String,
        category: String,
        targetDateMillis: Long,
        emoji: String,
        coverColor: String,
        notes: String = "",
        initialMilestones: List<String> = emptyList(),
        onSaved: (Long) -> Unit = {}
    ) {
        viewModelScope.launch {
            val goal = Goal(
                id = id,
                title = title.trim(),
                description = description.trim(),
                category = category,
                targetDateMillis = targetDateMillis,
                emoji = emoji,
                coverColor = coverColor,
                notes = notes
            )
            val savedId = repository.saveGoal(goal)

            if (id == 0L && initialMilestones.isNotEmpty()) {
                initialMilestones.forEachIndexed { index, mTitle ->
                    if (mTitle.isNotBlank()) {
                        repository.saveMilestone(
                            MilestoneItem(
                                goalId = savedId,
                                title = mTitle.trim(),
                                orderIndex = index + 1
                            )
                        )
                    }
                }
            }
            onSaved(savedId)
        }
    }

    fun deleteGoal(goal: Goal, onDeleted: () -> Unit = {}) {
        viewModelScope.launch {
            repository.deleteGoal(goal)
            onDeleted()
        }
    }

    fun addMilestone(
        goalId: Long,
        title: String,
        dueDateMillis: Long? = null,
        onAdded: () -> Unit = {}
    ) {
        viewModelScope.launch {
            repository.saveMilestone(
                MilestoneItem(
                    goalId = goalId,
                    title = title.trim(),
                    dueDateMillis = dueDateMillis
                )
            )
            onAdded()
        }
    }

    fun toggleMilestone(milestone: MilestoneItem, goal: Goal, allMilestones: List<MilestoneItem>) {
        viewModelScope.launch {
            val willBeComplete = !milestone.isCompleted
            repository.toggleMilestoneCompletion(milestone, goal, allMilestones)
            if (willBeComplete && settingsRepository.celebrationEffects.value) {
                // If this action completes the goal or completes a big milestone, celebrate!
                val otherMilestones = allMilestones.filter { it.id != milestone.id }
                if (otherMilestones.all { it.isCompleted }) {
                    triggerCelebration()
                }
            }
        }
    }

    fun deleteMilestone(milestone: MilestoneItem) {
        viewModelScope.launch {
            repository.deleteMilestone(milestone)
        }
    }

    fun updateGoalNotes(goal: Goal, notes: String) {
        viewModelScope.launch {
            repository.updateGoalNotes(goal.id, goal, notes)
        }
    }

    fun setGoalCompletion(goal: Goal, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.setGoalCompletion(goal, isCompleted)
            if (isCompleted && settingsRepository.celebrationEffects.value) {
                triggerCelebration()
            }
        }
    }

    fun resetToSampleData() {
        viewModelScope.launch {
            repository.resetToSampleData()
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.clearAllData()
        }
    }
}
