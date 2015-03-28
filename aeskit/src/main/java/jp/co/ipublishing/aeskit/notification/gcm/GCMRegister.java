/*
 * Copyright 2015 iPublishing Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jp.co.ipublishing.aeskit.notification.gcm;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.IOException;

/**
 * GCMに登録します。
 */
public class GCMRegister {
    private static final String TAG = "GCMRegister";

    private static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";

    /**
     * 端末をGCMに登録します。
     *
     * @param context  コンテキスト
     * @param senderId GCM送信者ID
     * @return GCM登録ID
     * @throws IOException
     */
    @NonNull
    public static String register(@NonNull Context context, @NonNull String senderId) throws IOException {
        final String registrationId = getRegistrationId(context);

        if (registrationId.isEmpty()) {
            // 未登録 or バージョンアップ
            final GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);

            final String newRegistrationId = gcm.register(senderId);
            storeRegistrationId(context, newRegistrationId);
        }

        return getRegistrationId(context);
    }

    /**
     * GCM登録IDを取得する。
     *
     * @param context コンテキスト
     * @return GCM登録ID。登録していない、バージョンが更新された際は空文字が返ります。
     */
    @NonNull
    private static String getRegistrationId(@NonNull Context context) {
        final SharedPreferences prefs = getPreferences(context);
        final String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }

        final int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        final int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * GCM登録IDを保存する。
     *
     * @param context        コンテキスト
     * @param registrationId GCM登録ID
     */
    private static void storeRegistrationId(@NonNull Context context, @NonNull String registrationId) {
        final SharedPreferences prefs = getPreferences(context);
        final int appVersion = getAppVersion(context);

        final SharedPreferences.Editor editor = prefs.edit();

        editor.putString(PROPERTY_REG_ID, registrationId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);

        editor.apply();
    }

    /**
     * このクラスでのみ使用するSharedPreferencesを取得する。
     *
     * @param context コンテキスト
     * @return このクラスでのみ使用するSharedPreferences
     */
    @NonNull
    private static SharedPreferences getPreferences(@NonNull Context context) {
        return context.getSharedPreferences(GCMRegister.class.getSimpleName(), Context.MODE_PRIVATE);
    }

    /**
     * アプリのバージョンを取得する。
     *
     * @param context コンテキスト
     * @return アプリのバージョン
     */
    private static int getAppVersion(@NonNull Context context) {
        try {
            final PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // この例外は発生しない前提
            Log.e(TAG, ExceptionUtils.getStackTrace(e));
            return -1;
        }
    }
}
