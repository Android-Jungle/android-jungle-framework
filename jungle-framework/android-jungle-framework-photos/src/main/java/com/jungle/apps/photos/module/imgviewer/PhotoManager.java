package com.jungle.apps.photos.module.imgviewer;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.jungle.apps.photos.R;
import com.jungle.apps.photos.base.component.AppUtils;
import com.jungle.apps.photos.base.manager.FileDownloadRequest;
import com.jungle.apps.photos.base.manager.HttpRequestManager;
import com.jungle.base.app.AppCore;
import com.jungle.base.manager.AppLifeManager;
import com.jungle.base.manager.AppManager;
import com.jungle.base.manager.ThreadManager;
import com.jungle.base.utils.FileUtils;
import com.jungle.base.utils.MiscUtils;
import com.jungle.widgets.dialog.DialogUtils;
import com.jungle.widgets.dialog.JungleDialog;
import com.jungle.widgets.dialog.JungleToast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class PhotoManager implements AppManager {

    public static PhotoManager getInstance() {
        return AppCore.getInstance().getManager(PhotoManager.class);
    }


    private Request mRequest;

    @Override
    public void onCreate() {
    }

    @Override
    public void onTerminate() {
        cancelDownloads();
    }

    private void cancelDownloads() {
        if (mRequest != null) {
            mRequest.cancel();
        }
    }

    public void setWallPaper(Context context, String url) {
        cancelDownloads();

        String localPath = AppUtils.getWallPaperDirectory();
        final String wallPaperFile = localPath + MiscUtils.generateMD5String(url);
        FileUtils.createFile(wallPaperFile);

        final JungleDialog dialog = DialogUtils.createCustomizedOneBtnDialog(
                context, R.layout.dialog_download_wallpaper,
                R.string.cancel,
                new JungleDialog.OnDialogBtnClickListener() {
                    @Override
                    public void onClick(JungleDialog dialog, JungleDialog.DialogBtn which) {
                        cancelDownloads();
                        dialog.dismiss();
                    }
                });

        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        mRequest = new FileDownloadRequest(url, wallPaperFile,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        setWallPaperLocal(wallPaperFile);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showToastInternal(R.string.download_wallpaper_failed);
                        dialog.dismiss();
                    }
                });
        HttpRequestManager.getInstance().add(mRequest);
    }

    public void setWallPaperLocal(String localPath) {
        cancelDownloads();

        File file = new File(localPath);
        if (!file.exists() || !file.isFile()) {
            return;
        }

        InputStream stream = null;
        try {
            stream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        final Context context = AppCore.getApplicationContext();
        WallpaperManager manager = WallpaperManager.getInstance(context);
        try {
            manager.setStream(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        showToastInternal(R.string.set_to_wallpaper_success);
    }

    private void showToastInternal(final int resId) {
        ThreadManager.getInstance().executeOnUIHandler(new Runnable() {
            @Override
            public void run() {
                Activity activity = AppLifeManager.getInstance().getCurrentActivity();
                if (activity == null) {
                    return;
                }

                JungleToast.makeText(activity, activity.getString(resId)).show();
            }
        });
    }
}
