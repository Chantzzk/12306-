package com.ticket12306.android.ui.order

import android.os.Bundle
import android.os.Parcelable
import androidx.navigation.NavDirections
import com.ticket12306.android.R
import com.ticket12306.android.`data`.model.OrderInfo
import java.io.Serializable
import java.lang.UnsupportedOperationException
import kotlin.Int
import kotlin.Suppress

public class OrderFragmentDirections private constructor() {
  private data class ActionNavigationOrderToOrderDetailFragment(
    public val orderInfo: OrderInfo,
  ) : NavDirections {
    public override val actionId: Int = R.id.action_navigation_order_to_orderDetailFragment

    public override val arguments: Bundle
      @Suppress("CAST_NEVER_SUCCEEDS")
      get() {
        val result = Bundle()
        if (Parcelable::class.java.isAssignableFrom(OrderInfo::class.java)) {
          result.putParcelable("orderInfo", this.orderInfo as Parcelable)
        } else if (Serializable::class.java.isAssignableFrom(OrderInfo::class.java)) {
          result.putSerializable("orderInfo", this.orderInfo as Serializable)
        } else {
          throw UnsupportedOperationException(OrderInfo::class.java.name +
              " must implement Parcelable or Serializable or must be an Enum.")
        }
        return result
      }
  }

  public companion object {
    public fun actionNavigationOrderToOrderDetailFragment(orderInfo: OrderInfo): NavDirections =
        ActionNavigationOrderToOrderDetailFragment(orderInfo)
  }
}
