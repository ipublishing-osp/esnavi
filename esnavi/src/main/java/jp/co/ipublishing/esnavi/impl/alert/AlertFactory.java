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

package jp.co.ipublishing.esnavi.impl.alert;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import jp.co.ipublishing.aeskit.alert.models.Alert;
import jp.co.ipublishing.aeskit.alert.models.AlertLevel;
import jp.co.ipublishing.aeskit.alert.models.figure.CircleFigure;
import jp.co.ipublishing.aeskit.alert.models.figure.Figure;
import jp.co.ipublishing.aeskit.alert.models.figure.FigureType;
import jp.co.ipublishing.aeskit.alert.models.figure.PolygonFigure;
import jp.co.ipublishing.aeskit.alert.models.figure.PolylineFigure;

/**
 * Alertオブジェクトの生成ファクトリ。
 */
public final class AlertFactory {
    private static final String TAG = "AlertFactory";

    /**
     * デフォルトコンストラクタ。
     */
    private AlertFactory() {
    }

    /**
     * JSON文字列から警報オブジェクトを生成する。
     *
     * @param jsonString JSON文字列
     * @return 警報オブジェクト
     */
    @NonNull
    public static Alert create(@NonNull String jsonString) throws JSONException, ParseException {
        final JSONObject jsonObject = new JSONObject(jsonString);

        final String warningString = jsonObject.getString("w");
        final String area = jsonObject.getString("a");
        final String timeString = jsonObject.getString("t");
        final String headlineBody = jsonObject.getString("hb");
        final String messageTitle = jsonObject.getString("mt");
        final String messageBody = jsonObject.getString("mb");
        final JSONArray figureArray = jsonObject.getJSONArray("f");

        final AlertLevel alertLevel = convertToAlertLevel(warningString);
        final Date time = convertToAlertTime(timeString);
        final List<Figure> warningAreas = createWarningAreas(figureArray);

        final jp.co.ipublishing.esnavi.impl.alert.Alert alert = new jp.co.ipublishing.esnavi.impl.alert.Alert();

        alert.setLevel(alertLevel);
        alert.setArea(area);
        alert.setTime(time);
        alert.setMessageTitle(messageTitle);
        alert.setMessageBody(messageBody);
        alert.setHeadlineBody(headlineBody);
        alert.setWarningAreas(warningAreas);

        return alert;
    }

    /**
     * 警報レベル文字列をAlertLevelに変換する。
     * @param waringString 警報レベル文字列
     * @return 警報レベル文字列に対応するAlertLevel
     */
    @NonNull
    private static AlertLevel convertToAlertLevel(@NonNull String waringString) {
        final AlertLevel alertLevel;

        switch (waringString) {
            case "NONE":
                alertLevel = AlertLevel.None;
                break;
            case "WARNING":
                alertLevel = AlertLevel.Warning;
                break;
            case "DANGER":
                alertLevel = AlertLevel.Emergency;
                break;
            default:
                alertLevel = AlertLevel.None;
                break;
        }

        return alertLevel;
    }

    /**
     * 日付文字列をDateに変換する。
     * @param timeString 日付文字列
     * @return Dateオブジェクト
     * @throws ParseException
     */
    @NonNull
    private static Date convertToAlertTime(@NonNull String timeString) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZ", Locale.JAPAN).parse(timeString);
    }

    /**
     * 形状を生成する。
     * @param figureArray 形状の種別と座標点を持つ配列
     * @return 形状
     * @throws JSONException
     */
    @NonNull
    private static List<Figure> createWarningAreas(@NonNull JSONArray figureArray) throws JSONException {
        final int length = figureArray.length();

        final List<Figure> warningAreas = new ArrayList<>(length);

        for (int i = 0; i < length; ++i) {
            final JSONObject figure = figureArray.getJSONObject(i);
            final String figureTypeString = figure.getString("fg");
            final String figurePoints = figure.getString("fp");

            final Figure warningArea = createWarningArea(figureTypeString, figurePoints);

            if (warningArea != null) {
                warningAreas.add(warningArea);
            }
        }

        return warningAreas;
    }

    /**
     * 危険区域の形状を生成する。
     * @param figureTypeString 形状の種別
     * @param figurePointsString 形状の座標点
     * @return 危険区域の形状
     */
    @Nullable
    private static Figure createWarningArea(@NonNull String figureTypeString, @NonNull String figurePointsString) {
        final FigureType figureType = convertToFigureType(figureTypeString);

        final String[] figurePoints = figurePointsString.split(":");

        final Figure warningArea;

        switch (figureType) {
            case None:
                warningArea = null;
                break;
            case Polygon:
                warningArea = createPolygonFigure(figurePoints);
                break;
            case Polyline:
                warningArea = createPolylineFigure(figurePoints);
                break;
            case Circle:
                warningArea = createCircleFigure(figurePoints);
                break;
            default:
                warningArea = null;
                break;
        }

        return warningArea;
    }

    /**
     * 形状の種別文字列をFigureTypeに変換する。
     * @param figureTypeString 形状の種別文字列
     * @return 形状の種別文字列に対応するFigureType
     */
    @NonNull
    private static FigureType convertToFigureType(@NonNull String figureTypeString) {
        final FigureType figureType;

        switch (figureTypeString) {
            case "Polygon":
                figureType = FigureType.Polygon;
                break;
            case "Polyline":
                figureType = FigureType.Polyline;
                break;
            case "Circle":
                figureType = FigureType.Circle;
                break;
            default:
                figureType = FigureType.None;
                break;
        }

        return figureType;
    }

    /**
     * ポリゴン形状を生成する。
     * @param figurePoints 座標点
     * @return ポリゴン形状
     */
    @NonNull
    private static PolygonFigure createPolygonFigure(@NonNull String[] figurePoints) {
        final List<LatLng> latlngs = new ArrayList<>(figurePoints.length);

        for (final String figurePoint : figurePoints) {
            final String[] latlngArr = figurePoint.split(",");
            final LatLng latlng = new LatLng(Double.valueOf(latlngArr[0]), Double.valueOf(latlngArr[1]));

            latlngs.add(latlng);
        }

        return new PolygonFigure(latlngs);
    }

    /**
     * ポリライン形状を生成する。
     * @param figurePoints 座標点
     * @return ポリライン形状
     */
    @NonNull
    private static PolylineFigure createPolylineFigure(@NonNull String[] figurePoints) {
        final List<LatLng> latlngs = new ArrayList<>(figurePoints.length);

        for (final String figurePoint : figurePoints) {
            final String[] latlngArr = figurePoint.split(",");
            final LatLng latlng = new LatLng(Double.valueOf(latlngArr[0]), Double.valueOf(latlngArr[1]));

            latlngs.add(latlng);
        }

        return new PolylineFigure(latlngs);
    }

    /**
     * 円形状を生成する。
     * @param figurePoints 座標点
     * @return 円形状
     */
    @Nullable
    private static CircleFigure createCircleFigure(@NonNull String[] figurePoints) {
        if (figurePoints.length != 1) {
            Log.w(TAG, "FigureType.Circleの場合、ポイントの数は1のみ");
        }

        if (figurePoints.length == 0) {
            return null;
        }

        final String figurePoint = figurePoints[0];

        final String[] latlngArr = figurePoint.split(",");
        final LatLng latlng = new LatLng(Double.valueOf(latlngArr[0]), Double.valueOf(latlngArr[1]));
        final double radius = Double.valueOf(latlngArr[2]);

        return new CircleFigure(latlng, radius);
    }
}
