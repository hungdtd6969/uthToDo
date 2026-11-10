package com.example.btt4

data class TaskItem(
    val id: Int,
    val title: String,
    val category: String,
    val time: String,
    val isCompleted: Boolean = false
) {
    fun toggle(): TaskItem = copy(isCompleted = !isCompleted)
}

data class ListTask(
    val id: Int,
    val title: String,
    val category: String,
    val dueText: String,
    val isDone: Boolean = false,
    val priority: String = "medium",
    val deadlineText: String = dueText,
    val remindText: String = "Trước 15 phút",
    val repeatText: String = "Không",
    val note: String = "",
    val subtasks: List<TaskSubtask> = emptyList(),
    val date: String = "",
    val time: String = dueText.substringAfterLast(", "),
    val dayChoice: String = dueText.substringBefore(", ")
)

data class TaskSubtask(val id: Int, val title: String, val isDone: Boolean = false)

val sampleListTasks = listOf(
    ListTask(1, "Tập thể dục 30 phút", "Cuộc sống", "Hôm nay, 06:30", isDone = true),
    ListTask(
        2, "Nộp bài tập Lập trình di động", "Học tập", "Hôm nay, 09:00",
        isDone = true, priority = "high",
        note = "Nộp file .zip lên hệ thống của trường. Nhớ kèm ảnh chụp màn hình app chạy trên máy ảo.",
        subtasks = listOf(
            TaskSubtask(21, "Hoàn thiện màn hình đăng nhập", true),
            TaskSubtask(22, "Chụp ảnh màn hình app", true),
            TaskSubtask(23, "Nén file và nộp bài", false)
        )
    ),
    ListTask(
        3, "Ôn chương 3 – Cấu trúc dữ liệu", "Học tập", "Hôm nay, 14:00",
        note = "Tập trung phần cây nhị phân và bảng băm.",
        subtasks = listOf(
            TaskSubtask(31, "Đọc lại slide bài giảng", true),
            TaskSubtask(32, "Làm 10 bài tập cuối chương", false)
        )
    ),
    ListTask(4, "Đi chợ mua đồ cho tuần", "Cuộc sống", "Hôm nay, 17:30", priority = "low", remindText = "Tắt"),
    ListTask(5, "Gọi điện về nhà", "Cuộc sống", "Ngày mai, 21:00", priority = "low"),
    ListTask(6, "Họp nhóm đồ án", "Học tập", "Tuần này, 20:00", priority = "high", deadlineText = "Tuần này, 20:00", note = "Chuẩn bị phần demo giao diện.")
)

data class CategoryData(val title: String, val remainingCount: Int, val isStudy: Boolean)

data class DayProgressData(
    val day: String,
    val heightDp: Int,
    val isHighlighted: Boolean = false,
    val isSecondary: Boolean = false
)

fun dateForDayChoice(choice: String): String {
    val calendar = java.util.Calendar.getInstance()
    when (choice) {
        "Ngày mai" -> calendar.add(java.util.Calendar.DAY_OF_YEAR, 1)
        "Tuần này" -> {
            val daysUntilSunday = (java.util.Calendar.SUNDAY - calendar.get(java.util.Calendar.DAY_OF_WEEK) + 7) % 7
            calendar.add(java.util.Calendar.DAY_OF_YEAR, if (daysUntilSunday == 0) 7 else daysUntilSunday)
        }
    }
    return java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(calendar.time)
}
