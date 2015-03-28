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

package jp.co.ipublishing.esnavi.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import icepick.Icepick;
import icepick.Icicle;
import jp.co.ipublishing.aeskit.helpers.dagger.ObjectGraph;
import jp.co.ipublishing.aeskit.shelter.ShelterManager;
import jp.co.ipublishing.aeskit.shelter.events.ShelterUpdatedCurrentNumberOfPeopleEvent;
import jp.co.ipublishing.aeskit.shelter.models.Shelter;
import jp.co.ipublishing.aeskit.shelter.models.ShelterNumberOfPeople;
import jp.co.ipublishing.esnavi.R;
import jp.co.ipublishing.esnavi.helpers.android.AppActivity;
import jp.co.ipublishing.esnavi.views.ArcView;
import jp.co.ipublishing.esnavi.factories.ShelterImageFactory;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * 避難所詳細画面。
 */
public class ShelterDetailActivity extends AppActivity implements LocationListener {
    private static final String TAG = "ShelterDetailActivity";

    /**
     * Intentに設定する避難所IDの名前。
     */
    public static final String EXTRA_SHELTER_ID = "SHELTER_ID";

    /**
     * 許容人数を超えているアラートを表示する許容人数割合の限界。
     */
    private static final double OVER_CAPACITY_LIMIT = 0.85;

    /**
     * 表示する避難所のID。
     */
    private int mShelterId;

    /**
     * 表示する避難所情報。
     */
    @Nullable
    Shelter mShelter;

    /**
     * 現在の人数。
     */
    @Icicle
    int mCurrentNumber = -1;

    /**
     * 現在地から避難所までの距離[m]。
     */
    @Icicle
    double mDistance = -1;

    /**
     * 位置情報要求リクエスト。
     */
    @Nullable
    private LocationRequest mLocationRequest;

    /**
     * 避難所情報管理マネージャ。
     */
    @NonNull
    @Inject
    ShelterManager mShelterManager;

    /**
     * 避難所画像ファクトリ。
     */
    @NonNull
    @Inject
    ShelterImageFactory mShelterImageFactory;

    /**
     * 避難所の写真。
     */
    @NonNull
    private ImageView mShelterImage;

    /**
     * 収容率。
     */
    @NonNull
    private ArcView mPercentView;

    /**
     * 許容人数。
     */
    @NonNull
    private TextView mCapacityValue;

    /**
     * 現在の人数。
     */
    @NonNull
    private TextView mCurrentValue;

    /**
     * 標高。
     */
    @NonNull
    private TextView mAltitudeValue;

    /**
     * 避難所までの距離。
     */
    @NonNull
    private TextView mDistanceValue;

