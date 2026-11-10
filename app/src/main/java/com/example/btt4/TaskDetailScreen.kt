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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TaskDetailScreen(
    task: ListTask,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onUpdateTask: (ListTask) -> Unit,
    onDeleteTask: (Int) -> Unit
) {
    var current by remember(task.id, task) { mutableStateOf(task) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showSubtaskDialog by remember { mutableStateOf(false) }
    var editingSubtaskId by remember { mutableStateOf<Int?>(null) }
    var deletingSubtaskId by remember { mutableStateOf<Int?>(null) }
    var subtaskDraft by remember(current.id) { mutableStateOf("") }
    val doneSubtasks = current.subtasks.count { it.isDone }
    val subtaskProgress = if (current.subtasks.isEmpty()) 0f else doneSubtasks.toFloat() / current.subtasks.size

    fun update(updated: ListTask) {
        current = updated
        onUpdateTask(updated)
    }

    Box(Modifier.fillMaxSize().background(Color(0xFFF5F5F7))) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp).padding(top = 22.dp, bottom = 112.dp)
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(Modifier.clip(RoundedCornerShape(8.dp)).clickable(onClick = onBack).padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Blue, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Danh sách", color = Blue, fontSize = 15.sp)
                }
                Text("Sửa", modifier = Modifier.clickable(onClick = onEdit).padding(6.dp), color = Blue, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(18.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val study = current.category == "Học tập"
                Tag(current.category, if (study) Color(0xFFE4ECFF) else Color(0xFFFFE9D9), if (study) Color(0xFF2454D8) else Color(0xFFC45108))
                Tag(when (current.priority) { "high" -> "Ưu tiên cao"; "low" -> "Ưu tiên thấp"; else -> "Ưu tiên vừa" }, if (current.priority == "high") Color(0xFFFFE5E5) else Color(0xFFE9E9ED), if (current.priority == "high") Color(0xFFD92D20) else Color(0xFF6B6B73))
            }
            Spacer(Modifier.height(14.dp))
            Text(
                current.title,
                fontSize = 27.sp,
                lineHeight = 34.sp,
                fontWeight = FontWeight.Bold,
                color = if (current.isDone) Color(0xFF77777F) else Color(0xFF202124),
                textDecoration = if (current.isDone) TextDecoration.LineThrough else TextDecoration.None
            )
            Spacer(Modifier.height(18.dp))

            Surface(shape = RoundedCornerShape(18.dp), color = Color.White) {
                Column {
                    InfoRow(Icons.Default.CalendarToday, "Hạn chót", current.deadlineText)
                    DividerLine()
                    InfoRow(Icons.Default.NotificationsNone, "Nhắc nhở", current.remindText)
                    DividerLine()
                    InfoRow(Icons.Default.Repeat, "Lặp lại", current.repeatText)
                }
            }

            if (current.note.isNotBlank()) {
                Spacer(Modifier.height(20.dp))
                Text("Ghi chú", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF77777F))
                Spacer(Modifier.height(8.dp))
                Surface(shape = RoundedCornerShape(18.dp), color = Color.White) {
                    Text(current.note, modifier = Modifier.fillMaxWidth().padding(16.dp), fontSize = 16.sp, lineHeight = 24.sp, color = Color(0xFF3E3E45))
                }
            }

            Spacer(Modifier.height(20.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Việc nhỏ", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF77777F))
                Text("$doneSubtasks/${current.subtasks.size}", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF77777F))
            }
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { subtaskProgress },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                color = Blue,
                trackColor = Color(0xFFE2E2E7)
            )
            Spacer(Modifier.height(12.dp))
            Surface(shape = RoundedCornerShape(18.dp), color = Color.White) {
                Column {
                    current.subtasks.forEachIndexed { index, item ->
                        SubtaskRow(
                            item = item,
                            onToggle = {
                                update(current.copy(subtasks = current.subtasks.map { if (it.id == item.id) it.copy(isDone = !it.isDone) else it }))
                            },
                            onEdit = { subtaskDraft = item.title; editingSubtaskId = item.id },
                            onDelete = { deletingSubtaskId = item.id }
                        )
                        if (index < current.subtasks.lastIndex) DividerLine()
                    }
                    if (current.subtasks.isNotEmpty()) DividerLine()
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable { showSubtaskDialog = true }.padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Blue, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Thêm việc nhỏ", color = Blue, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }

        Surface(Modifier.align(Alignment.BottomCenter).fillMaxWidth(), color = Color(0xFFF5F5F7)) {
            Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(
                    modifier = Modifier.fillMaxWidth().height(52.dp).clickable { update(current.copy(isDone = !current.isDone)) },
                    shape = RoundedCornerShape(15.dp), color = Blue
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(if (current.isDone) "Đánh dấu chưa xong" else "Đánh dấu hoàn thành", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
                Text("Xóa công việc", modifier = Modifier.clickable { showDeleteDialog = true }.padding(top = 10.dp, bottom = 2.dp), color = Color(0xFFD4493F), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }

    if (showSubtaskDialog) {
        var draft by remember(current.id, showSubtaskDialog) { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showSubtaskDialog = false },
            title = { Text("Thêm việc nhỏ") },
            text = { OutlinedTextField(value = draft, onValueChange = { draft = it }, label = { Text("Tên việc nhỏ") }, singleLine = true) },
            confirmButton = {
                TextButton(onClick = {
                    if (draft.isNotBlank()) {
                        val newId = (current.subtasks.maxOfOrNull { it.id } ?: current.id * 10) + 1
                        update(current.copy(subtasks = current.subtasks + TaskSubtask(newId, draft.trim())))
                    }
                    showSubtaskDialog = false
                }) { Text("Thêm") }
            },
            dismissButton = { TextButton(onClick = { showSubtaskDialog = false }) { Text("Hủy") } }
        )
    }

    editingSubtaskId?.let { id ->
        val subtask = current.subtasks.firstOrNull { it.id == id }
        if (subtask != null) {
            AlertDialog(
                onDismissRequest = { editingSubtaskId = null },
                title = { Text("Sửa việc nhỏ") },
                text = { OutlinedTextField(value = subtaskDraft, onValueChange = { subtaskDraft = it }, singleLine = true) },
                confirmButton = {
                    TextButton(onClick = {
                        if (subtaskDraft.isNotBlank()) update(current.copy(subtasks = current.subtasks.map { if (it.id == id) it.copy(title = subtaskDraft.trim()) else it }))
                        editingSubtaskId = null
                    }) { Text("Lưu") }
                },
                dismissButton = { TextButton(onClick = { editingSubtaskId = null }) { Text("Hủy") } }
            )
        }
    }

    deletingSubtaskId?.let { id ->
        AlertDialog(
            onDismissRequest = { deletingSubtaskId = null },
            title = { Text("Xóa việc nhỏ?") },
            confirmButton = {
                TextButton(onClick = {
                    update(current.copy(subtasks = current.subtasks.filterNot { it.id == id }))
                    deletingSubtaskId = null
                }) { Text("Xóa", color = Color(0xFFD4493F)) }
            },
            dismissButton = { TextButton(onClick = { deletingSubtaskId = null }) { Text("Hủy") } }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Xóa công việc?") },
            text = { Text("Bạn có chắc muốn xóa công việc này không?") },
            confirmButton = {
                TextButton(onClick = { showDeleteDialog = false; onDeleteTask(current.id) }) { Text("Xóa", color = Color(0xFFD4493F)) }
            },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Hủy") } }
        )
    }
}

private val Blue = Color(0xFF315CF5)

@Composable
private fun Tag(text: String, background: Color, foreground: Color) {
    Text(text, modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(background).padding(horizontal = 10.dp, vertical = 5.dp), color = foreground, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
}

@Composable
private fun InfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = Color(0xFF77777F), modifier = Modifier.size(21.dp))
        Spacer(Modifier.width(14.dp))
        Text(label, modifier = Modifier.weight(1f), color = Color(0xFF686870), fontSize = 16.sp)
        Text(value, color = Color(0xFF202124), fontSize = 15.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun DividerLine() {
    Box(Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFECECF0)))
}

@Composable
private fun SubtaskRow(item: TaskSubtask, onToggle: () -> Unit, onEdit: () -> Unit, onDelete: () -> Unit) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier.size(25.dp).clip(CircleShape)
                .then(if (item.isDone) Modifier.background(Blue) else Modifier.border(2.dp, Color(0xFFC7C7CE), CircleShape))
                .clickable(onClick = onToggle),
            contentAlignment = Alignment.Center
        ) {
            if (item.isDone) Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(17.dp))
        }
        Spacer(Modifier.width(12.dp))
        Text(
            item.title,
            modifier = Modifier.weight(1f).clickable(onClick = onToggle),
            color = if (item.isDone) Color(0xFF8A8A91) else Color(0xFF33333A),
            fontSize = 14.sp,
            textDecoration = if (item.isDone) TextDecoration.LineThrough else TextDecoration.None
        )
        Icon(Icons.Default.Edit, contentDescription = "Sửa việc nhỏ", tint = Color(0xFF77777F), modifier = Modifier.size(19.dp).clickable(onClick = onEdit).padding(1.dp))
        Spacer(Modifier.width(12.dp))
        Icon(Icons.Default.DeleteOutline, contentDescription = "Xóa việc nhỏ", tint = Color(0xFFD4493F), modifier = Modifier.size(20.dp).clickable(onClick = onDelete))
    }
}






