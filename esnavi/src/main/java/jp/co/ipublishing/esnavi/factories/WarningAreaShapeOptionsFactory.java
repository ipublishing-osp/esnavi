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
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import jp.co.ipublishing.aeskit.alert.models.figure.CircleFigure;
import jp.co.ipublishing.aeskit.alert.models.figure.Figure;
import jp.co.ipublishing.aeskit.alert.models.figure.PolygonFigure;
import jp.co.ipublishing.aeskit.alert.models.figure.PolylineFigure;
import jp.co.ipublishing.esnavi.R;

/**
 * 危険区域の形状生成ファクトリ。
 */
public final class WarningAreaShapeOptionsFactory {
    private static final String TAG = "WarningAreaShapeFactory";

    /**
     * デフォルトコンストラクタ。
     */
    private WarningAreaShapeOptionsFactory() {
    }

    /**
     * 地図用の形状を生成する。
     * @param context コンテキスト
     * @param figures 形状
     * @return 地図用の形状
     */
    @NonNull
    public static List<Parcelable> createShapeOptions(@NonNull Context context, @NonNull List<Figure> figures) {
        final List<Parcelable> shapeOptions = new ArrayList<>(figures.size());

        for (final Figure figure : figures) {
            if (figure instanceof PolygonFigure) {
                shapeOptions.add(createShapeOptions(context, (PolygonFigure) figure));
            } else if (figure instanceof PolylineFigure) {
                shapeOptions.add(createShapeOptions(context, (PolylineFigure) figure));
            } else if (figure instanceof CircleFigure) {
                shapeOptions.add(createShapeOptions(context, (CircleFigure) figure));
            } else {
                Log.w(TAG, "想定していない形状。" + figure.getClass().getSimpleName());
            }
        }

        return shapeOptions;
    }

    /**
     * 地図用の形状(ポリゴン)を生成する。
     * @param context コンテキスト
     * @param figure ポリゴン形状
     * @return 地図用の形状(ポリゴン)
     */
    @NonNull
    private static PolygonOptions createShapeOptions(@NonNull Context context, @NonNull PolygonFigure figure) {
        return new PolygonOptions()
                .strokeColor(context.getResources().getColor(R.color.deeporangeA700))
                .fillColor(context.getResources().getColor(R.color.deeporangeA700))
                .addAll(figure.getPoints());
    }

    /**
     * 地図用の形状(ポリライン)を生成する。
     * @param context コンテキスト
     * @param figure ポリライン形状
     * @return 地図用の形状(ポリライン)
     */
    @NonNull
    private static PolylineOptions createShapeOptions(@NonNull Context context, @NonNull PolylineFigure figure) {
        return new PolylineOptions()
                .width(5)
                .color(context.getResources().getColor(R.color.deeporangeA700))
                .addAll(figure.getPoints());
    }

    /**
     * 地図用の形状(円)を生成する。
     * @param context コンテキスト
     * @param figure 円形状
     * @return 地図用の形状(円)
     */
    @NonNull
    private static CircleOptions createShapeOptions(@NonNull Context context, @NonNull CircleFigure figure) {
        return new CircleOptions()
                .strokeColor(context.getResources().getColor(R.color.deeporangeA700))
                .fillColor(context.getResources().getColor(R.color.deeporangeA700))
                .center(figure.getCenter())
                .radius(figure.getRadius());
    }
}
