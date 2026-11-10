package com.example.btt4

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Màn hình Đăng nhập (LoginScreen)
 * Sử dụng OOP đóng gói thông tin đăng nhập
 */
data class LoginScreenData(
    val title: String = "Chào mừng đến với\nUTH Todo",
    val description: String = "Đăng nhập để lưu công việc và đồng bộ trên mọi thiết bị của bạn.",
    val googleButtonText: String = "Tiếp tục với Google",
    val guestButtonText: String = "Dùng thử không cần tài khoản",
    val termsText: String = "Khi tiếp tục, bạn đồng ý với Điều khoản sử dụng và Chính sách quyền riêng tư của UTH Todo."
)

@Composable
fun LoginScreen(
    onLoginWithGoogle: () -> Unit,
    onContinueAsGuest: () -> Unit
) {
    val screenData = LoginScreenData()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        Spacer(modifier = Modifier.weight(0.7f))

        // Icon ứng dụng UTH Todo
        Box(
            modifier = Modifier
                .size(76.dp)
                .shadow(elevation = 12.dp, shape = RoundedCornerShape(22.dp), spotColor = Color(0x332563EB))
                .background(Color(0xFF2563EB), shape = RoundedCornerShape(22.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.padding(14.dp)
            ) {
                // Hàng 1: Icon tròn check + thanh ngang dài
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color(0xFF2563EB),
                            modifier = Modifier.size(12.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .width(26.dp)
                            .height(5.dp)
                            .background(Color.White, RoundedCornerShape(3.dp))
                    )
                }

                // Hàng 2: Icon tròn rỗng + thanh ngang
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .border(2.dp, Color.White.copy(alpha = 0.85f), CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .width(22.dp)
                            .height(5.dp)
                            .background(Color.White.copy(alpha = 0.65f), RoundedCornerShape(3.dp))
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Tiêu đề & mô tả
        Text(
            text = screenData.title,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A),
            lineHeight = 34.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = screenData.description,
            fontSize = 14.sp,
            color = Color(0xFF64748B),
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.weight(1f))

        // Nút Tiếp tục với Google
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(20.dp))
                .clickable { onLoginWithGoogle() },
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // Biểu tượng Google G
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .border(1.dp, Color(0xFFE2E8F0), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "G",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF334155)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = screenData.googleButtonText,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF0F172A)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Dải phân cách "hoặc"
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                color = Color(0xFFE2E8F0),
                thickness = 1.dp
            )
            Text(
                text = "hoặc",
                color = Color(0xFF64748B),
                fontSize = 13.sp,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                color = Color(0xFFE2E8F0),
                thickness = 1.dp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Nút Dùng thử không cần tài khoản
        TextButton(
            onClick = onContinueAsGuest,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = screenData.guestButtonText,
                color = Color(0xFF2563EB),
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Điều khoản & Chính sách
        Text(
            text = screenData.termsText,
            color = Color(0xFF64748B),
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            lineHeight = 18.sp,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))
    }
}
