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
 * 各避難所の現在の人数。
 */
public class ShelterNumberOfPeople {
    /**
     * 更新日時。
     */
    @NonNull
    private Date mTime;

    /**
     * 現在の人数。
     * 人数が分からない場合は-1を返すこと。
     * キーは避難所ID。
     */
    @NonNull
    private SparseArray<Integer> mNumbers;

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
     * 現在の人数を取得する。
     * @return 現在の人数
     */
    @NonNull
    public SparseArray<Integer> getNumbers() {
        return mNumbers;
    }

    /**
     * 現在の人数を設定する。
     * @param numbers 現在の人数
     */
    public void setNumbers(@NonNull SparseArray<Integer> numbers) {
        mNumbers = numbers;
    }

    /**
     * コンストラクタ。
     *
     * @param time    更新日時
     * @param numbers 人数
     */
    public ShelterNumberOfPeople(@NonNull Date time, @NonNull SparseArray<Integer> numbers) {
        mTime = time;
        mNumbers = numbers;
    }

    /**
     * 指定の避難所の現在の収容人数を取得する。
     * @param shelterId 避難所ID
     * @return 指定の避難所の現在の収容人数。不明の場合は-1。
     */
    public int getNumber(int shelterId) {
        return mNumbers.get(shelterId, -1);
    }
}
