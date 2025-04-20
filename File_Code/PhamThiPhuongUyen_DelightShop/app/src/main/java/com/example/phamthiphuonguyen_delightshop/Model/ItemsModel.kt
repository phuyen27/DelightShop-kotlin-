package com.example.phamthiphuonguyen_delightshop.Model

import android.os.Parcel
import android.os.Parcelable

data class ItemsModel(
    var title: String="",
    var id: Int=0,
    var description: String="",
    var picUrl:ArrayList<String> = ArrayList(),
    var model: ArrayList<String> = ArrayList(),
    var price: Double=0.0,
    var rating: Double=0.0,
    var numberInCart:Int=0,
    var showRecommended: Boolean=false,
    var like:Boolean=false,
    var inventory:Int=0,
    var categoryID: String=""
):Parcelable{
   constructor(parcel: Parcel):this(
       parcel.readString().toString(),
       parcel.readInt(),
       parcel.readString().toString(),
       parcel.createStringArrayList() as ArrayList<String>,
       parcel.createStringArrayList() as ArrayList<String>,
       parcel.readDouble(),
       parcel.readDouble(),
       parcel.readInt(),
       parcel.readByte()!=0.toByte(),
       parcel.readByte()!=0.toByte(),
       parcel.readInt(),
       parcel.readString().toString()
   )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeInt(id)
        parcel.writeString(description)
        parcel.writeStringList(picUrl)
        parcel.writeStringList(model)
        parcel.writeDouble(price)
        parcel.writeDouble(rating)
        parcel.writeInt(numberInCart)
        parcel.writeByte(if(showRecommended)1 else 0)
        parcel.writeByte(if(like)1 else 0)
        parcel.writeInt(inventory)
        parcel.writeString(categoryID)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR: Parcelable.Creator<ItemsModel>{
        override fun createFromParcel(parcel: Parcel): ItemsModel {
            return ItemsModel(parcel)
        }

        override fun newArray(size: Int): Array<ItemsModel?> {
            return arrayOfNulls(size)
        }
    }
}
