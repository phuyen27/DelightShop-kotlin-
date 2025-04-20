package com.example.phamthiphuonguyen_delightshop.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.phamthiphuonguyen_delightshop.R
import com.google.firebase.database.*
import kotlin.random.Random

class ForgotPassActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var generatedCode: String
    private var currentScreen by mutableStateOf(ForgotPassScreenType.Email)
    private var email by mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = FirebaseDatabase.getInstance().getReference("Users")

        setContent {
            ForgotPasswordMainScreen()
        }
    }

    @Composable
    fun ForgotPasswordMainScreen() {

        when (currentScreen) {

            ForgotPassScreenType.Email -> EmailScreen()
            ForgotPassScreenType.Code -> CodeScreen()
            ForgotPassScreenType.NewPassword -> NewPasswordScreen()
        }
    }

    // Màn hình 1: Nhập email
    @Composable
    fun EmailScreen() {
        var email by remember { mutableStateOf("") }
        var context = LocalContext.current
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Quên mật khẩu")
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Nhập email của bạn") },
                modifier = Modifier.fillMaxWidth(),
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
                onClick = { handleEmailSubmit(email) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
            ) {
                Text("Gửi yêu cầu")

            }
        }
    }

    // Bước 1: Kiểm tra email và tạo mã xác nhận
    private fun handleEmailSubmit(inputEmail: String) {
        email = inputEmail
        database.orderByChild("email").equalTo(inputEmail)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {

                        // Gửi mã xác nhận
                        generatedCode = generateRandomCode()
                        val subject = "Xác nhận đổi mật khẩu - Delight Shop"
                        val content = "Xin chào,\n\nBạn vừa gửi yêu cầu thay đổi mật khẩu!" +
                                "\nMã xác nhận để thay đổi mật khẩu là: " + generatedCode +
                                "\n\nCảm ơn bạn đã sử dụng dịch vụ của chúng tôi." +
                                "\n\nTrân trọng,\nDelight Shop HCM"
                        val sender = GmailSender(inputEmail, content, subject)
                        sender.start()


                        // Chuyển qua màn hình nhập mã xác nhận
                        currentScreen = ForgotPassScreenType.Code
                    } else {
                        Toast.makeText(
                            this@ForgotPassActivity,
                            "Email không tồn tại!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        this@ForgotPassActivity,
                        "Lỗi kết nối Firebase!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    // Màn hình 2: Nhập mã xác nhận
    @Composable
    fun CodeScreen() {
        var code by remember { mutableStateOf("") }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Nhập mã xác nhận", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = code,
                onValueChange = { code = it },
                label = { Text("Mã xác nhận") },
                modifier = Modifier.fillMaxWidth(),
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
                onClick = { handleCodeSubmit(code) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text("Xác nhận mã")
            }
        }
    }

    // Bước 2: Kiểm tra mã xác nhận và chuyển qua màn hình nhập mật khẩu
    private fun handleCodeSubmit(code: String) {
        if (code == generatedCode) {
            currentScreen = ForgotPassScreenType.NewPassword
        } else {
            Toast.makeText(this@ForgotPassActivity, "Mã xác nhận không chính xác!", Toast.LENGTH_SHORT).show()
        }
    }

    // Màn hình 3: Nhập mật khẩu mới
    @Composable
    fun NewPasswordScreen() {
        var newPassword by remember { mutableStateOf("") }
        var passwordVisible by remember { mutableStateOf(false) }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Nhập mật khẩu mới")
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = { Text("Mật khẩu mới") },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = colorResource(R.color.green),
                    unfocusedIndicatorColor = Color.Gray,
                    focusedLabelColor = Color.Green,
                    unfocusedLabelColor = Color.Gray
                ),
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
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { handleNewPasswordSubmit(newPassword) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text("Cập nhật mật khẩu")
            }
        }
    }

    // Bước 3: Cập nhật mật khẩu mới vào Firebase
    private fun handleNewPasswordSubmit(newPassword: String) {
        if (newPassword.isNotEmpty()) {

            if (email.isNotEmpty()) {
                database.orderByChild("email").equalTo(email)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            for (userSnapshot in snapshot.children) {
                                userSnapshot.ref.child("password").setValue(newPassword)
                            }
                            Toast.makeText(this@ForgotPassActivity, "Mật khẩu đã được thay đổi!", Toast.LENGTH_SHORT).show()
                            finish()
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(this@ForgotPassActivity, "Lỗi cập nhật mật khẩu!", Toast.LENGTH_SHORT).show()
                        }
                    })
            } else {
                Toast.makeText(this@ForgotPassActivity, "Không tìm thấy email!", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this@ForgotPassActivity, "Mật khẩu không được để trống!", Toast.LENGTH_SHORT).show()
        }
    }


    // Tạo mã xác nhận ngẫu nhiên (5 chữ số)
    private fun generateRandomCode(): String {
        return (10000..99999).random().toString()
    }

    // Kiểu trạng thái cho các màn hình
    enum class ForgotPassScreenType {
        Email,
        Code,
        NewPassword
    }
}
