package com.example.phamthiphuonguyen_delightshop.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.phamthiphuonguyen_delightshop.Model.CategoryModel
import com.example.phamthiphuonguyen_delightshop.Model.ItemsModel
import com.example.phamthiphuonguyen_delightshop.Model.OrderModel
import com.example.phamthiphuonguyen_delightshop.Model.SliderModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch

class MainViewModel(): ViewModel() {
    private val firebaseDatabase= FirebaseDatabase.getInstance()
    private val _category= MutableLiveData<MutableList<CategoryModel>>()
    private val _banner = MutableLiveData<List<SliderModel>>()
    private val _recommended= MutableLiveData<MutableList<ItemsModel>>()
    private val _search = MutableLiveData<MutableList<ItemsModel>>()
    private val _orderItems = MutableLiveData<MutableList<OrderModel.OrderItem>>()
    private val _item  = MutableLiveData<MutableList<ItemsModel>>()
    private val _itemOrder = MutableLiveData<MutableList<ItemsModel>>()

    val banners: LiveData<List<SliderModel>> = _banner
    val categories: LiveData<MutableList<CategoryModel>> =_category
    val recommended: LiveData<MutableList<ItemsModel>> = _recommended
    val search: LiveData<MutableList<ItemsModel>> = _search
    private val _orders = MutableLiveData<MutableList<OrderModel>>()
    val orders: LiveData<MutableList<OrderModel>>  = _orders
    val orderItem : LiveData<MutableList<OrderModel.OrderItem>> =  _orderItems
    val item: LiveData<MutableList<ItemsModel>> = _item
    val itemOrder: LiveData<MutableList<ItemsModel>> = _itemOrder

    fun loadOrderByUser(userId: String) {
        val Ref=firebaseDatabase.getReference("Orders")
        val query: Query=Ref.orderByChild("userId").equalTo(userId)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists=mutableListOf<OrderModel>()
                for(childSnapshot in snapshot.children) {
                    val list=childSnapshot.getValue(OrderModel::class.java)
                    if(list!=null){
                        lists.add(list)
                    }
                }

                _orders.value=lists
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    fun updateInventoryAfterPurchase(itemId: Int, quantityPurchased: Int) {
        val itemRef = firebaseDatabase.getReference("Items")
        val query: Query = itemRef.orderByChild("id").equalTo(itemId.toDouble())

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (childSnapshot in snapshot.children) {
                    val item = childSnapshot.getValue(ItemsModel::class.java)
                    if (item != null) {
                        val currentInventory = item.inventory
                        val newInventory = (currentInventory - quantityPurchased).coerceAtLeast(0) // không âm

                        // Cập nhật tồn kho mới lên Firebase
                        childSnapshot.ref.child("inventory").setValue(newInventory)
                            .addOnSuccessListener {
                                Log.d("InventoryUpdate", "Cập nhật tồn kho thành công")
                            }
                            .addOnFailureListener {
                                Log.e("InventoryUpdate", "Lỗi khi cập nhật tồn kho", it)
                            }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("InventoryUpdate", "Lỗi khi đọc dữ liệu: ${error.message}")
            }
        })
    }


    fun loadItem(orderId: String) {
        val ref = firebaseDatabase.getReference("Orders")
        val query: Query = ref.orderByChild("orderId").equalTo(orderId)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists = mutableListOf<OrderModel.OrderItem>()

                for (childSnapshot in snapshot.children) {
                    val order = childSnapshot.getValue(OrderModel::class.java)
                    if (order != null) {
                        lists.addAll(order.items)
                    }
                }

                _orderItems.value = lists
            }

            override fun onCancelled(error: DatabaseError) {
                // Xử lý lỗi
            }
        })
    }

    fun loadItemOrder(itemsId: Int) {
        val ref = firebaseDatabase.getReference("Items")
        val query: Query = ref.orderByChild("id").equalTo(itemsId.toDouble())
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists = mutableListOf<ItemsModel>()

                for (childSnapshot in snapshot.children) {
                    val item = childSnapshot.getValue(ItemsModel::class.java)
                    if (item != null) {
                        lists.add(item)
                    }
                }

                _itemOrder.value = lists
            }

            override fun onCancelled(error: DatabaseError) {
                // Xử lý lỗi
            }
        })
    }

    fun loadSearch(title: String) {
        val Ref = firebaseDatabase.getReference("Items")

        Ref.get().addOnSuccessListener { snapshot ->
            val lists = mutableListOf<ItemsModel>()

            for (childSnapshot in snapshot.children) {
                val list = childSnapshot.getValue(ItemsModel::class.java)

                if (list != null && list.title.contains(title, ignoreCase = true)) {
                    lists.add(list)
                }
            }

            _search.value = lists
            Log.d("SearchResult", "Tìm thấy ${lists.size} kết quả")
        }.addOnFailureListener {
            Log.e("FirebaseError", "Lỗi khi tải dữ liệu", it)
        }
    }





    fun loadFiltered(id: String) {
        val Ref=firebaseDatabase.getReference("Items")
        val query: Query=Ref.orderByChild("categoryId").equalTo(id)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists=mutableListOf<ItemsModel>()
                for(childSnapshot in snapshot.children) {
                    val list=childSnapshot.getValue(ItemsModel::class.java)
                    if(list!=null){
                        lists.add(list)
                    }
                }

                _recommended.value=lists
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    fun loadRecommended() {
        val Ref=firebaseDatabase.getReference("Items")
        val query: Query=Ref.orderByChild("showRecommended").equalTo(true)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
               val lists=mutableListOf<ItemsModel>()
                for(childSnapshot in snapshot.children) {
                    val list=childSnapshot.getValue(ItemsModel::class.java)
                    if(list!=null){
                        lists.add(list)
                    }
                }

                _recommended.value=lists
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }



    fun loadBanners() {
        val Ref=firebaseDatabase.getReference("Banner")
        Ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists = mutableListOf<SliderModel>()
                for (childSnapshot in snapshot.children) {
                    val list=childSnapshot.getValue(SliderModel::class.java)
                    if (list!=null) {
                        lists.add(list)
                    }
                }

                _banner.value=lists
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    fun loadCategory() {
        val Ref =firebaseDatabase.getReference("Category")
        Ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists=mutableListOf<CategoryModel>()
                for(childSnapshot in snapshot.children) {
                    val list= childSnapshot.getValue(CategoryModel::class.java)
                    if (list!=null) {
                        lists.add(list)
                    }
                }
                _category.value=lists
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}