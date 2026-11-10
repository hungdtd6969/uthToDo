package com.example.btt4

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProfileScreen(
    userData: UserData = UserData(),
    onBackToHome: () -> Unit,
    onLogout: () -> Unit
) {
    // Dữ liệu biểu đồ thống kê tuần theo OOP DayProgressData
    val weeklyData = listOf(
        DayProgressData("T2", heightDp = 52, isSecondary = true),
        DayProgressData("T3", heightDp = 36, isHighlighted = true),
        DayProgressData("T4", heightDp = 8),
        DayProgressData("T5", heightDp = 8),
        DayProgressData("T6", heightDp = 8),
        DayProgressData("T7", heightDp = 8),
        DayProgressData("CN", heightDp = 8)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Top bar: Nút quay lại "< Trang chủ" + Tiêu đề "Hồ sơ"
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onBackToHome() }
                    .padding(end = 8.dp, top = 4.dp, bottom = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Quay lại",
                    tint = Color(0xFF2563EB),
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Trang chủ",
                    color = Color(0xFF2563EB),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Text(
                text = "Hồ sơ",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )

            // Khoảng trống cân đối với nút bên trái
            Spacer(modifier = Modifier.width(80.dp))
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 2. Avatar lớn + Tên + Email + Badge liên kết Google
        Box(
            modifier = Modifier
                .size(86.dp)
                .clip(CircleShape)
                .background(Color(0xFFDBEAFE)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = userData.avatarText,
                color = Color(0xFF1D4ED8),
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = userData.name,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = userData.email,
            fontSize = 14.sp,
            color = Color(0xFF64748B)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Badge: Đã liên kết tài khoản Google
        if (userData.isGoogleLinked) {
            Row(
                modifier = Modifier
                    .background(Color(0xFFDCFCE7), RoundedCornerShape(16.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color(0xFF16A34A),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Đã liên kết tài khoản Google",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF15803D)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 3. Hàng thống kê 3 thẻ (Việc đã xong, Đang chờ, Hoàn thành)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Thẻ Việc đã xong
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                shadowElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "${userData.completedCount}",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Việc đã xong",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }

            // Thẻ Đang chờ
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                shadowElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "${userData.pendingCount}",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Đang chờ",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }

            // Thẻ Hoàn thành
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                shadowElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = userData.completionPercentage,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Hoàn thành",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 4. Thẻ Tuần này (Biểu đồ cột việc xong mỗi ngày)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            shadowElevation = 1.dp
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tuần này",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "Việc xong mỗi ngày",
                        fontSize = 13.sp,
                        color = Color(0xFF64748B)
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Biểu đồ cột
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.Bottom
                ) {
                    weeklyData.forEach { dayData ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom
                        ) {
                            val barColor = when {
                                dayData.isHighlighted -> Color(0xFF2563EB)
                                dayData.isSecondary -> Color(0xFF93C5FD)
                                else -> Color(0xFFE2E8F0)
                            }
                            Box(
                                modifier = Modifier
                                    .width(26.dp)
                                    .height(dayData.heightDp.dp)
                                    .background(barColor, RoundedCornerShape(6.dp))
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = dayData.day,
                                fontSize = 12.sp,
                                color = Color(0xFF64748B),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // 5. Nút Đăng xuất ở cuối màn hình
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(26.dp))
                .clickable { onLogout() },
            shape = RoundedCornerShape(26.dp),
            color = Color.White,
            border = BorderStroke(1.dp, Color(0xFFE2E8F0))
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = "Đăng xuất",
                    tint = Color(0xFFDC2626),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Đăng xuất",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFDC2626)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}
