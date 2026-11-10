package com.example.btt4

import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class ApiTask(
    val id: Int,
    val title: String,
    val note: String,
    val category: String,
    val date: String,
    val time: String,
    val priority: String,
    val remind: Boolean,
    val done: Boolean,
    val subtasks: List<ApiSubtask>
)

data class ApiSubtask(
    val title: String,
    val done: Boolean
)

fun ApiTask.toListTask(): ListTask {
    val dayLabel = apiDateLabel(date)
    val dayChoice = when (dayLabel) {
        "Hôm nay", "Ngày mai", "Tuần này" -> dayLabel
        else -> "Hôm nay"
    }
    return ListTask(
        id = id,
        title = title,
        category = if (category == "study") "Học tập" else "Cuộc sống",
        dueText = "$dayLabel, $time",
        isDone = done,
        priority = priority,
        deadlineText = "$dayLabel, $time",
        remindText = if (remind) "Trước 15 phút" else "Tắt",
        note = note,
        subtasks = subtasks.mapIndexed { index, item ->
            TaskSubtask(id = id * 100 + index + 1, title = item.title, isDone = item.done)
        },
        date = date,
        time = time,
        dayChoice = dayChoice
    )
}

fun ListTask.toCreateJson(): JSONObject = JSONObject()
    .put("title", title)
    .put("note", note)
    .put("category", if (category == "Học tập") "study" else "life")
    .put("date", date.ifBlank { dateForDayChoice(dayChoice) })
    .put("time", time)
    .put("priority", priority)
    .put("remind", remindText != "Tắt")

fun ListTask.toPatchJson(): JSONObject {
    val subtaskArray = JSONArray()
    subtasks.forEach { item ->
        subtaskArray.put(JSONObject().put("title", item.title).put("done", item.isDone))
    }
    return toCreateJson()
        .put("done", isDone)
        .put("subtasks", subtaskArray)
}

internal fun apiTaskFromJson(json: JSONObject): ApiTask {
    val subtaskArray = json.optJSONArray("subtasks") ?: JSONArray()
    val subtasks = (0 until subtaskArray.length()).map { index ->
        val item = subtaskArray.getJSONObject(index)
        ApiSubtask(item.optString("title"), item.optBoolean("done"))
    }
    return ApiTask(
        id = json.getInt("id"),
        title = json.getString("title"),
        note = json.optString("note"),
        category = json.getString("category"),
        date = json.getString("date"),
        time = json.getString("time"),
        priority = json.getString("priority"),
        remind = json.optBoolean("remind"),
        done = json.optBoolean("done"),
        subtasks = subtasks
    )
}

private fun apiDateLabel(value: String): String {
    val parsed = runCatching {
        SimpleDateFormat("yyyy-MM-dd", Locale.US).apply { isLenient = false }.parse(value)
    }.getOrNull() ?: return value
    val target = Calendar.getInstance().apply { time = parsed; zeroTime() }
    val today = Calendar.getInstance().apply { zeroTime() }
    val tomorrow = (today.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, 1) }
    if (target.sameDay(today)) return "Hôm nay"
    if (target.sameDay(tomorrow)) return "Ngày mai"
    val daysToSunday = (Calendar.SUNDAY - today.get(Calendar.DAY_OF_WEEK) + 7) % 7
    val sunday = (today.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, daysToSunday) }
    if (target.after(today) && (target.before(sunday) || target.sameDay(sunday))) return "Tuần này"
    return SimpleDateFormat("d/M", Locale("vi", "VN")).format(parsed)
}

private fun Calendar.zeroTime() {
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}

private fun Calendar.sameDay(other: Calendar): Boolean =
    get(Calendar.YEAR) == other.get(Calendar.YEAR) && get(Calendar.DAY_OF_YEAR) == other.get(Calendar.DAY_OF_YEAR)
