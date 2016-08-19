package cn.yanweijia.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;


import java.util.List;

/**
 * 迅雷下载助手
 */
public class ThunderHelper {

    public static final String XUNLEI_PACKAGENAME = "com.xunlei.downloadprovider";
    private static ThunderHelper instance;
    private Activity activity;

    private ThunderHelper(Activity activity) {
        this.activity = activity;
    }

    public static ThunderHelper getInstance(Activity activity) {
        if (instance == null) {
            instance = new ThunderHelper(activity);
        }
        return instance;
    }

    public void onClickDownload(String ftpUrl) {
        if (checkIsInstall(activity, XUNLEI_PACKAGENAME)) {
            // 唤醒迅雷
            activity.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(getThunderEncode(ftpUrl))));
        }else{
            Toast.makeText(activity, "您还没有安装迅雷,请在手机应用市场上下载手机迅雷", Toast.LENGTH_LONG).show();
        }
    }

    private boolean checkIsInstall(Context paramContext, String paramString) {
        if ((paramString == null) || ("".equals(paramString)))
            return false;
        try {
            paramContext.getPackageManager().getApplicationInfo(paramString, 0);
            return true;
        } catch (PackageManager.NameNotFoundException localNameNotFoundException) {
        }
        return false;
    }

    private String getThunderEncode(String ftpUrl) {
        return "thunder://" + XunLeiBase64.base64encode(("AA" + ftpUrl + "ZZ").getBytes());
    }
}
