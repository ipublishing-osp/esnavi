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

import jp.co.ipublishing.aeskit.shelter.models.ShelterNumberOfPeople;
import jp.co.ipublishing.aeskit.shelter.models.ShelterStatuses;
import rx.Observable;

/**
 * 避難所情報クライアント。
 */
public interface ShelterClient {
    /**
     * サーバから避難所の状況を取得する。
     *
     * @return Observableオブジェクト
     */
    @NonNull
    Observable<ShelterStatuses> downloadStatuses();

    /**
     * サーバから避難所の現在の人数を取得する。
     *
     * @return Observableオブジェクト
     */
    @NonNull
    Observable<ShelterNumberOfPeople> downloadNumberOfPeople();
}
