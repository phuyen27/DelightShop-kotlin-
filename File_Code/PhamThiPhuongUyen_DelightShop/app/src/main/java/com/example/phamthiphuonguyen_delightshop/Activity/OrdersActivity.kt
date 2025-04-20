package com.example.phamthiphuonguyen_delightshop.Activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import com.example.phamthiphuonguyen_delightshop.Model.OrderModel
import com.example.phamthiphuonguyen_delightshop.R
import com.example.phamthiphuonguyen_delightshop.ViewModel.MainViewModel

class OrdersActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", "0") ?: "0"

        val viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        setContent {
            OrdersItemScreen(
                onBackClick = {finish()},
                viewModel=viewModel,
                userId=userId
            )
        }
    }
}


@Composable
fun OrdersItemScreen(
    onBackClick:()->Unit,
    viewModel: MainViewModel,
    userId: String
) {
    val orders by viewModel.orders.observeAsState(emptyList())
    var isLoading by remember { mutableStateOf(true) }
    LaunchedEffect(userId) {
        viewModel.loadOrderByUser(userId)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        ConstraintLayout(modifier = Modifier.padding(top=36.dp, start = 16.dp,end=16.dp)) {
            val(backBtn,cartTxt)=createRefs()

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs (cartTxt){centerTo(parent)  },
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 25.sp,
                text = "Thông tin đặt hàng"
            )

            Image(
                painter = painterResource(R.drawable.back),
                contentDescription = null,
                modifier = Modifier.clickable{
                    onBackClick()
                }
                    .constrainAs(backBtn) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                    }
            )
        }
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ){
                CircularProgressIndicator()
            }
        } else if (orders.isEmpty()) {
            // Hiển thị ảnh khi không có đơn hàng
            EmptyOrdersScreen()
        }
        else{
            OrderItemsFullSize(orders)
        }

        LaunchedEffect(orders) {
            isLoading=false
        }
    }
}

@Composable
fun EmptyOrdersScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.empty_order),
            contentDescription = "Không có đơn hàng",
            modifier = Modifier
                .size(200.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Bạn chưa có đơn hàng nào!",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Gray
        )
    }
}


@Composable
fun OrderItemsFullSize(orders: List<OrderModel>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(orders.size) { pos ->
            OrderItemCard(order = orders[pos])
        }
    }
}

@Composable
fun OrderItemCard(order: OrderModel) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                val intent = Intent(context, OrderItemActivity::class.java).apply {
                    putExtra("orderId", order.orderId)
                    putExtra("totalPrice", order.totalPrice)
                    putExtra("orderDate", order.orderDate)
                    putExtra("status", order.status)
                    putExtra("paymentMethod", order.paymentMethod)
                    putExtra("shippingAddress", order.shippingAddress)
                }
                context.startActivity(intent)
            },

        shape = RoundedCornerShape(16.dp),
        elevation = 6.dp,
        backgroundColor = Color.White
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = "Order Icon",
                tint = Color(0xFF4CAF50),
                modifier = Modifier.size(40.dp)
            )

            Text(
                text = order.orderId,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Text(
                text = "${order.totalPrice} VNĐ",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF3A3A3A)
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (order.status == "Completed") Color(0xFF4CAF50) else Color(0xFFFF9800))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = order.status,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
