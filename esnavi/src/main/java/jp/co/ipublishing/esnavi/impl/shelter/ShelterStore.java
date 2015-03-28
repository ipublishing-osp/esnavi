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

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.util.SparseArray;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jp.co.ipublishing.aeskit.shelter.models.Shelter;
import jp.co.ipublishing.aeskit.shelter.models.ShelterStatus;
import jp.co.ipublishing.aeskit.shelter.models.ShelterStatuses;
import rx.Observable;
import rx.Subscriber;

/**
 * 避難所情報ストアの実装。
 */
public class ShelterStore implements jp.co.ipublishing.aeskit.shelter.ShelterStore {

    private static final String TABLE_NAME = "places";

    private static final String COL_ID = "id";
    private static final String COL_LATITUDE = "latitude";
    private static final String COL_LONGITUDE = "longitude";
    private static final String COL_NAME = "name";
    private static final String COL_ALTITUDE = "height";
    private static final String COL_STATUS = "type";
    private static final String COL_CAPACITY = "capacity";

    private static final String[] ALL_COLUMNS = {COL_ID, COL_LATITUDE, COL_LONGITUDE, COL_NAME, COL_ALTITUDE, COL_STATUS, COL_CAPACITY};

    private final SQLiteOpenHelper mOpenHelper;

    public ShelterStore(SQLiteOpenHelper openHelper) {
        mOpenHelper = openHelper;
    }

