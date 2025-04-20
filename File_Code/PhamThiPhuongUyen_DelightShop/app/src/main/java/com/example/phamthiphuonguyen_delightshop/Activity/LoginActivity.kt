package com.example.phamthiphuonguyen_delightshop.Activity

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.compose.material3.TextFieldDefaults
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.phamthiphuonguyen_delightshop.R
import com.google.firebase.database.*

class LoginActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = FirebaseDatabase.getInstance().getReference("Users")

        setContent {
            LoginScreen { email, password ->
                authenticateUser(email, password)
            }
        }
    }

    private fun authenticateUser(email: String, password: String) {
        database.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val storedPassword = userSnapshot.child("password").value.toString()
                        if (storedPassword == password) {
                            // Đăng nhập thành công
                            Toast.makeText(this@LoginActivity, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()

                            val userId = userSnapshot.child("userId").value.toString()
                            val name = userSnapshot.child("name").value.toString()
                            val phone = userSnapshot.child("phone").value.toString()
                            val address = userSnapshot.child("address").value.toString()
                            val paymentMethod = userSnapshot.child("paymentMethod").value.toString()
                            val userImg = userSnapshot.child("userImg").value.toString()
                            val dob =userSnapshot.child("dob").value.toString()
                            val gender = userSnapshot.child("gender").value.toString()
                            val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                            with(sharedPreferences.edit()) {
                                putString("userId", userId)
                                putString("name", name)
                                putString("email", email)
                                putString("phone", phone)
                                putString("address", address)
                                putString("paymentMethod", paymentMethod)
                                putString("userImg", userImg)
                                putString("dob", dob)
                                putString("gender", gender)
                                apply()
                            }

                            val subject = "Xác nhận đăng nhập - Delight Shop"
                            val content = "Xin chào,\n\nBạn vừa đăng nhập vào tài khoản Delight Shop!\n\nCảm ơn bạn đã sử dụng dịch vụ của chúng tôi.\n\nTrân trọng,\nDelight Shop HCM"
                            val sender = GmailSender(email, content, subject)
                            sender.start()



                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish()

                            return
                        }
                    }

                    Toast.makeText(this@LoginActivity, "Sai mật khẩu!", Toast.LENGTH_SHORT).show()

                } else {
                    Toast.makeText(this@LoginActivity, "Email không tồn tại!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@LoginActivity, "Lỗi kết nối Firebase!", Toast.LENGTH_SHORT).show()
            }
        })
    }
}


@Composable
fun LoginScreen(onLogin: (String, String) -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(colorResource(R.color.LightBlue),colorResource(R.color.white)),
                    startY = 0f,
                    endY = 500f // Điều chỉnh giá trị này để kiểm soát độ dài gradient
                )
            )

            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(id = R.drawable.login_img),
            contentDescription = "Intro Image",
            modifier = Modifier
                .size(280.dp)
                .padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.user),
                    contentDescription = "Email Icon",
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

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Mật khẩu") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.lock),
                    modifier = Modifier.size(20.dp),
                    contentDescription = "Lock Icon",
                    tint = Color.Gray
                )
            },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        painter = painterResource(
                            id = if (passwordVisible) R.drawable.view else R.drawable.hide
                        ),
                        modifier = Modifier.size(20.dp),
                        contentDescription = if (passwordVisible) "Ẩn mật khẩu" else "Hiện mật khẩu",
                        tint = Color.Gray
                    )
                }
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

        Button(
            onClick = { onLogin(email, password) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
        ) {
            Text("Đăng nhập", color = Color.White)

        }

        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = "Quên mật khẩu?",
            color = colorResource(R.color.LightOranger),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontStyle = FontStyle.Italic, // Chữ nghiêng
                textDecoration = TextDecoration.Underline // Chữ gạch chân
            ),
            modifier = Modifier
                .clickable {
                    // Chuyển đến màn hình ForgotPassActivity khi nhấn
                    val intent = Intent(context, ForgotPassActivity::class.java)
                    context.startActivity(intent)
                }

        )
    }
}
