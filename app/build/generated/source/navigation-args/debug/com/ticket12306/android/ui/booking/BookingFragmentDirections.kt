package com.ticket12306.android.ui.booking

import android.os.Bundle
import android.os.Parcelable
import androidx.navigation.NavDirections
import com.ticket12306.android.R
import com.ticket12306.android.`data`.model.BookingTask
import com.ticket12306.android.`data`.model.TicketInfo
import java.io.Serializable
import java.lang.UnsupportedOperationException
import kotlin.Int
import kotlin.Suppress

public class BookingFragmentDirections private constructor() {
  private data class ActionNavigationBookingToBookingConfigFragment(
    public val ticketInfo: TicketInfo?,
    public val bookingTask: BookingTask?,
  ) : NavDirections {
    public override val actionId: Int = R.id.action_navigation_booking_to_bookingConfigFragment

    public override val arguments: Bundle
      @Suppress("CAST_NEVER_SUCCEEDS")
      get() {
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
  }

  public companion object {
    public fun actionNavigationBookingToBookingConfigFragment(ticketInfo: TicketInfo?,
        bookingTask: BookingTask?): NavDirections =
        ActionNavigationBookingToBookingConfigFragment(ticketInfo, bookingTask)
  }
}
