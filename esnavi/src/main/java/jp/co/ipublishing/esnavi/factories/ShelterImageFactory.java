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

package jp.co.ipublishing.esnavi.factories;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

/**
 * 避難所画像ファクトリ。
 */
public interface ShelterImageFactory {
    /**
     * 避難所の写真を取得する。
     *
     * @param shelterId 避難所ID
     * @return 避難所の写真
     */
    @NonNull
    Bitmap getPhoto(@NonNull Context context, int shelterId);

    /**
     * 避難所のサムネイルを取得する。
     *
     * @param shelterId 避難所ID
     * @return 避難所のサムネイル
     */
    @NonNull
    Bitmap getThumbnail(@NonNull Context context, int shelterId);
}
