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

package jp.co.ipublishing.aeskit.shelter.models;


import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

/**
 * 避難所情報。
 */
public interface Shelter {
    /**
     * IDを取得する。
     *
     * @return 避難所ID
     */
    int getId();

    /**
     * 緯度経度を取得する。
     *
     * @return 避難所の緯度経度
     */
    @NonNull
    LatLng getCoordinates();

    /**
     * 標高を取得する。
     *
     * @return 避難所の標高
     */
    double getAltitude();

    /**
     * 名前を取得する。
     *
     * @return 避難所の名前
     */
    @NonNull
    String getName();

    /**
     * 状況を取得する。
     *
     * @return 避難所の状況
     */
    @NonNull
    ShelterStatus getStatus();

    /**
     * 状況を設定する。
     *
     * @param status 避難所の状況
     */
    void setStatus(@NonNull ShelterStatus status);

    /**
     * 許容人数を取得する。
     *
     * @return 避難所の許容人数。不明の場合は-1を返す。
     */
    int getCapacity();
}
