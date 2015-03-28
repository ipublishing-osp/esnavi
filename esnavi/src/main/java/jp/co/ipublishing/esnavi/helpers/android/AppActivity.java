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

package jp.co.ipublishing.esnavi.helpers.android;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.apache.commons.lang3.exception.ExceptionUtils;

import jp.co.ipublishing.esnavi.activities.MapActivity;

/**
 * 共通の親Activityクラス。
 */
public abstract class AppActivity extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "AppActivity";

    /**
     * ダイアログヘルパー。
     */
    @NonNull
    private final DialogHelper mDialogHelper = new DialogHelper();

    /**
     * GoogleAPIのクライアント。
     */
    @NonNull
    private GoogleApiClient mGoogleApiClient;

    /**
     * GoogleAPIクライアントを取得する。
     * @return GoogleAPIクライアント
     */
    @NonNull
    protected GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();

        if (id == android.R.id.home) {
            final Intent intent = new Intent(this, MapActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * エラーダイアログを表示する。
     *
     * @param titleResId   タイトルのリソースID
     * @param messageResId 本文のリソースID
     * @param error        例外オブジェクト
     */
    protected void showErrorDialog(@StringRes int titleResId, @StringRes int messageResId, @Nullable Throwable error) {
        mDialogHelper.showErrorDialog(this, getFragmentManager(), titleResId, messageResId, error);
    }

    /**
     *
     */
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    /**
     * GooglePlayServicesが利用できるかをチェックする。
     *
     * @return GooglePlay Servicesが利用できればtrue
     */
    protected boolean checkPlayServices() {
        final int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.w(TAG, "This device is not supported.");
                // 終わらせる。
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * GPSが利用できるかをチェックする。
     *
     * @return GPSが利用できればtrue
     */
    protected boolean checkGpsServices() {
        // TODO: GoogleApiClientがやってくれていないかを確認する。
        return true;
    /*
    final LocationManager locationManager = getLocationManager();

    if (locationManager == null || !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
      new MaterialDialog.Builder(this)
          .title(R.string.dialog_title_waring)
          .content(R.string.error_disabled_gps)
          .positiveText(R.string.dialog_button_yes)
          .negativeText(R.string.dialog_button_no)
          .callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog dialog) {
              final Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
              startActivity(callGPSSettingIntent);
            }
          })
          .show();
      return false;
    }
    return true;*/
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    private final static int GOOGLE_API_RESOLUTION_REQUEST = 9001;

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        try {
            connectionResult.startResolutionForResult(this, GOOGLE_API_RESOLUTION_REQUEST);
        } catch (IntentSender.SendIntentException e) {
            Log.e(TAG, ExceptionUtils.getStackTrace(e));

            // TODO: エラーを表示する？
        }
    }
}
