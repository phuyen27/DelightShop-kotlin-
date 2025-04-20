package com.example.phamthiphuonguyen_delightshop.Activity

import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import com.example.phamthiphuonguyen_delightshop.R
import kotlinx.coroutines.delay

class DelightActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DelightScreen(onBackClick = {finish()})
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DelightScreen( onBackClick: () -> Unit) {
    val now = LocalDateTime.now()
    val christmas = LocalDateTime.of(now.year, 12, 25, 0, 0, 0)

    var secondsLeft by remember { mutableStateOf(ChronoUnit.SECONDS.between(now, christmas)) }

    // Cập nhật đếm ngược mỗi giây
    LaunchedEffect(Unit) {
        while (secondsLeft > 0) {
            delay(1000)
            secondsLeft--
        }
    }

    val daysLeft = secondsLeft / (24 * 3600)
    val hoursLeft = (secondsLeft % (24 * 3600)) / 3600
    val minutesLeft = (secondsLeft % 3600) / 60
    val secondsDisplay = secondsLeft % 60

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(colorResource(R.color.LightBlue),colorResource(R.color.white)),
                    startY = 0f,
                    endY = 500f
                )
                ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        ConstraintLayout(
            modifier = Modifier.fillMaxWidth()
        ) {
            val (backBtn) = createRefs()
            Image(
                painter = painterResource(R.drawable.back),
                contentDescription = null,
                modifier = Modifier
                    .clickable { onBackClick() }
                    .constrainAs(backBtn) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                    }
                    .padding(start = 10.dp,top=10.dp)
            )}
        Image(
            painter = painterResource(id = R.drawable.decoration),
            contentDescription = null
        )
        Spacer(modifier = Modifier.height(24.dp))
        val colors = listOf(colorResource(R.color.LightOranger), colorResource(R.color.green), Color.Blue)
        var colorIndex by remember { mutableStateOf(0) }

        val animatedColor by animateColorAsState(
            targetValue = colors[colorIndex],
            animationSpec = tween(durationMillis = 500),
            label = "Blinking Text Color"
        )


        LaunchedEffect(Unit) {
            while (true) {
                delay(500)
                colorIndex = (colorIndex + 1) % colors.size
            }
        }

        Text(
            text = "Còn $daysLeft ngày, $hoursLeft giờ, $minutesLeft phút, $secondsDisplay giây đến Giáng Sinh!",
            fontSize = 24.sp,
            modifier = Modifier.padding(16.dp),
            fontWeight = FontWeight.Bold,
            color = animatedColor,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Box(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.TopCenter
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(top = 32.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(90.dp))
                    Column {
                        Text(
                            text = "Cùng ông già noel Delight đón giáng sinh",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            Image(
                painter = painterResource(id = R.drawable.delight1),
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .padding(start = 20.dp)
                    .offset(x = (-16).dp, y = (-10).dp)
                    .clip(RoundedCornerShape(12.dp))
                    .align(Alignment.TopStart)
            )
        }

        Box(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.TopCenter
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(top = 32.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Column(

                    ) {
                        Text(
                            text = "Chơi người tuyết tạo hình xinh xắn kể cả trong nhà",
                            fontSize = 16.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(end=85.dp)
                        )

                    }
                }


            }

            Image(
                painter = painterResource(id = R.drawable.delight2),
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .offset(x = (16).dp, y = (-10).dp)
                    .clip(RoundedCornerShape(12.dp))
                    .align(Alignment.TopEnd)
                    .padding(end = 20.dp)
            )
        }


        Box(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.TopCenter
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(top = 32.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(90.dp))
                    Column {
                        Text(
                            text = "Đón giáng sinh cùng bé tuần lộc đáng yêu hết nấc!!!!",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            Image(
                painter = painterResource(id = R.drawable.delight3),
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp)
                    .padding(start = 20.dp)
                    .offset(x = (-16).dp, y = (-10).dp)
                    .clip(RoundedCornerShape(12.dp))
                    .align(Alignment.TopStart)
            )
        }
    }
}


