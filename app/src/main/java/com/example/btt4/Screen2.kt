package com.example.btt4

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Màn hình 2: Onboarding 1. */
@Composable
fun Screen2(onNext: () -> Unit, onSkip: () -> Unit) {
    val data = OnboardingData(
        imageRes = R.drawable.img_onboarding1,
        title = "Mọi việc, gọn trong một chỗ",
        description = "Ghi lại mọi điều vào danh sách Todo, không lo quên việc nữa.",
        buttonText = "Tiếp tục"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp)
            .padding(top = 16.dp, bottom = 28.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.logo_uth),
                    contentDescription = "Logo",
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "UTH Todo",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
            }

            TextButton(onClick = onSkip) {
                Text(text = "Bỏ qua", fontSize = 14.sp, color = Color(0xFF64748B))
            }
        }

        Spacer(modifier = Modifier.weight(0.5f))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .background(Color(0xFFEEF2FF), shape = RoundedCornerShape(24.dp))
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = data.imageRes),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = data.title,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A),
            lineHeight = 30.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = data.description,
            fontSize = 14.sp,
            color = Color(0xFF64748B),
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(28.dp)
                    .height(6.dp)
                    .background(Color(0xFF2563EB), RoundedCornerShape(3.dp))
            )
            Spacer(modifier = Modifier.width(6.dp))
            Box(
                modifier = Modifier
                    .width(8.dp)
                    .height(6.dp)
                    .background(Color(0xFFE2E8F0), RoundedCornerShape(3.dp))
            )
            Spacer(modifier = Modifier.width(6.dp))
            Box(
                modifier = Modifier
                    .width(8.dp)
                    .height(6.dp)
                    .background(Color(0xFFE2E8F0), RoundedCornerShape(3.dp))
            )
        }

        Button(
            onClick = onNext,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
            shape = RoundedCornerShape(26.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text(
                text = data.buttonText,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }
}
