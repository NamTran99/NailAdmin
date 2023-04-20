package android.support.viewmodel

import android.support.core.event.ErrorEvent
import android.support.core.event.LoadingEvent
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistryOwner
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

inline fun <reified T : ViewModel> ViewModelStoreOwner.getViewModel(): T {
    return ViewModelProvider(
        this,
        SavableViewModelFactory(this as SavedStateRegistryOwner)
    )[T::class.java].also {
        if (this is ViewModelRegistrable) registry(it)
    }
}

inline fun <reified T : ViewModel> FragmentActivity.viewModel(): Lazy<T> =
    lazy(LazyThreadSafetyMode.NONE) { getViewModel<T>() }


inline fun <reified T : ViewModel> Fragment.viewModel(): Lazy<T> =
    lazy(LazyThreadSafetyMode.NONE) { getViewModel<T>() }

inline fun <reified T : ViewModel> Fragment.shareViewModel(): Lazy<T> =
    lazy(LazyThreadSafetyMode.NONE) { requireActivity().getViewModel<T>() }

inline fun <reified T : ViewModel> viewModel(crossinline function: () -> ViewModelStoreOwner): Lazy<T> =
    lazy(LazyThreadSafetyMode.NONE) { function().getViewModel<T>() }

fun ViewModel.launch(
    loading: LoadingEvent? = null,
    error: ErrorEvent? = null,
    context: CoroutineContext = EmptyCoroutineContext,
    isBlockOther: Boolean = false,
    function: suspend CoroutineScope.() -> Unit
) {
    val handler = CoroutineExceptionHandler { _, throwable ->
        if (throwable !is CancellationException) {
            throwable.printStackTrace()
            error?.post(throwable)
            loading?.post(false)
        }
    }
    if (isBlockOther) context.cancelChildren()
    viewModelScope.launch(context = context + handler) {
        loading?.post(true)
        function()
        loading?.post(false)
    }
}