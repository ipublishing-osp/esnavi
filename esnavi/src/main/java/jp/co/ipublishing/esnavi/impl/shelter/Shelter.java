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

import com.google.android.gms.maps.model.LatLng;

import jp.co.ipublishing.aeskit.shelter.models.ShelterStatus;


/**
 * 避難所情報の実装。
 */
public class Shelter implements jp.co.ipublishing.aeskit.shelter.models.Shelter {
    private int mId;
    private LatLng mCoordinates;
    private double mAltitude;
    private String mName;
    private ShelterStatus mStatus;
    private int mCapacity;

    @Override
    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    @NonNull
    @Override
    public LatLng getCoordinates() {
        return mCoordinates;
    }

    public void setCoordinates(LatLng coordinates) {
        mCoordinates = coordinates;
    }

    @Override
    public double getAltitude() {
        return mAltitude;
    }

    public void setAltitude(double altitude) {
        mAltitude = altitude;
    }

    @NonNull
    @Override
    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    @NonNull
    @Override
    public ShelterStatus getStatus() {
        return mStatus;
    }

    @Override
    public void setStatus(@NonNull ShelterStatus status) {
        mStatus = status;
    }

    @Override
    public int getCapacity() {
        return mCapacity;
    }

    public void setCapacity(int capacity) {
        mCapacity = capacity;
    }
}
