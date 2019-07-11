package com.soul_login.friends.planets

import android.content.Context

object SizeUtils {

    /**
     * dp转px
     *
     * @param context 上下文
     * @param dp      dp值
     * @return px值
     */
    fun dp2px(context: Context, dp: Float): Int {
        return (dp * context.resources.displayMetrics.density + 0.5f).toInt()
    }

    /**
     * sp转px
     *
     * @param context 上下文
     * @param sp      sp值
     * @return px值
     */
    fun sp2px(context: Context, sp: Float): Int {
        return (sp * context.resources.displayMetrics.scaledDensity + 0.5f).toInt()
    }
}
