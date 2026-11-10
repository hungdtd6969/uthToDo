package com.example.btt4

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TaskListScreen(
    tasks: List<ListTask>,
    initialCategory: String? = null,
    onOpenHome: () -> Unit,
    onOpenNotifications: () -> Unit,
    onOpenSettings: () -> Unit,
    onToggleTask: (Int) -> Unit,
    onOpenTask: (ListTask) -> Unit,
    onAddTask: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    var selectedCategory by remember(initialCategory) { mutableStateOf(initialCategory ?: "Tất cả") }
    val pendingCount = tasks.count { !it.isDone }
    val visibleTasks = tasks.filter { task ->
        (selectedCategory == "Tất cả" || task.category == selectedCategory) &&
            task.title.contains(query.trim(), ignoreCase = true)
    }
    val pendingTasks = visibleTasks.filter { !it.isDone }
    val completedTasks = visibleTasks.filter { it.isDone }

    Box(Modifier.fillMaxSize().background(Color(0xFFF5F5F7))) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp).padding(top = 28.dp, bottom = 94.dp)
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Danh sách", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color(0xFF202124))
                Text("$pendingCount việc chưa xong", fontSize = 14.sp, color = Color(0xFF77777F))
            }
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth().height(46.dp).clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFE9E9ED)).padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF73737B), modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(10.dp))
                BasicTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    textStyle = TextStyle(fontSize = 15.sp, color = Color(0xFF202124)),
                    decorationBox = { inner ->
                        if (query.isEmpty()) Text("Tìm công việc", fontSize = 15.sp, color = Color(0xFF8A8A91))
                        inner()
                    }
                )
            }
            Spacer(Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Tất cả", "Học tập", "Cuộc sống").forEach { category ->
                    val selected = selectedCategory == category
                    Surface(
                        modifier = Modifier.clip(CircleShape).clickable { selectedCategory = category },
                        shape = CircleShape,
                        color = if (selected) Color(0xFF202124) else Color.White,
                        border = if (selected) null else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD9D9DE))
                    ) {
                        Text(
                            category,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            fontSize = 14.sp,
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                            color = if (selected) Color.White else Color(0xFF202124)
                        )
                    }
                }
            }
            Spacer(Modifier.height(14.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (pendingTasks.isNotEmpty()) {
                    item { SectionLabel("CẦN LÀM") }
                    items(pendingTasks, key = { it.id }) { task ->
                        ListTaskRow(task, onToggle = { onToggleTask(task.id) }, onOpen = { onOpenTask(task) })
                    }
                }
                if (completedTasks.isNotEmpty()) {
                    item { SectionLabel("ĐÃ XONG") }
                    items(completedTasks, key = { it.id }) { task ->
                        ListTaskRow(task, onToggle = { onToggleTask(task.id) }, onOpen = { onOpenTask(task) })
                    }
                }
                if (visibleTasks.isEmpty()) {
                    item { Text("Không tìm thấy công việc nào.", modifier = Modifier.padding(top = 12.dp), fontSize = 14.sp, color = Color(0xFF77777F)) }
                }
            }
        }
        Surface(Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(72.dp), color = Color.White, shadowElevation = 8.dp) {
            Row(Modifier.fillMaxSize().padding(horizontal = 8.dp), horizontalArrangement = Arrangement.SpaceAround, verticalAlignment = Alignment.CenterVertically) {
                BottomItem("Trang chủ", Icons.Default.Home, false, onOpenHome)
                BottomItem("Danh sách", Icons.AutoMirrored.Filled.FormatListBulleted, true, {})
                Box(Modifier.size(52.dp).clip(CircleShape).background(Color(0xFF315CF5)).clickable(onClick = onAddTask), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Add, contentDescription = "Thêm công việc", tint = Color.White, modifier = Modifier.size(28.dp))
                }
                BottomItem("Thông báo", Icons.Default.Notifications, false, onOpenNotifications)
                BottomItem("Cài đặt", Icons.Default.Tune, false, onOpenSettings)
            }
        }
    }
}

@Composable
private fun SectionLabel(label: String) {
    Text(label, modifier = Modifier.padding(top = 2.dp, bottom = 2.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF77777F))
}

@Composable
private fun ListTaskRow(task: ListTask, onToggle: () -> Unit, onOpen: () -> Unit) {
    Surface(Modifier.fillMaxWidth().clickable(onClick = onOpen), shape = RoundedCornerShape(16.dp), color = Color.White) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 13.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(25.dp).clip(CircleShape)
                    .then(if (task.isDone) Modifier.background(Color(0xFF315CF5)) else Modifier.border(2.dp, Color(0xFFC7C7CE), CircleShape))
                    .clickable(onClick = onToggle),
                contentAlignment = Alignment.Center
            ) {
                if (task.isDone) Text("✓", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    task.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (task.isDone) Color(0xFF898991) else Color(0xFF202124),
                    textDecoration = if (task.isDone) androidx.compose.ui.text.style.TextDecoration.LineThrough else androidx.compose.ui.text.style.TextDecoration.None
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val study = task.category == "Học tập"
                    Text(
                        task.category,
                        modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(if (study) Color(0xFFE4ECFF) else Color(0xFFFFE9D9)).padding(horizontal = 7.dp, vertical = 2.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (study) Color(0xFF2454D8) else Color(0xFFC45108)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(task.dueText, fontSize = 12.sp, color = Color(0xFF77777F))
                }
            }
            if (task.priority == "high" && !task.isDone) {
                Text("Gấp", modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(Color(0xFFFFE4E4)).padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD92D20))
            }
        }
    }
}

@Composable
private fun BottomItem(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, selected: Boolean, onClick: () -> Unit) {
    Column(Modifier.clickable(onClick = onClick).padding(horizontal = 4.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Icon(icon, contentDescription = label, tint = if (selected) Color(0xFF315CF5) else Color(0xFF767680), modifier = Modifier.size(23.dp))
        Text(label, fontSize = 10.sp, color = if (selected) Color(0xFF315CF5) else Color(0xFF767680), fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal)
    }
}



