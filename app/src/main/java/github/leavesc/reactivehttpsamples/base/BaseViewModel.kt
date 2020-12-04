package github.leavesc.reactivehttpsamples.base

import github.leavesc.reactivehttp.base.BaseReactiveViewModel
import github.leavesc.reactivehttpsamples.core.http.SelfRemoteDataSource

/**
 * @Author: leavesC
 * @Date: 2020/12/4 22:12
 * @Desc:
 * @GitHub：https://github.com/leavesC
 */
open class BaseViewModel : BaseReactiveViewModel() {

    protected val remoteDataSource by lazy {
        SelfRemoteDataSource(this)
    }

}