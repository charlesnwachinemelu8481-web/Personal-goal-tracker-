package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.Goal
import com.example.model.GoalWithMilestones
import com.example.model.MilestoneItem
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read app name from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Milestone", appName)
  }

  @Test
  fun `goal progress calculation test`() {
    val now = System.currentTimeMillis()
    val goal = Goal(
      title = "Launch Project",
      category = "Career",
      targetDateMillis = now + 10 * 24 * 60 * 60 * 1000
    )
    val milestones = listOf(
      MilestoneItem(goalId = 1, title = "Step 1", isCompleted = true),
      MilestoneItem(goalId = 1, title = "Step 2", isCompleted = false)
    )
    val goalWithMilestones = GoalWithMilestones(goal = goal, milestones = milestones)

    assertEquals(2, goalWithMilestones.totalMilestones)
    assertEquals(1, goalWithMilestones.completedMilestones)
    assertEquals(0.5f, goalWithMilestones.progressFraction, 0.001f)
    assertEquals(50, goalWithMilestones.progressPercentage)
  }
}
