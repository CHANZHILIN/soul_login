package com.soul_login.friends.planets


import java.util.*

/**
 * 坐标等计算
 */
class PlanetCalculator @JvmOverloads constructor(private var planetModelCloud: MutableList<PlanetModel>?, private var radius: Int = DEFAULT_RADIUS, private var tagColorLight: FloatArray? = DEFAULT_COLOR_DARK, private var tagColorDark: FloatArray? = DEFAULT_COLOR_LIGHT) {
    private var maxDelta = java.lang.Float.MIN_VALUE
    private var minDelta = java.lang.Float.MAX_VALUE
    private var sinAngleX: Float = 0.toFloat()
    private var cosAngleX: Float = 0.toFloat()
    private var sinAngleY: Float = 0.toFloat()
    private var cosAngleY: Float = 0.toFloat()
    private var sinAngleZ: Float = 0.toFloat()
    private var cosAngleZ: Float = 0.toFloat()
    private val mAngleZ = 0f
    private var mAngleX = 0f
    private var mAngleY = 0f
    /**
     * 用于查找标签颜色的光谱
     */
    private var smallest: Int = 0
    private var largest: Int = 0
    /**
     * 默认设置是在云端均匀分布标签
     */
    private var isEvenly = true

    var tagList: MutableList<PlanetModel>?
        get() = planetModelCloud
        set(list) {
            planetModelCloud = list
        }

    val top: PlanetModel
        get() {
            val i = planetModelCloud!!.size - 1
            return get(i)
        }

    @JvmOverloads
    constructor(radius: Int = DEFAULT_RADIUS) : this(ArrayList<PlanetModel>(), radius) {
    }

    fun clear() {
        planetModelCloud!!.clear()
    }

    operator fun get(position: Int): PlanetModel {
        return planetModelCloud!![position]
    }

    fun indexOf(planetModel: PlanetModel): Int {
        return planetModelCloud!!.indexOf(planetModel)
    }

    fun reset() {
        create(isEvenly)
    }

    /**
     * 创建并初始化每个Tag的位置
     *
     * @param isEvenly 是否平均分布
     */
    fun create(isEvenly: Boolean) {
        this.isEvenly = isEvenly
        // 计算和设置每个Tag的位置
        locationAll(isEvenly)
        sineCosine(mAngleX, mAngleY, mAngleZ)
        updateAll()
        // 现在，让我们计算并设置每个标记的颜色：
        // 首先遍历所有标记以查找最小和最大的填充
        // 权重得到t颜色2，最小的得到t颜色1，其余在中间
        smallest = 9999
        largest = 0
        for (i in planetModelCloud!!.indices) {
            val j = planetModelCloud!![i].popularity
            largest = Math.max(largest, j)
            smallest = Math.min(smallest, j)
        }
        // 计算并分配颜色/文本大小
        for (i in planetModelCloud!!.indices) {
            initTag(planetModelCloud!![i])
        }
    }

    /**
     * 计算所有的位置
     *
     *
     * 球坐标系(r,θ,φ)与直角坐标系(touchX,touchY,z)的转换关系:
     * touchX=rsinθcosφ.
     * touchY=rsinθsinφ.
     * z=rcosθ.
     *
     *
     * r -> radius
     * θ -> phi
     * φ -> theta
     *
     * @param isEvenly 是否均匀分布
     */
    private fun locationAll(isEvenly: Boolean) {
        var phi: Double
        var theta: Double
        val count = planetModelCloud!!.size
        for (i in 1 until count + 1) {
            if (isEvenly) {
                // 平均（三维直角得Z轴等分[-1,1]） θ范围[-π/2,π/2])
                phi = Math.acos(-1.0 + (2.0 * i - 1.0) / count)
                theta = Math.sqrt(count * Math.PI) * phi
            } else {
                phi = Math.random() * Math.PI
                theta = Math.random() * (2 * Math.PI)
            }

            planetModelCloud!![i - 1].locX = (radius.toDouble() * Math.cos(theta) * Math.sin(phi)).toFloat()
            planetModelCloud!![i - 1].locY = (radius.toDouble() * Math.sin(theta) * Math.sin(phi)).toFloat()
            planetModelCloud!![i - 1].locZ = (radius * Math.cos(phi)).toFloat()
        }
    }

    /**
     * 返回角度转换成弧度之后各方向的值
     *
     *
     * 1度=π/180
     *
     * @param mAngleX x方向旋转距离
     * @param mAngleY y方向旋转距离
     * @param mAngleZ z方向旋转距离
     */
    private fun sineCosine(mAngleX: Float, mAngleY: Float, mAngleZ: Float) {
        val degToRad = Math.PI / 180
        sinAngleX = Math.sin(mAngleX * degToRad).toFloat()
        cosAngleX = Math.cos(mAngleX * degToRad).toFloat()
        sinAngleY = Math.sin(mAngleY * degToRad).toFloat()
        cosAngleY = Math.cos(mAngleY * degToRad).toFloat()
        sinAngleZ = Math.sin(mAngleZ * degToRad).toFloat()
        cosAngleZ = Math.cos(mAngleZ * degToRad).toFloat()
    }