    /**
     * 許容人数可能性ありアラート。
     */
    @NonNull
    private TextView mOverCapacityText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shelter_detail);

        setSupportActionBar((Toolbar) findViewById(R.id.detail_toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ObjectGraph.inject(this);

        Icepick.restoreInstanceState(this, savedInstanceState);

        mShelterId = getIntent().getIntExtra(EXTRA_SHELTER_ID, -1);

        mShelterImage = (ImageView) findViewById(R.id.shelter_image);
        mPercentView = (ArcView) findViewById(R.id.percent_view);
        mCapacityValue = (TextView) findViewById(R.id.capacity_value);
        mCurrentValue = (TextView) findViewById(R.id.current_value);
        mAltitudeValue = (TextView) findViewById(R.id.altitude_value);
        mDistanceValue = (TextView) findViewById(R.id.distance_value);
        mOverCapacityText = (TextView) findViewById(R.id.over_capacity_text);

        findViewById(R.id.navi_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNavigation(mShelter);
            }
        });
        findViewById(R.id.other_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOtherShelters(mShelter);
            }
        });

        updateLayout(mShelter, mCurrentNumber, mDistance);
    }

    @Override
    protected void onResume() {
        super.onResume();

        EventBus.getDefault().register(this);

        if (mShelter == null) {
            updateShelter();
        } else {
            updateLayout(mShelter, mCurrentNumber, mDistance);
        }

        updateCurrentNumber();
    }

    @Override
    protected void onPause() {
        EventBus.getDefault().unregister(this);

        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Icepick.saveInstanceState(this, outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();

        if (id == R.id.action_refresh) {
            onMenuRefresh();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(Bundle bundle) {
        super.onConnected(bundle);

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000);

        LocationServices.FusedLocationApi.requestLocationUpdates(getGoogleApiClient(), mLocationRequest, this);
    }

    /**
     * 避難所の現在の収容人数の更新イベントハンドラ。
     *
     * @param event イベント情報
     */
    public void onEvent(ShelterUpdatedCurrentNumberOfPeopleEvent event) {
        mCurrentNumber = event.getNumberOfPeople().getNumber(mShelterId);

        updateLayout(mShelter, mCurrentNumber, mDistance);
    }

    /**
     * データを更新する。
     */
    private void onMenuRefresh() {
        updateShelter();
        updateCurrentNumber();
    }

    /**
     * 施設情報を更新する。
     */
    private void updateShelter() {
        mShelterManager.fetchShelter(mShelterId)
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Subscriber<Shelter>() {
                    @Override
                    public void onCompleted() {
                        // Nothing to do
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, ExceptionUtils.getStackTrace(e));
                        showErrorDialog(R.string.dialog_title_failed, R.string.error_fetch_shelter, e);
                    }

                    @Override
                    public void onNext(Shelter shelter) {
                        mShelter = shelter;
                        updateLayout(mShelter, mCurrentNumber, mDistance);
                    }
                });
    }

    /**
     * 現在の人数を更新する。
     */
    private void updateCurrentNumber() {
        mShelterManager.downloadNumberOfPeople()
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Subscriber<ShelterNumberOfPeople>() {
                    @Override
                    public void onCompleted() {
                        // Nothing to do
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, ExceptionUtils.getStackTrace(e));
                        showErrorDialog(R.string.dialog_title_failed, R.string.error_download_current_number, e);
                    }

                    @Override
                    public void onNext(ShelterNumberOfPeople shelterNumberOfPeople) {
                        mCurrentNumber = shelterNumberOfPeople.getNumber(mShelterId);
                        updateLayout(mShelter, mCurrentNumber, mDistance);
                    }
                });
    }

    /**
     * 画面を更新する。
     *
     * @param shelter       避難所情報
     * @param currentNumber 現在の人数
     * @param distance      避難所までの距離
     */
    private void updateLayout(
            @Nullable final Shelter shelter,
            final int currentNumber,
            final double distance) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (shelter == null) {
                    setTitle("");
                    mShelterImage.setImageBitmap(null);
                    mPercentView.setPercent(-1);
                    mCapacityValue.setText(R.string.default_capacity);
                    mCurrentValue.setText(R.string.default_current);
                    mAltitudeValue.setText(R.string.default_altitude);
                    mDistanceValue.setText(R.string.default_distance);
                    mOverCapacityText.setVisibility(View.INVISIBLE);
                } else {
                    setTitle(shelter.getName());
                    mShelterImage.setImageBitmap(getShelterImageBitmap(shelter));

                    if (currentNumber == -1) {
                        mPercentView.setPercent(-1);
                    } else {
                        final int proportion = (int) ((currentNumber / shelter.getCapacity()) * 100.0);
                        mPercentView.setPercent(proportion);
                    }

                    if (shelter.getCapacity() == -1) {
                        mCapacityValue.setText(R.string.default_capacity);
                    } else {
                        mCapacityValue.setText(String.format(getResources().getString(R.string.format_capacity), shelter.getCapacity()));
                    }

                    if (mCurrentNumber == -1) {
                        mCurrentValue.setText(R.string.default_current);
                    } else {
                        mCurrentValue.setText(String.format(getResources().getString(R.string.format_current), currentNumber));
                    }

                    if (shelter.getAltitude() == -1) {
                        mAltitudeValue.setText(R.string.default_altitude);
                    } else {
                        mAltitudeValue.setText(String.format(getResources().getString(R.string.format_altitude), shelter.getAltitude()));
                    }

                    updateDistance(distance);
                    updateOverCapacityText(shelter, currentNumber);
                }
            }
        });
    }

    /**
     * 避難所までの距離を更新する。
     */
    private void updateDistance(double distance) {
        if (mDistance == -1) {
            mDistanceValue.setText(R.string.default_distance);
        } else {
            mDistanceValue.setText(String.format(getResources().getString(R.string.format_distance), distance));
        }
    }

    /**
     * 許容人数を超えているアラートの表示切り替え。
     *
     * @param shelter       避難所情報
     * @param currentNumber 現在の人数
     */
    private void updateOverCapacityText(@Nullable Shelter shelter, int currentNumber) {
        if (currentNumber == -1 || shelter == null) {
            mOverCapacityText.setVisibility(View.INVISIBLE);
        } else {
            final double proportion = currentNumber / shelter.getCapacity();
            final boolean visible = proportion >= OVER_CAPACITY_LIMIT;
            mOverCapacityText.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
        }
    }

    /**
     * 避難所の写真を取得する。
     *
     * @param shelter 避難所情報
     * @return 避難所の写真
     */
    @Nullable
    private Bitmap getShelterImageBitmap(@Nullable Shelter shelter) {
        if (shelter == null) {
            return null;
        }

        return mShelterImageFactory.getPhoto(this, shelter.getId());
    }

    /**
     * ナビゲーションを開始する。
     */
    private void startNavigation(@Nullable final Shelter shelter) {
        if (shelter == null) {
            // TODO: メッセージを出してもいいかも。
            return;
        }

        // TODO: アプリがない場合
        final Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setClassName(
                "com.google.android.apps.maps",
                "com.google.android.maps.driveabout.app.NavigationActivity");

        final Uri uri = Uri.parse(String.format(
                "google.navigation:///?ll=%f,%f&q=%s&mode=w",
                shelter.getCoordinates().latitude,
                shelter.getCoordinates().longitude,
                shelter.getName()));

        intent.setData(uri);

        startActivity(intent);
    }

    /**
     * 付近の他の避難所を表示する。
     *
     * @param ignoreShelter 無視する避難所
     */
    private void showOtherShelters(@Nullable Shelter ignoreShelter) {
        if (ignoreShelter == null) {
            return;
        }

        final Intent intent = new Intent(this, ShelterListActivity.class);
        intent.putExtra(ShelterListActivity.EXTRA_IGNORE_SHELTER_ID, ignoreShelter.getId());
        startActivity(intent);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (mShelter == null) {
            return;
        }

        float[] results = new float[1];
        Location.distanceBetween(
                location.getLatitude(),
                location.getLongitude(),
                mShelter.getCoordinates().latitude,
                mShelter.getCoordinates().longitude,
                results);

        mDistance = results[0];

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateDistance(mDistance);
            }
        });
    }
}
