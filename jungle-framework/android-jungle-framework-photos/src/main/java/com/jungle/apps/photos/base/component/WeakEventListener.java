/**
 * Android photos application project.
 *
 * Copyright 2016 Arno Zhang <zyfgood12@163.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jungle.apps.photos.base.component;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class WeakEventListener<T> {

    private List<WeakReference<T>> mEventListenerList = new LinkedList<>();


    public WeakEventListener() {
    }

    public void addEventListener(T l) {
        Iterator iterator = this.mEventListenerList.iterator();

        while (iterator.hasNext()) {
            Object listener = ((WeakReference) iterator.next()).get();
            if (listener == null) {
                iterator.remove();
            } else if (listener == l) {
                return;
            }
        }

        this.mEventListenerList.add(new WeakReference<T>(l));
    }

    public void removeEventListener(T l) {
        Iterator iterator = this.mEventListenerList.iterator();

        while (true) {
            Object listener;
            do {
                if (!iterator.hasNext()) {
                    return;
                }

                listener = ((WeakReference) iterator.next()).get();
            } while (listener != null && listener != l);

            iterator.remove();
        }
    }

    public void removeEventListener(WeakReference<T> ref) {
        this.mEventListenerList.remove(ref);
    }

    public List<WeakReference<T>> getList() {
        return this.mEventListenerList;
    }

    public void notifyEvent(WeakEventListener.NotifyRunnable<T> runnable) {
        Iterator iterator = this.mEventListenerList.iterator();

        while (iterator.hasNext()) {
            Object listener = ((WeakReference) iterator.next()).get();
            if (listener == null) {
                iterator.remove();
            } else {
                runnable.notify((T) listener);
            }
        }

    }

    public interface NotifyRunnable<T> {
        void notify(T var1);
    }
}
