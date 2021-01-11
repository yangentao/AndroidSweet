package dev.entao.appbase

import java.lang.ref.WeakReference

class WeakRef<T : Any>(obj: T? = null) {
    private var weakRef: WeakReference<T>? = null

    init {
        if (obj != null) {
            weakRef = WeakReference(obj)
        }
    }


    var value: T?
        get() = weakRef?.get()
        set(value) {
            weakRef = null
            if (value != null) {
                weakRef = WeakReference(value)
            }
        }

}