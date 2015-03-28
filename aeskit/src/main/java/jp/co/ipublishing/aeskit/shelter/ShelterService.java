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

package jp.co.ipublishing.aeskit.shelter;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import jp.co.ipublishing.aeskit.helpers.dagger.ObjectGraph;
import jp.co.ipublishing.aeskit.shelter.events.ShelterFailedDownloadStatusesEvent;
import jp.co.ipublishing.aeskit.shelter.events.ShelterFailedUpdateStatusesEvent;
import jp.co.ipublishing.aeskit.shelter.events.ShelterRequestStatusesEvent;
import jp.co.ipublishing.aeskit.shelter.events.ShelterUpdatedStatusesEvent;
import jp.co.ipublishing.aeskit.shelter.models.ShelterStatuses;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * 避難所情報サービス。
 */
public class ShelterService extends Service {
    private static final String TAG = "ShelterService";

    /**
     * 避難所情報管理マネージャ。
     */
    @Inject
    ShelterManager mShelterManager;

    @Override
    public void onCreate() {
        super.onCreate();

        ObjectGraph.inject(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        downloadStatuses();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 避難所状況要求イベントハンドラ。
     *
     * @param event イベント情報
     */
    public void onEvent(ShelterRequestStatusesEvent event) {
        downloadStatuses();
    }

    private void downloadStatuses() {
        mShelterManager.downloadStatuses()
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Subscriber<ShelterStatuses>() {
                    @Override
                    public void onCompleted() {
                        // Nothing to do
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, ExceptionUtils.getStackTrace(e));
                        EventBus.getDefault().post(new ShelterFailedDownloadStatusesEvent(e));
                    }

                    @Override
                    public void onNext(final ShelterStatuses shelterStatuses) {
                        // ひとまず送信する。
                        EventBus.getDefault().post(new ShelterUpdatedStatusesEvent(shelterStatuses.getStatuses()));

                        // そしてストアを更新する。
                        mShelterManager.updateStatuses(shelterStatuses.getStatuses()).subscribe(new Subscriber<Void>() {
                            @Override
                            public void onCompleted() {
                                // Nothing to do
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, ExceptionUtils.getStackTrace(e));
                                EventBus.getDefault().post(new ShelterFailedUpdateStatusesEvent(e));
                            }

                            @Override
                            public void onNext(Void aVoid) {
                                // Nothing to do
                            }
                        });
                    }
                });
    }
}
