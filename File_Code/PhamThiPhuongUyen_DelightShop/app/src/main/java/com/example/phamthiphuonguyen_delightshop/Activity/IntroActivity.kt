package com.example.phamthiphuonguyen_delightshop.Activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.phamthiphuonguyen_delightshop.R


class IntroActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        setContent {
            IntroScreen(
                onClick = {
                    startActivity(Intent(this, LoginActivity::class.java))
                },
                onRegisterClick = {
                    startActivity(Intent(this, RegisterActivity::class.java))
                }
            )
        }
    }
}
@Composable
fun IntroScreen(onClick: () -> Unit = {}, onRegisterClick: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.White, colorResource(R.color.green), Color.White),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                )
            )
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_intro),
            contentDescription = null,
            modifier = Modifier
                .padding(top = 48.dp)
                .height(350.dp)
                .fillMaxWidth(),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(32.dp))

        val annotatedText = buildAnnotatedString {

            append("Chào mừng bạn đến với ")
            withStyle(style = SpanStyle(color = Color(0xFFB71C1C), fontWeight = FontWeight.Bold)) {
                append("Delight")
            }
        }


        Text(
            text = annotatedText,
            fontFamily = FontFamily.Cursive,
            fontSize = 30.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))


        // Button "Explore Now"
        Button(
            onClick = { onClick() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.Darkgreen)),
            shape = RoundedCornerShape(25.dp),
            elevation = ButtonDefaults.elevatedButtonElevation(10.dp)
        ) {
            Text(
                text = "Đăng nhập",
                color = Color.White,
                fontSize = 18.sp
            )
        }

        // Button "Register"
        Button(
            onClick = { onRegisterClick() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            border = ButtonDefaults.outlinedButtonBorder.copy(
                brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFB71C1C))
            ),
            shape = RoundedCornerShape(25.dp),
        ) {
            Text(
                text = "Đăng ký",
                color = Color(0xFFB71C1C),
                fontSize = 18.sp
            )
        }
    }
}
