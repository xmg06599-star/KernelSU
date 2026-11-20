package me.weishu.kernelsu.ui.moe

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.yukonga.miuix.kmp.basic.Text

/** 当前是否深色（跟随 App 里的主题设置，而非只看系统） */
@Composable
fun moeIsDark(): Boolean {
    val ctx = LocalContext.current
    val mode = ctx.getSharedPreferences("settings", Context.MODE_PRIVATE).getInt("color_mode", 0)
    val sys = isSystemInDarkTheme()
    return when (mode) {
        2, 5 -> true
        1, 4 -> false
        else -> sys
    }
}

/** 玻璃卡片底色 */
@Composable
fun moeGlass(): Color = if (moeIsDark()) Color.White.copy(alpha = 0.10f) else Color.White.copy(alpha = 0.62f)

/** 玻璃卡片描边 */
@Composable
fun moeGlassBorder(): Color = if (moeIsDark()) Color.White.copy(alpha = 0.14f) else Color.White.copy(alpha = 0.75f)

/** 主色文字 */
@Composable
fun moeInk(): Color = if (moeIsDark()) Color(0xFFF0E8FF) else Color(0xFF3B3157)

/** 次要文字 */
@Composable
fun moeInkSub(): Color = if (moeIsDark()) Color(0xFFB9AECF) else Color(0xFF7A6E96)

/** 强调粉 */
val MoePink = Color(0xFFF0629A)

/**
 * 首页大卡：粉紫渐变 + 描边 + 阴影，右下角一圈绿色勾。
 */
@Composable
fun MoeHeroCard(
    title: String,
    subtitle: String,
    showCheck: Boolean,
    onClick: () -> Unit,
) {
    val dark = moeIsDark()
    val shape = RoundedCornerShape(24.dp)
    val bg = if (dark) {
        Brush.linearGradient(listOf(Color(0xFF352941), Color(0xFF2A2438)))
    } else {
        Brush.linearGradient(listOf(Color(0xFFFFE4F0), Color(0xFFF4E8FF)))
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(14.dp, shape, clip = false)
            .clip(shape)
            .background(bg)
            .border(1.dp, moeGlassBorder(), shape)
            .clickable { onClick() },
    ) {
        // 右下角装饰勾（超出边界，靠 clip 裁掉一角）
        if (showCheck) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 18.dp, y = 22.dp)
                    .size(120.dp)
                    .clip(CircleShape)
                    .border(
                        width = 9.dp,
                        color = if (dark) Color(0xFF7AC48C).copy(alpha = 0.35f)
                        else Color(0xFF7AC48C).copy(alpha = 0.55f),
                        shape = CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "✓",
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6ABA80).copy(alpha = if (dark) 0.5f else 0.75f),
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 22.dp, end = 96.dp, top = 26.dp, bottom = 28.dp),
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = moeInk(),
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = moeInkSub(),
            )
        }
    }
}

/**
 * 小卡：玻璃底 + 左边标签、右边数字。
 */
@Composable
fun MoeMiniCard(
    label: String,
    number: String,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(18.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(10.dp, shape, clip = false)
            .clip(shape)
            .background(moeGlass())
            .border(1.dp, moeGlassBorder(), shape)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = label,
                fontSize = 11.sp,
                color = moeInkSub(),
                modifier = Modifier.weight(1f),
            )
            Text(
                text = number,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = MoePink,
                textAlign = TextAlign.End,
            )
        }
    }
}

/** 玻璃容器：给信息列表用 */
@Composable
fun moeGlassCardModifier(): Modifier {
    val shape = RoundedCornerShape(20.dp)
    return Modifier
        .fillMaxWidth()
        .shadow(10.dp, shape, clip = false)
        .clip(shape)
        .background(moeGlass())
        .border(1.dp, moeGlassBorder(), shape)
}
