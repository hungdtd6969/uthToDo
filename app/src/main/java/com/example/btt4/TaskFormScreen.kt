package com.example.btt4

import android.app.TimePickerDialog
import android.text.format.DateFormat
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Calendar

private val FormBlue = Color(0xFF315CF5)

@Composable
fun TaskFormScreen(
    task: ListTask?,
    onCancel: () -> Unit,
    onSave: (ListTask) -> Unit
) {
    val context = LocalContext.current
    var title by remember(task?.id) { mutableStateOf(task?.title.orEmpty()) }
    var note by remember(task?.id) { mutableStateOf(task?.note.orEmpty()) }
    var category by remember(task?.id) { mutableStateOf(task?.category ?: "Học tập") }
    var day by remember(task?.id) {
        mutableStateOf(
            task?.dayChoice?.takeIf { it in listOf("Hôm nay", "Ngày mai", "Tuần này") }
                ?: "Hôm nay"
        )
    }
    var time by remember(task?.id) { mutableStateOf(task?.time ?: "19:00") }
    var remind by remember(task?.id) { mutableStateOf(task?.remindText != "Tắt") }
    var priority by remember(task?.id) { mutableStateOf(task?.priority ?: "medium") }

    fun save() {
        val due = "$day, $time"
        val updated = (task ?: ListTask(0, "", category, due)).copy(
            title = title.trim(),
            note = note.trim(),
            category = category,
            dueText = due,
            deadlineText = due,
            remindText = if (remind) "Trước 15 phút" else "Tắt",
            priority = priority,
            date = dateForDayChoice(day),
            time = time,
            dayChoice = day
        )
        onSave(updated)
    }

    Box(Modifier.fillMaxSize().background(Color(0xFFF5F5F7))) {
        Column(Modifier.fillMaxSize().padding(horizontal = 20.dp).padding(top = 22.dp, bottom = 94.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Hủy", modifier = Modifier.clickable(onClick = onCancel).padding(vertical = 8.dp), color = FormBlue, fontSize = 15.sp)
                Text(if (task == null) "Việc mới" else "Sửa công việc", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF202124))
                Text("Lưu", modifier = Modifier.clickable(enabled = title.isNotBlank(), onClick = ::save).padding(vertical = 8.dp), color = if (title.isNotBlank()) FormBlue else Color(0xFF9999A1), fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(14.dp))
            Surface(shape = RoundedCornerShape(18.dp), color = Color.White) {
                Column {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
                        label = { Text("Tên công việc") },
                        placeholder = { Text("Ví dụ: Ôn chương 4 – Giải thuật") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        textStyle = TextStyle(fontSize = 16.sp, color = Color(0xFF202124))
                    )
                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
                        label = { Text("Ghi chú") },
                        placeholder = { Text("Thêm ghi chú (không bắt buộc)") },
                        minLines = 2,
                        maxLines = 3,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Default),
                        textStyle = TextStyle(fontSize = 15.sp, color = Color(0xFF202124))
                    )
                    Spacer(Modifier.height(6.dp))
                }
            }

            Spacer(Modifier.height(16.dp))
            FormSectionLabel("Nhóm")
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ChoiceButton("Học tập", category == "Học tập", Modifier.weight(1f)) { category = "Học tập" }
                ChoiceButton("Cuộc sống", category == "Cuộc sống", Modifier.weight(1f)) { category = "Cuộc sống" }
            }

            Spacer(Modifier.height(16.dp))
            FormSectionLabel("Ngày")
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Hôm nay", "Ngày mai", "Tuần này").forEach { option ->
                    ChoiceButton(option, day == option, Modifier.weight(1f)) { day = option }
                }
            }

            Spacer(Modifier.height(16.dp))
            Surface(shape = RoundedCornerShape(18.dp), color = Color.White) {
                Column {
                    Row(
                        Modifier.fillMaxWidth().clickable {
                            val parts = time.split(":")
                            val hour = parts.getOrNull(0)?.toIntOrNull() ?: 19
                            val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0
                            TimePickerDialog(context, { _, selectedHour, selectedMinute ->
                                time = "%02d:%02d".format(selectedHour, selectedMinute)
                            }, hour, minute, DateFormat.is24HourFormat(context)).show()
                        }.padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        androidx.compose.material3.Icon(Icons.Default.AccessTime, contentDescription = null, tint = Color(0xFF77777F))
                        Spacer(Modifier.width(12.dp))
                        Text("Giờ", modifier = Modifier.weight(1f), fontSize = 15.sp, color = Color(0xFF33333A))
                        Text(time, modifier = Modifier.clip(RoundedCornerShape(9.dp)).background(Color(0xFFE9E9ED)).padding(horizontal = 12.dp, vertical = 7.dp), fontSize = 15.sp, color = Color(0xFF202124))
                    }
                    Box(Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFECECF0)))
                    Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                        androidx.compose.material3.Icon(Icons.Default.NotificationsNone, contentDescription = null, tint = Color(0xFF77777F))
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text("Nhắc tôi", fontSize = 15.sp, color = Color(0xFF33333A))
                            Text(if (remind) "Trước 15 phút" else "Tắt", fontSize = 12.sp, color = Color(0xFF77777F))
                        }
                        Switch(checked = remind, onCheckedChange = { remind = it })
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            FormSectionLabel("Mức ưu tiên")
            Spacer(Modifier.height(8.dp))
            Surface(shape = RoundedCornerShape(14.dp), color = Color(0xFFE9E9ED)) {
                Row(Modifier.fillMaxWidth().padding(4.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf("low" to "Thấp", "medium" to "Vừa", "high" to "Cao").forEach { (value, label) ->
                        val selected = priority == value
                        Box(
                            Modifier.weight(1f).height(40.dp).clip(RoundedCornerShape(10.dp))
                                .background(if (selected) Color.White else Color.Transparent)
                                .then(if (selected) Modifier.border(1.dp, Color(0xFFE0E0E5), RoundedCornerShape(10.dp)) else Modifier)
                                .clickable { priority = value },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(label, fontSize = 14.sp, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium, color = if (selected) Color(0xFF202124) else Color(0xFF74747C))
                        }
                    }
                }
            }
        }

        Surface(Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp).height(52.dp)
            .clickable(enabled = title.isNotBlank(), onClick = ::save),
            shape = RoundedCornerShape(16.dp), color = if (title.isNotBlank()) FormBlue else Color(0xFFE9E9ED)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(if (task == null) "Lưu công việc" else "Lưu thay đổi", color = if (title.isNotBlank()) Color.White else Color(0xFF92929A), fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
    }
}

@Composable
private fun FormSectionLabel(label: String) {
    Text(label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF77777F))
}

@Composable
private fun ChoiceButton(label: String, selected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier.height(46.dp).clip(RoundedCornerShape(14.dp)).clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = if (selected) Color(0xFFE5ECFF) else Color.White,
        border = androidx.compose.foundation.BorderStroke(if (selected) 2.dp else 1.dp, if (selected) FormBlue else Color(0xFFD7D7DE))
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(label, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = if (selected) FormBlue else Color(0xFF202124))
        }
    }
}


