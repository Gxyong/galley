package com.gxy.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Android6.0运行时权限工具类
 */

public class MPermissionUtils {

    private static int mRequestCode = -1;
    private static OnPermissionListener mOnPermissionListener;

    public static void requestPermissionsResult(Activity activity, int requestCode
            , String[] permission, OnPermissionListener callback) {
        requestPermissions(activity, requestCode, permission, callback);
    }

    public static void requestPermissionsResult(android.app.Fragment fragment, int requestCode
            , String[] permission, OnPermissionListener callback) {
        requestPermissions(fragment, requestCode, permission, callback);
    }

    public static void requestPermissionsResult(android.support.v4.app.Fragment fragment, int requestCode
            , String[] permission, OnPermissionListener callback) {
        requestPermissions(fragment, requestCode, permission, callback);
    }

    /**
     * 请求权限处理
     *
     * @param requestCode 请求码
     * @param permissions 需要请求的权限
     * @param callback    结果回调
     */
    @TargetApi(Build.VERSION_CODES.M)
    private static void requestPermissions(Object object, int requestCode
            , String[] permissions, OnPermissionListener callback) {

        checkCallingObjectSuitability(object);
        mOnPermissionListener = callback;

        if (checkPermissions(ContextUtils.get(), permissions)) {
            if (mOnPermissionListener != null)
                mOnPermissionListener.onPermissionGranted();
        } else {
            List<String> deniedPermissions = getDeniedPermissions(ContextUtils.get(), permissions);
            if (deniedPermissions.size() > 0) {
                mRequestCode = requestCode;
                if (object instanceof Activity) {
                    ((Activity) object).requestPermissions(deniedPermissions
                            .toArray(new String[deniedPermissions.size()]), requestCode);
                } else if (object instanceof android.app.Fragment) {
                    ((android.app.Fragment) object).requestPermissions(deniedPermissions
                            .toArray(new String[deniedPermissions.size()]), requestCode);
                } else if (object instanceof android.support.v4.app.Fragment) {
                    ((android.support.v4.app.Fragment) object).requestPermissions(deniedPermissions
                            .toArray(new String[deniedPermissions.size()]), requestCode);
                } else {
                    mRequestCode = -1;
                }
            }
        }
    }

    /**
     * 请求权限结果，对应onRequestPermissionsResult()方法。
     */
    public static void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (mRequestCode != -1 && requestCode == mRequestCode) {
            if (verifyPermissions(grantResults)) {
                if (mOnPermissionListener != null)
                    mOnPermissionListener.onPermissionGranted();
            } else {
                if (mOnPermissionListener != null)
                    mOnPermissionListener.onPermissionDenied();
            }
        }
    }
//
//    /**
//     * 启动当前应用设置页面
//     */
//    private static void startAppSettings(Context context) {
//        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//        intent.setData(Uri.parse("package:" + context.getPackageName()));
//        context.startActivity(intent);
//    }

    /**
     * 验证权限是否都已经授权
     */
    private static boolean verifyPermissions(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取权限列表中所有需要授权的权限
     *
     * @param context     上下文
     * @param permissions 权限列表
     * @return
     */
    private static List<String> getDeniedPermissions(Context context, String... permissions) {
        List<String> deniedPermissions = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED) {
                deniedPermissions.add(permission);
            }
        }
        return deniedPermissions;
    }

    /**
     * 检查所传递对象的正确性
     *
     * @param object 必须为 activity or fragment
     */
    private static void checkCallingObjectSuitability(Object object) {
        if (object == null) {
            throw new NullPointerException("Activity or Fragment should not be null");
        }

        boolean isActivity = object instanceof Activity;
        boolean isSupportFragment = object instanceof android.support.v4.app.Fragment;
        boolean isAppFragment = object instanceof android.app.Fragment;

        if (!(isActivity || isSupportFragment || isAppFragment)) {
            throw new IllegalArgumentException("Caller must be an Activity or a Fragment");
        }
    }

    /**
     * 检查所有的权限是否已经被授权
     *
     * @param permissions 权限列表
     * @return
     */
    private static boolean checkPermissions(Context context, String... permissions) {
        if (isOverMarshmallow()) {
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 判断当前手机API版本是否 >= 6.0
     */
    private static boolean isOverMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public interface OnPermissionListener {
        void onPermissionGranted();

        void onPermissionDenied();
    }

}
