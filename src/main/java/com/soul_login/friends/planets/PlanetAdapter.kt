package com.soul_login.friends.planets

import android.content.Context
import android.view.View
import android.view.ViewGroup

/**
 * 星球云的适配器
 */
abstract class PlanetAdapter {

    /**
     * 数据改变监听
     */
    private var onDataSetChangeListener: OnDataSetChangeListener? = null

    /**
     * 星球（标签）个数
     *
     * @return 星球（标签）个数
     */
    abstract val count: Int

    /**
     * 获取标签的View
     *
     * @param context  上下文
     * @param position 位置
     * @param parent   父布局
     * @return 标签的View
     */
    abstract fun getView(context: Context, position: Int, parent: ViewGroup): View?

    /**
     * 获取Item
     *
     * @param position 位置
     * @return Item
     */
    abstract fun getItem(position: Int): Any

    /**
     * 获取标签的权重
     *
     * @param position 位置
     * @return 标签的权重
     */
    abstract fun getPopularity(position: Int): Int

    /**
     * 主题颜色改变
     *
     * @param view       视图
     * @param themeColor 主题色
     */
    abstract fun onThemeColorChanged(view: View, themeColor: Int)

    /**
     * 数据更新
     */
    fun notifyDataSetChanged() {
        if (onDataSetChangeListener == null) {
            return
        }
        onDataSetChangeListener!!.onChange()
    }

    /**
     * 设置数据改变监听
     *
     * @param listener 数据改变监听器
     */
    fun setOnDataSetChangeListener(listener: OnDataSetChangeListener) {
        onDataSetChangeListener = listener
    }

    /**
     * 数据改变监听器
     */
    interface OnDataSetChangeListener {
        /**
         * 数据改变
         */
        fun onChange()
    }
}