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

package jp.co.ipublishing.esnavi.fragments;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import icepick.Icepick;
import icepick.Icicle;
import jp.co.ipublishing.aeskit.alert.events.AlertUpdatedAlertEvent;
import jp.co.ipublishing.aeskit.alert.models.Alert;
import jp.co.ipublishing.aeskit.helpers.dagger.ObjectGraph;
import jp.co.ipublishing.aeskit.shelter.ShelterManager;
import jp.co.ipublishing.aeskit.shelter.events.ShelterUpdatedStatusesEvent;
import jp.co.ipublishing.aeskit.shelter.models.Shelter;
import jp.co.ipublishing.esnavi.R;
import jp.co.ipublishing.esnavi.activities.ShelterDetailActivity;
import jp.co.ipublishing.esnavi.helpers.android.AppFragment;
import jp.co.ipublishing.esnavi.managers.ShelterMarkerManager;
import jp.co.ipublishing.esnavi.managers.WarningAreaManager;
import rx.Subscriber;
import rx.android.app.AppObservable;
import rx.schedulers.Schedulers;

/**
 * 避難所地図。
 */
public class MapFragment extends AppFragment implements OnMapReadyCallback, GoogleMap.OnCameraChangeListener, GoogleMap.OnMyLocationChangeListener, GoogleMap.OnInfoWindowClickListener {
    private static final String TAG = "MapFragment";

    /**
     * 最寄りの避難所情報表示要求イベント。
     */
    public static class ShelterRequestShowNearEvent {
    }

    public interface MapSettings {
        CameraPosition getDefaultCameraPosition();
    }

    /**
     * 地図のインスタンス
     */
    @Nullable
    private GoogleMap mMap;

    /**
     * 地図内の危険区域管理。
     */
    @NonNull
    private final WarningAreaManager mWarningAreaManager = new WarningAreaManager();

    /**
     * 地図内の避難所管理。
     */
    @NonNull
    private final ShelterMarkerManager mShelterMarkerManager = new ShelterMarkerManager();

    /**
     * 地図の位置。
     */
    @Nullable
    @Icicle
    public CameraPosition mCameraPosition;

    /**
     * 警報情報。
     */
    @Nullable
    @Icicle
    public Alert mAlert;

    /**
     * 現在位置。
     */
    @Nullable
    @Icicle
    Location mUserLocation;

    /**
     * 避難所情報管理マネージャ。
     */
    @NonNull
    @Inject
    ShelterManager mShelterManager;

    /**
     * 地図の設定。
     */
    @NonNull
    @Inject
    MapSettings mMapSettings;

