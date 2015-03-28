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
import android.util.SparseArray;

import java.util.Date;

/**
 * 各避難所の状況。
 */
public class ShelterStatuses {
    /**
     * 更新日時。
     */
    @NonNull
    private Date mTime;

    /**
     * 状況。
     * キーは避難所ID。
     */
    @NonNull
    private SparseArray<ShelterStatus> mStatuses;

    /**
     * 更新日時を取得する。
     * @return 更新日時
     */
    @NonNull
    public Date getTime() {
        return mTime;
    }

    /**
     * 更新日時を設定する。
     * @param time 更新日時
     */
    public void setTime(@NonNull Date time) {
        mTime = time;
    }

    /**
     * 避難所の状況を取得する。
     * @return 避難所の状況
     */
    @NonNull
    public SparseArray<ShelterStatus> getStatuses() {
        return mStatuses;
    }

    /**
     * 避難所の状況を設定する
     * @param statuses 避難所の状況
     */
    public void setStatuses(@NonNull SparseArray<ShelterStatus> statuses) {
        mStatuses = statuses;
    }

    /**
     * コンストラクタ。
     *
     * @param time     更新日時
     * @param statuses 避難所の状況
     */
    public ShelterStatuses(@NonNull Date time, @NonNull SparseArray<ShelterStatus> statuses) {
        mTime = time;
        mStatuses = statuses;
    }
}
