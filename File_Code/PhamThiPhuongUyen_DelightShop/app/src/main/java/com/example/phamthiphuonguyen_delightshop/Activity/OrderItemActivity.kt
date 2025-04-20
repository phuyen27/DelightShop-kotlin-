package com.example.phamthiphuonguyen_delightshop.Activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.phamthiphuonguyen_delightshop.Model.OrderModel
import com.example.phamthiphuonguyen_delightshop.R
import com.example.phamthiphuonguyen_delightshop.ViewModel.MainViewModel

class OrderItemActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val order = OrderModel(
            orderId = intent.getStringExtra("orderId") ?: "",
            totalPrice = intent.getDoubleExtra("totalPrice", 0.0),
            orderDate = intent.getStringExtra("orderDate") ?: "",
            status = intent.getStringExtra("status") ?: "",
            paymentMethod = intent.getStringExtra("paymentMethod") ?: "",
            shippingAddress = intent.getStringExtra("shippingAddress") ?: ""
        )
        val viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        setContent {
            OrderItem(onBackClick  = {finish()},order =order, viewModel=viewModel)
        }
    }
}

@Composable
fun OrderItem(onBackClick: () -> Unit, order: OrderModel, viewModel: MainViewModel) {
    val orderItem by viewModel.orderItem.observeAsState(emptyList())
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(order.orderId) {
        viewModel.loadItem(order.orderId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F8FF)) // Pastel blue background
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.back),
                contentDescription = "Back",
                modifier = Modifier
                    .size(28.dp)
                    .clickable { onBackClick() },
                tint = Color(0xFFB22222)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Thông tin đơn hàng",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFB22222),
                fontFamily = FontFamily.Cursive,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = colorResource(R.color.white),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                OrderDetailRow("Mã đơn hàng:", order.orderId, Color(0xFF228B22))
                OrderDetailRow("Tổng tiền:", "${order.totalPrice} VNĐ", Color(0xFFD32F2F))
                OrderDetailRow("Ngày đặt:", order.orderDate, Color.Black)
                OrderDetailRow("Trạng thái:", order.status, Color.Black)
                OrderDetailRow("Thanh toán:", order.paymentMethod, Color.Black)
                OrderDetailRow("Địa chỉ giao hàng:", order.shippingAddress, Color.Black)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFFB22222))
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(orderItem.size) { pos ->
                    itemOrder(item = orderItem[pos], viewModel = viewModel)
                }
            }
        }
    }

    LaunchedEffect(orderItem) {
        isLoading = orderItem.isEmpty()
    }
}

@Composable
fun OrderDetailRow(label: String, value: String, color: Color) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = label, fontWeight = FontWeight.Bold, color = Color.DarkGray, fontSize = 16.sp)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = value, fontWeight = FontWeight.Medium, color = color, fontSize = 16.sp)
    }
}

@Composable
fun itemOrder(item: OrderModel.OrderItem,viewModel : MainViewModel) {

    Card(
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = Color(0xFFFFF0F0),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = "Tên SP: ${item.title}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF228B22))
            Text(text = "Số lượng: ${item.quantity}", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.Black)


        }
    }
}
