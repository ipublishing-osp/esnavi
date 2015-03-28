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

package jp.co.ipublishing.esnavi.impl.shelter;

import android.support.annotation.NonNull;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONException;

import java.io.IOException;
import java.text.ParseException;

import jp.co.ipublishing.aeskit.shelter.models.ShelterNumberOfPeople;
import jp.co.ipublishing.aeskit.shelter.models.ShelterStatuses;
import jp.co.ipublishing.esnavi.helpers.network.ApiMethod;
import rx.Observable;
import rx.Subscriber;


/**
 * 避難所情報クライアントの実装。
 */
public class ShelterClient implements jp.co.ipublishing.aeskit.shelter.ShelterClient {
    private static final String TAG = "ShelterClient";

    /**
     * 避難所関連のAPI
     */
    @NonNull
    private final ShelterApi mApi;

    /**
     * HTTPクライアント。
     */
    @NonNull
    private final OkHttpClient mClient = new OkHttpClient();

    /**
     * コンストラクタ。
     *
     * @param api 避難所関連のAPI。
     */
    public ShelterClient(@NonNull ShelterApi api) {
        mApi = api;
    }

    @NonNull
    @Override
    public Observable<ShelterStatuses> downloadStatuses() {
        return Observable.create(new Observable.OnSubscribe<ShelterStatuses>() {
            @Override
            public void call(Subscriber<? super ShelterStatuses> subscriber) {
                final ApiMethod apiMethod = mApi.downloadStatuses();

                try {
                    final Request request = new Request.Builder()
                            .url(apiMethod.getUrl())
                            .get()
                            .build();

                    final Response response = mClient.newCall(request).execute();
                    final String result = response.body().string();

                    final ShelterStatuses statuses = ShelterFactory.createStatuses(result);

                    subscriber.onNext(statuses);
                    subscriber.onCompleted();
                } catch (IOException | JSONException | ParseException e) {
                    Log.e(TAG, ExceptionUtils.getStackTrace(e));

                    subscriber.onError(e);
                }
            }
        });
    }

    @NonNull
    @Override
    public Observable<ShelterNumberOfPeople> downloadNumberOfPeople() {
        return Observable.create(new Observable.OnSubscribe<ShelterNumberOfPeople>() {
            @Override
            public void call(Subscriber<? super ShelterNumberOfPeople> subscriber) {
                final ApiMethod apiMethod = mApi.downloadNumberOfPeople();

                try {
                    final Request request = new Request.Builder()
                            .url(apiMethod.getUrl())
                            .get()
                            .build();

                    final Response response = mClient.newCall(request).execute();
                    final String result = response.body().string();

                    final ShelterNumberOfPeople numbers = ShelterFactory.createNumberOfPeople(result);

                    subscriber.onNext(numbers);
                    subscriber.onCompleted();
                } catch (IOException | JSONException | ParseException e) {
                    Log.e(TAG, ExceptionUtils.getStackTrace(e));

                    subscriber.onError(e);
                }
            }
        });
    }
}
