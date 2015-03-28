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

package jp.co.ipublishing.esnavi.dependence.shelter;

import android.support.annotation.NonNull;

import jp.co.ipublishing.esnavi.helpers.network.ApiMethod;
import jp.co.ipublishing.esnavi.dependence.Config;

/**
 * 避難所関係APIの実装。
 */
public class ShelterApi implements jp.co.ipublishing.esnavi.impl.shelter.ShelterApi {
    @NonNull
    @Override
    public ApiMethod downloadStatuses() {
        return new ApiMethod() {
            @NonNull
            @Override
            public String getUrl() {
                return Config.DOWNLOAD_SHELTER_STATUSES;
            }
        };
    }

    @NonNull
    @Override
    public ApiMethod downloadNumberOfPeople() {
        return new ApiMethod() {
            @NonNull
            @Override
            public String getUrl() {
                return Config.DOWNLOAD_SHELTER_NUMBER_OF_PEOPLE;
            }
        };
    }
}
