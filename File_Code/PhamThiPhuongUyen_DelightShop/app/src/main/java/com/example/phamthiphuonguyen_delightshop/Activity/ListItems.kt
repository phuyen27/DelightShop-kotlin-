package com.example.phamthiphuonguyen_delightshop.Activity


import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import coil.compose.AsyncImage
import com.example.phamthiphuonguyen_delightshop.Model.ItemsModel
import com.example.phamthiphuonguyen_delightshop.R

@Composable
fun ListItems(items: List<ItemsModel>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .height(500.dp)
            .padding(start = 8.dp,end=8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(items.size) {row ->
            Row (modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)

            ){
                RecommendedItem(items,row)
            }
        }
    }
}

@Composable
fun ListItemsFullSize(items: List<ItemsModel>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 8.dp,end=8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(items.size) {row ->
            Row (modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)

            ){
                RecommendedItem(items,row)
            }
        }
    }
}

@Composable
fun RecommendedItem(items: List<ItemsModel>,pos: Int) {
    var context = LocalContext.current
    Column (modifier = Modifier
        .padding(8.dp)
        .height(255.dp)){
        AsyncImage(model = items[pos].picUrl.firstOrNull(),
            contentDescription = items[pos].title,
            modifier = Modifier
                .width(175.dp)
                .background(colorResource(R.color.LightGrey), shape = RoundedCornerShape(10.dp))
                .height(175.dp)
                .padding(8.dp)
                .clickable{
                    val intent= Intent(context, DetailActivity::class.java).apply {
                        putExtra("object",items[pos])
                    }
                    startActivity(context,intent,null)
                },
            contentScale = ContentScale.Inside
        )

        Text(text=items[pos].title,
            color = Color.Black,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top=8.dp)
        )

        Row(modifier = Modifier.padding(top = 4.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween

        ){
            Row {
                Image(painter = painterResource(id=R.drawable.star),
                    contentDescription = "Rating",
                    modifier = Modifier.align(Alignment.CenterVertically))

                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text=items[pos].rating.toString(),
                    color = Color.Black,
                    fontSize = 15.sp
                )
            }
            Text(
                text = "${items[pos].price}₫",
                color=colorResource(R.color.green),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

        }
    }
}