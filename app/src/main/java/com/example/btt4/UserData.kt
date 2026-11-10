package com.example.btt4

/**
 * Mô hình OOP đại diện cho thông tin tài khoản người dùng
 */
data class UserData(
    val name: String = "Minh Anh",
    val email: String = "minhanh@example.com",
    val avatarText: String = "MA",
    val isGoogleLinked: Boolean = true,
    val completedCount: Int = 2,
    val pendingCount: Int = 4,
    val completionPercentage: String = "33%"
) {
    fun getGreeting(): String = "Chào $name!"
    fun getFullEmail(): String = email
}
