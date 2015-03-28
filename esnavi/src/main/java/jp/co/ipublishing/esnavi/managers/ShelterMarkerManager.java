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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseArray;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.co.ipublishing.aeskit.shelter.models.Shelter;
import jp.co.ipublishing.aeskit.shelter.models.ShelterStatus;

/**
 * 地図上の避難所の管理。
 */
public class ShelterMarkerManager {
    /**
     * 地図インスタンス。
     */
    @Nullable
    private GoogleMap mMap;

    /**
     * 避難所マーカーのキャッシュ。
     */
    @NonNull
    private final MarkerCache mMarkerCache = new MarkerCache();

    /**
     * マーカーに対応する避難所情報を取得する。
     *
     * @param marker マーカー
     * @return マーカーに対応する避難所情報
     */
    @Nullable
    public Shelter getShelter(Marker marker) {
        return mMarkerCache.getShelter(marker);
    }

    /**
     * 設定する。
     *
     * @param map 地図インスタンス
     */
    public void setup(@NonNull GoogleMap map) {
        mMap = map;
        mMarkerCache.clear();
    }

    /**
     * 避難所を追加する。
     *
     * @param shelters 避難所
     */
    public void addShelters(@NonNull List<Shelter> shelters) {
        if (mMap != null) {
            final GoogleMap map = mMap;
            final MarkerCache markerCache = mMarkerCache;

            for (final Shelter shelter : shelters) {
                if (!markerCache.has(shelter)) {
                    final MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(shelter.getCoordinates());
                    markerOptions.title(shelter.getName());
                    markerOptions.icon(getMarkerBitmapDescriptor(shelter.getStatus()));

                    final Marker marker = map.addMarker(markerOptions);

                    markerCache.add(shelter, marker);
                }
            }
        }
    }

    /**
     * 避難所の状況を更新する。
     *
     * @param statues 避難所の状況
     */
    public void updateStatuses(@NonNull SparseArray<ShelterStatus> statues) {
        if (mMap == null) {
            return;
        }

        final MarkerCache markerCache = mMarkerCache;

        final int length = statues.size();

        for (int i = 0; i < length; ++i) {
            final int shelterId = statues.keyAt(i);
            final ShelterStatus latestStatus = statues.valueAt(i);

            final Shelter shelter = markerCache.getShelter(shelterId);
            if (shelter == null) {
                continue;
            }
            shelter.setStatus(latestStatus);

            final Marker marker = markerCache.getMarker(shelterId);
            if (marker != null) {
                marker.setIcon(getMarkerBitmapDescriptor(latestStatus));
            }
        }
    }

    /**
     * マーカーのビットマップ情報を取得する。
     *
     * @param status 避難所状況
     * @return マーカーのビットマップ情報
     */
    private static BitmapDescriptor getMarkerBitmapDescriptor(@Nullable ShelterStatus status) {
        final ShelterStatus state = status == null ? ShelterStatus.Unknown : status;
        final float bitmapDescriptorValue;

        switch (state) {
            case Unknown:
                bitmapDescriptorValue = BitmapDescriptorFactory.HUE_ORANGE;
                break;
            case Safety:
                bitmapDescriptorValue = BitmapDescriptorFactory.HUE_GREEN;
                break;
            case Waring:
                bitmapDescriptorValue = BitmapDescriptorFactory.HUE_RED;
                break;
            default:
                bitmapDescriptorValue = BitmapDescriptorFactory.HUE_ORANGE;
                break;
        }

        return BitmapDescriptorFactory.defaultMarker(bitmapDescriptorValue);
    }

    private class MarkerCache {
        @NonNull
        private final Map<Integer, Shelter> mShelterIdToShelter = new HashMap<>();

        @NonNull
        private final Map<Integer, Marker> mShelterIdToMarker = new HashMap<>();

        @NonNull
        private final Map<Marker, Shelter> mMarkerToShelter = new HashMap<>();

        public void clear() {
            mShelterIdToShelter.clear();
            mShelterIdToMarker.clear();
            mMarkerToShelter.clear();
        }

        public void add(@NonNull Shelter shelter, @NonNull Marker marker) {
            mShelterIdToShelter.put(shelter.getId(), shelter);
            mShelterIdToMarker.put(shelter.getId(), marker);
            mMarkerToShelter.put(marker, shelter);
        }

        public boolean has(@NonNull Shelter shelter) {
            return mShelterIdToMarker.containsKey(shelter.getId());
        }

        @Nullable
        public Shelter getShelter(Marker marker) {
            return mMarkerToShelter.get(marker);
        }

        @Nullable
        public Shelter getShelter(int shelterId) {
            return mShelterIdToShelter.get(shelterId);
        }

        @Nullable
        public Marker getMarker(int shelterId) {
            return mShelterIdToMarker.get(shelterId);
        }
    }
}
