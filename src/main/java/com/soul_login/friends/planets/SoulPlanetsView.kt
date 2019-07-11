package com.soul_login.friends.planets

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.os.Handler
import android.os.Looper
import android.support.annotation.IntDef
import android.util.AttributeSet
import android.view.*
import com.soul_login.R
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * 星球云
 */
class SoulPlanetsView : ViewGroup, Runnable, PlanetAdapter.OnDataSetChangeListener {
    /**
     * 设置滚动模式
     *
     * @param mode 滚动模式
     */
    @get:Mode
    var autoScrollMode: Int = 0
    private var speed = 4f
    private var mPlanetCalculator: PlanetCalculator? = null
    private var mAngleX: Float = 0.toFloat()
    private var mAngleY: Float = 0.toFloat()
    private var centerX: Float = 0.toFloat()
    private var centerY: Float = 0.toFloat()
    private var radius: Float = 0.toFloat()
    /**
     * 半径的百分比
     */
    private var radiusPercent = 0.9f
    private var darkColor = floatArrayOf(1.0f, 0.0f, 0.0f, 1.0f)
    private var lightColor = floatArrayOf(0.9412f, 0.7686f, 0.2f, 1.0f)
    /**
     * 是否支持手动滑动
     */
    private var manualScroll: Boolean = false
    private var layoutParams: ViewGroup.MarginLayoutParams? = null
    private var minSize: Int = 0
    private var isOnTouch = false
    private val mHandler = Handler(Looper.getMainLooper())
    private var planetAdapter: PlanetAdapter = NullPlanetAdapter()
    private var onTagClickListener: OnTagClickListener? = null
    private var downX: Float = 0f
    private var downY: Float = 0f
    private var mScaleX: Float = 0f
    private var startDistance: Float = 0.toFloat()
    private var multiplePointer: Boolean = false
    private var startX: Float = 0.toFloat()
    private var startY: Float = 0.toFloat()

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        isFocusableInTouchMode = true
        mPlanetCalculator = PlanetCalculator()
        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SoulPlanetsView)
            autoScrollMode = typedArray.getInteger(R.styleable.SoulPlanetsView_autoScrollMode, MODE_DISABLE)
            setManualScroll(typedArray.getBoolean(R.styleable.SoulPlanetsView_manualScroll, true))
            mAngleX = typedArray.getFloat(R.styleable.SoulPlanetsView_startAngleX, 0.5f)
            mAngleY = typedArray.getFloat(R.styleable.SoulPlanetsView_startAngleY, 0.5f)
            setLightColor(typedArray.getColor(R.styleable.SoulPlanetsView_lightColor, Color.WHITE))
            setDarkColor(typedArray.getColor(R.styleable.SoulPlanetsView_darkColor, Color.BLACK))
            setRadiusPercent(typedArray.getFloat(R.styleable.SoulPlanetsView_radiusPercent, radiusPercent))
            setScrollSpeed(typedArray.getFloat(R.styleable.SoulPlanetsView_scrollSpeed, 2f))
            typedArray.recycle()
        }
        val wm = getContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val point = Point()
        wm?.defaultDisplay?.getSize(point)
        val screenWidth = point.x
        val screenHeight = point.y
        minSize = if (screenHeight < screenWidth) screenHeight else screenWidth

        initFromAdapter()
    }

    fun setManualScroll(manualScroll: Boolean) {
        this.manualScroll = manualScroll
    }

    fun setLightColor(color: Int) {
        lightColor = floatArrayOf(Color.alpha(color).toFloat() / 1.0f / 0xff, Color.red(color).toFloat() / 1.0f / 0xff, Color.green(color).toFloat() / 1.0f / 0xff, Color.blue(color).toFloat() / 1.0f / 0xff)
                .clone()
        onChange()
    }

    fun setDarkColor(color: Int) {
        darkColor = floatArrayOf(Color.alpha(color).toFloat() / 1.0f / 0xff, Color.red(color).toFloat() / 1.0f / 0xff, Color.green(color).toFloat() / 1.0f / 0xff, Color.blue(color).toFloat() / 1.0f / 0xff)
                .clone()
        onChange()
    }

    fun setRadiusPercent(percent: Float) {
        if (percent > 1 || percent < 0) {
            throw IllegalArgumentException("Percent value not in range 0 to 1.")
        } else {
            radiusPercent = percent
            onChange()
        }
    }

    fun setScrollSpeed(scrollSpeed: Float) {
        speed = scrollSpeed
    }

    /**
     * 初始化VIew根据Adapter
     */
    fun initFromAdapter() {
        this.post {
            // 中心坐标
            centerX = (right - left) / 2f
            centerY = (bottom - top) / 2f
            // 半径
            radius = Math.min(centerX, centerY) * radiusPercent
            mPlanetCalculator!!.setRadius(radius.toInt())

            mPlanetCalculator!!.setTagColorLight(lightColor)
            mPlanetCalculator!!.setTagColorDark(darkColor)

            mPlanetCalculator!!.clear()

            for (i in 0 until planetAdapter.count) {
                // 为每个Tag绑定View
                val planetModel = PlanetModel(planetAdapter.getPopularity(i))
                val view = planetAdapter.getView(context, i, this@SoulPlanetsView)
                planetModel.view = view
                mPlanetCalculator!!.add(planetModel)
                // 点击事件监听
                addListener(view!!, i)
            }
            mPlanetCalculator!!.create(true)
            mPlanetCalculator!!.setAngleX(mAngleX)
            mPlanetCalculator!!.setAngleY(mAngleY)
            mPlanetCalculator!!.update()

            resetChildren()
        }
    }

    override fun onChange() {
        post(this)
    }

    private fun addListener(view: View, position: Int) {
        if (!view.hasOnClickListeners() && onTagClickListener != null) {
            view.setOnClickListener { v -> onTagClickListener!!.onItemClick(this@SoulPlanetsView, v, position) }
        }
    }

    /**
     * 重新设置子View
     */
    private fun resetChildren() {
        removeAllViews()
        // 必须保证getChildAt(i) == mTagCloud.getTagList().get(i)
        for (planetModel in mPlanetCalculator!!.tagList!!) {
            addView(planetModel.view)
        }
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    /**
     * 谁知适配器
     *
     * @param adapter 适配器
     */
    fun setAdapter(adapter: PlanetAdapter) {
        planetAdapter = adapter
        planetAdapter.setOnDataSetChangeListener(this)
        onChange()
    }

    fun reset() {
        mPlanetCalculator!!.reset()
        resetChildren()
    }

    override fun onTrackballEvent(e: MotionEvent): Boolean {
        if (manualScroll) {
            val x = e.x
            val y = e.y

            mAngleX = y * speed * TRACKBALL_SCALE_FACTOR
            mAngleY = -x * speed * TRACKBALL_SCALE_FACTOR

            mPlanetCalculator!!.setAngleX(mAngleX)
            mPlanetCalculator!!.setAngleY(mAngleY)
            mPlanetCalculator!!.update()

            resetChildren()
        }
        return true
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        if (manualScroll) {
            handleTouchEvent(e)
        }
        return true
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val contentWidth = View.MeasureSpec.getSize(widthMeasureSpec)
        val contentHeight = View.MeasureSpec.getSize(heightMeasureSpec)
        val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)

        if (layoutParams == null) {
            layoutParams = getLayoutParams() as ViewGroup.MarginLayoutParams
        }

        val dimensionX = if (widthMode == View.MeasureSpec.EXACTLY) contentWidth else minSize - layoutParams!!.leftMargin - layoutParams!!.rightMargin
        val dimensionY = if (heightMode == View.MeasureSpec.EXACTLY) contentHeight else minSize - layoutParams!!.leftMargin - layoutParams!!.rightMargin
        setMeasuredDimension(dimensionX, dimensionY)

        measureChildren(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
    }

    /**
     * 处理触摸事件
     */
    private fun handleTouchEvent(event: MotionEvent): Boolean {
        // 触摸点个数
        val pointerCount = event.pointerCount
        if (pointerCount > 1) {
            multiplePointer = true
        }
        val action = event.actionMasked
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                isOnTouch = true
                downX = event.x
                downY = event.y
                startX = downX
                startY = downY
            }
            MotionEvent.ACTION_POINTER_DOWN -> if (event.actionIndex == 1) {
                // 第二个触摸点
                mScaleX = getScaleX()
                startDistance = distance(event.getX(0) - event.getX(1),
                        event.getY(0) - event.getY(1))
                return true
            }
            MotionEvent.ACTION_MOVE -> if (pointerCount == 1 && !multiplePointer) {
                // 单点触摸，旋转星球
                val dx = event.x - downX
                val dy = event.y - downY
                if (isValidMove(dx, dy)) {
                    mAngleX = dy / radius * speed * TOUCH_SCALE_FACTOR
                    mAngleY = -dx / radius * speed * TOUCH_SCALE_FACTOR
                    processTouch()
                    downX = event.x
                    downY = event.y
                }
                return isValidMove(downX - startX, downY - startY)
            } else if (pointerCount == 2) {
                // 双点触摸，缩放
                val endDistance = distance(event.getX(0) - event.getX(1),
                        event.getY(0) - event.getY(1))
                // 缩放比例
                var scale = ((endDistance - startDistance) / (endDistance * 2) + 1) * mScaleX
                if (scale > 1.4f) {
                    scale = 1.2f
                }
                if (scale < 1) {
                    scale = 1f
                }
                setScaleX(scale)
                scaleY = scale
                return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                multiplePointer = false
                isOnTouch = false
            }
            else -> {
            }
        }
        return false
    }

    /**
     * 两点之间的距离
     *
     * @param x x轴距离
     * @param y y轴距离
     * @return 两点之间的距离
     */
    private fun distance(x: Float, y: Float): Float {
        return Math.sqrt((x * x + y * y).toDouble()).toFloat()
    }

    /**
     * 是否是有效移动
     *
     * @param dx x轴位移
     * @param dy y轴位移
     * @return 是否是有效移动
     */
    private fun isValidMove(dx: Float, dy: Float): Boolean {
        val minDistance = ViewConfiguration.get(context).scaledTouchSlop
        return Math.abs(dx) > minDistance || Math.abs(dy) > minDistance
    }

    /**
     * 更新视图
     */
    private fun processTouch() {
        // 设置旋转的X,Y
        if (mPlanetCalculator != null) {
            mPlanetCalculator!!.setAngleX(mAngleX)
            mPlanetCalculator!!.setAngleY(mAngleY)
            mPlanetCalculator!!.update()
        }
        for (i in 0 until childCount) {
            val planetModel = mPlanetCalculator!![i]
            val child = planetModel.view
            // 更新每一个ChildView
            if (child != null && child.visibility != View.GONE) {
                planetAdapter.onThemeColorChanged(child, planetModel.color)
                // 缩放小于1的设置不可点击
                if (planetModel.getScale() < 1.0f) {
                    child.scaleX = planetModel.getScale()
                    child.scaleY = planetModel.getScale()
                    child.isClickable = false
                } else {
                    child.isClickable = true
                }
                // 设置透明度
                child.alpha = planetModel.getScale()
                val left = (centerX + planetModel.loc2DX).toInt() - child.measuredWidth / 2
                val top = (centerY + planetModel.loc2DY).toInt() - child.measuredHeight / 2
                // 从View的Tag里取出位置之前的位置信息，平移新旧位置差值
                val originLocation :IntArray? = child.tag as IntArray?
                if (originLocation != null && originLocation.size > 0) {
                    child.translationX = (left - originLocation[0]).toFloat()
                    child.translationY = (top - originLocation[1]).toFloat()
                    // 小于移动速度，刷新
                    if (Math.abs(mAngleX) <= speed && Math.abs(mAngleY) <= speed) {
                        child.invalidate()
                    }
                }
            }
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return if (manualScroll) {
            handleTouchEvent(ev)
        } else false
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        mHandler.post(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mHandler.removeCallbacksAndMessages(null)
    }

    @SuppressLint("DrawAllocation")
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val planetModel = mPlanetCalculator!![i]
            if (child != null && child.visibility != View.GONE) {
                planetAdapter.onThemeColorChanged(child, planetModel.color)
                // 设置缩放
                if (planetModel.getScale() < 1f) {
                    child.scaleX = planetModel.getScale()
                    child.scaleY = planetModel.getScale()
                }
                // 设置透明度
                child.alpha = planetModel.getScale()
                // 设置位置
                val left = (centerX + planetModel.loc2DX).toInt() - child.measuredWidth / 2
                val top = (centerY + planetModel.loc2DY).toInt() - child.measuredHeight / 2

                child.layout(left, top, left + child.measuredWidth, top + child.measuredHeight)
                // 设置位置信息的TAG
                child.tag = intArrayOf(left, top)
            }
        }
    }

    /**
     * 设置标签点击事件监听
     */
    fun setOnTagClickListener(listener: OnTagClickListener) {
        onTagClickListener = listener
    }

    @IntDef(MODE_DISABLE, MODE_DECELERATE, MODE_UNIFORM)
    @Retention(RetentionPolicy.SOURCE)
    annotation class Mode

    interface OnTagClickListener {
        fun onItemClick(parent: ViewGroup, view: View, position: Int)
    }

    override fun run() {
        // 非用户触摸状态，和非不可滚动状态
        if (!isOnTouch && autoScrollMode != MODE_DISABLE) {
            // 减速模式（均速衰减）
            if (autoScrollMode == MODE_DECELERATE) {
                if (Math.abs(mAngleX) > 0.2f) {
                    mAngleX -= mAngleX * 0.1f
                }
                if (Math.abs(mAngleY) > 0.2f) {
                    mAngleY -= mAngleY * 0.1f
                }
            }
            processTouch()
        }
        mHandler.removeCallbacksAndMessages(null)
        // 延时
        mHandler.postDelayed(this, 30)
    }

    companion object {

        const val MODE_DISABLE = 0
        const val MODE_DECELERATE = 1
        const val MODE_UNIFORM = 2
        private val TOUCH_SCALE_FACTOR = 1f
        private val TRACKBALL_SCALE_FACTOR = 10f
    }
}