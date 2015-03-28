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

package jp.co.ipublishing.aeskit.shelter.events;

import android.support.annotation.NonNull;

import jp.co.ipublishing.aeskit.shelter.models.ShelterNumberOfPeople;

/**
 * 避難所の現在の収容人数の更新のイベント。
 */
public class ShelterUpdatedCurrentNumberOfPeopleEvent {
    /**
     * 避難所の現在の収容人数
     */
    @NonNull
    private final ShelterNumberOfPeople mNumberOfPeople;

    /**
     * 避難所の現在の収容人数を取得する。
     *
     * @return 避難所の現在の収容人数
     */
    @NonNull
    public ShelterNumberOfPeople getNumberOfPeople() {
        return mNumberOfPeople;
    }

    /**
     * コンストラクタ。
     *
     * @param numberOfPeople 避難所の現在の収容人数。
     */
    public ShelterUpdatedCurrentNumberOfPeopleEvent(@NonNull ShelterNumberOfPeople numberOfPeople) {
        mNumberOfPeople = numberOfPeople;
    }
}
