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

import android.support.annotation.NonNull;
import android.util.SparseArray;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.List;

import jp.co.ipublishing.aeskit.shelter.models.Shelter;
import jp.co.ipublishing.aeskit.shelter.models.ShelterStatus;
import jp.co.ipublishing.aeskit.shelter.models.ShelterStatuses;
import rx.Observable;

/**
 * 避難所情報ストア。
 */
public interface ShelterStore {
    /**
     * 避難所を取得する。
     *
     * @param center 中心位置
     * @param bounds 範囲
     * @return Observableオブジェクト
     */
    @NonNull
    Observable<List<Shelter>> fetchShelters(@NonNull LatLng center, @NonNull LatLngBounds bounds);

    /**
     * 一番近い避難所を取得する。
     *
     * @param target 現在位置
     * @return Observableオブジェクト
     */
    @NonNull
    Observable<Shelter> fetchNearShelter(@NonNull LatLng target);

    /**
     * 最寄りの避難所を取得する。
     *
     * @param target          現在位置
     * @param ignoreShelterId 無視する避難所のID
     * @return Observableオブジェクト
     */
    @NonNull
    Observable<List<Shelter>> fetchNearShelters(@NonNull LatLng target, int ignoreShelterId);

    /**
     * 避難所を取得する。
     *
     * @param shelterId 避難所ID
     * @return Observableオブジェクト
     */
    @NonNull
    Observable<Shelter> fetchShelter(int shelterId);

    /**
     * 避難所状況を更新する。
     *
     * @param statuses 避難所状況
     * @return Observableオブジェクト
     */
    @NonNull
    Observable<Void> updateStatuses(@NonNull SparseArray<ShelterStatus> statuses);

    /**
     * アプリ内から避難所の状況を取得する。
     *
     * @return Observableオブジェクト
     */
    @NonNull
    Observable<ShelterStatuses> fetchStatuses();
}