    /**
     * 更新所有的
     */
    private fun updateAll() {
        // 更新标签透明度和比例
        val count = planetModelCloud!!.size
        for (i in 0 until count) {
            val planetModel = planetModelCloud!![i]
            // 此部分有两个选项：
            // 绕x轴旋转
            val rx1 = planetModel.locX
            val ry1 = planetModel.locY * cosAngleX + planetModel.locZ * -sinAngleX
            val rz1 = planetModel.locY * sinAngleX + planetModel.locZ * cosAngleX
            // 绕y轴旋转
            val rx2 = rx1 * cosAngleY + rz1 * sinAngleY
            val rz2 = rx1 * -sinAngleY + rz1 * cosAngleY
            // 绕z轴旋转
            val rx3 = rx2 * cosAngleZ + ry1 * -sinAngleZ
            val ry3 = rx2 * sinAngleZ + ry1 * cosAngleZ
// 将数组设置为新位置
            planetModel.locX = rx3
            planetModel.locY = ry3
            planetModel.locZ = rz2

            // 添加透视图
            val diameter = 2 * radius
            val per = diameter / (diameter + rz2)
            // 让我们为标签设置位置、比例和透明度
            planetModel.loc2DX = rx3
            planetModel.loc2DY = ry3
            planetModel.mScale = per

            // 计算透明度
            val delta = diameter + rz2
            maxDelta = Math.max(maxDelta, delta)
            minDelta = Math.min(minDelta, delta)
            val alpha = (delta - minDelta) / (maxDelta - minDelta)
            planetModel.alpha = 1 - alpha
        }
        sortTagByScale()
    }

    private fun initTag(planetModel: PlanetModel) {
        val percentage = getPercentage(planetModel)
        val argb = getColorFromGradient(percentage)
        planetModel.setColorByArray(argb)
    }

    /**
     * 根据缩放值排序
     */
    fun sortTagByScale() {
        Collections.sort(planetModelCloud!!, TagComparator())
    }

    private fun getPercentage(planetModel: PlanetModel): Float {
        val p = planetModel.popularity
        return if (smallest == largest) 1.0f else (p.toFloat() - smallest) / (largest.toFloat() - smallest)
    }

    private fun getColorFromGradient(percentage: Float): FloatArray {
        val rgba = FloatArray(4)
        rgba[0] = 1f
        rgba[1] = percentage * tagColorDark!![0] + (1f - percentage) * tagColorLight!![0]
        rgba[2] = percentage * tagColorDark!![1] + (1f - percentage) * tagColorLight!![1]
        rgba[3] = percentage * tagColorDark!![2] + (1f - percentage) * tagColorLight!![2]
        return rgba
    }

    /**
     * 更新所有元素的透明度/比例
     */
    fun update() {
        // 如果mAngleX和mAngleY低于阈值，则跳过运动计算以获得性能
        if (Math.abs(mAngleX) > .1 || Math.abs(mAngleY) > .1) {
            sineCosine(mAngleX, mAngleY, mAngleZ)
            updateAll()
        }
    }

    /**
     * 添加单个标签
     *
     * @param planetModel 标签
     */
    fun add(planetModel: PlanetModel) {
        initTag(planetModel)
        location(planetModel)
        planetModelCloud!!.add(planetModel)
        updateAll()
    }

    /**
     * 添加新标签时，只需将其放置在某个随机位置
     * 在多次添加之后，执行一次重置以重新排列所有标记
     *
     * @param planetModel 标签
     */
    private fun location(planetModel: PlanetModel) {
        val phi: Double
        val theta: Double
        phi = Math.random() * Math.PI
        theta = Math.random() * (2 * Math.PI)
        planetModel.locX = (radius.toDouble() * Math.cos(theta) * Math.sin(phi)).toInt().toFloat()
        planetModel.locY = (radius.toDouble() * Math.sin(theta) * Math.sin(phi)).toInt().toFloat()
        planetModel.locZ = (radius * Math.cos(phi)).toInt().toFloat()
    }

    /**
     * 设置半径
     *
     * @param radius 半径
     */
    fun setRadius(radius: Int) {
        this.radius = radius
    }

    fun setTagColorLight(tagColor: FloatArray) {
        this.tagColorLight = tagColor
    }

    fun setTagColorDark(tagColorDark: FloatArray) {
        this.tagColorDark = tagColorDark
    }

    fun setAngleX(mAngleX: Float) {
        this.mAngleX = mAngleX
    }

    fun setAngleY(mAngleY: Float) {
        this.mAngleY = mAngleY
    }

    private class TagComparator : Comparator<PlanetModel> {

        override fun compare(planetModel1: PlanetModel, planetModel2: PlanetModel): Int {
            return if (planetModel1.mScale > planetModel2.mScale) 1 else 0
        }
    }

    companion object {

        private val DEFAULT_RADIUS = 3
        private val DEFAULT_COLOR_DARK = floatArrayOf(0.886f, 0.725f, 0.188f, 1f)
        private val DEFAULT_COLOR_LIGHT = floatArrayOf(0.3f, 0.3f, 0.3f, 1f)
    }
}
