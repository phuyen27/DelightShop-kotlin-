package com.example.phamthiphuonguyen_delightshop.Model

import android.os.Parcel
import android.os.Parcelable

data class OrderModel(
    var orderId: String = "",
    var userId: String = "",
    var items: List<OrderItem> = listOf(),
    var totalPrice: Double = 0.0,
    var status: String = "",
    var orderDate: String = "",
    var shippingAddress: String = "",
    var paymentMethod: String = "",
    var paymentDetails: PaymentDetails = PaymentDetails()
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.createTypedArrayList(OrderItem.CREATOR) ?: listOf(),
        parcel.readDouble(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readParcelable(PaymentDetails::class.java.classLoader) ?: PaymentDetails()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(orderId)
        parcel.writeString(userId)
        parcel.writeTypedList(items)
        parcel.writeDouble(totalPrice)
        parcel.writeString(status)
        parcel.writeString(orderDate)
        parcel.writeString(shippingAddress)
        parcel.writeString(paymentMethod)
        parcel.writeParcelable(paymentDetails, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<OrderModel> {
        override fun createFromParcel(parcel: Parcel): OrderModel {
            return OrderModel(parcel)
        }

        override fun newArray(size: Int): Array<OrderModel?> {
            return arrayOfNulls(size)
        }
    }

    data class OrderItem(
        @get:JvmName("getId")
        var itemId: Int = 0,
        var title :String="",
        var quantity: Int = 0
    ) : Parcelable {
        constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString().toString(),
            parcel.readInt()
        )

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeInt(itemId)
            parcel.writeString(title)
            parcel.writeInt(quantity)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<OrderItem> {
            override fun createFromParcel(parcel: Parcel): OrderItem {
                return OrderItem(parcel)
            }

            override fun newArray(size: Int): Array<OrderItem?> {
                return arrayOfNulls(size)
            }
        }
    }

    // Nested class for payment details
    data class PaymentDetails(
        var cardNumber: String = "",
        var expiryDate: String = "",
        var cvv: String = ""
    ) : Parcelable {
        constructor(parcel: Parcel) : this(
            parcel.readString().toString(),
            parcel.readString().toString(),
            parcel.readString().toString()
        )

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(cardNumber)
            parcel.writeString(expiryDate)
            parcel.writeString(cvv)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<PaymentDetails> {
            override fun createFromParcel(parcel: Parcel): PaymentDetails {
                return PaymentDetails(parcel)
            }

            override fun newArray(size: Int): Array<PaymentDetails?> {
                return arrayOfNulls(size)
            }
        }
    }
}
