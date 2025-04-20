package com.example.phamthiphuonguyen_delightshop.Activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.content.ContextCompat.startActivity
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.phamthiphuonguyen_delightshop.Model.ItemsModel
import com.example.phamthiphuonguyen_delightshop.R
import com.example.phamthiphuonguyen_delightshop.ViewModel.MainViewModel

class searchActivity : BaseActivity() {
    private val viewModel= MainViewModel()
    private var name:String=""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val searchText = intent.getStringExtra("SEARCH_TEXT") ?: ""

        setContent {
            searchItemScreen(
                onBackClick = { finish() },
                viewModel = viewModel,
                name = searchText
            )
        }
    }
}

@Composable
fun searchItemScreen(
    onBackClick:()->Unit,
    viewModel: MainViewModel,
    name: String
) {
    val items by viewModel.search.observeAsState(emptyList())
    var isLoading by remember { mutableStateOf(true) }
    LaunchedEffect(name) {
        viewModel.loadSearch(name)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        ConstraintLayout(modifier = Modifier.padding(top=36.dp, start = 16.dp,end=16.dp)) {
            val(backBtn,cartTxt)=createRefs()

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs (cartTxt){centerTo(parent)},
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 25.sp,
                text = "Tìm kiếm sản phẩm"
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
        }else{
            ListItemsFullSize(items)
        }

        LaunchedEffect(items) {
            isLoading=items.isEmpty()
        }
    }
}
