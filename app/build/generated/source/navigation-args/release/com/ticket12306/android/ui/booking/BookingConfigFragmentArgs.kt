package com.ticket12306.android.ui.booking

import android.os.Bundle
import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavArgs
import com.ticket12306.android.`data`.model.BookingTask
import com.ticket12306.android.`data`.model.TicketInfo
import java.io.Serializable
import java.lang.IllegalArgumentException
import java.lang.UnsupportedOperationException
import kotlin.Suppress
import kotlin.jvm.JvmStatic

public data class BookingConfigFragmentArgs(
  public val ticketInfo: TicketInfo?,
  public val bookingTask: BookingTask?,
) : NavArgs {
  @Suppress("CAST_NEVER_SUCCEEDS")
  public fun toBundle(): Bundle {
    val result = Bundle()
    if (Parcelable::class.java.isAssignableFrom(TicketInfo::class.java)) {
      result.putParcelable("ticketInfo", this.ticketInfo as Parcelable?)
    } else if (Serializable::class.java.isAssignableFrom(TicketInfo::class.java)) {
      result.putSerializable("ticketInfo", this.ticketInfo as Serializable?)
    } else {
      throw UnsupportedOperationException(TicketInfo::class.java.name +
          " must implement Parcelable or Serializable or must be an Enum.")
    }
    if (Parcelable::class.java.isAssignableFrom(BookingTask::class.java)) {
      result.putParcelable("bookingTask", this.bookingTask as Parcelable?)
    } else if (Serializable::class.java.isAssignableFrom(BookingTask::class.java)) {
      result.putSerializable("bookingTask", this.bookingTask as Serializable?)
    } else {
      throw UnsupportedOperationException(BookingTask::class.java.name +
          " must implement Parcelable or Serializable or must be an Enum.")
    }
    return result
  }

  @Suppress("CAST_NEVER_SUCCEEDS")
  public fun toSavedStateHandle(): SavedStateHandle {
    val result = SavedStateHandle()
    if (Parcelable::class.java.isAssignableFrom(TicketInfo::class.java)) {
      result.set("ticketInfo", this.ticketInfo as Parcelable?)
    } else if (Serializable::class.java.isAssignableFrom(TicketInfo::class.java)) {
      result.set("ticketInfo", this.ticketInfo as Serializable?)
    } else {
      throw UnsupportedOperationException(TicketInfo::class.java.name +
          " must implement Parcelable or Serializable or must be an Enum.")
    }
    if (Parcelable::class.java.isAssignableFrom(BookingTask::class.java)) {
      result.set("bookingTask", this.bookingTask as Parcelable?)
    } else if (Serializable::class.java.isAssignableFrom(BookingTask::class.java)) {
      result.set("bookingTask", this.bookingTask as Serializable?)
    } else {
      throw UnsupportedOperationException(BookingTask::class.java.name +
          " must implement Parcelable or Serializable or must be an Enum.")
    }
    return result
  }

  public companion object {
    @JvmStatic
    @Suppress("DEPRECATION")
    public fun fromBundle(bundle: Bundle): BookingConfigFragmentArgs {
      bundle.setClassLoader(BookingConfigFragmentArgs::class.java.classLoader)
      val __ticketInfo : TicketInfo?
      if (bundle.containsKey("ticketInfo")) {
        if (Parcelable::class.java.isAssignableFrom(TicketInfo::class.java) ||
            Serializable::class.java.isAssignableFrom(TicketInfo::class.java)) {
          __ticketInfo = bundle.get("ticketInfo") as TicketInfo?
        } else {
          throw UnsupportedOperationException(TicketInfo::class.java.name +
              " must implement Parcelable or Serializable or must be an Enum.")
        }
      } else {
        throw IllegalArgumentException("Required argument \"ticketInfo\" is missing and does not have an android:defaultValue")
      }
      val __bookingTask : BookingTask?
      if (bundle.containsKey("bookingTask")) {
        if (Parcelable::class.java.isAssignableFrom(BookingTask::class.java) ||
            Serializable::class.java.isAssignableFrom(BookingTask::class.java)) {
          __bookingTask = bundle.get("bookingTask") as BookingTask?
        } else {
          throw UnsupportedOperationException(BookingTask::class.java.name +
              " must implement Parcelable or Serializable or must be an Enum.")
        }
      } else {
        throw IllegalArgumentException("Required argument \"bookingTask\" is missing and does not have an android:defaultValue")
      }
      return BookingConfigFragmentArgs(__ticketInfo, __bookingTask)
    }

    @JvmStatic
    public fun fromSavedStateHandle(savedStateHandle: SavedStateHandle): BookingConfigFragmentArgs {
      val __ticketInfo : TicketInfo?
      if (savedStateHandle.contains("ticketInfo")) {
        if (Parcelable::class.java.isAssignableFrom(TicketInfo::class.java) ||
            Serializable::class.java.isAssignableFrom(TicketInfo::class.java)) {
          __ticketInfo = savedStateHandle.get<TicketInfo?>("ticketInfo")
        } else {
          throw UnsupportedOperationException(TicketInfo::class.java.name +
              " must implement Parcelable or Serializable or must be an Enum.")
        }
      } else {
        throw IllegalArgumentException("Required argument \"ticketInfo\" is missing and does not have an android:defaultValue")
      }
      val __bookingTask : BookingTask?
      if (savedStateHandle.contains("bookingTask")) {
        if (Parcelable::class.java.isAssignableFrom(BookingTask::class.java) ||
            Serializable::class.java.isAssignableFrom(BookingTask::class.java)) {
          __bookingTask = savedStateHandle.get<BookingTask?>("bookingTask")
        } else {
          throw UnsupportedOperationException(BookingTask::class.java.name +
              " must implement Parcelable or Serializable or must be an Enum.")
        }
      } else {
        throw IllegalArgumentException("Required argument \"bookingTask\" is missing and does not have an android:defaultValue")
      }
      return BookingConfigFragmentArgs(__ticketInfo, __bookingTask)
    }
  }
}
