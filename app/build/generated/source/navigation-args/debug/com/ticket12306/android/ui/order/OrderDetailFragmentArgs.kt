package com.ticket12306.android.ui.order

import android.os.Bundle
import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavArgs
import com.ticket12306.android.`data`.model.OrderInfo
import java.io.Serializable
import java.lang.IllegalArgumentException
import java.lang.UnsupportedOperationException
import kotlin.Suppress
import kotlin.jvm.JvmStatic

public data class OrderDetailFragmentArgs(
  public val orderInfo: OrderInfo,
) : NavArgs {
  @Suppress("CAST_NEVER_SUCCEEDS")
  public fun toBundle(): Bundle {
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

  @Suppress("CAST_NEVER_SUCCEEDS")
  public fun toSavedStateHandle(): SavedStateHandle {
    val result = SavedStateHandle()
    if (Parcelable::class.java.isAssignableFrom(OrderInfo::class.java)) {
      result.set("orderInfo", this.orderInfo as Parcelable)
    } else if (Serializable::class.java.isAssignableFrom(OrderInfo::class.java)) {
      result.set("orderInfo", this.orderInfo as Serializable)
    } else {
      throw UnsupportedOperationException(OrderInfo::class.java.name +
          " must implement Parcelable or Serializable or must be an Enum.")
    }
    return result
  }

  public companion object {
    @JvmStatic
    @Suppress("DEPRECATION")
    public fun fromBundle(bundle: Bundle): OrderDetailFragmentArgs {
      bundle.setClassLoader(OrderDetailFragmentArgs::class.java.classLoader)
      val __orderInfo : OrderInfo?
      if (bundle.containsKey("orderInfo")) {
        if (Parcelable::class.java.isAssignableFrom(OrderInfo::class.java) ||
            Serializable::class.java.isAssignableFrom(OrderInfo::class.java)) {
          __orderInfo = bundle.get("orderInfo") as OrderInfo?
        } else {
          throw UnsupportedOperationException(OrderInfo::class.java.name +
              " must implement Parcelable or Serializable or must be an Enum.")
        }
        if (__orderInfo == null) {
          throw IllegalArgumentException("Argument \"orderInfo\" is marked as non-null but was passed a null value.")
        }
      } else {
        throw IllegalArgumentException("Required argument \"orderInfo\" is missing and does not have an android:defaultValue")
      }
      return OrderDetailFragmentArgs(__orderInfo)
    }

    @JvmStatic
    public fun fromSavedStateHandle(savedStateHandle: SavedStateHandle): OrderDetailFragmentArgs {
      val __orderInfo : OrderInfo?
      if (savedStateHandle.contains("orderInfo")) {
        if (Parcelable::class.java.isAssignableFrom(OrderInfo::class.java) ||
            Serializable::class.java.isAssignableFrom(OrderInfo::class.java)) {
          __orderInfo = savedStateHandle.get<OrderInfo?>("orderInfo")
        } else {
          throw UnsupportedOperationException(OrderInfo::class.java.name +
              " must implement Parcelable or Serializable or must be an Enum.")
        }
        if (__orderInfo == null) {
          throw IllegalArgumentException("Argument \"orderInfo\" is marked as non-null but was passed a null value")
        }
      } else {
        throw IllegalArgumentException("Required argument \"orderInfo\" is missing and does not have an android:defaultValue")
      }
      return OrderDetailFragmentArgs(__orderInfo)
    }
  }
}
