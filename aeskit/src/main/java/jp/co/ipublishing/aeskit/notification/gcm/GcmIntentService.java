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

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public abstract class GcmIntentService extends IntentService {
    /**
     * デフォルトコンストラクタ。
     */
    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final Bundle extras = intent.getExtras();
        final GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        final String messageType = gcm.getMessageType(intent);

        GcmBroadcastReceiver.completeWakefulIntent(intent);

        if (!extras.isEmpty()) {
            switch (messageType) {
                case GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE: {
                    onReceivedMessage(extras);
                    break;
                }
                case GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR: {
                    onReceivedErrorMessage(extras);
                    break;
                }
                case GoogleCloudMessaging.MESSAGE_TYPE_DELETED: {
                    onReceivedDeletedMessage(extras);
                    break;
                }
            }
        }

        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    /**
     * 通常メッセージを受信した。
     *
     * @param extras 受信内容
     */
    protected abstract void onReceivedMessage(Bundle extras);

    /**
     * エラーメッセージを受信した。
     *
     * @param extras 受信内容
     */
    protected abstract void onReceivedErrorMessage(Bundle extras);

    /**
     * 削除メッセージを受信した。
     *
     * @param extras 受信内容
     */
    protected abstract void onReceivedDeletedMessage(Bundle extras);
}
