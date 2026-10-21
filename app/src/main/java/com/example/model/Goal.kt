package com.example.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Embedded
import androidx.room.Relation

@Entity(tableName = "goals")
data class Goal(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val category: String, // Career, Health, Learning, Finance, Creative, Personal
    val targetDateMillis: Long,
    val emoji: String = "🎯",
    val coverColor: String = "#7C3AED",
    val isCompleted: Boolean = false,
    val completedDateMillis: Long? = null,
    val notes: String = "",
    val createdAtMillis: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "milestones",
    foreignKeys = [
        ForeignKey(
            entity = Goal::class,
            parentColumns = ["id"],
            childColumns = ["goalId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("goalId")]
)
data class MilestoneItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val goalId: Long,
    val title: String,
    val dueDateMillis: Long? = null,
    val isCompleted: Boolean = false,
    val completedDateMillis: Long? = null,
    val orderIndex: Int = 0
)

data class GoalWithMilestones(
    @Embedded val goal: Goal,
    @Relation(
        parentColumn = "id",
        entityColumn = "goalId"
    )
    val milestones: List<MilestoneItem>
) {
    val totalMilestones: Int
        get() = milestones.size

    val completedMilestones: Int
        get() = milestones.count { it.isCompleted }

    val progressFraction: Float
        get() = if (totalMilestones == 0) {
            if (goal.isCompleted) 1f else 0f
        } else {
            completedMilestones.toFloat() / totalMilestones.toFloat()
        }

    val progressPercentage: Int
        get() = (progressFraction * 100).toInt()

    fun daysLeft(): Long {
        val now = System.currentTimeMillis()
        val diff = goal.targetDateMillis - now
        return (diff / (1000 * 60 * 60 * 24)).coerceAtLeast(0)
    }

    fun deadlineLabel(): String {
        if (goal.isCompleted) return "Completed"
        val days = daysLeft()
        return when {
            days == 0L -> "Due today"
            days == 1L -> "1 day left"
            days > 0L -> "$days days left"
            else -> "Overdue"
        }
    }
}

enum class GoalCategory(val displayName: String, val emoji: String, val colorHex: String) {
    CAREER("Career", "🚀", "#7C3AED"),
    HEALTH("Health", "🏃", "#10B981"),
    LEARNING("Learning", "📚", "#3B82F6"),
    FINANCE("Finance", "💰", "#F59E0B"),
    CREATIVE("Creative", "🎨", "#EC4899"),
    PERSONAL("Personal", "🌱", "#14B8A6");

    companion object {
        fun fromName(name: String): GoalCategory {
            return entries.firstOrNull { it.displayName.equals(name, ignoreCase = true) } ?: PERSONAL
        }
    }
}
