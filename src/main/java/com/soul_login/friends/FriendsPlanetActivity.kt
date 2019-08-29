package com.soul_login.friends

import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin_baselib.api.Constants
import com.kotlin_baselib.base.BaseActivity
import com.kotlin_baselib.base.EmptyModelImpl
import com.kotlin_baselib.base.EmptyPresenterImpl
import com.kotlin_baselib.base.EmptyView
import com.kotlin_baselib.utils.SnackbarUtil
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
class FriendsPlanetActivity : BaseActivity<EmptyView, EmptyModelImpl, EmptyPresenterImpl>(), EmptyView {
    override fun createPresenter(): EmptyPresenterImpl {
        return EmptyPresenterImpl(this)
    }

    override fun preSetContentView() {
        super.preSetContentView()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }
    }

    override fun getResId(): Int {
        return R.layout.activity_friends_planet_activity
    }

    override fun initData() {
        planetsView.setAdapter(TestAdapter())
        planetsView.setOnTagClickListener(object : SoulPlanetsView.OnTagClickListener {
            override fun onItemClick(parent: ViewGroup, view: View, position: Int) {
                SnackbarUtil.ShortSnackbar(view, "位置" + position, SnackbarUtil.ALERT).show()
            }
        })
    }

    override fun initListener() {
    }

}
