package com.soul_login.friends.planets

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

/**
 * 单个planet
 */
class PlanetView : View {
    private var signY: Float = 0.toFloat()
    private var signWidth: Float = 0.toFloat()
    private var totalSignWidth = 0.0f
    private var maxSignRange = 0.0f
    private var signX: Float = 0.toFloat()
    private var signDistanceX = 5.0f
    private var signPaint: Paint? = null
    private var signTextSize: Int = 0
    private var scale: Float = 0.toFloat()
    private var isOverstep: Boolean = false
    private var sign: String? = null

    private var totalWidth: Float = 0.toFloat()

    var signColor: Int = 0

    var bitmap: Bitmap? = null
    private var bitmapPaint: Paint? = null

    constructor(context: Context) : super(context) {
        init(context)
    }

    private fun init(context: Context) {
        signTextSize = SizeUtils.sp2px(context, 9.0f)
        signPaint = Paint(Paint.HINTING_ON)
        signPaint!!.textSize = signTextSize.toFloat()
        bitmapPaint = Paint()
        setLayerType(View.LAYER_TYPE_SOFTWARE, null)
    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        init(context)
    }

    constructor(context: Context, attributeSet: AttributeSet, i: Int) : super(context, attributeSet, i) {
        init(context)
    }

    fun setScale(scale: Float) {
        this.scale = scale
    }


    /**
     * 设置名字
     *
     * @param sign 名字
     */
    fun setSign(sign: String) {
        this.sign = sign
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        totalWidth = w.toFloat()
        signY = (bitmap!!.height + signTextSize + 4).toFloat()
        signWidth = signPaint!!.measureText(sign)
        if (signWidth > bitmap!!.width) {
            isOverstep = true
            totalSignWidth = signWidth + bitmap!!.width
            maxSignRange = bitmap!!.width + signWidth + signWidth
            signDistanceX = totalSignWidth
        } else {
            signX = (bitmap!!.width - signWidth) / 2.0f
        }

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val bitmapMargin = (totalWidth - bitmap!!.width) / 2.0f
        canvas.drawBitmap(bitmap!!, bitmapMargin, 0f, bitmapPaint)
        // 设置昵称颜色
        signPaint!!.color = signColor
        // 昵称文字过长（跑马灯）
        if (isOverstep) {
            canvas.drawText(sign!!, totalSignWidth - signDistanceX, signY, signPaint!!)
        } else {
            canvas.drawText(sign!!, signX, signY, signPaint!!)
        }

        if (isOverstep) {
            signDistanceX = signDistanceX + 2.0f
            if (signDistanceX > maxSignRange) {
                signDistanceX = signWidth
            }
        }
    }

    companion object {

        val COLOR_MALE = Color.parseColor("#FA9392")       //  男
        val COLOR_FEMALE = Color.parseColor("#7BA792")    //   女
    }
}