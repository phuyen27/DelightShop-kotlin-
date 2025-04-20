package com.example.phamthiphuonguyen_delightshop.Helper

import android.content.Context
import android.widget.Toast
import com.example.phamthiphuonguyen_delightshop.Model.ItemsModel

class ManagmentFavorite(val context: Context) {

    private val tinyDB = TinyDB(context)

    fun insertItem(item: ItemsModel) {
        var listFood = getFavoriteList()
        val existAlready = listFood.any { it.title == item.title }
        val index = listFood.indexOfFirst { it.title == item.title }

        if (existAlready) {
            listFood[index].numberInCart = item.numberInCart
            listFood[index].like = true
        } else {
            item.like = true
            listFood.add(item)
        }

        tinyDB.putListObject("FavoriteList", listFood)
        Toast.makeText(context, "Đã thêm vào yêu thích", Toast.LENGTH_SHORT).show()
    }

    fun isFavorite(item: ItemsModel): Boolean {
        val listFood = getFavoriteList()
        return listFood.any { it.title == item.title }
    }


    fun removeItem(item: ItemsModel) {
        var listFood = getFavoriteList()
        val existAlready = listFood.any { it.title == item.title }

        if (existAlready) {
            listFood = listFood.map {
                if (it.title == item.title) it.copy(like = false)
                else it
            }.filter { it.title != item.title } as ArrayList<ItemsModel>

            tinyDB.putListObject("FavoriteList", listFood)
            Toast.makeText(context, "Đã xóa khỏi yêu thích", Toast.LENGTH_SHORT).show()
        }
    }


    fun getFavoriteList(): ArrayList<ItemsModel> {
        return tinyDB.getListObject("FavoriteList") ?: arrayListOf()
    }
}