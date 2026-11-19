package me.weishu.kernelsu.ui.moe

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme

/** 自定义输入框（不依赖 Material） */
@Composable
private fun MoeField(
    label: String,
    value: String,
    hint: String,
    onChange: (String) -> Unit,
) {
    Text(
        text = label,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        color = colorScheme.onSurfaceVariantSummary,
        modifier = Modifier.padding(top = 10.dp, bottom = 4.dp),
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
            .background(colorScheme.surface)
            .padding(horizontal = 14.dp, vertical = 11.dp),
    ) {
        if (value.isEmpty()) {
            Text(hint, fontSize = 13.sp, color = colorScheme.onSurfaceVariantSummary)
        }
        BasicTextField(
            value = value,
            onValueChange = onChange,
            singleLine = true,
            textStyle = androidx.compose.ui.text.TextStyle(
                fontSize = 14.sp,
                color = colorScheme.onSurface,
            ),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

/** 蒙层强度滑块 */
@Composable
private fun MoeSlider(
    label: String,
    value: Float,
    onChange: (Float) -> Unit,
) {
    Text(
        text = "$label  ${(value * 100).toInt()}%",
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        color = colorScheme.onSurfaceVariantSummary,
        modifier = Modifier.padding(top = 10.dp, bottom = 6.dp),
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(28.dp)
            .pointerInput(Unit) {
                detectHorizontalDragGestures { change, _ ->
                    val f = (change.position.x / size.width.toFloat()).coerceIn(0f, 1f)
                    onChange(f)
                }
            }
            .padding(vertical = 10.dp),
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(androidx.compose.foundation.shape.RoundedCornerShape(4.dp))
                .background(colorScheme.surfaceVariant)
        )
        Box(
            Modifier
                .fillMaxWidth(value)
                .height(8.dp)
                .clip(androidx.compose.foundation.shape.RoundedCornerShape(4.dp))
                .background(colorScheme.primary)
        )
    }
}

@Composable
fun MoeAppearanceDialog(show: Boolean, onDismiss: () -> Unit) {
    if (!show) return
    val ctx = LocalContext.current

    var title by remember { mutableStateOf(MoePrefs.get(ctx, MoePrefs.KEY_BANNER_TITLE)) }
    var subtitle by remember { mutableStateOf(MoePrefs.get(ctx, MoePrefs.KEY_BANNER_SUBTITLE)) }
    var tMagisk by remember { mutableStateOf(MoePrefs.get(ctx, MoePrefs.KEY_MAGISK)) }
    var tApp by remember { mutableStateOf(MoePrefs.get(ctx, MoePrefs.KEY_APP)) }
    var tZygisk by remember { mutableStateOf(MoePrefs.get(ctx, MoePrefs.KEY_ZYGISK)) }
    var tRamdisk by remember { mutableStateOf(MoePrefs.get(ctx, MoePrefs.KEY_RAMDISK)) }
    var tSupport by remember { mutableStateOf(MoePrefs.get(ctx, MoePrefs.KEY_SUPPORT)) }
    var scrim by remember { mutableStateOf(MoePrefs.scrim(ctx)) }
    var showCheck by remember { mutableStateOf(MoePrefs.showCheck(ctx)) }
    var bgTick by remember { mutableStateOf(0) }
    var hasBg by remember { mutableStateOf(MoePrefs.hasBg(ctx)) }

    val picker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null && MoePrefs.importBg(ctx, uri)) {
            hasBg = true
            bgTick++
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 18.dp)
                    .verticalScroll(rememberScrollState()),
            ) {
                Text("打扮本小姐", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(
                    "留空 = 用默认文案",
                    fontSize = 11.sp,
                    color = colorScheme.onSurfaceVariantSummary,
                    modifier = Modifier.padding(top = 2.dp),
                )

                MoeField("首页横幅 · 标题", title, "哼，KernelSU 才不是给你玩的") { title = it }
                MoeField("首页横幅 · 副标题", subtitle, "本小姐今天也在好好工作哦") { subtitle = it }
                MoeField("超级用户卡片名", tMagisk, "超级用户") { tMagisk = it }
                MoeField("模块卡片名", tApp, "模块") { tApp = it }
                MoeField("内核项名称", tZygisk, "内核版本") { tZygisk = it }
                MoeField("管理器项名称", tRamdisk, "管理器版本") { tRamdisk = it }
                MoeField("支持开发标题", tSupport, "支持开发") { tSupport = it }

                MoeSlider("背景蒙层", scrim) { scrim = it }

                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    MoeAction("选背景图", Modifier.weight(1f)) { picker.launch("image/*") }
                    if (hasBg) {
                        MoeAction("清除背景", Modifier.weight(1f)) {
                            MoePrefs.clearBg(ctx); hasBg = false; bgTick++
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text("显示大卡上的状态勾", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    MoeAction(if (showCheck) "开着" else "关掉", Modifier.width(84.dp)) {
                        showCheck = !showCheck
                    }
                }

                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    MoeAction("算了", Modifier.weight(1f)) { onDismiss() }
                    MoeAction("好了", Modifier.weight(1f)) {
                        MoePrefs.set(ctx, MoePrefs.KEY_BANNER_TITLE, title)
                        MoePrefs.set(ctx, MoePrefs.KEY_BANNER_SUBTITLE, subtitle)
                        MoePrefs.set(ctx, MoePrefs.KEY_MAGISK, tMagisk)
                        MoePrefs.set(ctx, MoePrefs.KEY_APP, tApp)
                        MoePrefs.set(ctx, MoePrefs.KEY_ZYGISK, tZygisk)
                        MoePrefs.set(ctx, MoePrefs.KEY_RAMDISK, tRamdisk)
                        MoePrefs.set(ctx, MoePrefs.KEY_SUPPORT, tSupport)
                        MoePrefs.setScrim(ctx, (scrim * 100).toInt())
                        MoePrefs.setShowCheck(ctx, showCheck)
                        onDismiss()
                    }
                }
            }
        }
    }
}

@Composable
private fun MoeAction(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
            .background(colorScheme.secondaryContainer)
            .clickable { onClick() }
            .padding(vertical = 11.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}
