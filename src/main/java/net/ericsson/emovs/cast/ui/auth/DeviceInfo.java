package net.ebstv.emp.castsenderdemo.auth;
/*
 * Copyright (c) 2017 Ericsson. All Rights Reserved
 *
 * This SOURCE CODE FILE, which has been provided by Ericsson as part
 * of an Ericsson software product for use ONLY by licensed users of the
 * product, includes CONFIDENTIAL and PROPRIETARY information of Ericsson.
 *
 * USE OF THIS SOFTWARE IS GOVERNED BY THE TERMS AND CONDITIONS OF
 * THE LICENSE STATEMENT AND LIMITED WARRANTY FURNISHED WITH
 * THE PRODUCT.
 */

import android.content.ContentResolver;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;

public class DeviceInfo {
    private static final String TAG = "DeviceInfo";

    private static final double TABLET_SIZE_TRESHOLD = 7;
    private static final String FALLBACK_ID = "AndroidId";

    public String deviceId;
    public int height;
    public int width;
    public String model;
    public String name;
    public String os;
    public String osVersion;
    public String manufacturer;
    public String type;

    private static String getDeviceId(ContentResolver resolver) {
        try {
            String android_id = Settings.Secure.getString(resolver, Settings.Secure.ANDROID_ID);
            if (android_id == null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                    android_id = Build.SERIAL;
                }
            }
            if (android_id == null) {
                android_id = FALLBACK_ID;
            }
            return android_id;
        }
        catch (Exception e) {
            Log.e(TAG, "Error getting device id: " + e.toString());
            return FALLBACK_ID;
        }
    }

    public static DeviceInfo collect(Context context) {
        DeviceInfo info = new DeviceInfo();
        info.model = Build.MODEL;
        info.manufacturer = Build.MANUFACTURER;
        info.deviceId = getDeviceId(context.getContentResolver());
        info.os = "Android";
        info.osVersion = Build.VERSION.RELEASE;

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        info.width = metrics.widthPixels;
        info.height = metrics.heightPixels;

        // not completely true, large mobiles can be categorized as tablet here
        double diagonalInchesSQ = info.width * info.width + info.height * info.height;
        info.type = diagonalInchesSQ > TABLET_SIZE_TRESHOLD * TABLET_SIZE_TRESHOLD ? "TABLET" : "MOBILE";

        return info;
    }
}
