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
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.List;

import javax.inject.Inject;

import icepick.Icepick;
import icepick.Icicle;
import jp.co.ipublishing.aeskit.helpers.dagger.ObjectGraph;
import jp.co.ipublishing.aeskit.shelter.ShelterManager;
import jp.co.ipublishing.aeskit.shelter.models.Shelter;
import jp.co.ipublishing.esnavi.R;
import jp.co.ipublishing.esnavi.helpers.android.AppActivity;
import jp.co.ipublishing.esnavi.factories.ShelterImageFactory;
import jp.co.ipublishing.esnavi.views.adapters.ShelterListAdapter;
import rx.Subscriber;
import rx.android.app.AppObservable;
import rx.schedulers.Schedulers;

/**
 * 避難所一覧画面。
 */
public class ShelterListActivity extends AppActivity implements LocationListener {
    private static final String TAG = "ShelterListActivity";

    /**
     * Intentに設定する表示しない避難所IDの名前。
     */
    public static final String EXTRA_IGNORE_SHELTER_ID = "IGNORE_SHELTER_ID";

    /**
     * 表示しない避難所のID。
     */
    private int mIgnoreShelterId;

    /**
     * 位置情報要求リクエスト。
     */
    @Nullable
    private LocationRequest mLocationRequest;

    /**
     * 現在位置。
     */
    @Nullable
    @Icicle
    Location mLocation;

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
     * 避難所一覧アダプタ。
     */
    @NonNull
    private ShelterListAdapter mListAdapter;

    /**
     * 避難所一覧ビュー。
     */
    @NonNull
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shelter_list);
        setTitle(R.string.title_shelter_list);

        setSupportActionBar((Toolbar) findViewById(R.id.list_toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ObjectGraph.inject(this);

        Icepick.restoreInstanceState(this, savedInstanceState);

        mIgnoreShelterId = getIntent().getIntExtra(EXTRA_IGNORE_SHELTER_ID, -1);

        mListView = (ListView) findViewById(R.id.listView);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Shelter shelter = mListAdapter.getItem(position);
                showShelter(shelter.getId());
            }
        });

        mListAdapter = new ShelterListAdapter(this, android.R.layout.simple_list_item_1, mShelterImageFactory);
        mListView.setAdapter(mListAdapter);
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
        mLocationRequest.setNumUpdates(1);

        LocationServices.FusedLocationApi.requestLocationUpdates(getGoogleApiClient(), mLocationRequest, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        mLocation = location;

        if (mListAdapter.getCount() == 0) {
            updateListView();
        }
    }

    /**
     * 画面を更新する。
     */
    private void onMenuRefresh() {
        mListAdapter.clear();
        updateListView();
    }

    /**
     * 一覧を更新する。
     */
    private void updateListView() {
        if (mLocation == null) {
            return;
        }

        AppObservable.bindActivity(this, mShelterManager.fetchNearShelters(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()), mIgnoreShelterId))
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Subscriber<List<Shelter>>() {
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
                    public void onNext(List<Shelter> shelters) {
                        mListAdapter.addAll(shelters);
                    }
                });
    }

    /**
     * 避難所を表示する。
     *
     * @param shelterId 避難所ID
     */
    private void showShelter(int shelterId) {
        final Intent intent = new Intent(this, ShelterDetailActivity.class);
        intent.putExtra(ShelterDetailActivity.EXTRA_SHELTER_ID, shelterId);
        startActivity(intent);
    }
}
