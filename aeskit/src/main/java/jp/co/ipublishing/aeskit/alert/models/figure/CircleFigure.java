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

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**
 * 円図形。
 */
public class CircleFigure extends Figure implements Serializable {
    /**
     * 円の中心座標。
     */
    @NonNull
    private final LatLng mCenter;

    /**
     * 円の半径。
     */
    private final double mRadius;

    /**
     * 円の中心座標を取得する。
     *
     * @return 円の中心座標
     */
    @NonNull
    public LatLng getCenter() {
        return mCenter;
    }

    /**
     * 円の半径を取得する。
     *
     * @return 円の半径
     */
    public double getRadius() {
        return mRadius;
    }

    /**
     * コンストラクタ。
     *
     * @param center 中心座標
     * @param radius 半径
     */
    public CircleFigure(@NonNull LatLng center, double radius) {
        super(FigureType.Circle);

        mCenter = center;
        mRadius = radius;
    }
}
