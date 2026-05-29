package com.ticket12306.android.ui.ticket

import android.os.Bundle
import android.os.Parcelable
import androidx.navigation.NavDirections
import com.ticket12306.android.R
import com.ticket12306.android.`data`.model.TicketInfo
import java.io.Serializable
import java.lang.UnsupportedOperationException
import kotlin.Int
import kotlin.Suppress

public class TicketDetailFragmentDirections private constructor() {
  private data class ActionTicketDetailFragmentToSeatSelectFragment(
    public val ticketInfo: TicketInfo,
  ) : NavDirections {
    public override val actionId: Int = R.id.action_ticketDetailFragment_to_seatSelectFragment

    public override val arguments: Bundle
      @Suppress("CAST_NEVER_SUCCEEDS")
      get() {
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
  }

  public companion object {
    public fun actionTicketDetailFragmentToSeatSelectFragment(ticketInfo: TicketInfo): NavDirections
        = ActionTicketDetailFragmentToSeatSelectFragment(ticketInfo)
  }
}
