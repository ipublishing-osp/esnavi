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

package jp.co.ipublishing.esnavi.impl.alert;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import net.iharder.Base64;

import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.IOException;

import jp.co.ipublishing.aeskit.alert.models.Alert;
import rx.Observable;
import rx.Subscriber;

/**
 * 警報情報ストアの実装。
 * 1つのみを保存します。
 */
public class AlertStore implements jp.co.ipublishing.aeskit.alert.AlertStore {
    private static final String TAG = "AlertStore";

    /**
     * コンテキスト。
     */
    @NonNull
    private final Context mContext;

    /**
     * 最新の警報情報。
     */
    @Nullable
    private Alert mLatestAlert;

    /**
     * コンストラクタ。
     *
     * @param context コンテキスト
     */
    public AlertStore(@NonNull Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public Observable<Alert> fetchAlert() {
        return Observable.create(new Observable.OnSubscribe<Alert>() {
            @Override
            public void call(Subscriber<? super Alert> subscriber) {
                if (mLatestAlert == null) {
                    mLatestAlert = restoreLatestAlert();
                    if (mLatestAlert != null) {
                        storeLatestAlert(mLatestAlert);
                    }
                }

                if (mLatestAlert != null) {
                    subscriber.onNext(mLatestAlert);
                }

                subscriber.onCompleted();
            }
        });
    }

    @NonNull
    @Override
    public Observable<Alert> storeAlert(@NonNull final Alert alert) {
        return Observable.create(new Observable.OnSubscribe<Alert>() {
            @Override
            public void call(Subscriber<? super Alert> subscriber) {
                // 1つのみ扱うので上書きする。
                if (mLatestAlert != null && mLatestAlert.getTime().getTime() == alert.getTime().getTime()) {
                    subscriber.onNext(null);
                } else {
                    mLatestAlert = alert;
                    storeLatestAlert(mLatestAlert);
                    subscriber.onNext(mLatestAlert);
                }

                subscriber.onCompleted();
            }
        });
    }

    /**
     * 最新警報情報の保存用のプロパティ名。
     */
    private static final String KEY_LATEST_ALERT = "latest_alert";

    /**
     * 最新の警報情報を読み込む。
     *
     * @return 警報情報
     */
    @Nullable
    private Alert restoreLatestAlert() {
        final String string = getPreferences(mContext).getString(KEY_LATEST_ALERT, null);
        if (string != null) {
            try {
                return (Alert) Base64.decodeToObject(string);
            } catch (IOException | ClassCastException | ClassNotFoundException e) {
                Log.e(TAG, ExceptionUtils.getStackTrace(e));
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * 警報情報を保存する。
     *
     * @param alert 警報情報
     */
    private void storeLatestAlert(@NonNull Alert alert) {
        try {
            final String string = Base64.encodeObject(alert);
            getPreferences(mContext).edit().putString(KEY_LATEST_ALERT, string).commit();
        } catch (IOException e) {
            Log.e(TAG, ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * このクラスでしか使用しないSharedPreferencesを取得する。
     * @param context コンテキスト
     * @return SharedPreferences
     */
    @NonNull
    private static SharedPreferences getPreferences(@NonNull Context context) {
        return context.getSharedPreferences(AlertStore.class.getSimpleName(), Context.MODE_PRIVATE);
    }
}
