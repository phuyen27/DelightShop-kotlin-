package com.example.phamthiphuonguyen_delightshop.Activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.constraintlayout.compose.ConstraintLayout

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.phamthiphuonguyen_delightshop.Model.CategoryModel
import com.example.phamthiphuonguyen_delightshop.Model.SliderModel
import com.example.phamthiphuonguyen_delightshop.ViewModel.MainViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import kotlinx.coroutines.delay
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.core.content.ContextCompat.startActivity
import com.example.phamthiphuonguyen_delightshop.Model.ItemsModel
import com.example.phamthiphuonguyen_delightshop.R
import java.time.LocalTime


class MainActivity : BaseActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Lấy thông tin người dùng từ SharedPreferences
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val userName = sharedPreferences.getString("name", "user") ?: "user"

        setContent {
            MainActivityScreen(
                userName,
                onCartClick = {
                    startActivity(Intent(this, CartActivity::class.java))
                },
                onProfileClick = {
                    startActivity(Intent(this, UserInforActivity::class.java))
                },
                onLikeItemClick = {
                    startActivity(Intent(this, ItemLikeActivity::class.java))
                },

                onDelightClick = {
                    startActivity(Intent(this, DelightActivity::class.java))
                },
                onOdersClick = {
                    startActivity(Intent(this, OrdersActivity::class.java))
                }
            )
        }

    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainActivityScreen(userName: String, onCartClick:()->Unit, onProfileClick:()->Unit, onLikeItemClick:()-> Unit, onDelightClick:()->Unit,onOdersClick:()->Unit) {
    val viewModel= MainViewModel()
    val banners = remember { mutableStateListOf<SliderModel>() }
    val categories = remember { mutableStateListOf<CategoryModel>() }
    val recommended = remember { mutableStateListOf<ItemsModel>() }
    val context = LocalContext.current
    var showBannerLoading by remember {mutableStateOf(true)}
    var showCategoryLoading by remember {mutableStateOf(true)}
    var showRecommendLoading by remember {mutableStateOf(true)}
    var searchText by remember { mutableStateOf("") }

    val currentHour = LocalTime.now().hour
    val greeting = when {
        currentHour in 5..11 -> "Chào buổi sáng"
        currentHour in 12..17 -> "Chào buổi chiều"
        else -> "Chào buổi tối"
    }


    //banner
    LaunchedEffect(Unit) {
        viewModel.loadBanners()
        viewModel.banners.observeForever {
            banners.clear()
            banners.addAll(it)
            showBannerLoading=false
        }
    }

    //category
    LaunchedEffect(Unit) {
        viewModel.loadCategory()
        viewModel.categories.observeForever {
            categories.clear()
            categories.addAll(it)
            showCategoryLoading=false
        }
    }

    //recommended
    LaunchedEffect(Unit) {
        viewModel.loadRecommended()
        viewModel.recommended.observeForever {
            recommended.clear()
            recommended.addAll(it)
            showRecommendLoading=false
        }
    }

    ConstraintLayout(modifier= Modifier.background(Color.White)) {
        val (scrollList,bottomMenu) = createRefs()
        LazyColumn (
            modifier = Modifier
                .fillMaxSize()
                .constrainAs(scrollList) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    end.linkTo(parent.end)
                    start.linkTo(parent.start)
                }
        ){
            item {
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top=48.dp, start = 16.dp,end=16.dp),
                    horizontalArrangement=Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Column {
                        Text(greeting,color= Color.Black)

                        Text(userName,
                            color = Color.Black,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Row {
                        OutlinedTextField(
                            value = searchText,
                            onValueChange = { searchText = it },
                            modifier = Modifier
                                .width(150.dp) // Đặt kích thước cố định
                                .heightIn(min = 36.dp, max = 40.dp) // Giới hạn chiều cao
                                .background(Color.Transparent),
                            placeholder = { Text("Tìm kiếm sản phẩm...", color = Color.Gray, fontSize = 4.sp) },
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                        )
                        Spacer(modifier = Modifier.width(16.dp))

                        androidx.compose.foundation.Image(
                            painter = painterResource(R.drawable.search_icon),
                            contentDescription = "",
                            modifier = Modifier
                                .clickable {
                                    val intent = Intent(context, searchActivity::class.java)
                                    intent.putExtra("SEARCH_TEXT", searchText)
                                    context.startActivity(intent)
                                }
                        )
                    }
                }
            }

            // banners
            item {
                if(showBannerLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    Banners(banners)
                }
            }

            item{
                SectionTitle("Danh mục sản phẩm","Xem tất cả")
            }

            item{
                if(showCategoryLoading) {
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                        contentAlignment = Alignment.Center)
                    {
                        CircularProgressIndicator()
                    }
                } else {
                    CategoryList(categories)
                }
            }

            item{
                SectionTitle("Gợi ý cho bạn","Xem tất cả")
            }

            item {(
                if(showRecommendLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ){
                        CircularProgressIndicator()
                    }
                }else {
                    ListItems(recommended)
                }
            )}

            item{
                Spacer(modifier = Modifier.height(100.dp))
            }
        }



        BottomMenu(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(bottomMenu){
                    bottom.linkTo(parent.bottom)
                },
            onItemClick = onCartClick,
            onProfileClick = onProfileClick,
            onLikeItemClick=onLikeItemClick,
            onDelightClick=onDelightClick,
            onOdersClick = onOdersClick
        )


    }
}

