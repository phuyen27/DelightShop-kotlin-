package com.example.phamthiphuonguyen_delightshop.Activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
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
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import coil.compose.rememberImagePainter
import com.example.phamthiphuonguyen_delightshop.Helper.ChangeNumberItemsListener
import com.example.phamthiphuonguyen_delightshop.Helper.ManagmentFavorite
import com.example.phamthiphuonguyen_delightshop.Model.ItemsModel
import com.example.phamthiphuonguyen_delightshop.R
import com.example.phamthiphuonguyen_delightshop.ViewModel.MainViewModel
import com.example.project1762.Helper.ManagmentCart

class ItemLikeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ItemLikeScreen(ManagmentFavorite(this),
                onBackClick = {
                    finish() })
        }
    }
}


@Composable
private fun ItemLikeScreen(
    managementFavorite: ManagmentFavorite=ManagmentFavorite(LocalContext.current),
    onBackClick: () -> Unit

) {
    val favoriteItems=remember { mutableStateOf(managementFavorite.getFavoriteList()) }
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
                text = "Sản phẩm yêu thích",
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
        if(favoriteItems.value.isEmpty()) {

            Text(text = "Mục yêu thích trống",
                modifier = Modifier.align(Alignment.CenterHorizontally))

            Image(
                painter = painterResource(id = R.drawable.empty_favorite),
                contentDescription = null,
                modifier = Modifier
                    .padding(top = 48.dp)
                    .height(350.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.Fit
            )

        }else{
            FavoriteList(
                favoriteItems = favoriteItems.value,
                managementFavorite
            )
        }
    }
}
@Composable
fun FavoriteList(favoriteItems: ArrayList<ItemsModel>,
             managementFavorite: ManagmentFavorite) {
    LazyColumn(Modifier.padding(top=16.dp)){
        items(favoriteItems){item->
            FavoriteItem(favoriteItems,
                item=item,
                managementFavorite=managementFavorite)
        }
    }
}

@Composable
fun FavoriteItem(
    cartItems: ArrayList<ItemsModel>,
    item: ItemsModel,
    managementFavorite: ManagmentFavorite
) {
    val context = LocalContext.current // Lấy context để mở Activity

    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 8.dp)
            .clickable {
                val intent = Intent(context, DetailActivity::class.java).apply {
                    putExtra("object", item) // Truyền đối tượng sản phẩm
                }
                context.startActivity(intent)
            }
    ) {
        val (pic, titleTxt, feeEachTime) = createRefs()

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
                .constrainAs(titleTxt) {
                    start.linkTo(pic.end)
                    top.linkTo(pic.top)
                }
                .padding(start = 8.dp, top = 8.dp)
        )

        Text(
            text = "${item.price}₫",
            color = colorResource(R.color.green),
            modifier = Modifier
                .constrainAs(feeEachTime) {
                    start.linkTo(titleTxt.start)
                    top.linkTo(titleTxt.bottom)
                }
                .padding(start = 8.dp, top = 8.dp)
        )
    }
}

