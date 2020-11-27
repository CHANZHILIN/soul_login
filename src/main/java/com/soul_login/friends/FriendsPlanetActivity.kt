package com.soul_login.friends

import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin_baselib.api.Constants
import com.kotlin_baselib.base.BaseViewModelActivity
import com.kotlin_baselib.base.EmptyViewModel
import com.kotlin_baselib.utils.SnackBarUtil
import com.soul_login.R
import com.soul_login.friends.planets.SoulPlanetsView
import com.soul_login.friends.planets.TestAdapter
import kotlinx.android.synthetic.main.activity_friends_planet_activity.*

/**
 *  Created by CHEN on 2019/7/9
 *  Email:1181785848@qq.com
 *  Package:com.soul_login.login.friends
 *  Introduce:  朋友星球
 **/
@Route(path = Constants.FRIENDS_PLANNET_ACTIVITY_PATH)
class FriendsPlanetActivity : BaseViewModelActivity<EmptyViewModel>() {

    override fun providerVMClass(): Class<EmptyViewModel>? = EmptyViewModel::class.java
    override fun isTransparentPage(): Boolean  = true

    override fun getResId(): Int {
        return R.layout.activity_friends_planet_activity
    }

    override fun initData() {
        planetsView.setAdapter(TestAdapter())
        planetsView.setOnTagClickListener(object : SoulPlanetsView.OnTagClickListener {
            override fun onItemClick(parent: ViewGroup, view: View, position: Int) {
                SnackBarUtil.shortSnackBar(view, "位置" + position, SnackBarUtil.ALERT).show()
            }
        })
    }

    override fun initListener() {
    }

}
