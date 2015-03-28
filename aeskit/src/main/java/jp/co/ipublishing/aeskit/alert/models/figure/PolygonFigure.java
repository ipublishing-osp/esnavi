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
import java.util.List;

/**
 * ポリゴン形状。
 */
public class PolygonFigure extends Figure implements Serializable {
    /**
     * 座標点。
     */
    @NonNull
    private final List<LatLng> mPoints;

    /**
     * 座標点を取得する。
     *
     * @return 座標点
     */
    @NonNull
    public List<LatLng> getPoints() {
        return mPoints;
    }

    /**
     * コンストラクタ。
     *
     * @param points 座標点
     */
    public PolygonFigure(@NonNull List<LatLng> points) {
        super(FigureType.Polygon);
        mPoints = points;
    }
}
