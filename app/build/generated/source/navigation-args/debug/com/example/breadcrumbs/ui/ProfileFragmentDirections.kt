package com.example.breadcrumbs.ui

import android.os.Bundle
import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.example.breadcrumbs.R
import kotlin.Int
import kotlin.String

public class ProfileFragmentDirections private constructor() {
  private data class ActionProfileToTripDetail(
    public val tripId: String,
  ) : NavDirections {
    public override val actionId: Int = R.id.action_profile_to_tripDetail

    public override val arguments: Bundle
      get() {
        val result = Bundle()
        result.putString("tripId", this.tripId)
        return result
      }
  }

  public companion object {
    public fun actionProfileToCreateTrip(): NavDirections =
        ActionOnlyNavDirections(R.id.action_profile_to_createTrip)

    public fun actionProfileToTripDetail(tripId: String): NavDirections =
        ActionProfileToTripDetail(tripId)
  }
}
