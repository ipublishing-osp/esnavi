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

package jp.co.ipublishing.aeskit.alert;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import jp.co.ipublishing.aeskit.alert.events.AlertFailedDownloadAlertEvent;
import jp.co.ipublishing.aeskit.alert.events.AlertFailedStoreAlertEvent;
import jp.co.ipublishing.aeskit.alert.events.AlertRequestAlertEvent;
import jp.co.ipublishing.aeskit.alert.events.AlertUpdatedAlertEvent;
import jp.co.ipublishing.aeskit.alert.models.Alert;
import jp.co.ipublishing.aeskit.helpers.dagger.ObjectGraph;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * 警報情報管理サービス。
 */
@SuppressLint("Registered")
public class AlertService extends Service {
    private static final String TAG = "AlertService";

    /**
     * 警報情報管理マネージャ。
     */
    @Inject
    AlertManager mAlertManager;

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

        Log.i(TAG, "AlertService#onStartCommand");

        fetchAlert();

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
     * 警報情報要求イベントハンドラ。
     *
     * @param event イベント情報
     */
    public void onEvent(AlertRequestAlertEvent event) {
        Log.i(TAG, "AlertService#onEvent");

        fetchAlert();
    }

    /**
     * アプリ内の警報情報を取得する。
     */
    private void fetchAlert() {
        mAlertManager.fetchAlert().subscribeOn(Schedulers.newThread()).subscribe(new Subscriber<Alert>() {
            @Override
            public void onCompleted() {
                // FIXME: Alertの発生時間を見て、サーバから取得し直すか判断する。
                downloadAlert();
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, ExceptionUtils.getStackTrace(e));

                // ここではエラーイベントは発行しない。
                // downloadLatestAlertに任せる。
                downloadAlert();
            }

            @Override
            public void onNext(Alert alert) {
                // 警報情報をpostする。
                EventBus.getDefault().post(new AlertUpdatedAlertEvent(alert));
            }
        });
    }

    /**
     * サーバから警報情報をダウンロードする。
     * 取得が成功し、最新情報であれば、AlertUpdateEventイベントが発生します。
     * 最新情報でなければ、イベントは発生しません。
     */
    private void downloadAlert() {
        mAlertManager.downloadAlert().subscribe(new Subscriber<Alert>() {
            @Override
            public void onCompleted() {
                // Nothing to do
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, ExceptionUtils.getStackTrace(e));

                EventBus.getDefault().post(new AlertFailedDownloadAlertEvent(e));
            }

            @Override
            public void onNext(Alert alert) {
                // addAlertに任せる。
                // 最新情報であれば、AlertUpdateLatestEventイベントが発生する。
                mAlertManager.storeAlert(alert).subscribe(new Subscriber<Alert>() {
                    @Override
                    public void onCompleted() {
                        // Nothing to do
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, ExceptionUtils.getStackTrace(e));
                        EventBus.getDefault().post(new AlertFailedStoreAlertEvent(e));
                    }

                    @Override
                    public void onNext(Alert alert) {
                        if (alert != null) {
                            EventBus.getDefault().post(new AlertUpdatedAlertEvent(alert));
                        }
                    }
                });
            }
        });
    }
}