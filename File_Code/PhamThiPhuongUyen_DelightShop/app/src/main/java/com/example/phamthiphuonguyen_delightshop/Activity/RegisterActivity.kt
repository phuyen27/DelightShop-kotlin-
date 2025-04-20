package com.example.phamthiphuonguyen_delightshop.Activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.phamthiphuonguyen_delightshop.R
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.UUID
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.lifecycleScope
import coil.compose.rememberImagePainter
import java.text.SimpleDateFormat
import java.util.Calendar
import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.material.Text
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.DropdownMenuItem
import java.util.Locale

val cloudinary = Cloudinary(ObjectUtils.asMap(
    "cloud_name", "duunwe78n",
    "api_key", "165915974528129",
    "api_secret", "OFAb8CW7lGVA4dS9ORyMZzj_TWs"
))

class RegisterActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private var imageUrl: String = ""
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { uploadImageToCloudinary(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = FirebaseDatabase.getInstance().getReference("Users")

        setContent {
            RegisterScreen(
                onRegister = { name, email, password, phone, address, dob, gender ->
                    registerUser(name, email, password, phone, address, imageUrl, dob, gender)
                },
                onPickImage = { pickImageLauncher.launch("image/*") }
            )
        }
    }

    private fun registerUser(
        name: String,
        email: String,
        password: String,
        phone: String,
        address: String,
        imageUrl: String,
        dob: String,
        gender: String
    ) {

        generateUserId { userId ->
            val user = mapOf(
                "userId" to userId,
                "name" to name,
                "email" to email,
                "password" to password,
                "phone" to phone,
                "address" to address,
                "userImg" to imageUrl,
                "dob" to dob,
                "gender" to gender
            )

            // Lưu user vào Firebase
            database.child(userId.toString()).setValue(user).addOnSuccessListener {
                Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show()
                val subject = "🎉 Chào mừng bạn đến với Delight Shop! - Xác nhận đăng ký"
                val content = """
                Xin chào $name,
            
                Chúng tôi rất vui mừng chào đón bạn đến với Delight Shop! Tài khoản của bạn đã được tạo thành công.
            
                Với Delight Shop, bạn sẽ có trải nghiệm mua sắm nhanh chóng, tiện lợi cùng nhiều ưu đãi hấp dẫn. Hãy đăng nhập và khám phá ngay!
            
                Nếu bạn có bất kỳ câu hỏi nào hoặc cần hỗ trợ, đừng ngần ngại liên hệ với chúng tôi qua email hoặc fanpage chính thức.
            
                🌟 Chúc bạn có những trải nghiệm tuyệt vời cùng Delight Shop! 🌟
            
                Trân trọng,
                Delight Shop HCM
            """.trimIndent()

                val sender = GmailSender(email, content, subject)
                sender.start()

                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }.addOnFailureListener {
                Toast.makeText(this, "Lỗi đăng ký!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun generateUserId(callback: (Long) -> Unit) {
        val userId = (100000L..999999L).random() // Tạo số ngẫu nhiên 6 chữ số
        database.child(userId.toString()).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                // Nếu userId đã tồn tại, tạo lại
                generateUserId(callback)
            } else {
                // Nếu userId chưa tồn tại, trả về userId hợp lệ
                callback(userId)
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Lỗi kiểm tra userId!", Toast.LENGTH_SHORT).show()
        }
    }



    private fun uploadImageToCloudinary(imageUri: Uri) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val inputStream = contentResolver.openInputStream(imageUri)
                val response = cloudinary.uploader().upload(inputStream, ObjectUtils.emptyMap())
                val uploadedImageUrl = response["secure_url"] as String

                // Update imageUrl with the uploaded image URL
                imageUrl = uploadedImageUrl

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@RegisterActivity, "Ảnh đã tải lên!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@RegisterActivity, "Lỗi tải ảnh!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(onRegister: (String, String, String, String, String, String, String) -> Unit, onPickImage: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Nam") }
    val context=LocalContext.current
    // Dropdown Menu state for gender
    val expanded = remember { mutableStateOf(false) }
    val genderOptions = listOf("Nam", "Nữ", "Khác")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Các trường nhập liệu
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Họ và tên") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(painter = painterResource(id = R.drawable.user), contentDescription = "user Icon", modifier = Modifier.size(16.dp), tint = Color.Gray)
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

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(painter = painterResource(id = R.drawable.email), contentDescription = "Email Icon", modifier = Modifier.size(16.dp), tint = Color.Gray)
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

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Mật khẩu") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(painter = painterResource(id = R.drawable.lock), contentDescription = "pass Icon", modifier = Modifier.size(16.dp), tint = Color.Gray)
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

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Số điện thoại") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(painter = painterResource(id = R.drawable.telephone), contentDescription = "phone Icon", modifier = Modifier.size(16.dp), tint = Color.Gray)
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

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Địa chỉ") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(painter = painterResource(id = R.drawable.address), contentDescription = "address Icon", modifier = Modifier.size(16.dp), tint = Color.Gray)
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

        Spacer(modifier = Modifier.height(8.dp))

        // Chọn ngày sinh
        OutlinedTextField(
            value = birthDate,
            onValueChange = { birthDate = it },
            label = { Text("Ngày sinh (dd/mm/yy)") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(painter = painterResource(id = R.drawable.calendar), contentDescription = "Expiry Date Icon", modifier = Modifier.size(16.dp), tint = Color.Gray)
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

        Spacer(modifier = Modifier.height(8.dp))

        // Spinner giới tính
        ExposedDropdownMenuBox(
            expanded = expanded.value,
            onExpandedChange = { expanded.value = !expanded.value }
        ) {
            OutlinedTextField(
                value = gender,
                onValueChange = {},
                label = { Text("Giới tính") },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value)
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.genders),
                        contentDescription = "Gender Icon",
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

            ExposedDropdownMenu(
                expanded = expanded.value,
                onDismissRequest = { expanded.value = false }
            ) {
                genderOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            gender = option
                            expanded.value = false
                        }
                    )
                }
            }
        }


        Spacer(modifier = Modifier.height(16.dp))

        // Nút chọn ảnh
        Button(
            onClick = onPickImage,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
        ) {
            Text("Chọn ảnh đại diện", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (name.isBlank() || email.isBlank() || password.isBlank() || phone.isBlank() || address.isBlank() || birthDate.isBlank()) {
                    Toast.makeText(context, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
                } else {
                    onRegister(name, email, password, phone, address, birthDate, gender)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
        ) {
            Text("Đăng ký", color = Color.White)
        }
    }
}
