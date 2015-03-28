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

package jp.co.ipublishing.esnavi.dependence;

import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

/**
 * 必要最低限の設定が記載されている。
 *
 * 細かい設定は他のクラスなどを参照すること。
 */
public class Config {
    /**
     * GCMの送信者ID。
     * <a href="https://support.google.com/googleplay/android-developer/answer/2663268?hl=ja">Google Cloud メッセージング（GCM）の使用</a>
     */
    public static final String GCM_SENDER_ID = "";

    /**
     * 警報情報のダウンロードURL。
     */
    public static final String DOWNLOAD_ALERT_URL = "";

    /**
     * 避難所状況のダウンロードURL。
     */
    public static final String DOWNLOAD_SHELTER_STATUSES = "";

    /**
     * 避難所の現在の収容人数のダウンロードURL。
     */
    public static final String DOWNLOAD_SHELTER_NUMBER_OF_PEOPLE = "";

    /**
     * GCM登録IDの登録URL。
     */
    public static final String REGISTER_GCM_ID = "";

    /**
     * 地図の最初の位置。
     */
    public static final CameraPosition DEFAULT_MAP_POSITION = CameraPosition.fromLatLngZoom(new LatLng(36.594682, 136.625573), 18.0f);

}
