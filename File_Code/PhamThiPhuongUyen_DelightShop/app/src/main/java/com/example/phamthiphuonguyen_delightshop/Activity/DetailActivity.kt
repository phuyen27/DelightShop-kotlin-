package com.example.phamthiphuonguyen_delightshop.Activity


import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.phamthiphuonguyen_delightshop.Model.ItemsModel
import com.example.phamthiphuonguyen_delightshop.R
import com.example.project1762.Helper.ManagmentCart
import java.nio.file.WatchEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.rememberAsyncImagePainter
import com.example.phamthiphuonguyen_delightshop.Helper.ManagmentFavorite

class DetailActivity : BaseActivity() {
    private lateinit var item: ItemsModel
    private lateinit var managmentCart: ManagmentCart
    private lateinit var managmentFavorite: ManagmentFavorite

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        item=intent.getParcelableExtra("object")!!
        managmentCart= ManagmentCart(this)
        managmentFavorite= ManagmentFavorite(this)

        setContent {
            DetailScreen(
                item= item,
                onBackClick = {finish()},
                managmentFavorite = managmentFavorite,
                onAddToCartClick = {
                    item.numberInCart=1
                    managmentCart.insertItem(item)
                },
                onAddToFavoriteClick = { isLiked ->
                    if (isLiked) {
                        managmentFavorite.insertItem(item)
                    } else {
                        managmentFavorite.removeItem(item)
                    }
                },
                onCartClick = {
                    startActivity(Intent(this, CartActivity::class.java))
                }
            )
        }
    }
}

@Composable
fun DetailScreen(item: ItemsModel,
                 onBackClick:()-> Unit,
                 managmentFavorite: ManagmentFavorite,
                 onAddToCartClick:()->Unit,
                 onAddToFavoriteClick: (Boolean) -> Unit,
                 onCartClick:()->Unit) {
    var selectedImageUrl by remember { mutableStateOf(item.picUrl.first()) }
    var selectedModelIndex by remember { mutableStateOf(-1) }
    var isLiked by remember { mutableStateOf(managmentFavorite.isFavorite(item)) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        ConstraintLayout(
            modifier= Modifier
                .padding(top=36.dp, bottom = 16.dp)
                .fillMaxWidth()
        ){
            val(back,fav) = createRefs()
            Image(
                painter = painterResource(R.drawable.back),
                contentDescription = "",
                modifier = Modifier
                    .clickable{onBackClick()}
                    .constrainAs(back) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                    }
            )

            Image(
                painter = painterResource(if (isLiked) R.drawable.heart else R.drawable.fav_icon),
                contentDescription = "",
                modifier = Modifier
                    .size(40.dp)
                    .constrainAs(fav) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        end.linkTo(parent.end)
                    }
                    .clickable { isLiked = !isLiked
                        onAddToFavoriteClick(isLiked)
                    }

            )

        }
        Image(painter = rememberAsyncImagePainter(model = selectedImageUrl),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(290.dp)
                .background(colorResource(R.color.LightGrey),
                    shape = RoundedCornerShape(8.dp))
                .padding(16.dp)
            )
        LazyRow(modifier = Modifier.padding(vertical = 16.dp)) {
            items (item.picUrl) {
                imageUrl->
                ImageThumbnail(
                    imageUrl= imageUrl,
                    isSelected = selectedImageUrl==imageUrl,
                    onClick ={selectedImageUrl=imageUrl}
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically
        , modifier = Modifier.padding()
        ) {
            Text(
                text=item.title,
                fontSize = 23.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(end = 16.dp)
            )

            Text(
                text = "${item.price}₫",
                fontSize = 22.sp
            )
        }

        RatingBar(rating=item.rating)
        ModelSelector(
            models = item.model,
            selectedModelIndex=selectedModelIndex,
            onModelSelected = {selectedModelIndex=it}
        )

        Text(
            text = item.description,
            fontSize = 14.sp,
            color = Color.Black,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        Row(verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()) {
            Button (
                onClick=onAddToCartClick,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.green)),
                modifier = Modifier
                    .weight(1f)
                    .padding(end=8.dp)
                    .height(50.dp)
            )
            {
                Text(text="Thêm vào giỏ hàng", fontSize = 18.sp,color=Color.White)
            }
            IconButton(
                onClick = onCartClick,
                modifier= Modifier.background(
                    colorResource(R.color.LightGrey),
                    shape = RoundedCornerShape(10.dp)
                )
            ) {
                Icon(
                    painter = painterResource(id=R.drawable.btn_2),
                    contentDescription = "Cart",
                    tint=Color.Black
                )
            }
        }
    }
}

@Composable
fun RatingBar(rating: Double) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 16.dp)) {
        Text(
            text = "Chọn kiểu sản phẩm",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )

        Image(
            painter = painterResource(id=R.drawable.star),
            contentDescription = null,
            modifier = Modifier.padding(end=8.dp)
        )

        Text(text= "$rating Rating", style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun ModelSelector(models: List<String>, selectedModelIndex: Int,
    onModelSelected:(Int)->Unit) {
        LazyRow(modifier = Modifier.padding(vertical = 8.dp)) {
            itemsIndexed(models) {index,model->
                Box(modifier = Modifier
                    .padding(end=8.dp)
                    .height(28.dp)
                    .then(
                        if(index==selectedModelIndex){
                            Modifier.border(1.dp,colorResource(R.color.green),
                                RoundedCornerShape(10.dp))

                        }
                        else{
                            Modifier
                        }
                    )
                    .background(
                        if(index==selectedModelIndex)colorResource(R.color.LightGreen)
                        else colorResource(R.color.LightGrey),
                        shape = RoundedCornerShape(10.dp)
                    )

                    .clickable{onModelSelected(index)}
                    .padding(horizontal = 16.dp)
                ){
                    Text(
                        text = model,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        color = if(index==selectedModelIndex) colorResource(R.color.green)
                        else colorResource(R.color.black),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }


@Composable
fun ImageThumbnail(
    imageUrl:String,
    isSelected: Boolean,
    onClick:()-> Unit
) {
    val backColor=if(isSelected) colorResource(R.color.LightGreen) else colorResource(R.color.LightGrey)

    Box(modifier = Modifier
        .padding(4.dp)
        .size(55.dp)
        .then(
            if (isSelected){
                Modifier.border(1.dp,colorResource(R.color.green),RoundedCornerShape(10.dp))

            }else{
                Modifier
            }
        )
        .background(backColor, shape = RoundedCornerShape(10.dp))
        .clickable(onClick=onClick)
        .padding(4.dp)
    ){
        Image(
            painter = rememberAsyncImagePainter(model = imageUrl),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
        )
    }
}