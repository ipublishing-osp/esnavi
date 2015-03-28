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
import jp.co.ipublishing.aeskit.shelter.models.ShelterNumberOfPeople;
import jp.co.ipublishing.aeskit.shelter.models.ShelterStatus;
import jp.co.ipublishing.aeskit.shelter.models.ShelterStatuses;
import rx.Observable;

/**
 * 避難所情報管理。
 */
public class ShelterManager {
    /**
     * 避難所ストア。
     */
    @NonNull
    private final ShelterStore mStore;

    /**
     * 避難所クライアント。
     */
    @NonNull
    private final ShelterClient mClient;

    /**
     * コンストラクタ
     *
     * @param store  避難所ストア
     * @param client 避難所クライアント
     */
    public ShelterManager(@NonNull ShelterStore store, @NonNull ShelterClient client) {
        mStore = store;
        mClient = client;
    }

    /**
     * 避難所情報を取得する。
     *
     * @param center 中心位置
     * @param bounds 範囲
     * @return Observableオブジェクト
     */
    @NonNull
    public Observable<List<Shelter>> fetchShelters(@NonNull LatLng center, @NonNull LatLngBounds bounds) {
        return mStore.fetchShelters(center, bounds);
    }

    /**
     * 避難所情報を取得する。
     *
     * @param shelterId 避難所ID
     * @return Observableオブジェクト
     */
    @NonNull
    public Observable<Shelter> fetchShelter(int shelterId) {
        return mStore.fetchShelter(shelterId);
    }

    /**
     * 最寄りの避難所情報を取得する。
     *
     * @param center 中心位置
     * @return Observableオブジェクト
     */
    @NonNull
    public Observable<Shelter> fetchNearShelter(LatLng center) {
        return mStore.fetchNearShelter(center);
    }

    /**
     * 最寄りの避難所情報を取得する。
     *
     * @param center 中心位置
     * @return Observableオブジェクト
     */
    @NonNull
    public Observable<List<Shelter>> fetchNearShelters(LatLng center, int ignoreShelterId) {
        return mStore.fetchNearShelters(center, ignoreShelterId);
    }

    /**
     * サーバから避難所の状況を取得する。
     *
     * @return Observableオブジェクト
     */
    @NonNull
    public Observable<ShelterStatuses> fetchStatuses() {
        return mStore.fetchStatuses();
    }

    /**
     * サーバから避難所の状況を取得する。
     *
     * @return Observableオブジェクト
     */
    @NonNull
    public Observable<ShelterStatuses> downloadStatuses() {
        return mClient.downloadStatuses();
    }

    /**
     * サーバから避難所の現在の人数を取得する。
     *
     * @return Observableオブジェクト
     */
    @NonNull
    public Observable<ShelterNumberOfPeople> downloadNumberOfPeople() {
        return mClient.downloadNumberOfPeople();
    }

    /**
     * 避難所状況を更新する。
     *
     * @param statuses 避難所状況
     * @return Observableオブジェクト
     */
    @NonNull
    public Observable<Void> updateStatuses(@NonNull SparseArray<ShelterStatus> statuses) {
        return mStore.updateStatuses(statuses);
    }
}
