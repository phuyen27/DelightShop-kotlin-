package com.example.phamthiphuonguyen_delightshop.Activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.example.phamthiphuonguyen_delightshop.R
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.google.firebase.database.FirebaseDatabase


class UserInforActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", "") ?: ""
        val name = sharedPreferences.getString("name", "") ?: ""
        val email = sharedPreferences.getString("email", "") ?: ""
        val phone = sharedPreferences.getString("phone", "") ?: ""
        val address = sharedPreferences.getString("address", "") ?: ""
        val userImg = sharedPreferences.getString("userImg", "") ?: ""
        val dob = sharedPreferences.getString("dob", "") ?: ""
        val gender = sharedPreferences.getString("gender", "") ?: ""
        setContent {
            UserInfoScreen( onBackClick = {finish()},onLogoutClick = { logout() },userId, name, email, phone, address, userImg,gender,dob)
        }
    }

    private fun logout() {
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply() // Xóa dữ liệu người dùng

        val intent = Intent(this, IntroActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}

@Composable
fun UserInfoScreen(
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit,
    userId: String,
    initialName: String,
    initialEmail: String,
    initialPhone: String,
    initialAddress: String,
    userImg: String,
    initgender: String,
    initdob: String
) {
    var name by remember { mutableStateOf(initialName) }
    var email by remember { mutableStateOf(initialEmail) }
    var phone by remember { mutableStateOf(initialPhone) }
    var address by remember { mutableStateOf(initialAddress) }
    var dob by remember { mutableStateOf(initdob) }
    var gender by remember { mutableStateOf(initgender) }
    var isEditing by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(top = 36.dp, start = 16.dp, end = 16.dp)
    ) {
        ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
            val (backBtn, title, logoutBtn) = createRefs()

            Text(
                text = "Thông tin cá nhân",
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.constrainAs(title) { centerTo(parent) }
            )

            // Nút quay lại
            Image(
                painter = painterResource(R.drawable.back),
                contentDescription = "Quay lại",
                modifier = Modifier
                    .clickable { onBackClick() }
                    .constrainAs(backBtn) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                    }
            )

            // Nút logout
            Image(
                painter = painterResource(R.drawable.logout),
                contentDescription = "Logout",
                modifier = Modifier
                    .clickable { showDialog = true }
                    .constrainAs(logoutBtn) {
                        end.linkTo(parent.end)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    }
                    .size(40.dp)
            )

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text(text = "Xác nhận đăng xuất") },
                    text = { Text(text = "Bạn có chắc chắn muốn đăng xuất không?") },
                    confirmButton = {
                        TextButton(onClick = {
                            showDialog = false
                            onLogoutClick()
                        }) {
                            Text("Có")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDialog = false }) {
                            Text("Không")
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isEditing) {
            EditableUserInfoScreen(userId, name, email, phone, address, gender, dob, userImg) { newName, newEmail, newPhone, newAddress, newGender, newDob ->
                name = newName
                email = newEmail
                phone = newPhone
                address = newAddress
                gender = newGender
                dob = newDob
                isEditing = false
            }
        } else {
            ReadOnlyUserInfoScreen(name, email, phone, address, gender, dob, userImg) { isEditing = true }
        }
    }
}

@Composable
fun ReadOnlyUserInfoScreen(
    name: String, email: String, phone: String, address: String, gender: String, dob: String, userImg: String, onEditClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = if (userImg.isNotEmpty()) rememberImagePainter(userImg) else painterResource(id = R.drawable.user),
            contentDescription = "User Avatar",
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.height(16.dp))

        UserInfoText(label = "Họ và Tên", value = name)
        UserInfoText(label = "Email", value = email)
        UserInfoText(label = "Số Điện Thoại", value = phone)
        UserInfoText(label = "Địa Chỉ", value = address)
        UserInfoText(label = "Giới tính", value = gender)
        UserInfoText(label = "Ngày sinh", value = dob)

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onEditClick,
            colors = ButtonDefaults.buttonColors(colorResource(R.color.green))
        ) {
            Text("Chỉnh sửa",color=colorResource(R.color.white))
        }
    }
}

@Composable
fun EditableUserInfoScreen(
    userId: String,
    name: String,
    email: String,
    phone: String,
    address: String,
    gender: String,
    dob: String,
    userImg: String,
    onSave: (String, String, String, String, String, String) -> Unit
) {
    var nameState by remember { mutableStateOf(name) }
    var emailState by remember { mutableStateOf(email) }
    var phoneState by remember { mutableStateOf(phone) }
    var addressState by remember { mutableStateOf(address) }
    var genderState by remember { mutableStateOf(gender) }
    var dobState by remember { mutableStateOf(dob) }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = if (userImg.isNotEmpty()) rememberImagePainter(userImg) else painterResource(id = R.drawable.user),
            contentDescription = "User Avatar",
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.height(16.dp))

        UserInfoEditableTextField("Họ và Tên", nameState) { nameState = it }
        UserInfoEditableTextField("Số Điện Thoại", phoneState) { phoneState = it }
        UserInfoEditableTextField("Địa Chỉ", addressState) { addressState = it }
        UserInfoEditableTextField("Giới tính", genderState) { genderState = it }
        UserInfoEditableTextField("Ngày sinh", dobState) { dobState = it }

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = {
            updateUserInfo(context, userId, nameState, emailState, phoneState, addressState, genderState, dobState) {
                onSave(nameState, emailState, phoneState, addressState, genderState, dobState)
            }
        }, colors = ButtonDefaults.buttonColors(colorResource(R.color.green))) {
            Text("Lưu thay đổi",color=colorResource(R.color.white))
        }
    }
}

@Composable
fun UserInfoText(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, style = MaterialTheme.typography.bodySmall)
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}

@Composable
fun UserInfoEditableTextField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, style = MaterialTheme.typography.bodySmall)
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
fun updateUserInfo(
    context: Context,
    userId: String,
    name: String,
    email: String,
    phone: String,
    address: String,
    gender: String,
    dob: String,
    onSuccess: () -> Unit
) {
    if (userId.isEmpty()) {
        Toast.makeText(context, "Không thể cập nhật thông tin!", Toast.LENGTH_SHORT).show()
        return
    }

    val database = FirebaseDatabase.getInstance().getReference("Users").child(userId)
    val userUpdates = mapOf(
        "name" to name,
        "email" to email,
        "phone" to phone,
        "address" to address,
        "gender" to gender,
        "dob" to dob
    )

    database.updateChildren(userUpdates).addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            with(sharedPreferences.edit()) {
                putString("name", name)
                putString("email", email)
                putString("phone", phone)
                putString("address", address)
                putString("gender", gender)
                putString("dob", dob)
                apply()
            }

            Toast.makeText(context, "Cập nhật thành công!", Toast.LENGTH_SHORT).show()
            onSuccess()
        } else {
            Toast.makeText(context, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show()
        }
    }
}
