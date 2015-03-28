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

package jp.co.ipublishing.esnavi.impl.shelter;

import android.support.annotation.NonNull;
import android.util.SparseArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import jp.co.ipublishing.aeskit.shelter.models.ShelterNumberOfPeople;
import jp.co.ipublishing.aeskit.shelter.models.ShelterStatus;
import jp.co.ipublishing.aeskit.shelter.models.ShelterStatuses;

/**
 * 避難所情報ファクトリ。
 */
public final class ShelterFactory {

    /**
     * デフォルトコンストラクタ。
     */
    private ShelterFactory() {
    }

    /**
     * 避難所状況を生成する。
     * @param jsonString JSON文字列
     * @return 避難所状況
     * @throws JSONException
     * @throws ParseException
     */
    @NonNull
    public static ShelterStatuses createStatuses(@NonNull String jsonString) throws JSONException, ParseException {
        final JSONObject jsonObject = new JSONObject(jsonString);

        final JSONObject head = jsonObject.getJSONObject("Head");
        final String dateTimeSting = head.getString("DateTime");
        final Date dateTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZ", Locale.JAPAN).parse(dateTimeSting);

        final JSONArray placeArray = jsonObject.getJSONArray("Place");

        final int placeLength = placeArray.length();
        final SparseArray<ShelterStatus> statuses = new SparseArray<>(placeLength);

        for (int i = 0; i < placeLength; ++i) {
            final JSONObject placeObject = placeArray.getJSONObject(i);

            final String shelterIdString = placeObject.getString("id");
            final String statusString = placeObject.getString("type");

            final int shelterId = Integer.valueOf(shelterIdString);
            final ShelterStatus status = convertToStatus(statusString);

            statuses.put(shelterId, status);
        }

        return new ShelterStatuses(dateTime, statuses);
    }

    /**
     * 避難所状況文字列をShelterStatusに変換する。
     * @param string 避難所状況文字列
     * @return 避難所状況文字列に対応したShelterStatus
     */
    private static ShelterStatus convertToStatus(String string) {
        final ShelterStatus status;
        switch (string) {
            case "0":
                status = ShelterStatus.Safety;
                break;
            case "1":
                status = ShelterStatus.Waring;
                break;
            default:
                status = ShelterStatus.Unknown;
                break;
        }
        return status;
    }

    /**
     * 避難所の現在の人数を生成する。
     * @param jsonString JSON文字列
     * @return 避難所の現在の人数
     * @throws JSONException
     * @throws ParseException
     */
    @NonNull
    public static ShelterNumberOfPeople createNumberOfPeople(@NonNull String jsonString) throws JSONException, ParseException {
        final JSONObject jsonObject = new JSONObject(jsonString);

        final JSONObject head = jsonObject.getJSONObject("Head");
        final String dateTimeSting = head.getString("DateTime");
        final Date dateTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZ", Locale.JAPAN).parse(dateTimeSting);

        final JSONArray placeArray = jsonObject.getJSONArray("Place");

        final int placeLength = placeArray.length();
        final SparseArray<Integer> numbers = new SparseArray<>(placeLength);

        for (int i = 0; i < placeLength; ++i) {
            final JSONObject placeObject = placeArray.getJSONObject(i);

            final String shelterIdString = placeObject.getString("id");
            final String numberString = placeObject.getString("people");

            final int shelterId = Integer.valueOf(shelterIdString);
            final int number;
            if ("".equals(numberString)) {
                number = -1;
            } else {
                number = Integer.valueOf(numberString);
            }

            numbers.put(shelterId, number);
        }

        return new ShelterNumberOfPeople(dateTime, numbers);
    }
}
