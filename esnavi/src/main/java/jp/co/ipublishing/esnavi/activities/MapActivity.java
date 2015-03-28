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

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import jp.co.ipublishing.aeskit.alert.events.AlertFailedDownloadAlertEvent;
import jp.co.ipublishing.aeskit.alert.events.AlertRequestAlertEvent;
import jp.co.ipublishing.aeskit.alert.events.AlertUpdatedAlertEvent;
import jp.co.ipublishing.aeskit.alert.models.Alert;
import jp.co.ipublishing.aeskit.helpers.dagger.ObjectGraph;
import jp.co.ipublishing.aeskit.shelter.events.ShelterRequestStatusesEvent;
import jp.co.ipublishing.aeskit.user.UserManager;
import jp.co.ipublishing.esnavi.R;
import jp.co.ipublishing.esnavi.fragments.MapFragment;
import jp.co.ipublishing.esnavi.fragments.MapLegendFragment;
import jp.co.ipublishing.esnavi.helpers.android.AppActivity;
import rx.Subscriber;
import rx.android.app.AppObservable;
import rx.schedulers.Schedulers;

/**
 * 地図画面のアクティビティ。
 */
public class MapActivity extends AppActivity {
    private static final String TAG = "MapActivity";

    /**
     * 地図の凡例。
     */
    @NonNull
    private MapLegendFragment mLegendFragment;

    /**
     * 地図。
     */
    @NonNull
    private MapFragment mMapFragment;

    /**
     * ユーザ管理マネージャ。
     */
    @NonNull
    @Inject
    UserManager mUserManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        setSupportActionBar((Toolbar) findViewById(R.id.map_toolbar));

        ObjectGraph.inject(this);

        mLegendFragment = (MapLegendFragment) getFragmentManager().findFragmentById(R.id.map_legend);
        mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map_map);

        updateLayout(null);
    }

    @Override
    protected void onResume() {
        super.onResume();

        EventBus.getDefault().register(this);

        if (checkPlayServices()) {
            // GCMやアプリサーバへの登録
            registerNotification();
        }

        // GPSの利用可否チェック
        checkGpsServices();

        // データを更新する。
        refreshData();
    }

    @Override
    protected void onPause() {
        EventBus.getDefault().unregister(this);

        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            onMenuRefresh();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 最新警報情報の要求失敗イベントハンドラ。
     *
     * @param event イベント情報
     */
    public void onEvent(AlertFailedDownloadAlertEvent event) {
        showErrorDialog(0, 0, event.getError());
    }

    /**
     * 最新警報情報の更新イベントハンドラ。
     *
     * @param event イベント情報
     */
    public void onEvent(AlertUpdatedAlertEvent event) {
        // レイアウトを更新する。
        updateLayout(event.getAlert());
    }

    /**
     * 更新メニューが選択された。
     */
    private void onMenuRefresh() {
        refreshData();
    }

    /**
     * データを更新する。
     */
    private void refreshData() {
        EventBus.getDefault().post(new AlertRequestAlertEvent());
        EventBus.getDefault().post(new ShelterRequestStatusesEvent());
    }

    /**
     * 通知を登録する。
     */
    private void registerNotification() {
        AppObservable.bindActivity(this, mUserManager.registerNotificationKey(this))
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Subscriber<Void>() {
                    @Override
                    public void onCompleted() {
                        // Nothing to do
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, ExceptionUtils.getStackTrace(e));

                        showErrorDialog(R.string.dialog_title_failed, R.string.error_register_notification, e);
                    }

                    @Override
                    public void onNext(Void aVoid) {
                        // Nothing to do
                    }
                });
    }

    /**
     * 警報の内容に応じて、画面のレイアウトを変更する。
     *
     * @param alert 警報情報
     */
    private void updateLayout(@Nullable final Alert alert) {
        // 警報内容によりレイアウトを変更する。

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final View legendView = mLegendFragment.getView();
                final View mapView = mMapFragment.getView();

                if (legendView != null && mapView != null) {
                    final LinearLayout.LayoutParams legendParams = (LinearLayout.LayoutParams) legendView.getLayoutParams();
                    final LinearLayout.LayoutParams mapParams = (LinearLayout.LayoutParams) mapView.getLayoutParams();

                    // FIXME: activity_map.xmlのlayout_weightの値をここに書きたくない…。
                    if (alert != null && alert.isEvacuationSituation()) {
                        // 避難状況であければ凡例を表示する。
                        legendParams.weight = 3.0f;
                        mapParams.weight = 19.0f;
                    } else {
                        // 避難状況でなければ凡例は消す。
                        legendParams.weight = 0.0f;
                        mapParams.weight = 22.0f;
                    }

                    legendView.setLayoutParams(legendParams);
                    mapView.setLayoutParams(mapParams);
                } else {
                    // いづれかのビューが取得できない場合。想定していない。
                    if (legendView == null) {
                        Log.w(TAG, "legendView is null...");
                    }
                    if (mapView == null) {
                        Log.w(TAG, "mapView is null...");
                    }
                }
            }
        });
    }
}