    @NonNull
    @Override
    public Observable<Void> updateStatuses(@NonNull final SparseArray<ShelterStatus> statuses) {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

                final int statusLength = statuses.size();
                for (int i = 0; i < statusLength; ++i) {
                    final int shelterId = statuses.keyAt(i);
                    final ShelterStatus status = statuses.valueAt(i);

                    final ContentValues values = new ContentValues();
                    values.put(COL_STATUS, toInt(status));

                    db.update(TABLE_NAME, values, "id=?", new String[]{"" + shelterId});
                }
            }
        });
    }

    @NonNull
    @Override
    public Observable<ShelterStatuses> fetchStatuses() {
        return Observable.create(new Observable.OnSubscribe<ShelterStatuses>() {
            @Override
            public void call(Subscriber<? super ShelterStatuses> subscriber) {
                final SQLiteDatabase db = mOpenHelper.getReadableDatabase();

                final Cursor cursor = db.query(TABLE_NAME, new String[]{COL_ID, COL_STATUS}, null, null, null, null, null, COL_ID);
                final SparseArray<ShelterStatus> statuses = new SparseArray<>();

                if (cursor.moveToFirst()) {
                    do {
                        final int shelterId = cursor.getInt(0);
                        final int statusInt = cursor.getInt(1);

                        statuses.put(shelterId, toStatus(statusInt));

                    } while (cursor.moveToNext());
                }

                cursor.close();

                subscriber.onNext(new ShelterStatuses(new Date(), statuses));
                subscriber.onCompleted();
            }
        });
    }

    @NonNull
    @Override
    public Observable<List<Shelter>> fetchShelters(@NonNull LatLng center, @NonNull LatLngBounds bounds) {
        final double centerLatitude = center.latitude;
        final double centerLongitude = center.longitude;
        final double topLatitude = bounds.northeast.latitude;
        final double bottomLatitude = bounds.southwest.latitude;
        final double leftLongitude = bounds.southwest.longitude;
        final double rightLongitude = bounds.northeast.longitude;

        return Observable.create(new Observable.OnSubscribe<List<Shelter>>() {
            @Override
            public void call(Subscriber<? super List<Shelter>> subscriber) {
                final SQLiteDatabase db = mOpenHelper.getReadableDatabase();

                final String where = "latitude >= ? AND latitude <= ? AND longitude >= ? AND longitude <= ?";
                final String[] whereParams = new String[]{String.valueOf(bottomLatitude), String.valueOf(topLatitude), String.valueOf(leftLongitude), String.valueOf(rightLongitude)};
                final String order = String.format("abs(latitude - %f) + abs(longitude - %f)", centerLatitude, centerLongitude);
                final Cursor cursor = db.query(TABLE_NAME, ALL_COLUMNS, where, whereParams, null, null, order, "25");

                final List<Shelter> shelters = new ArrayList<>();

                if (cursor.moveToFirst()) {
                    do {
                        final Shelter shelter = fetchShelter(cursor);
                        shelters.add(shelter);
                    } while (cursor.moveToNext());
                }
                cursor.close();

                subscriber.onNext(shelters);
                subscriber.onCompleted();
            }
        });
    }

    @NonNull
    @Override
    public Observable<Shelter> fetchNearShelter(@NonNull LatLng target) {
        final double centerLatitude = target.latitude;
        final double centerLongitude = target.longitude;

        return Observable.create(new Observable.OnSubscribe<Shelter>() {
            @Override
            public void call(Subscriber<? super Shelter> subscriber) {
                final SQLiteDatabase db = mOpenHelper.getReadableDatabase();

                final String order = String.format("abs(latitude - %f) + abs(longitude - %f)", centerLatitude, centerLongitude);
                final Cursor cursor = db.query(TABLE_NAME, ALL_COLUMNS, null, null, null, null, order, "1");

                if (cursor.moveToFirst()) {
                    do {
                        final Shelter shelter = fetchShelter(cursor);
                        subscriber.onNext(shelter);

                    } while (cursor.moveToNext());
                }
                cursor.close();

                subscriber.onCompleted();
            }
        });
    }

    @NonNull
    @Override
    public Observable<List<Shelter>> fetchNearShelters(@NonNull LatLng target, final int ignoreShelterId) {
        final double centerLatitude = target.latitude;
        final double centerLongitude = target.longitude;

        return Observable.create(new Observable.OnSubscribe<List<Shelter>>() {
            @Override
            public void call(Subscriber<? super List<Shelter>> subscriber) {
                final SQLiteDatabase db = mOpenHelper.getReadableDatabase();

                final String where = "id <> ?";
                final String[] param = new String[]{String.valueOf(ignoreShelterId)};

                final String order = String.format("abs(latitude - %f) + abs(longitude - %f)", centerLatitude, centerLongitude);
                final Cursor cursor = db.query(TABLE_NAME, ALL_COLUMNS, where, param, null, null, order, "5");

                final List<Shelter> shelters = new ArrayList<>();

                if (cursor.moveToFirst()) {
                    do {
                        final Shelter shelter = fetchShelter(cursor);
                        shelters.add(shelter);

                    } while (cursor.moveToNext());
                }
                cursor.close();

                subscriber.onNext(shelters);

                subscriber.onCompleted();
            }
        });
    }

    @NonNull
    @Override
    public Observable<Shelter> fetchShelter(final int shelterId) {
        return Observable.create(new Observable.OnSubscribe<Shelter>() {
            @Override
            public void call(Subscriber<? super Shelter> subscriber) {
                final SQLiteDatabase db = mOpenHelper.getReadableDatabase();

                final String where = "id=?";
                final String[] whereParams = new String[]{"" + shelterId};
                final Cursor cursor = db.query(TABLE_NAME, ALL_COLUMNS, where, whereParams, null, null, null, "1");

                if (cursor.moveToFirst()) {
                    do {
                        final Shelter shelter = fetchShelter(cursor);
                        subscriber.onNext(shelter);
                    } while (cursor.moveToNext());
                }
                cursor.close();

                subscriber.onCompleted();
            }
        });
    }

    @NonNull
    private static Shelter fetchShelter(@NonNull Cursor cursor) {
        final jp.co.ipublishing.esnavi.impl.shelter.Shelter shelter = new jp.co.ipublishing.esnavi.impl.shelter.Shelter();

        final int shelterId = cursor.getInt(0);
        final double latitude = cursor.getDouble(1);
        final double longitude = cursor.getDouble(2);
        final String name = cursor.getString(3);
        final double altitude = cursor.getDouble(4);
        final int status = cursor.getInt(5);

        final int capacity;
        if (cursor.isNull(6)) {
            capacity = -1;
        } else {
            capacity = cursor.getInt(6);
        }

        shelter.setId(shelterId);
        shelter.setCoordinates(new LatLng(latitude, longitude));
        shelter.setName(name);
        shelter.setAltitude(altitude);
        shelter.setStatus(toStatus(status));
        shelter.setCapacity(capacity);

        return shelter;
    }

    private static int toInt(@NonNull ShelterStatus status) {
        final int value;
        switch (status) {
            case Unknown:
                value = -1;
                break;
            case Safety:
                value = 0;
                break;
            case Waring:
                value = 1;
                break;
            default:
                value = -1;
        }
        return value;
    }

    @NonNull
    private static ShelterStatus toStatus(int value) {
        final ShelterStatus status;

        switch (value) {
            case 0:
                status = ShelterStatus.Safety;
                break;
            case 1:
                status = ShelterStatus.Waring;
                break;
            default:
                status = ShelterStatus.Unknown;
                break;
        }

        return status;
    }
}
