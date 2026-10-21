package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.model.Goal
import com.example.model.GoalWithMilestones
import com.example.model.MilestoneItem
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {
    @Transaction
    @Query("SELECT * FROM goals ORDER BY isCompleted ASC, targetDateMillis ASC")
    fun getAllGoalsWithMilestones(): Flow<List<GoalWithMilestones>>

    @Transaction
    @Query("SELECT * FROM goals WHERE isCompleted = 0 ORDER BY targetDateMillis ASC")
    fun getActiveGoalsWithMilestones(): Flow<List<GoalWithMilestones>>

    @Transaction
    @Query("SELECT * FROM goals WHERE isCompleted = 1 ORDER BY completedDateMillis DESC, targetDateMillis DESC")
    fun getCompletedGoalsWithMilestones(): Flow<List<GoalWithMilestones>>

    @Transaction
    @Query("SELECT * FROM goals WHERE id = :goalId")
    fun getGoalWithMilestonesById(goalId: Long): Flow<GoalWithMilestones?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: Goal): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoals(goals: List<Goal>): List<Long>

    @Update
    suspend fun updateGoal(goal: Goal)

    @Delete
    suspend fun deleteGoal(goal: Goal)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMilestone(milestone: MilestoneItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMilestones(milestones: List<MilestoneItem>): List<Long>

    @Update
    suspend fun updateMilestone(milestone: MilestoneItem)

    @Delete
    suspend fun deleteMilestone(milestone: MilestoneItem)

    @Query("DELETE FROM milestones WHERE goalId = :goalId")
    suspend fun deleteMilestonesForGoal(goalId: Long)

    @Query("DELETE FROM goals")
    suspend fun clearAllGoals()

    @Query("DELETE FROM milestones")
    suspend fun clearAllMilestones()
}
