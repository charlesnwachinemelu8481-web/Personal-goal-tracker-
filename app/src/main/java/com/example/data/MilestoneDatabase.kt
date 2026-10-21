package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.model.Goal
import com.example.model.MilestoneItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Goal::class, MilestoneItem::class], version = 1, exportSchema = false)
abstract class MilestoneDatabase : RoomDatabase() {
    abstract fun goalDao(): GoalDao

    companion object {
        @Volatile
        private var INSTANCE: MilestoneDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): MilestoneDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MilestoneDatabase::class.java,
                    "milestone_database"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialGoals(database.goalDao())
                    }
                }
            }
        }

        suspend fun populateInitialGoals(dao: GoalDao) {
            val now = System.currentTimeMillis()
            val dayMillis = 24L * 60 * 60 * 1000

            // Goal 1: Half Marathon
            val goal1Id = dao.insertGoal(
                Goal(
                    title = "Run a Half Marathon (21.1 km)",
                    description = "Build endurance and achieve personal best time under 2 hours.",
                    category = "Health",
                    targetDateMillis = now + (32 * dayMillis),
                    emoji = "🏃",
                    coverColor = "#10B981",
                    isCompleted = false,
                    notes = "Consistency is key. Focus on weekend long runs and recovery hydration."
                )
            )
            dao.insertMilestone(MilestoneItem(goalId = goal1Id, title = "Complete 5 km non-stop run", isCompleted = true, completedDateMillis = now - (20 * dayMillis), orderIndex = 1))
            dao.insertMilestone(MilestoneItem(goalId = goal1Id, title = "Hit 10 km weekend milestone", isCompleted = true, completedDateMillis = now - (10 * dayMillis), orderIndex = 2))
            dao.insertMilestone(MilestoneItem(goalId = goal1Id, title = "Reach 15 km threshold at race pace", isCompleted = true, completedDateMillis = now - (2 * dayMillis), orderIndex = 3))
            dao.insertMilestone(MilestoneItem(goalId = goal1Id, title = "18 km rehearsal run", isCompleted = false, dueDateMillis = now + (14 * dayMillis), orderIndex = 4))
            dao.insertMilestone(MilestoneItem(goalId = goal1Id, title = "Cross the Half Marathon finish line!", isCompleted = false, dueDateMillis = now + (32 * dayMillis), orderIndex = 5))

            // Goal 2: Launch Mobile App MVP
            val goal2Id = dao.insertGoal(
                Goal(
                    title = "Launch Mobile App MVP",
                    description = "Design, build, and publish high-converting mobile app on Google Play.",
                    category = "Career",
                    targetDateMillis = now + (18 * dayMillis),
                    emoji = "🚀",
                    coverColor = "#7C3AED",
                    isCompleted = false,
                    notes = "Core features implemented. Focus on onboarding animations and zero bugs."
                )
            )
            dao.insertMilestone(MilestoneItem(goalId = goal2Id, title = "Finalize UI wireframes and user flow", isCompleted = true, completedDateMillis = now - (15 * dayMillis), orderIndex = 1))
            dao.insertMilestone(MilestoneItem(goalId = goal2Id, title = "Implement local database and architecture", isCompleted = true, completedDateMillis = now - (5 * dayMillis), orderIndex = 2))
            dao.insertMilestone(MilestoneItem(goalId = goal2Id, title = "Conduct internal closed beta testing", isCompleted = false, dueDateMillis = now + (7 * dayMillis), orderIndex = 3))
            dao.insertMilestone(MilestoneItem(goalId = goal2Id, title = "Publish production release to store", isCompleted = false, dueDateMillis = now + (18 * dayMillis), orderIndex = 4))

            // Goal 3: Master Jetpack Compose
            val goal3Id = dao.insertGoal(
                Goal(
                    title = "Master Modern Jetpack Compose",
                    description = "Gain expertise in declarative UI, custom Canvas, animations, and M3 design systems.",
                    category = "Learning",
                    targetDateMillis = now + (45 * dayMillis),
                    emoji = "📚",
                    coverColor = "#3B82F6",
                    isCompleted = false,
                    notes = "Explore advanced animation specs like spring physics and custom layout components."
                )
            )
            dao.insertMilestone(MilestoneItem(goalId = goal3Id, title = "State & side-effects deep dive", isCompleted = true, completedDateMillis = now - (12 * dayMillis), orderIndex = 1))
            dao.insertMilestone(MilestoneItem(goalId = goal3Id, title = "Custom Canvas drawing & progress rings", isCompleted = true, completedDateMillis = now - (4 * dayMillis), orderIndex = 2))
            dao.insertMilestone(MilestoneItem(goalId = goal3Id, title = "Complex gesture handling & drag physics", isCompleted = false, dueDateMillis = now + (15 * dayMillis), orderIndex = 3))
            dao.insertMilestone(MilestoneItem(goalId = goal3Id, title = "Adaptive layouts for foldables & tablets", isCompleted = false, dueDateMillis = now + (30 * dayMillis), orderIndex = 4))
            dao.insertMilestone(MilestoneItem(goalId = goal3Id, title = "Publish open-source UI component library", isCompleted = false, dueDateMillis = now + (45 * dayMillis), orderIndex = 5))

            // Goal 4: Emergency Fund & Investments
            val goal4Id = dao.insertGoal(
                Goal(
                    title = "Build 6-Month Emergency Fund",
                    description = "Save $10,000 in high-yield savings to provide total peace of mind.",
                    category = "Finance",
                    targetDateMillis = now + (75 * dayMillis),
                    emoji = "💰",
                    coverColor = "#F59E0B",
                    isCompleted = false,
                    notes = "Automate $1,500 monthly transfer immediately on payday."
                )
            )
            dao.insertMilestone(MilestoneItem(goalId = goal4Id, title = "Reach $2,500 milestone (Starter Cushion)", isCompleted = true, completedDateMillis = now - (30 * dayMillis), orderIndex = 1))
            dao.insertMilestone(MilestoneItem(goalId = goal4Id, title = "Reach $5,000 milestone (Halfway point!)", isCompleted = true, completedDateMillis = now - (2 * dayMillis), orderIndex = 2))
            dao.insertMilestone(MilestoneItem(goalId = goal4Id, title = "Reach $7,500 milestone", isCompleted = false, dueDateMillis = now + (35 * dayMillis), orderIndex = 3))
            dao.insertMilestone(MilestoneItem(goalId = goal4Id, title = "Reach full $10,000 emergency fortress", isCompleted = false, dueDateMillis = now + (75 * dayMillis), orderIndex = 4))

            // Goal 5: Completed Goal - Summit Mountain Peak
            val goal5Id = dao.insertGoal(
                Goal(
                    title = "Conquer Mount Rainier Peak",
                    description = "14,411 ft alpine climb via Disappointment Cleaver route.",
                    category = "Creative",
                    targetDateMillis = now - (10 * dayMillis),
                    emoji = "🏔️",
                    coverColor = "#EC4899",
                    isCompleted = true,
                    completedDateMillis = now - (10 * dayMillis),
                    notes = "Incredible sunrise above the cloud deck at Columbia Crest! Unforgettable journey."
                )
            )
            dao.insertMilestone(MilestoneItem(goalId = goal5Id, title = "Glacier travel & crevasse rescue training", isCompleted = true, completedDateMillis = now - (60 * dayMillis), orderIndex = 1))
            dao.insertMilestone(MilestoneItem(goalId = goal5Id, title = "High altitude conditioning at 10,000 ft", isCompleted = true, completedDateMillis = now - (30 * dayMillis), orderIndex = 2))
            dao.insertMilestone(MilestoneItem(goalId = goal5Id, title = "Camp Muir ascent and acclimatization", isCompleted = true, completedDateMillis = now - (12 * dayMillis), orderIndex = 3))
            dao.insertMilestone(MilestoneItem(goalId = goal5Id, title = "Reach Columbia Crest Summit 14,411 ft!", isCompleted = true, completedDateMillis = now - (10 * dayMillis), orderIndex = 4))

            // Goal 6: Completed Goal - Read 24 Non-Fiction Books
            val goal6Id = dao.insertGoal(
                Goal(
                    title = "Read 24 Non-Fiction Books",
                    description = "Expand horizons in psychology, philosophy, architecture, and technology.",
                    category = "Personal",
                    targetDateMillis = now - (25 * dayMillis),
                    emoji = "🌱",
                    coverColor = "#14B8A6",
                    isCompleted = true,
                    completedDateMillis = now - (25 * dayMillis),
                    notes = "Key takeaway: Systems outlast fleeting motivation every single time."
                )
            )
            dao.insertMilestone(MilestoneItem(goalId = goal6Id, title = "Read 6 books (Quarter 1)", isCompleted = true, completedDateMillis = now - (180 * dayMillis), orderIndex = 1))
            dao.insertMilestone(MilestoneItem(goalId = goal6Id, title = "Read 12 books (Mid-year mark)", isCompleted = true, completedDateMillis = now - (100 * dayMillis), orderIndex = 2))
            dao.insertMilestone(MilestoneItem(goalId = goal6Id, title = "Read 18 books (Quarter 3)", isCompleted = true, completedDateMillis = now - (50 * dayMillis), orderIndex = 3))
            dao.insertMilestone(MilestoneItem(goalId = goal6Id, title = "Complete book #24 & compile key notes", isCompleted = true, completedDateMillis = now - (25 * dayMillis), orderIndex = 4))
        }
    }
}
