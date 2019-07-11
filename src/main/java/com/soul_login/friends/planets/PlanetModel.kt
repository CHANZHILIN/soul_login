package com.soul_login.friends.planets

import android.graphics.Color
import android.view.View

/**
 * 星球属性实体
 */
class PlanetModel @JvmOverloads constructor(
        /**
         * 3D坐标位置
         */
        var locX: Float, var locY: Float, var locZ: Float,
        /**
         * 缩放比
         */
        var mScale: Float = 1.0f,
        /**
         * 权重
         */
        var popularity: Int = DEFAULT_POPULARITY) {
    /**
     * 2D坐标位置
     */
    var loc2DX: Float = 0.toFloat()
    var loc2DY: Float = 0.toFloat()
    /**
     * 透明度
     */
    var alpha: Float = 0.toFloat()
        set(alpha) {
            field = alpha
            this.argb[0] = alpha
        }
    /**
     * 颜色
     */
    private val argb: FloatArray
    /**
     * View
     */
    var view: View? = null

    val color: Int
        get() {
            val result = IntArray(4)
            for (i in 0..3) {
                result[i] = (this.argb[i] * 0xff).toInt()
            }
            return Color.argb(result[0], result[1], result[2], result[3])
        }


    constructor() : this(0f, 0f, 0f, 1.0f, 0) {}

    init {

        this.loc2DX = 0f
        this.loc2DY = 0f

        this.argb = floatArrayOf(1.0f, 0.5f, 0.5f, 0.5f)
    }

    constructor(popularity: Int) : this(0f, 0f, 0f, 1.0f, popularity) {}

    fun getScale(): Float {
        return mScale
    }

    fun setScale(scale: Float) {
        this.mScale = scale
        if (this.view != null) {
            (this.view as PlanetView).setScale(scale)
        }
    }

    fun setColorByArray(rgb: FloatArray?) {
        if (rgb != null) {
            System.arraycopy(rgb, 0, this.argb, this.argb.size - rgb.size, rgb.size)
        }
    }

    companion object {

        /**
         * 默认权重
         */
        private val DEFAULT_POPULARITY = 5
    }
}