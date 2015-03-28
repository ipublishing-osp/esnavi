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

import android.support.annotation.NonNull;

import jp.co.ipublishing.aeskit.alert.models.Alert;
import rx.Observable;

/**
 * 警報情報管理マネージャ。
 */
public class AlertManager {
    /**
     * 警報情報ストア。
     */
    @NonNull
    private final AlertStore mStore;

    /**
     * 警報情報クライアント。
     */
    @NonNull
    private final AlertClient mClient;

    /**
     * コンストラクタ。
     *
     * @param store  警報情報ストア
     * @param client 警報情報クライアント
     */
    public AlertManager(@NonNull AlertStore store, @NonNull AlertClient client) {
        mStore = store;
        mClient = client;
    }

    /**
     * ストアから警報情報を取得する。
     *
     * @return Observableオブジェクト
     */
    @NonNull
    public Observable<Alert> fetchAlert() {
        return mStore.fetchAlert();
    }

    /**
     * サーバから警報情報をダウンロードする。
     *
     * @return Observableオブジェクト
     */
    @NonNull
    public Observable<Alert> downloadAlert() {
        return mClient.downloadAlert();
    }

    /**
     * ストアに警報情報を保存します。
     * 既に保存されている場合はnullが返ります。
     *
     * @param alert 警報情報
     * @return Observableオブジェクト
     */
    @NonNull
    public Observable<Alert> storeAlert(@NonNull Alert alert) {
        return mStore.storeAlert(alert);
    }
}
