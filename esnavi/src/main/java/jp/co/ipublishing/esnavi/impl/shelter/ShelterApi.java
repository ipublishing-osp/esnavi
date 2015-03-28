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

package jp.co.ipublishing.esnavi.impl.shelter;

import android.support.annotation.NonNull;

import jp.co.ipublishing.esnavi.helpers.network.ApiMethod;

/**
 * 避難所関係のAPIインターフェース。
 *
 * @see jp.co.ipublishing.esnavi.impl.shelter.ShelterClient
 */
public interface ShelterApi {
    /**
     * 避難所状況ダウンロードのAPI情報を取得する。
     *
     * @return 避難所状況ダウンロードのAPI情報
     * @see ShelterClient#downloadStatuses()
     */
    @NonNull
    ApiMethod downloadStatuses();

    /**
     * 現在の人数ダウンロードのAPI情報を取得する。
     *
     * @return 現在の人数ダウンロードのAPI情報
     * @see ShelterClient#downloadNumberOfPeople()
     */
    @NonNull
    ApiMethod downloadNumberOfPeople();
}
