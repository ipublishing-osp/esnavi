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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.Log;

import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * 避難所画像ファクトリ。
 */
public class ShelterImageFactory implements jp.co.ipublishing.esnavi.factories.ShelterImageFactory {
    private static final String TAG = "ShelterImageFactory";

    @NonNull
    @Override
    public Bitmap getPhoto(@NonNull Context context, int shelterId) {
        try {
            final InputStream is = context.getResources().getAssets().open(String.format("shelter_images/%d.jpg", shelterId));
            final Bitmap bmp = BitmapFactory.decodeStream(is);
            is.close();
            return bmp;
        } catch (IOException e) {
            Log.e(TAG, ExceptionUtils.getStackTrace(e));
        }

        return getNoImageBitmap(context);
    }

    @NonNull
    @Override
    public Bitmap getThumbnail(@NonNull Context context, int shelterId) {
        return getPhoto(context, shelterId);
    }

    /**
     * NO IMAGE 画像を取得する。
     *
     * @return NO IMAGE画像
     */
    private Bitmap getNoImageBitmap(Context context) {
        try {
            final InputStream is = context.getResources().getAssets().open("shelter_images/noimage.png");
            final Bitmap bmp = BitmapFactory.decodeStream(is);
            is.close();
            return bmp;
        } catch (IOException e) {
            Log.e(TAG, ExceptionUtils.getStackTrace(e));
        }
        return null;
    }
}
