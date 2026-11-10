package com.example.btt4

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.messaging.FirebaseMessaging
import com.example.btt4.ui.theme.BtT4Theme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                "todo_updates",
                "Thông báo UTH Todo",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            getSystemService(NotificationManager::class.java)
                .createNotificationChannel(notificationChannel)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1001)
        }

        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { token -> Log.d("FCM_TOKEN", token) }
            .addOnFailureListener { error -> Log.e("FCM_TOKEN", "Could not get FCM token", error) }

        setContent {
            BtT4Theme {
                MainApp()
            }
        }
    }
}

/**
 * Điều hướng giữa các màn hình:
 * 1: Screen1 (Splash Screen)
 * 2: Screen2 (Onboarding 1)
 * 3: Screen3 (Onboarding 2)
 * 4: Screen4 (Onboarding 3 - "Nhắc đúng lúc")
 * 5: LoginScreen (Trang Đăng nhập)
 * 6: HomeScreen (Trang chủ - Quản lý việc)
 * 7: ProfileScreen (Trang Hồ sơ cá nhân)
 */
@Composable
fun MainApp() {
    var currentScreen by remember { mutableStateOf(1) }
    var listCategory by remember { mutableStateOf<String?>(null) }
    val listTasks = remember { mutableStateListOf(*sampleListTasks.toTypedArray()) }
    var selectedTask by remember { mutableStateOf<ListTask?>(null) }
    var formReturnScreen by remember { mutableStateOf(8) }
    var currentUser by remember { mutableStateOf(UserData()) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val completedTasks = listTasks.count { it.isDone }
    val pendingTasks = listTasks.size - completedTasks
    val profileUser = currentUser.copy(
        completedCount = completedTasks,
        pendingCount = pendingTasks,
        completionPercentage = if (listTasks.isEmpty()) "0%" else "${completedTasks * 100 / listTasks.size}%"
    )

    fun updateLocalTask(updated: ListTask) {
        val index = listTasks.indexOfFirst { it.id == updated.id }
        if (index >= 0) listTasks[index] = updated
        if (selectedTask?.id == updated.id) selectedTask = updated
    }

    fun patchTask(updated: ListTask) {
        updateLocalTask(updated)
        coroutineScope.launch {
            try {
                TaskApi.updateTask(BuildConfig.MOCK_API_TOKEN, updated)
            } catch (error: Exception) {
                Toast.makeText(context, "Không đồng bộ được: ${error.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun toggleTask(id: Int) {
        val task = listTasks.firstOrNull { it.id == id } ?: return
        patchTask(task.copy(isDone = !task.isDone))
    }
    fun openTask(id: Int) {
        selectedTask = listTasks.firstOrNull { it.id == id }
        if (selectedTask == null) return
        currentScreen = 9
        coroutineScope.launch {
            try {
                val remoteTask = TaskApi.getTask(BuildConfig.MOCK_API_TOKEN, id).toListTask()
                if (remoteTask.id == id) updateLocalTask(remoteTask)
            } catch (error: Exception) {
                Toast.makeText(context, "Không tải được chi tiết: ${error.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    LaunchedEffect(Unit) {
        if (BuildConfig.MOCK_API_TOKEN.isBlank()) {
            Toast.makeText(context, "Thiếu MOCK_API_TOKEN trong local.properties", Toast.LENGTH_LONG).show()
        } else {
            try {
                val loadedTasks = TaskApi.getTasks(BuildConfig.MOCK_API_TOKEN).map { it.toListTask() }
                listTasks.clear()
                listTasks.addAll(loadedTasks)
            } catch (error: Exception) {
                Toast.makeText(context, "Không tải được công việc: ${error.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }
    fun showFirebaseUser(firebaseUser: FirebaseUser, isGoogleLinked: Boolean) {
        val displayName = firebaseUser.displayName
            ?.takeIf { it.isNotBlank() }
            ?: firebaseUser.email?.substringBefore("@")
            ?: "Người dùng"
        val avatarText = displayName
            .split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .joinToString("") { it.first().uppercase() }
            .ifBlank { "U" }

        currentUser = UserData(
            name = displayName,
            email = firebaseUser.email.orEmpty(),
            avatarText = avatarText,
            isGoogleLinked = isGoogleLinked
        )
        currentScreen = 6
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Crossfade(
            targetState = currentScreen,
            modifier = Modifier.padding(innerPadding),
            label = "ScreenTransition"
        ) { screen ->
            when (screen) {
                // 1. Màn hình Chào (Splash)
                1 -> Screen1(
                    onNext = { currentScreen = 2 }
                )

                // 2. Màn hình Hướng dẫn 1
                2 -> Screen2(
                    onNext = { currentScreen = 3 },
                    onSkip = { currentScreen = 5 } // Bỏ qua đến Đăng nhập
                )

                // 3. Màn hình Hướng dẫn 2
                3 -> Screen3(
                    onNext = { currentScreen = 4 },
                    onSkip = { currentScreen = 5 } // Bỏ qua đến Đăng nhập
                )

                // 4. Màn hình Hướng dẫn 3: Bấm "Bắt đầu ngay" -> Nhảy ra trang Đăng nhập
                4 -> Screen4(
                    onStart = { currentScreen = 5 },
                    onSkip = { currentScreen = 5 }
                )

                // 5. Trang Đăng nhập: Google hoặc dùng thử không cần tài khoản
                5 -> LoginScreen(
                    onLoginWithGoogle = {
                        coroutineScope.launch {
                            try {
                                val googleIdOption = GetGoogleIdOption.Builder()
                                    .setServerClientId(context.getString(R.string.default_web_client_id))
                                    .setFilterByAuthorizedAccounts(false)
                                    .build()

                                val request = GetCredentialRequest.Builder()
                                    .addCredentialOption(googleIdOption)
                                    .build()

                                val result = CredentialManager.create(context)
                                    .getCredential(context, request)
                                val credential = result.credential

                                if (credential is CustomCredential &&
                                    credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                                ) {
                                    val googleIdToken =
                                        GoogleIdTokenCredential.createFrom(credential.data).idToken
                                    val firebaseCredential =
                                        GoogleAuthProvider.getCredential(googleIdToken, null)

                                    FirebaseAuth.getInstance()
                                        .signInWithCredential(firebaseCredential)
                                        .addOnCompleteListener { task ->
                                            val firebaseUser = if (task.isSuccessful) {
                                                task.result?.user
                                            } else {
                                                null
                                            }

                                            if (firebaseUser != null) {
                                                showFirebaseUser(firebaseUser, isGoogleLinked = true)
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    task.exception?.localizedMessage
                                                        ?: "Đăng nhập Firebase thất bại.",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                        }
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Không nhận được thông tin tài khoản Google.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            } catch (error: GetCredentialException) {
                                Toast.makeText(
                                    context,
                                    error.localizedMessage ?: "Không thể đăng nhập Google.",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    },
                    onContinueAsGuest = {
                        currentUser = UserData(
                            name = "Minh Anh",
                            email = "minhanh@example.com",
                            avatarText = "MA",
                            isGoogleLinked = false
                        )
                        currentScreen = 6
                    }
                )

                // 6. Trang chủ: Bấm ô tròn bên trái (Avatar) -> Vào Hồ sơ
                6 -> HomeScreen(
                    userData = currentUser,
                    tasks = listTasks,
                    onOpenProfile = { currentScreen = 7 },
                    onOpenTaskList = { category ->
                        listCategory = category
                        currentScreen = 8
                    },
                    onOpenTask = { id -> openTask(id) },
                    onToggleTask = { id -> toggleTask(id) },
                    onAddTask = {
                        selectedTask = null
                        formReturnScreen = 6
                        currentScreen = 10
                    }
                )

                // 8. Danh sách công việc
                8 -> TaskListScreen(
                    tasks = listTasks,
                    initialCategory = listCategory,
                    onOpenHome = { currentScreen = 6 },
                    onOpenNotifications = { },
                    onOpenSettings = { },
                    onAddTask = { selectedTask = null; formReturnScreen = 8; currentScreen = 10 },
                    onToggleTask = { id -> toggleTask(id) },
                    onOpenTask = { task -> openTask(task.id) }
                )

                // 9. Chi tiết công việc
                9 -> selectedTask?.let { task ->
                    TaskDetailScreen(
                        task = task,
                        onBack = { currentScreen = 8 },
                        onEdit = { formReturnScreen = 9; currentScreen = 10 },
                        onUpdateTask = { updated -> patchTask(updated) },
                        onDeleteTask = { id ->
                            coroutineScope.launch {
                                try {
                                    TaskApi.deleteTask(BuildConfig.MOCK_API_TOKEN, id)
                                    listTasks.removeAll { it.id == id }
                                    selectedTask = null
                                    currentScreen = 8
                                } catch (error: Exception) {
                                    Toast.makeText(context, "Không xóa được công việc: ${error.localizedMessage}", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    )
                }

                // 10. Thêm / sửa công việc
                10 -> TaskFormScreen(
                    task = selectedTask,
                    onCancel = { currentScreen = formReturnScreen },
                    onSave = { savedTask ->
                        coroutineScope.launch {
                            try {
                                if (savedTask.id == 0) {
                                    val created = TaskApi.createTask(BuildConfig.MOCK_API_TOKEN, savedTask)
                                    val taskToAdd = savedTask.copy(id = created.id)
                                    listTasks.add(taskToAdd)
                                    selectedTask = taskToAdd
                                    Toast.makeText(context, "Đã thêm công việc", Toast.LENGTH_SHORT).show()
                                } else {
                                    TaskApi.updateTask(BuildConfig.MOCK_API_TOKEN, savedTask)
                                    updateLocalTask(savedTask)
                                    selectedTask = savedTask
                                    Toast.makeText(context, "Đã lưu thay đổi", Toast.LENGTH_SHORT).show()
                                }
                                currentScreen = 9
                            } catch (error: Exception) {
                                Toast.makeText(context, "Không lưu được: ${error.localizedMessage}", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                )
                // 7. Trang Hồ sơ: Bấm "< Trang chủ" quay lại Trang chủ, Bấm "Đăng xuất" về Đăng nhập
                7 -> ProfileScreen(
                    userData = profileUser,
                    onBackToHome = { currentScreen = 6 },
                    onLogout = {
                        FirebaseAuth.getInstance().signOut()
                        currentUser = UserData()
                        currentScreen = 5
                    }
                )
            }
        }
    }
}













