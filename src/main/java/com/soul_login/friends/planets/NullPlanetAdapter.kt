package com.soul_login.friends.planets

import android.content.Context
import android.view.View
import android.view.ViewGroup

/**
 * 空对象模式
 */
class NullPlanetAdapter : PlanetAdapter() {
    override val count: Int
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

    override fun getView(context: Context, position: Int, parent: ViewGroup): View? {
        return null
    }


    override fun getItem(position: Int): Any {
        return 0
    }

    override fun getPopularity(position: Int): Int {
        return 0
    }

    override fun onThemeColorChanged(view: View, themeColor: Int) {
    }

}
