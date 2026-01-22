package com.example.breadcrumbs.ui

import android.os.Bundle
import androidx.navigation.NavDirections
import com.example.breadcrumbs.R
import kotlin.Int
import kotlin.String

public class TripDetailFragmentDirections private constructor() {
  private data class ActionTripDetailToAddPoi(
    public val tripId: String,
  ) : NavDirections {
    public override val actionId: Int = R.id.action_tripDetail_to_addPoi

    public override val arguments: Bundle
      get() {
        val result = Bundle()
        result.putString("tripId", this.tripId)
        return result
      }
  }

  public companion object {
    public fun actionTripDetailToAddPoi(tripId: String): NavDirections =
        ActionTripDetailToAddPoi(tripId)
  }
}
