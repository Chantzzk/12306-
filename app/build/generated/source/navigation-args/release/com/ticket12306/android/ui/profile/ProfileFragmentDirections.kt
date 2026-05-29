package com.ticket12306.android.ui.profile

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.ticket12306.android.R

public class ProfileFragmentDirections private constructor() {
  public companion object {
    public fun actionNavigationProfileToPassengerManageFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_navigation_profile_to_passengerManageFragment)

    public fun actionNavigationProfileToQueryHistoryFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_navigation_profile_to_queryHistoryFragment)

    public fun actionNavigationProfileToSettingsFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_navigation_profile_to_settingsFragment)

    public fun actionNavigationProfileToAboutFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_navigation_profile_to_aboutFragment)
  }
}
