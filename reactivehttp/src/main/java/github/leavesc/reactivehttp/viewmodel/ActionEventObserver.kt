package github.leavesc.reactivehttp.viewmodel

import android.content.Context
import androidx.lifecycle.*
import github.leavesc.reactivehttp.coroutine.ICoroutineEvent

/**
 * @Author: leavesC
 * @Date: 2020/4/30 15:23
 * @Desc: 用于定义 View 和  ViewModel 均需要实现的一些 UI 层行为
 * @GitHub：https://github.com/leavesC
 */
interface IUIActionEvent : ICoroutineEvent {

    fun showLoading(msg: String)

    fun showLoading() {
        showLoading("")
    }

    fun dismissLoading()

    fun showToast(msg: String)

    fun finishView()

}

interface IViewModelActionEvent : IUIActionEvent {

    val showLoadingLD: MutableLiveData<ShowLoadingEvent>

    val dismissLoadingLD: MutableLiveData<DismissLoadingEvent>

    val showToastEventLD: MutableLiveData<ShowToastEvent>

    val finishViewEventLD: MutableLiveData<FinishViewEvent>

    override fun showLoading(msg: String) {
        showLoadingLD.value = ShowLoadingEvent(msg)
    }

    override fun dismissLoading() {
        dismissLoadingLD.value = DismissLoadingEvent
    }

    override fun showToast(msg: String) {
        showToastEventLD.value = ShowToastEvent(msg)
    }

    override fun finishView() {
        finishViewEventLD.value = FinishViewEvent
    }

}

interface IUIActionEventObserver : IUIActionEvent {

    val lContext: Context?

    val lLifecycleOwner: LifecycleOwner

    fun <VM> getViewModel(clazz: Class<VM>,
                          factory: ViewModelProvider.Factory? = null,
                          initializer: (VM.(lifecycleOwner: LifecycleOwner) -> Unit)? = null): Lazy<VM> where VM : ViewModel, VM : IViewModelActionEvent {
        return lazy {
            getViewModelFast(clazz, factory, initializer)
        }
    }

    fun <VM> getViewModelFast(clazz: Class<VM>,
                              factory: ViewModelProvider.Factory? = null,
                              initializer: (VM.(lifecycleOwner: LifecycleOwner) -> Unit)? = null): VM where VM : ViewModel, VM : IViewModelActionEvent {
        return when (val localValue = lLifecycleOwner) {
            is ViewModelStoreOwner -> {
                if (factory == null) {
                    ViewModelProvider(localValue).get(clazz)
                } else {
                    ViewModelProvider(localValue, factory).get(clazz)
                }
            }
            else -> {
                factory?.create(clazz) ?: clazz.newInstance()
            }
        }.apply {
            generateActionEvent(this)
            initializer?.invoke(this, lLifecycleOwner)
        }
    }

    fun <VM> generateActionEvent(viewModel: VM) where VM : ViewModel, VM : IViewModelActionEvent {
        viewModel.showLoadingLD.observe(lLifecycleOwner, Observer {
            this@IUIActionEventObserver.showLoading(it.message)
        })
        viewModel.dismissLoadingLD.observe(lLifecycleOwner, Observer {
            this@IUIActionEventObserver.dismissLoading()
        })
        viewModel.showToastEventLD.observe(lLifecycleOwner, Observer {
            if (it.message.isNotBlank()) {
                this@IUIActionEventObserver.showToast(it.message)
            }
        })
        viewModel.finishViewEventLD.observe(lLifecycleOwner, Observer {
            this@IUIActionEventObserver.finishView()
        })
    }

}