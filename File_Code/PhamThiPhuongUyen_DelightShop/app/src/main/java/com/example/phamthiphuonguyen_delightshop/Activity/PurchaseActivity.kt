package com.example.phamthiphuonguyen_delightshop.Activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.rememberAsyncImagePainter
import com.example.phamthiphuonguyen_delightshop.Helper.ChangeNumberItemsListener
import com.example.phamthiphuonguyen_delightshop.Model.ItemsModel
import com.example.phamthiphuonguyen_delightshop.R
import com.example.project1762.Helper.ManagmentCart
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.example.phamthiphuonguyen_delightshop.ViewModel.MainViewModel


class PurchaseActivity : AppCompatActivity() {
    private val viewModel= MainViewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val totalPrice = intent.getDoubleExtra("totalPrice", 0.0)

        setContent {


            PurchaseScreen(totalPrice,ManagmentCart(this),
                onBackClick = {
                    finish()
                }, viewMain = viewModel
            )
        }
    }


}

@Composable
private fun PurchaseScreen(totalPrice: Double,
    managmentCart: ManagmentCart= ManagmentCart(LocalContext.current),
    onBackClick:()-> Unit,
    viewMain: MainViewModel
) {
    val orderItems= remember { mutableStateOf(managmentCart.getListCart()) }
    val tax=remember { mutableStateOf(0.0) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)

    ) {
        ConstraintLayout(modifier = Modifier.padding(16.dp)) {
            val(backBtn,cartTxt) = createRefs()
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(cartTxt){centerTo(parent)},
                text = "Đơn đặt hàng ",
                textAlign= TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize=25.sp
            )

            Image(painter = painterResource(R.drawable.back),
                contentDescription = null,
                modifier = Modifier
                    .clickable{
                        onBackClick()
                    }
                    .constrainAs(backBtn) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                    }
            )
        }

            OrderList (
                cartItems=orderItems.value,managmentCart
            ){
                orderItems.value=managmentCart.getListCart()
                calculatorCart(managmentCart,tax)
            }

        totalOrder(
                totalPrice=totalPrice,
                cartItems = orderItems.value,
                viewMain=viewMain
            )



    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun totalOrder(totalPrice: Double, cartItems: ArrayList<ItemsModel>,viewMain: MainViewModel) {
    val context = LocalContext.current
    var address by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf("Thanh toán khi nhận hàng") } // Default option
    // Các biến lưu thông tin thẻ
    var cardNumber by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { androidx.compose.material3.Text("Địa chỉ") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(painter = painterResource(id = R.drawable.address), contentDescription = "address Icon", modifier = Modifier.size(16.dp), tint = Color.Gray)
            },
            shape = RoundedCornerShape(20.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = colorResource(R.color.green),
                unfocusedIndicatorColor = Color.Gray,
                focusedLabelColor = Color.Green,
                unfocusedLabelColor = Color.Gray
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        val expanded = remember { mutableStateOf(false) }
        val payOptions = listOf("Thanh toán khi nhận hàng", "Thanh toán bằng thẻ")

        ExposedDropdownMenuBox(
            expanded = expanded.value,
            onExpandedChange = { expanded.value = !expanded.value }
        ) {
            OutlinedTextField(
                value = paymentMethod,
                onValueChange = {},
                label = { androidx.compose.material3.Text("Phương thức thanh toán") },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value)
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.payment),
                        contentDescription = "Payment Icon",
                        modifier = Modifier.size(16.dp),
                        tint = Color.Gray
                    )
                },
                shape = RoundedCornerShape(20.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = colorResource(R.color.green),
                    unfocusedIndicatorColor = Color.Gray,
                    focusedLabelColor = Color.Green,
                    unfocusedLabelColor = Color.Gray
                )
            )

            ExposedDropdownMenu(
                expanded = expanded.value,
                onDismissRequest = { expanded.value = false }
            ) {
                payOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { androidx.compose.material3.Text(option) },
                        onClick = {
                            paymentMethod = option
                            expanded.value = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (paymentMethod == "Thanh toán bằng thẻ") {
            OutlinedTextField(
                value = cardNumber,
                onValueChange = { cardNumber = it },
                label = { Text("Số thẻ") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(painter = painterResource(id = R.drawable.credit_card), contentDescription = "Credit Card Icon", modifier = Modifier.size(16.dp), tint = Color.Gray)
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = expiryDate,
                onValueChange = { expiryDate = it },
                label = { Text("Ngày hết hạn (MM/YY)") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(painter = painterResource(id = R.drawable.calendar), contentDescription = "Expiry Date Icon", modifier = Modifier.size(16.dp), tint = Color.Gray)
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = cvv,
                onValueChange = { cvv = it },
                label = { Text("CVV") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(painter = painterResource(id = R.drawable.lock), contentDescription = "CVV Icon", modifier = Modifier.size(16.dp), tint = Color.Gray)
                },
                visualTransformation = PasswordVisualTransformation() // Hiển thị dấu ***
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        Button(
            onClick = {
                saveOrderToFirebase(
                    cartItems = cartItems,
                    total = totalPrice,
                    context = context,
                    address = address,
                    paymentMethod = paymentMethod,
                    cardNumber = if (paymentMethod == "Thanh toán bằng thẻ") cardNumber else null,
                    expiryDate = if (paymentMethod == "Thanh toán bằng thẻ") expiryDate else null,
                    cvv = if (paymentMethod == "Thanh toán bằng thẻ") cvv else null,
                    viewMain = viewMain
                )
            },
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.green)),
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(text = "Đặt hàng", fontSize = 18.sp, color = Color.White)
        }

    }
}

fun saveOrderToFirebase(
    cartItems: ArrayList<ItemsModel>,
    total: Double,
    context: Context,
    address: String,
    paymentMethod: String,
    cardNumber: String?,
    expiryDate: String?,
    cvv: String?,
    viewMain: MainViewModel
) {
    val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
    val userId = sharedPreferences.getString("userId", null) ?: return // Lấy userId, nếu không có thì return luôn
    val name = sharedPreferences.getString("name", null) ?: return
    val email = sharedPreferences.getString("email", null) ?: return
    val database = FirebaseDatabase.getInstance()
    val ordersRef = database.getReference("Orders")

    val orderId = ordersRef.push().key ?: return

    // Chuẩn bị dữ liệu đơn hàng
    val paymentDetails = mutableMapOf("paymentMethod" to paymentMethod)

    if (paymentMethod == "Thanh toán bằng thẻ") {
        paymentDetails["cardNumber"] = cardNumber.toString()
        paymentDetails["expiryDate"] = expiryDate.toString()
        paymentDetails["cvv"] = cvv.toString()
    }

    val orderData = mapOf(
        "orderId" to orderId,
        "orderDate" to SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
        "status" to "Processing",
        "totalPrice" to total,
        "userId" to userId,
        "paymentMethod" to paymentMethod,
        "paymentDetails" to paymentDetails,
        "shippingAddress" to address,
        "items" to cartItems.map { item ->
            mapOf(
                "id" to item.id,
                "title" to item.title,
                "price" to item.price,
                "quantity" to item.numberInCart
            )
        }
    )

    cartItems.forEach { item ->
        viewMain.updateInventoryAfterPurchase(item.numberInCart, item.id)
    }

    // Lưu vào Firebase
    ordersRef.child(orderId).setValue(orderData)
        .addOnSuccessListener {
            Toast.makeText(context, "Đơn hàng đã được lưu", Toast.LENGTH_SHORT).show()

            val cartManager = ManagmentCart(context)
            cartManager.clearCart()


            val subject = "Xác nhận đơn hàng"
            val content = """
                Xin chào $name,
                
                Cảm ơn bạn đã chọn Delight Shop để mua sắm. Đơn hàng của bạn đã được ghi nhận và đang trong quá trình xử lý.
                
                🛒 Thông tin đơn hàng:
                - Mã đơn hàng: $orderId
                - Ngày đặt: ${SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())}
                - Tổng giá trị: $total VND
                - Địa chỉ giao hàng: $address
                
                Chúng tôi sẽ nhanh chóng chuẩn bị và giao hàng đến bạn. Bạn sẽ nhận được thông báo khi đơn hàng của bạn đã sẵn sàng để giao.
                
                Nếu bạn có bất kỳ câu hỏi nào hoặc cần hỗ trợ, đừng ngần ngại liên hệ với chúng tôi qua email hoặc fanpage chính thức của Delight Shop.
                
                🌟 Chúc bạn có những trải nghiệm tuyệt vời cùng Delight Shop! 🌟
                
                Trân trọng,
                Đội ngũ Delight Shop HCM
                """.trimIndent()

            val sender = GmailSender(email, content, subject)
            sender.start()


            // Chuyển qua OrderActivity
            val intent = Intent(context, OrdersActivity::class.java)
            context.startActivity(intent)
        }
        .addOnFailureListener {
            Toast.makeText(context, "Lưu đơn hàng thất bại", Toast.LENGTH_SHORT).show()
        }
}




@Composable
fun OrderList(cartItems: ArrayList<ItemsModel>,
             managmentCart: ManagmentCart,
             onItemChange:()->Unit) {
    LazyColumn(Modifier.padding(top=16.dp)){
        items(cartItems){item->
            OrderItem (cartItems,
                item=item,
                managmentCart=managmentCart,
                onItemChange=onItemChange)
        }
    }
}

@Composable
fun OrderItem(
    cartItems: ArrayList<ItemsModel>,
    item: ItemsModel,managmentCart: ManagmentCart,
    onItemChange: () -> Unit
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top=8.dp, bottom = 8.dp)
    ) {
        val(pic,titleTxt,feeEachTime,totalEachItem,Quantity)=createRefs()
        Image(
            painter = rememberAsyncImagePainter(item.picUrl[0]),
            contentDescription = null,
            modifier = Modifier
                .size(90.dp)
                .background(colorResource(R.color.LightGrey), shape = RoundedCornerShape(10.dp))
                .padding(8.dp)
                .constrainAs(pic) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
        )

        Text(
            text = item.title,
            modifier = Modifier
                .constrainAs(titleTxt){
                    start.linkTo(pic.end)
                    top.linkTo(pic.top)
                }
                .padding(start = 8.dp,top=8.dp)
        )
        Text(
            text = "${item.price}VND", color = colorResource(R.color.green),
            modifier = Modifier
                .constrainAs(feeEachTime) {
                    start.linkTo(titleTxt.start)
                    top.linkTo(titleTxt.bottom)
                }
                .padding(start = 8.dp,top=8.dp)
        )
        Text(
            text = "$${item.numberInCart*item.price}",
            fontSize=18.sp,
            fontWeight = FontWeight.Bold, modifier = Modifier
                .constrainAs(totalEachItem) {
                    start.linkTo(titleTxt.start)
                    bottom.linkTo(pic.bottom)
                }
                .padding(start = 8.dp)
        )
        var itemInCart by remember { mutableStateOf(item.numberInCart) }

        ConstraintLayout(modifier = Modifier
            .width(100.dp)
            .constrainAs(Quantity) {
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
            }
            .background(colorResource(R.color.LightGrey),
                shape = RoundedCornerShape(10.dp))
        ) {
            val (numberItemTxt)=createRefs()
            Text(
                text = itemInCart.toString()
                , color = Color.Black,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.constrainAs(numberItemTxt) {
                    end.linkTo(parent.end)
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
            )


        }
    }
}