package com.example.breadcrumbs.ui

import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavArgs
import java.lang.IllegalArgumentException
import kotlin.String
import kotlin.jvm.JvmStatic

public data class TripDetailFragmentArgs(
  public val tripId: String,
) : NavArgs {
  public fun toBundle(): Bundle {
    val result = Bundle()
    result.putString("tripId", this.tripId)
    return result
  }

  public fun toSavedStateHandle(): SavedStateHandle {
    val result = SavedStateHandle()
    result.set("tripId", this.tripId)
    return result
  }

  public companion object {
    @JvmStatic
    public fun fromBundle(bundle: Bundle): TripDetailFragmentArgs {
      bundle.setClassLoader(TripDetailFragmentArgs::class.java.classLoader)
      val __tripId : String?
      if (bundle.containsKey("tripId")) {
        __tripId = bundle.getString("tripId")
        if (__tripId == null) {
          throw IllegalArgumentException("Argument \"tripId\" is marked as non-null but was passed a null value.")
        }
      } else {
        throw IllegalArgumentException("Required argument \"tripId\" is missing and does not have an android:defaultValue")
      }
      return TripDetailFragmentArgs(__tripId)
    }

    @JvmStatic
    public fun fromSavedStateHandle(savedStateHandle: SavedStateHandle): TripDetailFragmentArgs {
      val __tripId : String?
      if (savedStateHandle.contains("tripId")) {
        __tripId = savedStateHandle["tripId"]
        if (__tripId == null) {
          throw IllegalArgumentException("Argument \"tripId\" is marked as non-null but was passed a null value")
        }
      } else {
        throw IllegalArgumentException("Required argument \"tripId\" is missing and does not have an android:defaultValue")
      }
      return TripDetailFragmentArgs(__tripId)
    }
  }
}
