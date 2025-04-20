package com.example.phamthiphuonguyen_delightshop.Activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import coil.compose.rememberAsyncImagePainter
import com.example.phamthiphuonguyen_delightshop.Helper.ChangeNumberItemsListener
import com.example.phamthiphuonguyen_delightshop.Model.ItemsModel
import com.example.phamthiphuonguyen_delightshop.R
import com.example.project1762.Helper.ManagmentCart
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CartActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent{
            CartSreen(ManagmentCart(this),
                onBackClick = {
                    finish() })
        }
    }
}
val database = FirebaseDatabase.getInstance()

@Composable
private fun CartSreen(
    managmentCart: ManagmentCart= ManagmentCart(LocalContext.current),
    onBackClick:()-> Unit

) {
    val cartItems= remember { mutableStateOf(managmentCart.getListCart()) }
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
                text = "Giỏ hàng ",
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
        if(cartItems.value.isEmpty()) {

            Text(text = "Giỏ hàng trống",
                modifier = Modifier.align(Alignment.CenterHorizontally))

            Image(
                painter = painterResource(id = R.drawable.cart_empty),
                contentDescription = null,
                modifier = Modifier
                    .padding(top = 48.dp)
                    .height(350.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.Fit
            )

        }else{
            CartList(
                cartItems=cartItems.value,managmentCart
            ){
                cartItems.value=managmentCart.getListCart()
                calculatorCart(managmentCart,tax)
            }

            CartSummary(
                itemTotal=managmentCart.getTotalFee(),
                tax=tax.value,
                delivery = 10.0,
                cartItems = cartItems.value
            )
        }
    }
}

@Composable
fun CartSummary(itemTotal: Double, tax: Double, delivery: Double, cartItems: ArrayList<ItemsModel>) {
    val total = itemTotal + tax + delivery
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text(text = "Tổng tiền sản phẩm:", Modifier.weight(1f), fontWeight = FontWeight.Bold, color = colorResource(R.color.grey))
            Text(text = "${itemTotal}₫")
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text(text = "Thuế:", Modifier.weight(1f), fontWeight = FontWeight.Bold, color = colorResource(R.color.grey))
            Text(text = "${tax}₫")
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text(text = "Phí vận chuyển:", Modifier.weight(1f), fontWeight = FontWeight.Bold, color = colorResource(R.color.grey))
            Text(text = "${delivery}₫")
        }

        Box(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(colorResource(R.color.grey))
                .padding(vertical = 16.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 16.dp)
        ) {
            Text(text = "Tổng đơn:", Modifier.weight(1f), fontWeight = FontWeight.Bold, color = colorResource(R.color.grey))
            Text(text = "${total}₫")
        }


        Button(
            onClick = {
                val intent = Intent(context, PurchaseActivity::class.java).apply {
                    putExtra("totalPrice", total)
                }
                context.startActivity(intent)
            },
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.green)),
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth()
                .height(50.dp)
        )  {
            Text(text = "Mua ngay", fontSize = 18.sp, color = Color.White)
        }
    }
}


fun calculatorCart(
    managmentCart: ManagmentCart,
    tax: MutableState<Double>
) {
     val percentTax=0.02
     tax.value= Math.round(managmentCart.getTotalFee()*percentTax*100)/100.0
}

@Composable
fun CartList(cartItems: ArrayList<ItemsModel>,
             managmentCart: ManagmentCart,
             onItemChange:()->Unit) {
    LazyColumn(Modifier.padding(top=16.dp)){
        items(cartItems){item->
            CartItem(cartItems,
                item=item,
                managmentCart=managmentCart,
                onItemChange=onItemChange)
        }
    }
}

@Composable
fun CartItem(
    cartItems: ArrayList<ItemsModel>,
    item: ItemsModel,managmentCart: ManagmentCart,
    onItemChange: () -> Unit
) {

    val context = LocalContext.current
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorResource(R.color.LightGreen), shape = RoundedCornerShape(20.dp))
            .padding(8.dp)
              ) {
        val(pic,titleTxt,feeEachTime,totalEachItem,Quantity,deleteBtn)=createRefs()
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
            text = "${item.price}₫", color = colorResource(R.color.green),
            modifier = Modifier
                .constrainAs(feeEachTime) {
                    start.linkTo(titleTxt.start)
                    top.linkTo(titleTxt.bottom)
                }
                .padding(start = 8.dp,top=8.dp)
        )
        var itemInCart by remember { mutableStateOf(item.numberInCart) }
        var inventory by remember { mutableStateOf(item.inventory) }
        Text(
            text = "${itemInCart*item.price}₫",
            fontSize=18.sp,
            fontWeight = FontWeight.Bold, modifier = Modifier
                .constrainAs(totalEachItem) {
                    start.linkTo(titleTxt.start)
                    bottom.linkTo(pic.bottom)
                }
                .padding(start = 8.dp)
        )

        Box(
            modifier = Modifier
               .clip(CircleShape)
               .constrainAs(deleteBtn) {
                    top.linkTo(pic.top)
                    end.linkTo(parent.end)
                }
                .background(colorResource(R.color.LightGrey))
                .clickable {
                    managmentCart.deleteItem(item)
                    onItemChange()
                }
        ) {
            Image(
                painter = painterResource(id = R.drawable.remove),
                contentDescription = "Xóa sản phẩm",
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.TopEnd)
            )
        }

        ConstraintLayout(modifier = Modifier
            .width(100.dp)
            .constrainAs(Quantity) {
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
            }
            .background(colorResource(R.color.LightGrey),
                shape = RoundedCornerShape(10.dp))
        ) {
            val (plusCartBtn,minusCartBtn,numberItemTxt)=createRefs()
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
            Box(
                modifier = Modifier
                    .padding(2.dp)
                    .size(28.dp)
                    .background(colorResource(R.color.green),
                        shape = RoundedCornerShape(10.dp))
                    .constrainAs(plusCartBtn) {
                        end.linkTo(parent.end)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)

                    }
                    .clickable {
                        if (itemInCart < inventory) {
                            managmentCart.plusItem(cartItems, cartItems.indexOf(item),
                                object : ChangeNumberItemsListener {
                                    override fun onChanged() {
                                        itemInCart += 1
                                        onItemChange()
                                    }
                                })
                        } else {
                            Toast.makeText(
                                context,
                                "Số lượng đã đạt giới hạn",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

            ){
                Text(
                    text = "+",
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center),
                    textAlign = TextAlign.Center
                )
            }
            Box(
                modifier = Modifier
                    .padding(2.dp)
                    .size(28.dp)
                    .background(colorResource(R.color.white),
                        shape = RoundedCornerShape(10.dp))
                    .constrainAs(minusCartBtn) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    }
                    // Disable clickable if itemInCart is 1
                    .clickable(enabled = itemInCart > 1) {
                        managmentCart.minusItem(cartItems,
                            cartItems.indexOf(item), object : ChangeNumberItemsListener {
                                override fun onChanged() {
                                    itemInCart -= 1
                                    onItemChange()
                                }
                            })
                    }
            ){
                Text(
                    text = "-",
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.Center),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}