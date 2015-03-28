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

package jp.co.ipublishing.esnavi.managers;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import jp.co.ipublishing.aeskit.alert.models.Alert;
import jp.co.ipublishing.aeskit.alert.models.figure.Figure;
import jp.co.ipublishing.esnavi.factories.WarningAreaShapeOptionsFactory;

/**
 * 地図上の危険区域の管理。
 */
public class WarningAreaManager {
    private static final String TAG = "WarningAreaLayer";

    /**
     * コンテキスト
     */
    @Nullable
    private Context mContext;

    /**
     * 地図インスタンス
     */
    @Nullable
    private GoogleMap mMap;

    /**
     * 地図上に表示した形状。
     */
    @NonNull
    private final List<Object> mShapes = new ArrayList<>();

    /**
     * 準備する。
     *
     * @param context コンテキスト
     * @param map     地図インスタンス
     */
    public void setup(@NonNull Context context, @NonNull GoogleMap map) {
        mContext = context;
        mMap = map;
    }

    /**
     * 危険区域を更新する。
     *
     * @param alert 警報情報
     */
    public void updateWarningArea(@Nullable Alert alert) {
        if (alert != null && mContext != null) {
            final List<Figure> warningAreas = alert.getWarningAreas();
            final List<Parcelable> shapeOptions = WarningAreaShapeOptionsFactory.createShapeOptions(mContext, warningAreas);

            removeAllShapes();
            addShapes(shapeOptions);
        }
    }

    /**
     * 形状を追加する。
     *
     * @param shapeOptions 形状オプション
     */
    private void addShapes(@NonNull List<Parcelable> shapeOptions) {
        if (mMap == null) {
            return;
        }

        for (final Parcelable shapeOption : shapeOptions) {
            if (shapeOption instanceof PolygonOptions) {
                mShapes.add(mMap.addPolygon((PolygonOptions) shapeOption));
            } else if (shapeOption instanceof PolylineOptions) {
                mShapes.add(mMap.addPolyline((PolylineOptions) shapeOption));
            } else if (shapeOption instanceof CircleOptions) {
                mShapes.add(mMap.addCircle((CircleOptions) shapeOption));
            } else {
                Log.w(TAG, "想定していない形状。" + shapeOption.getClass().getSimpleName());
            }
        }
    }

    /**
     * 形状を全削除する。
     */
    private void removeAllShapes() {
        for (final Object shape : mShapes) {
            if (shape instanceof Polygon) {
                ((Polygon) shape).remove();
            } else if (shape instanceof Polyline) {
                ((Polyline) shape).remove();
            } else if (shape instanceof Circle) {
                ((Circle) shape).remove();
            } else {
                Log.w(TAG, "想定していない形状。" + shape.getClass().getSimpleName());
            }
        }
        mShapes.clear();
    }
}
