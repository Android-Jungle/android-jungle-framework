package com.jungle.apps.photos.base.manager;

import com.jungle.base.app.AppCore;
import com.jungle.base.manager.AppManager;
import com.jungle.share.ShareHelper;

public class ShareManager extends ShareHelper implements AppManager {

    public static ShareManager getInstance() {
        return AppCore.getInstance().getManager(ShareManager.class);
    }


    @Override
    public void onCreate() {
    }

    @Override
    public void onTerminate() {
        destroy();
    }
}
