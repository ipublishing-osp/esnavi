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

package jp.co.ipublishing.aeskit.notification;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.IOException;

import jp.co.ipublishing.aeskit.notification.gcm.GCMRegister;
import rx.Observable;
import rx.Subscriber;

/**
 * 通知登録クラス。
 */
public class NotificationRegister {
    private static final String TAG = "NotificationRegister";

    /**
     * 通知用キーを登録する。
     *
     * @param context コンテキスト
     * @param key     通知用のキー
     * @return Observableオブジェクト
     */
    public static Observable<String> registerKey(@NonNull final Context context, @NonNull final String key) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    final String registrationId = GCMRegister.register(context, key);

                    subscriber.onNext(registrationId);
                    subscriber.onCompleted();

                } catch (IOException e) {
                    Log.e(TAG, ExceptionUtils.getStackTrace(e));

                    subscriber.onError(e);
                }
            }
        });
    }
}