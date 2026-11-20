package me.weishu.kernelsu.ui.moe

import android.graphics.BitmapFactory
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext

/** 读背景图（缩放到合适尺寸，避免大图爆内存） */
@Composable
fun rememberMoeBg(): ImageBitmap? {
    val ctx = LocalContext.current
    return remember(ctx) {
        val f = MoePrefs.bgFile(ctx)
        if (!f.exists()) return@remember null
        runCatching {
            val opts = BitmapFactory.Options().apply { inSampleSize = 2 }
            BitmapFactory.decodeFile(f.absolutePath, opts)?.asImageBitmap()
        }.getOrNull()
    }
}

/** 花瓣 + 光斑，衬在渐变上 */
@Composable
private fun MoeDecor() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // 光斑
        fun glow(cx: Float, cy: Float, r: Float, a: Float) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color.White.copy(alpha = a), Color.Transparent),
                    center = Offset(cx, cy),
                    radius = r,
                ),
                radius = r,
                center = Offset(cx, cy),
            )
        }
        glow(w * 0.78f, h * 0.10f, w * 0.55f, 0.55f)
        glow(w * 0.10f, h * 0.42f, w * 0.40f, 0.40f)
        glow(w * 0.62f, h * 0.80f, w * 0.50f, 0.35f)

        // 花瓣
        val petals = listOf(
            Triple(0.86f, 0.10f, 22f),
            Triple(0.72f, 0.23f, -28f),
            Triple(0.92f, 0.40f, 45f),
            Triple(0.14f, 0.17f, 12f),
            Triple(0.24f, 0.66f, -18f),
            Triple(0.80f, 0.58f, 34f),
        )
        for ((fx, fy, deg) in petals) {
            val pw = w * 0.026f
            val ph = w * 0.046f
            rotate(deg, pivot = Offset(w * fx, h * fy)) {
                drawOval(
                    color = Color(0xFFFFC8DE).copy(alpha = 0.55f),
                    topLeft = Offset(w * fx - pw / 2, h * fy - ph / 2),
                    size = Size(pw, ph),
                )
            }
        }
    }
}

/**
 * 页面背景：渐变（或你选的照片）+ 光斑花瓣 + 可调白色蒙层。
 * Android 11 没有 RenderEffect，做不了实时高斯模糊，用蒙层代替。
 */
@Composable
fun MoePageBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val ctx = LocalContext.current
    val bg = rememberMoeBg()
    val scrim = MoePrefs.scrim(ctx)
    MoePrefs.hasBg(ctx) // 让换图后能重组

    Box(modifier = modifier.fillMaxSize()) {
        if (bg != null) {
            Image(
                bitmap = bg,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        } else {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFFFF0F7),
                                Color(0xFFF1EBFF),
                                Color(0xFFE6F6FF),
                            ),
                        )
                    )
            )
            MoeDecor()
        }
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.White.copy(alpha = scrim))
        )
        content()
    }
}

/** 取自定义文案 */
@Composable
fun moeText(key: String, fallback: String): String {
    val ctx = LocalContext.current
    return MoePrefs.text(ctx, key, fallback)
}
