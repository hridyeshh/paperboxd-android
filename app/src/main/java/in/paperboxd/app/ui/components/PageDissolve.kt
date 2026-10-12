package `in`.paperboxd.app.ui.components

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer

/**
 * Shopify Editions–style page change for `AnimatedContent`, ported from the
 * CrossFade pass on shopify.com/editions/winter2026 and re-tuned for the paper
 * ground: the old page turns into an ink sketch of itself, then burns away
 * along a ragged edge rising from the CTA while the new page shows through.
 *
 * Use `EnterTransition.None togetherWith ExitTransition.None` and put
 * [pageDissolve] on every page's root. Both pages run the same edge field and
 * each keeps only its side of it, so draw order doesn't matter. `reverse`
 * (back navigation) plays it backwards: the page you return to un-burns over
 * the one you leave. Twin of iOS `PageDissolve.metal` — change both together.
 */
val pageDissolveSupported: Boolean by lazy {
    // RuntimeShader is Android 13+; a driver that rejects the AGSL falls back too.
    Build.VERSION.SDK_INT >= 33 && runCatching { RuntimeShader(AGSL) }
        .onFailure { Log.w("PageDissolve", "AGSL rejected, using fallback transition", it) }
        .isSuccess
}

@Composable
fun AnimatedVisibilityScope.pageDissolve(reverse: Boolean = false): Modifier =
    if (pageDissolveSupported) dissolveLayer(reverse) else Modifier

@RequiresApi(33)
@Composable
private fun AnimatedVisibilityScope.dissolveLayer(reverse: Boolean): Modifier {
    // 0 while settled, 1 at the far end of a page change: the incoming page
    // runs 1→0, the leaving one 0→1, so both map to the same timeline.
    val away by transition.animateFloat({ tween(800, easing = LinearEasing) }, label = "dissolve") {
        if (it == EnterExitState.Visible) 0f else 1f
    }
    val leaving = transition.targetState == EnterExitState.PostExit
    val shader = remember { RuntimeShader(AGSL) }
    return Modifier.graphicsLayer {
        if (away == 0f) {
            renderEffect = null
        } else {
            val t = if (leaving) away else 1f - away
            shader.setFloatUniform("size", size.width, size.height)
            shader.setFloatUniform("progress", if (reverse) 1f - t else t)
            // Forwards the leaving page burns; backwards the arriving one un-burns.
            shader.setFloatUniform("burning", if (leaving != reverse) 1f else 0f)
            renderEffect = RenderEffect.createRuntimeShaderEffect(shader, "content").asComposeRenderEffect()
        }
    }
}

private const val AGSL = """
uniform shader content;
uniform float2 size;
uniform float progress;
uniform float burning;

const float3 PAPER = float3(0.949, 0.929, 0.882); // HL.Paper
const float3 INK = float3(0.082, 0.082, 0.075);   // HL.Ink
const float3 EMBER = float3(0.824, 0.231, 0.149); // HL.Accent
const float3 OCHRE = float3(0.788, 0.588, 0.169); // HL.Ochre

float pdHash(float2 p) {
    p = fract(p * float2(123.34, 456.21));
    p += dot(p, p + 45.32);
    return fract(p.x * p.y);
}

float pdNoise(float2 p) {
    float2 i = floor(p);
    float2 f = fract(p);
    float2 u = f * f * (3.0 - 2.0 * f);
    return mix(mix(pdHash(i), pdHash(i + float2(1.0, 0.0)), u.x),
               mix(pdHash(i + float2(0.0, 1.0)), pdHash(i + float2(1.0, 1.0)), u.x), u.y);
}

float pdFbm(float2 p) {
    float v = 0.0;
    float a = 0.5;
    for (int i = 0; i < 4; i++) {
        v += a * pdNoise(p);
        p = p * 2.03 + 17.0;
        a *= 0.5;
    }
    return v / 0.9375;
}

float pdLuma(float4 c) { return dot(c.rgb, float3(0.299, 0.587, 0.114)); }

half4 main(float2 pos) {
    float2 uv = pos / size;
    float aspect = size.x / size.y;
    float t = progress * 0.8;
    float p = progress * (2.0 - progress);

    float2 q = float2(uv.x * aspect, uv.y);
    float shape = length((uv - float2(0.5, 1.08)) * float2(aspect, 1.0)) / length(float2(0.5 * aspect, 1.08));
    float n = pdFbm(q * 2.5 + float2(0.0, t * 0.4)) * 2.0 - 1.0;
    float fibre = pdNoise(q * 40.0 + t * 3.0) * 0.66 + pdNoise(q * 110.0 - t * 2.0) * 0.34;
    float threshold = shape * 0.66 + 0.17 + n * 0.15 + (fibre * 2.0 - 1.0) * 0.035;
    threshold *= 1.0 - smoothstep(0.9, 1.0, progress);
    float edge = mix(0.12, 0.84, p) - threshold;
    float aa = 1.5 / size.y;

    // Pages draw over the screen's paper Box, so composite them onto paper first.
    if (burning < 0.5) {
        float shown = smoothstep(-aa, aa, edge);
        if (shown <= 0.0) return half4(0.0);
        float4 c = float4(content.eval(pos));
        return half4(float4((c.rgb + PAPER * (1.0 - c.a)) * shown, shown));
    }
    if (edge > aa) return half4(0.0);

    float2 ctr = size * 0.5;
    float2 sp = ctr + (pos - ctr) * (1.0 - 0.05 * smoothstep(0.1, 1.0, p));
    float4 col = float4(content.eval(sp));
    float3 rgb = col.rgb + PAPER * (1.0 - col.a);

    float o = size.y / 850.0;
    float gx = pdLuma(float4(content.eval(sp + float2(o, 0.0)))) - pdLuma(float4(content.eval(sp - float2(o, 0.0))));
    float gy = pdLuma(float4(content.eval(sp + float2(0.0, o)))) - pdLuma(float4(content.eval(sp - float2(0.0, o))));
    float3 sketch = mix(PAPER, INK, clamp((abs(gx) + abs(gy)) * 3.0, 0.0, 1.0));
    rgb = mix(rgb, sketch, smoothstep(0.0, 0.5, p));

    float x = -edge;
    float flicker = 0.75 + 0.25 * sin(t * 9.0 + uv.x * 18.0 + n * 6.0);
    float heat = exp(-max(x, 0.0) / 0.003) * flicker;
    rgb = mix(rgb, mix(EMBER, OCHRE, heat), min(heat * 1.6, 1.0));
    rgb = mix(rgb, INK, (1.0 - smoothstep(0.0, 0.002, x)) * 0.8);

    float a = 1.0 - smoothstep(-aa, aa, edge);
    return half4(float4(rgb * a, a));
}
"""
