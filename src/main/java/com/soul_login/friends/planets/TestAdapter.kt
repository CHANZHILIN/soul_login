package com.soul_login.friends.planets

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.soul_login.R
import java.io.UnsupportedEncodingException
import java.util.*

class TestAdapter : PlanetAdapter() {

    private var mContext: Context? = null

    override val count: Int
        get() = 20

    /**
     * 获取随机昵称
     *
     * @return 随机昵称
     */
    private val randomNick: String
        get() {
            val random = Random()
            val len = random.nextInt(10) + 1
            val builder = StringBuilder()
            for (i in 0 until len) {
                builder.append(randomSingleCharacter)
            }
            return builder.toString()
        }

    /**
     * 获取随机单个汉字
     *
     * @return 随机单个汉字
     */
    private val randomSingleCharacter: String
        get() {
            var str = ""
            val heightPos: Int
            val lowPos: Int
            val rd = Random()
            heightPos = 176 + Math.abs(rd.nextInt(39))
            lowPos = 161 + Math.abs(rd.nextInt(93))
            val bt = ByteArray(2)
            bt[0] = Integer.valueOf(heightPos).toByte()
            bt[1] = Integer.valueOf(lowPos).toByte()
            try {
                str = String(bt, charset("GBK"))
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            }

            return str
        }

    override fun getView(context: Context, position: Int, parent: ViewGroup): View {
        mContext = context
        val planetView = PlanetView(context)


        val bitmap = imgUtis(context.resources, R.drawable.bg, 38, 35)
        planetView.bitmap = bitmap

        if (position % 2 == 0) {
            planetView.signColor = PlanetView.COLOR_MALE
        } else if (position % 2 == 1) {
            planetView.signColor = PlanetView.COLOR_FEMALE
        }
        planetView.setSign(randomNick)

        val starWidth = SizeUtils.dp2px(context, 50.0f)
        val starHeight = SizeUtils.dp2px(context, 40.0f)
        val starPaddingTop = SizeUtils.dp2px(context, 2.0f)
        val layoutParams = FrameLayout.LayoutParams(starWidth, starHeight)
        layoutParams.gravity = Gravity.CENTER
        planetView.setPadding(starPaddingTop, starPaddingTop, starPaddingTop, starPaddingTop)
        planetView.layoutParams = layoutParams
        return planetView
    }

    override fun getItem(position: Int): Any {
        return 0
    }

    override fun getPopularity(position: Int): Int {
        return position % 2
    }

    override fun onThemeColorChanged(view: View, themeColor: Int) {

    }

    private fun imgUtis(res: Resources, img: Int, reqWidth: Int, reqHeight: Int): Bitmap {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true  // 让解析方法禁止为bitmap分配内存,返回值也不再是一个Bitmap对象，而是null。
        BitmapFactory.decodeResource(res, img, options)
        // 在加载图片之前就获取到图片的长宽值和MIME类型，并返回压缩的尺寸
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight)
        options.inPreferredConfig = Bitmap.Config.RGB_565
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeResource(mContext!!.resources, img, options)

    }

    companion object {

        /**
         * @param options   操作对象
         * @param reqWidth  目标宽
         * @param reqHeight 目标高
         * @return
         */
        fun calculateInSampleSize(options: BitmapFactory.Options,
                                  reqWidth: Int, reqHeight: Int): Int {
            // 源图片的高度和宽度
            val height = options.outHeight
            val width = options.outWidth
            val inSampleSize: Int
            val scaleW = Math.max(reqWidth, width) / (Math.min(reqWidth, width) * 1.0)
            val scaleH = Math.max(reqHeight, height) / (Math.min(reqHeight, height) * 1.0)
            inSampleSize = Math.max(scaleW, scaleH).toInt()
            return inSampleSize
        }
    }
}