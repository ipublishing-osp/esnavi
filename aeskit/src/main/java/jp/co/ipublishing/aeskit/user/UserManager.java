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

package jp.co.ipublishing.aeskit.user;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import org.apache.commons.lang3.exception.ExceptionUtils;

import jp.co.ipublishing.aeskit.notification.NotificationRegister;
import rx.Observable;
import rx.Subscriber;

/**
 * ユーザ情報管理マネージャ。
 */
public class UserManager {
    private static final String TAG = "UserManager";

    /**
     * ユーザ情報クライアント。
     */
    @NonNull
    private final UserClient mClient;

    /**
     * GCM送信者ID。
     */
    @NonNull
    private final String mGcmSenderID;

    /**
     * コンストラクタ
     *
     * @param client      警報情報クライアント
     * @param gcmSenderID GCM送信者ID
     */
    public UserManager(@NonNull UserClient client, @NonNull String gcmSenderID) {
        mClient = client;
        mGcmSenderID = gcmSenderID;
    }

    /**
     * 通知用のキーを登録する。
     *
     * @return Observableオブジェクト
     */
    @NonNull
    public Observable<Void> registerNotificationKey(@NonNull final Context context) {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(final Subscriber<? super Void> subscriber) {
                NotificationRegister.registerKey(context, mGcmSenderID)
                        .subscribe(new Subscriber<String>() {

                            @Override
                            public void onCompleted() {
                                // Nothing to do
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, ExceptionUtils.getStackTrace(e));
                                subscriber.onError(e);
                            }

                            @Override
                            public void onNext(String registrationKey) {
                                mClient.registerNotificationKey(registrationKey)
                                        .subscribe(new Subscriber<Void>() {
                                            @Override
                                            public void onCompleted() {
                                                subscriber.onCompleted();
                                            }

                                            @Override
                                            public void onError(Throwable e) {
                                                Log.e(TAG, ExceptionUtils.getStackTrace(e));
                                                subscriber.onError(e);
                                            }

                                            @Override
                                            public void onNext(Void aVoid) {
                                                subscriber.onNext(null);
                                                subscriber.onCompleted();
                                            }
                                        });
                            }
                        });
            }
        });
    }
}
