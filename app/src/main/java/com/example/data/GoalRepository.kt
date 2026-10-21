package com.example.data

import com.example.model.Goal
import com.example.model.GoalWithMilestones
import com.example.model.MilestoneItem
import kotlinx.coroutines.flow.Flow

class GoalRepository(private val goalDao: GoalDao) {
    val allGoals: Flow<List<GoalWithMilestones>> = goalDao.getAllGoalsWithMilestones()
    val activeGoals: Flow<List<GoalWithMilestones>> = goalDao.getActiveGoalsWithMilestones()
    val completedGoals: Flow<List<GoalWithMilestones>> = goalDao.getCompletedGoalsWithMilestones()

    fun getGoalWithMilestones(goalId: Long): Flow<GoalWithMilestones?> {
        return goalDao.getGoalWithMilestonesById(goalId)
    }

    suspend fun saveGoal(goal: Goal): Long {
        return if (goal.id == 0L) {
            goalDao.insertGoal(goal)
        } else {
            goalDao.updateGoal(goal)
            goal.id
        }
    }

    suspend fun deleteGoal(goal: Goal) {
        goalDao.deleteGoal(goal)
    }

    suspend fun saveMilestone(milestone: MilestoneItem): Long {
        return if (milestone.id == 0L) {
            goalDao.insertMilestone(milestone)
        } else {
            goalDao.updateMilestone(milestone)
            milestone.id
        }
    }

    suspend fun toggleMilestoneCompletion(milestone: MilestoneItem, goal: Goal, allMilestones: List<MilestoneItem>) {
        val newStatus = !milestone.isCompleted
        val updatedMilestone = milestone.copy(
            isCompleted = newStatus,
            completedDateMillis = if (newStatus) System.currentTimeMillis() else null
        )
        goalDao.updateMilestone(updatedMilestone)

        // Check if all milestones are now completed
        val otherMilestones = allMilestones.filter { it.id != milestone.id }
        val allDone = newStatus && otherMilestones.all { it.isCompleted }

        if (allDone && !goal.isCompleted) {
            // Auto complete goal
            goalDao.updateGoal(
                goal.copy(
                    isCompleted = true,
                    completedDateMillis = System.currentTimeMillis()
                )
            )
        } else if (!newStatus && goal.isCompleted) {
            // Reopen goal if a milestone is uncompleted
            goalDao.updateGoal(
                goal.copy(
                    isCompleted = false,
                    completedDateMillis = null
                )
            )
        }
    }

    suspend fun setGoalCompletion(goal: Goal, isCompleted: Boolean) {
        goalDao.updateGoal(
            goal.copy(
                isCompleted = isCompleted,
                completedDateMillis = if (isCompleted) System.currentTimeMillis() else null
            )
        )
    }

    suspend fun updateGoalNotes(goalId: Long, currentGoal: Goal, newNotes: String) {
        goalDao.updateGoal(currentGoal.copy(notes = newNotes))
    }

    suspend fun deleteMilestone(milestone: MilestoneItem) {
        goalDao.deleteMilestone(milestone)
    }

    suspend fun resetToSampleData() {
        goalDao.clearAllMilestones()
        goalDao.clearAllGoals()
        MilestoneDatabase.populateInitialGoals(goalDao)
    }

    suspend fun clearAllData() {
        goalDao.clearAllMilestones()
        goalDao.clearAllGoals()
    }
}
