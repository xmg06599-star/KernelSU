package me.weishu.kernelsu.ui.moe

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
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

/**
 * 页面背景：铺一张图 + 半透明白色蒙层。
 * （Android 11 没有 RenderEffect，做不了实时高斯模糊，用蒙层代替）
 */
@Composable
fun MoePageBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val ctx = LocalContext.current
    val bg = rememberMoeBg()
    val scrim = MoePrefs.scrim(ctx)

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
                        androidx.compose.ui.graphics.Brush.linearGradient(
                            listOf(
                                Color(0xFFFFF0F7),
                                Color(0xFFF1EBFF),
                                Color(0xFFE6F6FF),
                            )
                        )
                    )
            )
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
