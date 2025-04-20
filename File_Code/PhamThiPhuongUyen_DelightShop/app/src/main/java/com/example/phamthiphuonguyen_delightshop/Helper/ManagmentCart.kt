package com.example.project1762.Helper

import android.content.Context
import android.widget.Toast
import com.example.phamthiphuonguyen_delightshop.Helper.ChangeNumberItemsListener
import com.example.phamthiphuonguyen_delightshop.Helper.TinyDB
import com.example.phamthiphuonguyen_delightshop.Model.ItemsModel


class ManagmentCart(val context: Context) {

    private val tinyDB = TinyDB(context)

    fun insertItem(item: ItemsModel) {
        var listFood = getListCart()
        val existAlready = listFood.any { it.title == item.title }
        val index = listFood.indexOfFirst { it.title == item.title }

        if (existAlready) {
            listFood[index].numberInCart = item.numberInCart
        } else {
            listFood.add(item)
        }
        tinyDB.putListObject("CartList", listFood)
        Toast.makeText(context, "Đã thêm sản phẩm vào giỏ hàng", Toast.LENGTH_SHORT).show()
    }

    fun deleteItem(item: ItemsModel) {
        var listFood = getListCart()
        // Tìm sản phẩm trong giỏ hàng theo tên
        val index = listFood.indexOfFirst { it.title == item.title }

        if (index != -1) {
            // Nếu sản phẩm tồn tại trong giỏ hàng, xóa sản phẩm đó
            listFood.removeAt(index)
            tinyDB.putListObject("CartList", listFood)
            Toast.makeText(context, "Đã xóa sản phẩm khỏi giỏ hàng", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Sản phẩm không tồn tại trong giỏ hàng", Toast.LENGTH_SHORT).show()
        }
    }


    fun getListCart(): ArrayList<ItemsModel> {
        return tinyDB.getListObject("CartList") ?: arrayListOf()
    }

    fun clearCart() {
        tinyDB.remove("CartList")  // Xóa toàn bộ giỏ hàng khỏi TinyDB
        Toast.makeText(context, "Giỏ hàng đã được làm trống", Toast.LENGTH_SHORT).show()  // Thông báo cho người dùng
    }


    fun minusItem(listFood: ArrayList<ItemsModel>, position: Int, listener: ChangeNumberItemsListener) {
        if (listFood[position].numberInCart == 1) {
            listFood.removeAt(position)
        } else {
            listFood[position].numberInCart--
        }
        tinyDB.putListObject("CartList", listFood)
        listener.onChanged()
    }

    fun plusItem(listFood: ArrayList<ItemsModel>, position: Int, listener: ChangeNumberItemsListener) {
        listFood[position].numberInCart++
        tinyDB.putListObject("CartList", listFood)
        listener.onChanged()
    }

    fun getTotalFee(): Double {
        val listFood = getListCart()
        var fee = 0.0
        for (item in listFood) {
            fee += item.price * item.numberInCart
        }
        return fee
    }
}