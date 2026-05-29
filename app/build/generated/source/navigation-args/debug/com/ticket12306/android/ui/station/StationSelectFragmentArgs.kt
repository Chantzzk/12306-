package com.ticket12306.android.ui.station

import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavArgs
import java.lang.IllegalArgumentException
import kotlin.Boolean
import kotlin.jvm.JvmStatic

public data class StationSelectFragmentArgs(
  public val isDeparture: Boolean,
) : NavArgs {
  public fun toBundle(): Bundle {
    val result = Bundle()
    result.putBoolean("isDeparture", this.isDeparture)
    return result
  }

  public fun toSavedStateHandle(): SavedStateHandle {
    val result = SavedStateHandle()
    result.set("isDeparture", this.isDeparture)
    return result
  }

  public companion object {
    @JvmStatic
    public fun fromBundle(bundle: Bundle): StationSelectFragmentArgs {
      bundle.setClassLoader(StationSelectFragmentArgs::class.java.classLoader)
      val __isDeparture : Boolean
      if (bundle.containsKey("isDeparture")) {
        __isDeparture = bundle.getBoolean("isDeparture")
      } else {
        throw IllegalArgumentException("Required argument \"isDeparture\" is missing and does not have an android:defaultValue")
      }
      return StationSelectFragmentArgs(__isDeparture)
    }

    @JvmStatic
    public fun fromSavedStateHandle(savedStateHandle: SavedStateHandle): StationSelectFragmentArgs {
      val __isDeparture : Boolean?
      if (savedStateHandle.contains("isDeparture")) {
        __isDeparture = savedStateHandle["isDeparture"]
        if (__isDeparture == null) {
          throw IllegalArgumentException("Argument \"isDeparture\" of type boolean does not support null values")
        }
      } else {
        throw IllegalArgumentException("Required argument \"isDeparture\" is missing and does not have an android:defaultValue")
      }
      return StationSelectFragmentArgs(__isDeparture)
    }
  }
}
