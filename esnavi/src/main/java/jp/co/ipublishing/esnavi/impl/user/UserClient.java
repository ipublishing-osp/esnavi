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

package jp.co.ipublishing.esnavi.impl.user;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.IOException;

import jp.co.ipublishing.esnavi.helpers.network.ApiMethod;
import rx.Observable;
import rx.Subscriber;

/**
 * ユーザ情報クライアントの実装。
 */
public class UserClient implements jp.co.ipublishing.aeskit.user.UserClient {
    private static final String TAG = "UserClient";

    /**
     * コンテキスト。
     */
    @NonNull
    private final Context mContext;

    /**
     * 避難所関連のAPI。
     */
    @NonNull
    private final UserApi mApi;

    /**
     * HTTPクライアント。
     */
    @NonNull
    private final OkHttpClient mClient = new OkHttpClient();

    /**
     * コンストラクタ。
     *
     * @param context コンテキスト
     * @param api     避難所関連のAPI。
     */
    public UserClient(@NonNull Context context, @NonNull UserApi api) {
        mContext = context;
        mApi = api;
    }

    @NonNull
    @Override
    public Observable<Void> registerNotificationKey(@NonNull final String key) {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                // 保存されているキーと比較し、更新されていなければ、サーバに送信せず処理を終了する。
                final String latestKey = getRegistrationId(mContext);

                if (!key.equals(latestKey)) {
                    try {
                        final ApiMethod apiMethod = mApi.registerNotificationKey();

                        final RequestBody formBody = new FormEncodingBuilder().add("gcmid", key).build();

                        final Request request = new Request.Builder()
                                .url(apiMethod.getUrl())
                                .post(formBody)
                                .build();

                        mClient.newCall(request).execute();

                        storeRegistrationId(mContext, key);

                        subscriber.onNext(null);
                        subscriber.onCompleted();

                    } catch (IOException e) {
                        Log.e(TAG, ExceptionUtils.getStackTrace(e));

                        subscriber.onError(e);
                    }
                } else {
                    subscriber.onNext(null);
                    subscriber.onCompleted();
                }
            }
        });
    }

    /**
     * GCM登録IDの保存用のプロパティ名。
     */
    private static final String PROPERTY_REG_ID = "registration_id";

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
        prefs.edit().putString(PROPERTY_REG_ID, registrationId).apply();
    }

    /**
     * このクラスでのみ使用するSharedPreferencesを取得する。
     *
     * @param context コンテキスト
     * @return このクラスでのみ使用するSharedPreferences
     */
    @NonNull
    private static SharedPreferences getPreferences(@NonNull Context context) {
        return context.getSharedPreferences(UserClient.class.getSimpleName(), Context.MODE_PRIVATE);
    }
}
