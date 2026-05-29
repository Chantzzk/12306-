package com.ticket12306.android.ui.ticket

import android.os.Bundle
import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavArgs
import com.ticket12306.android.`data`.model.TicketInfo
import java.io.Serializable
import java.lang.IllegalArgumentException
import java.lang.UnsupportedOperationException
import kotlin.Suppress
import kotlin.jvm.JvmStatic

public data class SeatSelectFragmentArgs(
  public val ticketInfo: TicketInfo,
) : NavArgs {
  @Suppress("CAST_NEVER_SUCCEEDS")
  public fun toBundle(): Bundle {
    val result = Bundle()
    if (Parcelable::class.java.isAssignableFrom(TicketInfo::class.java)) {
      result.putParcelable("ticketInfo", this.ticketInfo as Parcelable)
    } else if (Serializable::class.java.isAssignableFrom(TicketInfo::class.java)) {
      result.putSerializable("ticketInfo", this.ticketInfo as Serializable)
    } else {
      throw UnsupportedOperationException(TicketInfo::class.java.name +
          " must implement Parcelable or Serializable or must be an Enum.")
    }
    return result
  }

  @Suppress("CAST_NEVER_SUCCEEDS")
  public fun toSavedStateHandle(): SavedStateHandle {
    val result = SavedStateHandle()
    if (Parcelable::class.java.isAssignableFrom(TicketInfo::class.java)) {
      result.set("ticketInfo", this.ticketInfo as Parcelable)
    } else if (Serializable::class.java.isAssignableFrom(TicketInfo::class.java)) {
      result.set("ticketInfo", this.ticketInfo as Serializable)
    } else {
      throw UnsupportedOperationException(TicketInfo::class.java.name +
          " must implement Parcelable or Serializable or must be an Enum.")
    }
    return result
  }

  public companion object {
    @JvmStatic
    @Suppress("DEPRECATION")
    public fun fromBundle(bundle: Bundle): SeatSelectFragmentArgs {
      bundle.setClassLoader(SeatSelectFragmentArgs::class.java.classLoader)
      val __ticketInfo : TicketInfo?
      if (bundle.containsKey("ticketInfo")) {
        if (Parcelable::class.java.isAssignableFrom(TicketInfo::class.java) ||
            Serializable::class.java.isAssignableFrom(TicketInfo::class.java)) {
          __ticketInfo = bundle.get("ticketInfo") as TicketInfo?
        } else {
          throw UnsupportedOperationException(TicketInfo::class.java.name +
              " must implement Parcelable or Serializable or must be an Enum.")
        }
        if (__ticketInfo == null) {
          throw IllegalArgumentException("Argument \"ticketInfo\" is marked as non-null but was passed a null value.")
        }
      } else {
        throw IllegalArgumentException("Required argument \"ticketInfo\" is missing and does not have an android:defaultValue")
      }
      return SeatSelectFragmentArgs(__ticketInfo)
    }

    @JvmStatic
    public fun fromSavedStateHandle(savedStateHandle: SavedStateHandle): SeatSelectFragmentArgs {
      val __ticketInfo : TicketInfo?
      if (savedStateHandle.contains("ticketInfo")) {
        if (Parcelable::class.java.isAssignableFrom(TicketInfo::class.java) ||
            Serializable::class.java.isAssignableFrom(TicketInfo::class.java)) {
          __ticketInfo = savedStateHandle.get<TicketInfo?>("ticketInfo")
        } else {
          throw UnsupportedOperationException(TicketInfo::class.java.name +
              " must implement Parcelable or Serializable or must be an Enum.")
        }
        if (__ticketInfo == null) {
          throw IllegalArgumentException("Argument \"ticketInfo\" is marked as non-null but was passed a null value")
        }
      } else {
        throw IllegalArgumentException("Required argument \"ticketInfo\" is missing and does not have an android:defaultValue")
      }
      return SeatSelectFragmentArgs(__ticketInfo)
    }
  }
}
