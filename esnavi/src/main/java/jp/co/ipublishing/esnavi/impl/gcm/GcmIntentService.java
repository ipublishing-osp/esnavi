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

package jp.co.ipublishing.esnavi.impl.gcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONException;

import java.text.ParseException;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import jp.co.ipublishing.aeskit.alert.AlertManager;
import jp.co.ipublishing.aeskit.alert.events.AlertUpdatedAlertEvent;
import jp.co.ipublishing.aeskit.alert.models.Alert;
import jp.co.ipublishing.aeskit.helpers.dagger.ObjectGraph;
import jp.co.ipublishing.esnavi.activities.MapActivity;
import jp.co.ipublishing.esnavi.impl.alert.AlertFactory;
import rx.Subscriber;

/**
 * GCM受信サービス。
 * アプリ固有の情報があるため、抽象クラスとしている。
 */
public abstract class GcmIntentService extends jp.co.ipublishing.aeskit.notification.gcm.GcmIntentService {
    private static final String TAG = "GcmIntentService";

    /**
     * 通知ID。
     * 現在1種類しかないので固定で使用する。
     */
    private static final int NOTIFICATION_ID = 1;

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

    /**
     * 通常メッセージを順した。
     *
     * @param extras 受信内容
     */
    @Override
    protected void onReceivedMessage(Bundle extras) {
        try {
            final String message = extras.getString("message");

            final Alert alert = convertToAlert(message);

            // 通知する
            sendNotification(alert);

            // 更新イベントを発行
            EventBus.getDefault().post(new AlertUpdatedAlertEvent(alert));

            mAlertManager.storeAlert(alert).subscribe(new Subscriber<Alert>() {
                @Override
                public void onCompleted() {
                    // Nothing to do
                }

                @Override
                public void onError(Throwable e) {
                    Log.e(TAG, ExceptionUtils.getStackTrace(e));
                }

                @Override
                public void onNext(Alert alert) {
                    // Nothing to do
                }
            });
        } catch (JSONException | ParseException e) {
            Log.e(TAG, ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * エラーメッセージを受信した。
     *
     * @param extras 受信内容
     */
    @Override
    protected void onReceivedErrorMessage(Bundle extras) {
        // Nothing to do
        Log.e(TAG, extras.toString());
    }

    /**
     * 削除メッセージを受信した。
     *
     * @param extras 受信内容
     */
    @Override
    protected void onReceivedDeletedMessage(Bundle extras) {
        // Nothing to do
        Log.i(TAG, extras.toString());
    }

    /**
     * 受信メッセージを警報情報に変換する。
     *
     * @param gcmMessage 受信メッセージ
     * @return 警報情報
     * @throws JSONException
     * @throws ParseException
     */
    private Alert convertToAlert(String gcmMessage) throws JSONException, ParseException {
        return AlertFactory.create(gcmMessage);
    }

    /**
     * Notificationを送信する。
     *
     * @param alert 警報情報
     */
    private void sendNotification(@NonNull Alert alert) {
        final NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        final Intent resultIntent = new Intent(this, MapActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        final PendingIntent contentIntent = PendingIntent.getActivity(this, 1, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        final Notification.Builder builder =
                new Notification.Builder(this)
                        .setWhen(System.currentTimeMillis())
                        .setContentIntent(contentIntent)
                        .setDefaults(Notification.DEFAULT_SOUND
                                | Notification.DEFAULT_VIBRATE
                                | Notification.DEFAULT_LIGHTS)
                        .setAutoCancel(true)
                        .setTicker(alert.getHeadlineBody())
                        .setContentText(alert.getHeadlineBody());

        onPreSendNotification(builder, alert);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        } else {
            notificationManager.notify(NOTIFICATION_ID, builder.getNotification());
        }
    }

    /**
     * Notification送信前イベント。
     * アイコンやタイトルを設定する。
     *
     * @param builder Notificationビルダー
     * @param alert   警報情報
     */
    protected abstract void onPreSendNotification(@NonNull Notification.Builder builder, @NonNull Alert alert);
}
