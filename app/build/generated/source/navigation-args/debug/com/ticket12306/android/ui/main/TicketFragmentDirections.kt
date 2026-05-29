package com.ticket12306.android.ui.main

import android.os.Bundle
import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.ticket12306.android.R
import kotlin.Boolean
import kotlin.Int

public class TicketFragmentDirections private constructor() {
  private data class ActionNavigationTicketToStationSelectFragment(
    public val isDeparture: Boolean,
  ) : NavDirections {
    public override val actionId: Int = R.id.action_navigation_ticket_to_stationSelectFragment

    public override val arguments: Bundle
      get() {
        val result = Bundle()
        result.putBoolean("isDeparture", this.isDeparture)
        return result
      }
  }

  public companion object {
    public fun actionNavigationTicketToTicketDetailFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_navigation_ticket_to_ticketDetailFragment)

    public fun actionNavigationTicketToStationSelectFragment(isDeparture: Boolean): NavDirections =
        ActionNavigationTicketToStationSelectFragment(isDeparture)
  }
}
