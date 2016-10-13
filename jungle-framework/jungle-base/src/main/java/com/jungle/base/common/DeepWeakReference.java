/*
 * Copyright (C) 2016. All Rights Reserved.
 *
 * @author  Arno Zhang
 * @email   zyfgood12@163.com
 * @date    2016/07/27
 */

package com.jungle.base.common;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

public class DeepWeakReference<T> extends WeakReference<T> {

    public DeepWeakReference(T r) {
        super(r);
    }

    public DeepWeakReference(T r, ReferenceQueue<? super T> q) {
        super(r, q);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof DeepWeakReference)) {
            return super.equals(o);
        }

        return o == this
                || ((DeepWeakReference) o).get() == get();
    }
}
