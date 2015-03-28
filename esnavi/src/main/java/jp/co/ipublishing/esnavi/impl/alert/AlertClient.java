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

import android.support.annotation.NonNull;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONException;

import java.io.IOException;
import java.text.ParseException;

import jp.co.ipublishing.aeskit.alert.models.Alert;
import jp.co.ipublishing.esnavi.helpers.network.ApiMethod;
import rx.Observable;
import rx.Subscriber;

/**
 * 警報情報クライアントの実装。
 */
public class AlertClient implements jp.co.ipublishing.aeskit.alert.AlertClient {
    private static final String TAG = "AlertClient";

    /**
     * 警報関連のAPI
     */
    @NonNull
    private final AlertApi mApi;

    /**
     * HTTPクライアント。
     */
    @NonNull
    private final OkHttpClient mClient = new OkHttpClient();

    /**
     * コンストラクタ。
     *
     * @param api 警報関連のAPI。
     */
    public AlertClient(@NonNull AlertApi api) {
        mApi = api;
    }

    @NonNull
    @Override
    public Observable<Alert> downloadAlert() {
        return Observable.create(new Observable.OnSubscribe<Alert>() {
            @Override
            public void call(Subscriber<? super Alert> subscriber) {
                final ApiMethod apiMethod = mApi.downloadAlert();

                try {
                    final Request request = new Request.Builder()
                            .url(apiMethod.getUrl())
                            .get()
                            .build();

                    final Response response = mClient.newCall(request).execute();
                    final String result = response.body().string();

                    final Alert alert = AlertFactory.create(result);

                    subscriber.onNext(alert);
                    subscriber.onCompleted();
                } catch (IOException | JSONException | ParseException e) {
                    Log.e(TAG, ExceptionUtils.getStackTrace(e));

                    subscriber.onError(e);
                }
            }
        });
    }
}
