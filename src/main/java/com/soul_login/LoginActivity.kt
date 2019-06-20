package com.soul_login

import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin_baselib.api.Constants
import com.kotlin_baselib.base.BaseActivity
import com.kotlin_baselib.base.EmptyModelImpl
import com.kotlin_baselib.base.EmptyPresenterImpl
import com.kotlin_baselib.base.EmptyView

@Route(path = Constants.LOGIN_ACTIVITY_PATH)
class LoginActivity : BaseActivity<EmptyView, EmptyModelImpl, EmptyPresenterImpl>(), EmptyView {
    override fun createPresenter(): EmptyPresenterImpl {
        return EmptyPresenterImpl(this)
    }

    override fun getResId(): Int {
        return R.layout.activity_login
    }

    override fun initData() {
    }

    override fun initListener() {
    }

}