    /**
     * 地図ビュー。
     */
    @NonNull
    private MapView mMapView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_map, container, false);

        MapsInitializer.initialize(getActivity());

        mMapView = (MapView) view.findViewById(R.id.map_map);
        mMapView.onCreate(savedInstanceState);

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Icepick.restoreInstanceState(this, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ObjectGraph.inject(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mCameraPosition == null) {
            mCameraPosition = mMapSettings.getDefaultCameraPosition();
        }

        mMapView.onResume();

        EventBus.getDefault().register(this);

        if (mMap == null) {
            mMapView.getMapAsync(this);
        } else {
            // 地図のデータを更新する。
            updateMapData();
        }
    }

    @Override
    public void onPause() {
        mMapView.onPause();

        EventBus.getDefault().unregister(this);

        super.onPause();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();

        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        mMapView.onSaveInstanceState(outState);

        Icepick.saveInstanceState(this, outState);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (mMap != null) {
            // 地図の設定
            setupMap(mMap);

            final Context context = getActivity().getApplicationContext();

            mWarningAreaManager.setup(context, mMap);
            mShelterMarkerManager.setup(mMap);

            // 地図のデータを更新する。
            updateMapData();
        }
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        mCameraPosition = cameraPosition;

        updateShelters(cameraPosition);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        final Shelter shelter = mShelterMarkerManager.getShelter(marker);
        if (shelter != null) {
            showShelter(shelter.getId());
        }
    }

    @Override
    public void onMyLocationChange(Location location) {
        if (mUserLocation == null) {
            Log.d(TAG, "mUserLocation is null");
        }

        // 位置補足初回は、地図を移動させる。
        if (mMap != null && mUserLocation == null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
        }

        mUserLocation = location;
    }

    /**
     * 最寄りの避難所表示要求イベントハンドラ。
     *
     * @param event イベント情報
     */
    public void onEvent(ShelterRequestShowNearEvent event) {
        showNearShelter();
    }

    /**
     * 警報情報更新イベントハンドラ。
     *
     * @param event イベント情報
     */
    public void onEvent(AlertUpdatedAlertEvent event) {
        mAlert = event.getAlert();

        mWarningAreaManager.updateWarningArea(mAlert);
    }

    /**
     * 避難所状況更新イベントハンドラ。
     *
     * @param event イベント情報。
     */
    public void onEvent(ShelterUpdatedStatusesEvent event) {
        mShelterMarkerManager.updateStatuses(event.getStatuses());
    }

    /**
     * 地図を設定する。
     *
     * @param map 地図インスタンス
     */
    private void setupMap(@NonNull GoogleMap map) {
        // 地図の設定
        // 特にしない。デフォルトで。
        map.setMyLocationEnabled(true);
        map.setOnInfoWindowClickListener(this);
        map.setOnCameraChangeListener(this);
        map.setOnMyLocationChangeListener(this);

        // 地図の復元
        restoreMap(map);

        // 危険区域を復元する。
        mWarningAreaManager.updateWarningArea(mAlert);

        // 避難所の復元は行わない。onMapReadyに任せる。
    }

    /**
     * 地図の状態を復元する。
     *
     * @param map 地図インスタンス
     */
    private void restoreMap(@NonNull GoogleMap map) {
        // 地図の状態を復元する。
        // 位置・ズーム
        map.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
    }

    /**
     * 地図のデータを更新する。
     */
    private void updateMapData() {
        if (mMap != null) {
            // 危険区域を更新する。
            if (mAlert != null) {
                mWarningAreaManager.updateWarningArea(mAlert);
            }

            // 避難所情報を更新する。
            updateShelters(mMap.getCameraPosition());
        }
    }

    /**
     * 地図内の避難所情報を更新する。
     *
     * @param cameraPosition 中心位置
     */
    private void updateShelters(@NonNull CameraPosition cameraPosition) {
        if (mMap != null) {
            AppObservable.bindFragment(this, mShelterManager.fetchShelters(cameraPosition.target, mMap.getProjection().getVisibleRegion().latLngBounds))
                    .subscribeOn(Schedulers.newThread())
                    .subscribe(new Subscriber<List<Shelter>>() {
                        @Override
                        public void onCompleted() {
                            // Nothing to do
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e(TAG, ExceptionUtils.getStackTrace(e));

                            Toast.makeText(MapFragment.this.getActivity(), R.string.error_fetch_shelters, Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onNext(List<Shelter> shelters) {
                            Log.d(TAG, "updateShelters " + shelters.size());
                            mShelterMarkerManager.addShelters(shelters);
                        }
                    });
        }
    }

    /**
     * 最寄りの避難所を表示する。
     */
    private void showNearShelter() {
        if (mMap == null) {
            return;
        }

        final LatLng target;

        if (mUserLocation == null) {
            // 現在位置が分からない場合は地図の中心を使用する。
            target = mMap.getCameraPosition().target;
            // 警告ダイアログを表示する。
            showDialog(R.string.dialog_title_waring, R.string.error_no_userlocation);
        } else {
            target = new LatLng(mUserLocation.getLatitude(), mUserLocation.getLongitude());
        }

        AppObservable.bindFragment(this, mShelterManager.fetchNearShelter(target))
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Subscriber<Shelter>() {
                    @Override
                    public void onCompleted() {
                        // Nothing to do
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, ExceptionUtils.getStackTrace(e));

                        showErrorDialog(R.string.dialog_title_failed, R.string.error_fetch_near_shelter, e);
                    }

                    @Override
                    public void onNext(Shelter shelter) {
                        showShelter(shelter.getId());
                    }
                });
    }

    /**
     * 避難所詳細画面を表示する。
     *
     * @param shelterId 避難所ID
     */
    private void showShelter(int shelterId) {
        final Intent intent = new Intent(getActivity(), ShelterDetailActivity.class);
        intent.putExtra(ShelterDetailActivity.EXTRA_SHELTER_ID, shelterId);

        startActivity(intent);
    }
}
