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

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Date;

import de.greenrobot.event.EventBus;
import icepick.Icepick;
import icepick.Icicle;
import jp.co.ipublishing.aeskit.alert.events.AlertUpdatedAlertEvent;
import jp.co.ipublishing.aeskit.alert.models.Alert;
import jp.co.ipublishing.aeskit.alert.models.AlertLevel;
import jp.co.ipublishing.esnavi.R;
import jp.co.ipublishing.esnavi.helpers.android.AppFragment;
import jp.co.ipublishing.esnavi.views.MarqueeView;

/**
 * 警報・注意報を表示する。
 */
public class AlertFragment extends AppFragment {
    /**
     * 全体のレイアウト。
     */
    @NonNull
    private LinearLayout mMainArea;

    /**
     * メッセージタイトルのビュー。
     */
    @NonNull
    private TextView mMessageTitleView;

    /**
     * メッセージ本文のビュー。
     */
    @NonNull
    private TextView mMessageBodyView;

    /**
     * ヘッドラインタイトルのビュー。
     */
    @NonNull
    private TextView mHeadlineTitleView;

    /**
     * ヘッドライン本文のビュー。
     */
    @NonNull
    private MarqueeView mHeadlineBodyView;

    /**
     * 最寄りの避難所ボタン(レイアウト)。
     */
    @NonNull
    private LinearLayout mNearShelterButton;

    /**
     * 警報情報。
     */
    @Nullable
    @Icicle
    public Alert mAlert;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Icepick.restoreInstanceState(this, savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_alert, container, false);

        mMainArea = (LinearLayout) view.findViewById(R.id.alert_main_area);
        mMessageTitleView = (TextView) view.findViewById(R.id.alert_message_title);
        mMessageBodyView = (TextView) view.findViewById(R.id.alert_message_body);
        mHeadlineTitleView = (TextView) view.findViewById(R.id.alert_headline_title);
        mHeadlineBodyView = (MarqueeView) view.findViewById(R.id.alert_headline_body);
        mNearShelterButton = (LinearLayout) view.findViewById(R.id.alert_near_shelter);

        view.findViewById(R.id.alert_near_shelter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickNearShelter();
            }
        });

        updateView();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);

        mHeadlineBodyView.clearMarquee();

        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Icepick.saveInstanceState(this, outState);
    }

    /**
     * 「最寄りの避難所へ」ボタンタップハンドラ。
     */
    private void onClickNearShelter() {
        // 最寄りの避難所の詳細画面を表示する。
        EventBus.getDefault().post(new MapFragment.ShelterRequestShowNearEvent());
    }

    /**
     * 警報情報更新イベントハンドラ。
     *
     * @param event イベント情報
     */
    public void onEvent(AlertUpdatedAlertEvent event) {
        mAlert = event.getAlert();

        updateView();
    }

    /**
     * ビューを更新する。
     */
    private void updateView() {
        // TODO: 最寄り避難所ボタンが押せないときは、レイアウトを変更させたい。

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mAlert != null) {
                    // 警報情報がある場合
                    mMainArea.setBackgroundColor(getResources().getColor(getMainAreaBackgroundColorResourceID(mAlert.getLevel())));
                    mMessageTitleView.setText(mAlert.getMessageTitle());
                    mMessageBodyView.setText(mAlert.getMessageBody());
                    mHeadlineTitleView.setText(getHeadlineTitleText(AlertFragment.this, mAlert.getArea(), mAlert.getTime()));
                    mHeadlineBodyView.setText(mAlert.getHeadlineBody());
                    mNearShelterButton.setVisibility(mAlert.isEvacuationSituation() ? View.VISIBLE : View.INVISIBLE);
                    mHeadlineBodyView.startMarquee();
                } else {
                    // 警報情報がない場合は、デフォルト値を設定する。
                    mMainArea.setBackgroundColor(getResources().getColor(getMainAreaBackgroundColorResourceID(AlertLevel.None)));
                    mMessageTitleView.setText(R.string.no_alert_message_title);
                    mMessageBodyView.setText(R.string.no_alert_message_body);
                    mHeadlineTitleView.setText("");
                    mHeadlineBodyView.setText("");
                    mNearShelterButton.setVisibility(View.INVISIBLE);
                    mHeadlineBodyView.startMarquee();
                }
            }
        });
    }

    /**
     * 警報レベルに応じた警報エリアの背景色のリソースIDを取得する。
     *
     * @param level 警報レベル
     * @return 背景色のリソースID
     */
    @ColorRes
    private static int getMainAreaBackgroundColorResourceID(@NonNull AlertLevel level) {
        final int colorId;
        switch (level) {
            case None:
                colorId = R.color.grey800;
                break;
            case Advisory:
                colorId = R.color.grey800;
                break;
            case Warning:
                colorId = R.color.orange500;
                break;
            case Emergency:
                colorId = R.color.deeporange600;
                break;
            default:
                colorId = R.color.grey800;
                break;
        }
        return colorId;
    }

    /**
     * ヘッドラインのタイトルを取得する。
     *
     * @param fragment フラグメント
     * @param areaName 警報エリア名
     * @param time     警報発生時刻
     * @return ヘッドラインのテキスト
     */
    @NonNull
    private static String getHeadlineTitleText(@NonNull Fragment fragment, @NonNull String areaName, @NonNull Date time) {
        return String.format("%s - %s", areaName, getFormattedElapsedTime(fragment, time));
    }

    /**
     * 表示形式に変換された発生経過時間を取得する。
     *
     * @param fragment フラグメント
     * @param time     警報発生時刻
     * @return 表示形式に変換された発生経過時間
     */
    @NonNull
    private static String getFormattedElapsedTime(@NonNull Fragment fragment, @NonNull Date time) {
        final long currentTime = new Date(System.currentTimeMillis()).getTime();
        final long targetTime = time.getTime();
        final long elapsedTime = (currentTime - targetTime) / 1000;

        final String formattedElapsedTime;

        if ((elapsedTime / 60) < 1) {
            // 1分未満
            formattedElapsedTime = String.format(fragment.getString(R.string.elapsed_time_format_seconds), elapsedTime);
        } else if ((elapsedTime / 60) < 60) {
            // 1時間未満
            formattedElapsedTime = String.format(fragment.getString(R.string.elapsed_time_format_minutes), elapsedTime / 60);
        } else if ((elapsedTime / 60 / 60) < 24) {
            // 1日未満
            formattedElapsedTime = String.format(fragment.getString(R.string.elapsed_time_format_hours), elapsedTime / 60 / 60);
        } else if ((elapsedTime / 60 / 60 / 24) < 3) {
            // 3日未満
            formattedElapsedTime = String.format(fragment.getString(R.string.elapsed_time_format_days), elapsedTime / 60 / 60 / 24);
        } else {
            // 3日以上
            formattedElapsedTime = fragment.getString(R.string.elapsed_time_format_over_three_days);
        }

        return formattedElapsedTime;
    }
}
