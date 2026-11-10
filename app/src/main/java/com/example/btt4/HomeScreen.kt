package com.example.btt4

import android.widget.Toast
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    userData: UserData = UserData(),
    tasks: List<ListTask>,
    onOpenProfile: () -> Unit,
    onOpenTaskList: (String?) -> Unit,
    onOpenTask: (Int) -> Unit,
    onToggleTask: (Int) -> Unit,
    onAddTask: () -> Unit
) {
    val fcmNotice by FcmMessageStore.latest.collectAsState()
    val currentDate = remember {
        val vietnameseLocale = Locale("vi", "VN")
        SimpleDateFormat("EEEE, d 'tháng' M", vietnameseLocale)
            .format(Date())
            .replaceFirstChar { it.titlecase(vietnameseLocale) }
    }
    var remoteConfigMessage by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        val remoteConfig = FirebaseRemoteConfig.getInstance()
        val settings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(0)
            .build()
        remoteConfig.setConfigSettingsAsync(settings)
        remoteConfig.setDefaultsAsync(mapOf("home_banner_text" to ""))
        remoteConfig.fetchAndActivate().addOnCompleteListener { result ->
            if (result.isSuccessful) remoteConfigMessage = remoteConfig.getString("home_banner_text")
        }
    }

    val todayTasks = tasks.filter { it.dueText.startsWith("Hôm nay,") }
    val completedCount = todayTasks.count { it.isDone }
    val totalCount = todayTasks.size
    val progressPercent = if (totalCount > 0) (completedCount * 100 / totalCount) else 0
    val studyRemaining = tasks.count { it.category == "Học tập" && !it.isDone }
    val lifeRemaining = tasks.count { it.category == "Cuộc sống" && !it.isDone }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 16.dp, bottom = 90.dp)
        ) {
            // 1. Header: Avatar (bấm để xem Hồ sơ) + Lời chào + Chuông thông báo
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Ô tròn bên trái đại diện hồ sơ - Bấm để chuyển sang trang Hồ sơ
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFDBEAFE))
                            .clickable { onOpenProfile() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = userData.avatarText,
                            color = Color(0xFF1D4ED8),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = currentDate,
                            fontSize = 13.sp,
                            color = Color(0xFF64748B)
                        )
                        Text(
                            text = userData.getGreeting(),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                    }
                }

                // Nút thông báo chuông
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .shadow(elevation = 2.dp, shape = CircleShape)
                        .background(Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Thông báo",
                        tint = Color(0xFF0F172A),
                        modifier = Modifier.size(22.dp)
                    )
                    // Chấm đỏ thông báo
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .offset(x = 6.dp, y = (-6).dp)
                            .background(Color(0xFFEF4444), CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 2. Banner Tiến độ hôm nay (2/4 việc, 50%)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF2563EB), shape = RoundedCornerShape(22.dp))
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Tiến độ hôm nay",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "$completedCount/$totalCount việc",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (progressPercent >= 50) "Đã hơn nửa chặng, cố lên!" else "Hãy bắt đầu hoàn thành các việc nhé!",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }

                    // Biểu đồ tròn hiển thị phần trăm
                    Box(
                        modifier = Modifier.size(76.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val strokeWidth = 9.dp.toPx()
                            // Vòng nền mờ
                            drawCircle(
                                color = Color.White.copy(alpha = 0.25f),
                                style = Stroke(width = strokeWidth)
                            )
                            // Vòng tiến độ
                            val sweepAngle = (progressPercent / 100f) * 360f
                            drawArc(
                                color = Color.White,
                                startAngle = -90f,
                                sweepAngle = sweepAngle,
                                useCenter = false,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )
                        }
                        Text(
                            text = "$progressPercent%",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            if (remoteConfigMessage.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFEFF6FF)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Thông báo từ Firebase Remote Config",
                            fontSize = 12.sp,
                            color = Color(0xFF1D4ED8),
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = remoteConfigMessage,
                            fontSize = 14.sp,
                            color = Color(0xFF0F172A)
                        )
                    }
                }
            }

            fcmNotice?.let { notice ->
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { FcmMessageStore.clear() },
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFFFF7ED)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Thông báo Firebase Cloud Messaging",
                            fontSize = 12.sp,
                            color = Color(0xFFC2410C),
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = notice.title,
                            fontSize = 14.sp,
                            color = Color(0xFF0F172A),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = notice.body,
                            fontSize = 14.sp,
                            color = Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Chạm để ẩn",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 3. Hai thẻ danh mục (Học tập & Cuộc sống)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Thẻ Học tập
                Surface(
                    modifier = Modifier.weight(1f).clickable { onOpenTaskList("Học tập") },
                    shape = RoundedCornerShape(18.dp),
                    color = Color.White,
                    shadowElevation = 1.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color(0xFFEFF6FF), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                tint = Color(0xFF2563EB),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Học tập",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$studyRemaining việc còn lại",
                            fontSize = 13.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                // Thẻ Cuộc sống
                Surface(
                    modifier = Modifier.weight(1f).clickable { onOpenTaskList("Cuộc sống") },
                    shape = RoundedCornerShape(18.dp),
                    color = Color.White,
                    shadowElevation = 1.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color(0xFFFFF7ED), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = null,
                                tint = Color(0xFFEA580C),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Cuộc sống",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$lifeRemaining việc còn lại",
                            fontSize = 13.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 4. Danh sách Việc hôm nay
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Việc hôm nay",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
                Text(
                    modifier = Modifier.clickable { onOpenTaskList(null) },
                    text = "Xem tất cả",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF2563EB)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Hiển thị từng TaskItem theo mô hình OOP
            todayTasks.forEach { task ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onOpenTask(task.id) },
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    shadowElevation = 1.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Nút tròn check hoàn thành
                        if (task.isDone) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF2563EB))
                                    .clickable { onToggleTask(task.id) },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .border(2.dp, Color(0xFFCBD5E1), CircleShape)
                                    .clickable { onToggleTask(task.id) }
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        // Nội dung công việc + Thể loại + Giờ
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = task.title,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (task.isDone) Color(0xFF64748B) else Color(0xFF0F172A),
                                textDecoration = if (task.isDone) TextDecoration.LineThrough else TextDecoration.None
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                val isStudy = task.category == "Học tập"
                                Box(
                                    modifier = Modifier
                                        .background(
                                            color = if (isStudy) Color(0xFFDBEAFE) else Color(0xFFFFEDD5),
                                            shape = RoundedCornerShape(6.dp)
                                        )
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = task.category,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (isStudy) Color(0xFF1D4ED8) else Color(0xFFC2410C)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = task.dueText.substringAfterLast(", "),
                                    fontSize = 12.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // 5. Thanh điều hướng đáy (Bottom Navigation Bar)
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(70.dp),
            color = Color.White,
            shadowElevation = 10.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tab Trang chủ (Đang chọn)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { }
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Trang chủ",
                        tint = Color(0xFF2563EB),
                        modifier = Modifier.size(24.dp)
                    )
                    Text(text = "Trang chủ", fontSize = 11.sp, color = Color(0xFF2563EB), fontWeight = FontWeight.SemiBold)
                }

                // Tab Danh sách
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { onOpenTaskList(null) }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.FormatListBulleted,
                        contentDescription = "Danh sách",
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(24.dp)
                    )
                    Text(text = "Danh sách", fontSize = 11.sp, color = Color(0xFF64748B))
                }

                // Nút Thêm ở chính giữa (+)
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .shadow(elevation = 6.dp, shape = CircleShape)
                        .background(Color(0xFF2563EB), CircleShape).clickable(onClick = onAddTask),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Thêm công việc",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Tab Thông báo
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { }
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Thông báo",
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(24.dp)
                    )
                    Text(text = "Thông báo", fontSize = 11.sp, color = Color(0xFF64748B))
                }

                // Tab Cài đặt
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { }
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Cài đặt",
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(24.dp)
                    )
                    Text(text = "Cài đặt", fontSize = 11.sp, color = Color(0xFF64748B))
                }
            }
        }
    }
}








