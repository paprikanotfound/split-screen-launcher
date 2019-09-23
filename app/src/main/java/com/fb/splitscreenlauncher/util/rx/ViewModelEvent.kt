package com.fb.splitscreenlauncher.util.rx

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean


/**
 *
 * Observable that notifies on once for new updates
 *
 */
class ViewModelEvent<T>: MutableLiveData<T>() {


    private var hasBeenHandled = AtomicBoolean(false)


    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        super.observe(owner, Observer<T> { t ->

            if (hasBeenHandled.compareAndSet(false, true))
                observer.onChanged(t)

        })
    }


    override fun setValue(value: T) {

        hasBeenHandled.set(false)

        super.setValue(value)
    }


}