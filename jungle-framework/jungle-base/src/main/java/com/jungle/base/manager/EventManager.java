/**
 * Android Jungle framework project.
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

package com.jungle.base.manager;

import com.jungle.base.app.AppCore;
import com.jungle.base.common.HoldWeakRef;
import com.jungle.base.event.Event;
import com.jungle.base.event.EventListener;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class EventManager implements AppManager {

    public static EventManager getInstance() {
        return AppCore.getInstance().getManager(EventManager.class);
    }


    private Map<Event, List<WeakReference<EventListener>>> mListenerMap =
            new HashMap<>();


    @Override
    public void onCreate() {
    }

    @Override
    public void onTerminate() {
    }

    private List<WeakReference<EventListener>> ensure(Event event) {
        List<WeakReference<EventListener>> list = mListenerMap.get(event);
        if (list == null) {
            list = new LinkedList<>();
            mListenerMap.put(event, list);
        }

        return list;
    }

    public void addListener(Event event, @HoldWeakRef EventListener listener) {
        boolean found = false;

        List<WeakReference<EventListener>> list = ensure(event);
        for (Iterator<WeakReference<EventListener>> iterator = list.iterator();
             iterator.hasNext(); ) {
            EventListener existEventListener = iterator.next().get();
            if (existEventListener == null) {
                iterator.remove();
            } else if (existEventListener == listener) {
                found = true;
            }
        }

        if (!found) {
            list.add(new WeakReference<EventListener>(listener));
        }
    }

    private void removeFromList(List<WeakReference<EventListener>> list, EventListener listener) {
        for (Iterator<WeakReference<EventListener>> iterator = list.iterator();
             iterator.hasNext(); ) {
            EventListener existEventListener = iterator.next().get();
            if (existEventListener == null || existEventListener == listener) {
                iterator.remove();
            }
        }
    }

    public void removeListener(EventListener listener) {
        for (List<WeakReference<EventListener>> list : mListenerMap.values()) {
            removeFromList(list, listener);
        }
    }

    public void removeListener(Event event) {
        mListenerMap.remove(event);
    }

    public void removeListener(Event event, EventListener listener) {
        List<WeakReference<EventListener>> list = mListenerMap.get(event);
        removeFromList(list, listener);
    }

    public void notifyAsync(Event event) {
        notifyAsync(event, null);
    }

    public void notify(Event event) {
        notify(event, null);
    }

    public void notifyAsync(final Event event, final Object data) {
        ThreadManager.getInstance().postOnUIHandler(new Runnable() {
            @Override
            public void run() {
                EventManager.this.notify(event, data);
            }
        });
    }

    public void notify(final Event event, final Object data) {
        ThreadManager.getInstance().executeOnUIHandler(new Runnable() {
            @Override
            public void run() {
                List<WeakReference<EventListener>> list = mListenerMap.get(event);
                if (list == null) {
                    return;
                }

                // Copy to dump List, and remove invalid Listener.
                //
                List<EventListener> dump = new ArrayList<>(list.size());
                for (Iterator<WeakReference<EventListener>> iterator = list.iterator();
                     iterator.hasNext(); ) {
                    EventListener listener = iterator.next().get();
                    if (listener == null) {
                        iterator.remove();
                    } else {
                        dump.add(listener);
                    }
                }

                // Notify now.
                for (EventListener listener : dump) {
                    try {
                        listener.onEvent(event, data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void tryRemoveAllEventListener(Object object) {
        if (object == null) {
            return;
        }

        Field[] fields = object.getClass().getFields();
        for (Field field : fields) {
            if (EventListener.class.isAssignableFrom(field.getType())) {
                try {
                    field.setAccessible(true);
                    EventListener listener = (EventListener) field.get(object);
                    removeListener(listener);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