@Composable
fun CategoryList(categories: SnapshotStateList<CategoryModel>) {
    var selectedIndex by remember { mutableStateOf(-1) }

    val context =LocalContext.current
    LazyRow (modifier = Modifier
        .fillMaxWidth()
        , horizontalArrangement = Arrangement.spacedBy (16.dp),
        contentPadding = PaddingValues(start = 16.dp,end=16.dp, top=8.dp)
    ){
        items(categories.size) { index ->
            CategoryItem(
                item = categories[index],
                isSelected = selectedIndex == index,
                onItemClick = {
                    selectedIndex = index
                    Handler(Looper.getMainLooper()).postDelayed({
                        val intent= Intent(context, ListItemsActivity::class.java).apply {
                            putExtra("id",categories[index].id.toString())
                            putExtra("title",categories[index].title)
                        }
                        startActivity(context,intent,null)
                    },1000)
                }
            )
        }
    }
}

@Composable
fun CategoryItem(item: CategoryModel,isSelected: Boolean,onItemClick:()-> Unit) {
    Row(modifier = Modifier
        .clickable(onClick = onItemClick)
        .background(
            color = if (isSelected) colorResource(R.color.green) else Color.Transparent,
            shape = RoundedCornerShape(8.dp)
        ),
        verticalAlignment = Alignment.CenterVertically
    ){
        AsyncImage(
            model = (item.picUrl),
            contentDescription = item.title,

            modifier = Modifier
                .size(45.dp)
                .background(
                    color = if (isSelected) Color.Transparent else colorResource(R.color.LightGrey),
                    shape = RoundedCornerShape(8.dp)
                ),
            contentScale = ContentScale.Inside,
            colorFilter = if(isSelected) {
                ColorFilter.tint(Color.White)
            }else {
                ColorFilter.tint(Color.Black)
            }
        )
        if(isSelected) {
            Text(
                text=item.title,
                color=Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(end=8.dp)
            )
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun Banners(banners: List<SliderModel>) {
    AutoSlidingCarousel(banners = banners)
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun AutoSlidingCarousel(modifier: Modifier= Modifier,
                        pagerState: com.google.accompanist.pager.PagerState = remember { com.google.accompanist.pager.PagerState() },

                        banners: List<SliderModel>) {
    val isDragged by pagerState.interactionSource.collectIsDraggedAsState()

    LaunchedEffect(key1 = true) {
        while (true) {
            delay(5000) // Đợi 5 giây
            val nextPage = (pagerState.currentPage + 1) % banners.size
            pagerState.animateScrollToPage(
                nextPage,
                animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing) // Thêm hiệu ứng mượt
            )
        }
    }
    Column ( modifier=modifier.fillMaxSize()){
        com.google.accompanist.pager.HorizontalPager (count =banners.size, state = pagerState) {
            page->

            AsyncImage(
                model= ImageRequest.Builder(LocalContext.current)
                    .data(banners[page].url)
                    .build(),
                contentDescription=null,
                contentScale= ContentScale.FillBounds,
                modifier= Modifier
                    .padding(start=16.dp,end=16.dp,top=16.dp,bottom=8.dp)
                    .height(150.dp)
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
            )
        }
        DotIndicator(
            modifier= Modifier
                .padding(horizontal = 8.dp)
                .align(Alignment.CenterHorizontally),
            totalDots = banners.size,
            selectedIndex = if(isDragged) pagerState.currentPage else pagerState.currentPage,
            dotSize = 8.dp
        )
    }
}

@Composable
fun DotIndicator(
    modifier: Modifier= Modifier,
    totalDots: Int,
    selectedIndex: Int,
    selectedColor: Color= colorResource(R.color.green),
    unSelectedColor: Color=colorResource(R.color.grey),
    dotSize: Dp
){
    LazyRow(
        modifier= modifier
            .wrapContentWidth()
            .wrapContentHeight()
    ) {
        items (totalDots){
            index->
            IndicatorDot(
                color = if(index==selectedIndex)selectedColor else unSelectedColor,
                size=dotSize
            )

            if(index!=totalDots-1) {
                Spacer(modifier= Modifier.padding(horizontal = 2.dp))
            }
        }
    }
}

@Composable
fun IndicatorDot(modifier: Modifier= Modifier,
                 size: Dp,
                 color: Color
                 ) {
    Box(modifier=modifier
        .size(size)
        .clip(CircleShape)
        .background(color)
    )
}

@Composable
fun SectionTitle(title: String,actionText: String) {
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp,end=16.dp,top=1.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Text(
            text = title,
            color = Color.Black,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = actionText,
            color = colorResource(R.color.green)
        )
    }
}

@Composable
fun BottomMenu(modifier: Modifier,onItemClick: () -> Unit,onProfileClick: () -> Unit,onLikeItemClick:()-> Unit,onDelightClick:()->Unit,onOdersClick:()->Unit) {
    Row(modifier=modifier
        .padding(start = 16.dp,end=16.dp, bottom = 32.dp)
        .background(colorResource(R.color.green),
            shape = RoundedCornerShape(10.dp)
        ),
        horizontalArrangement = Arrangement.SpaceAround
    ){
        BottomMenuItem(icon = painterResource(R.drawable.btn_delight), text = "Delight",onItemClick=onDelightClick)
        BottomMenuItem(icon = painterResource(R.drawable.btn_2), text = "Giỏ hàng",onItemClick=onItemClick)
        BottomMenuItem(icon = painterResource(R.drawable.btn_3), text = "Yêu thích",onItemClick=onLikeItemClick)
        BottomMenuItem(icon = painterResource(R.drawable.btn_4), text = "Đơn hàng",onItemClick=onOdersClick)
        BottomMenuItem(icon=painterResource(R.drawable.btn_5), text = "Hồ sơ",onItemClick = onProfileClick)
    }
}

@Composable
fun BottomMenuItem(icon: Painter,text: String,onItemClick: (()->Unit)?=null){
    Column(modifier = Modifier
        .height(60.dp)
        .clickable{onItemClick?.invoke()}
        .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(icon, contentDescription = text,tint=Color.White, modifier = Modifier.size(25.dp))
        Text(text,color=Color.White, fontSize = 10.sp)
    }
}
