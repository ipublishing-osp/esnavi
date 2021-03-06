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

package jp.co.ipublishing.aeskit.alert.models.figure;

import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * 地図図形抽象クラス。
 */
public abstract class Figure implements Serializable {
    /**
     * 図形の種類。
     */
    @NonNull
    private final FigureType mType;

    /**
     * 図形の種類を取得する。
     *
     * @return 図形の種類
     */
    @NonNull
    public FigureType getType() {
        return mType;
    }

    /**
     * コンストラクタ
     *
     * @param type 図形の種類
     */
    protected Figure(@NonNull FigureType type) {
        mType = type;
    }
}
