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

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.greenrobot.event.EventBus;
import icepick.Icepick;
import icepick.Icicle;
import jp.co.ipublishing.aeskit.alert.events.AlertUpdatedAlertEvent;
import jp.co.ipublishing.aeskit.alert.models.Alert;
import jp.co.ipublishing.esnavi.R;
import jp.co.ipublishing.esnavi.helpers.android.AppFragment;
import jp.co.ipublishing.esnavi.views.MarqueeView;

/**
 * 地図の凡例。
 */
public class MapLegendFragment extends AppFragment {
    /**
     * メッセージのビュー。
     */
    @NonNull
    private MarqueeView mMessageView;

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
        final View view = inflater.inflate(R.layout.fragment_map_legend, container, false);

        mMessageView = (MarqueeView) view.findViewById(R.id.legend_message);

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

        // メッセージの移動を停止
        mMessageView.clearMarquee();

        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Icepick.saveInstanceState(this, outState);
    }

    /**
     * モデル更新イベントハンドラ。
     *
     * @param event イベント情報
     */
    public void onEvent(AlertUpdatedAlertEvent event) {
        mAlert = event.getAlert();

        updateView();
    }

    /**
     * マーキーを開始できるかどうか。
     *
     * @param alert 警報情報
     * @return マーキーを開始できるならtrue。
     */
    private boolean canStartMarquee(@Nullable Alert alert) {
        return alert != null && alert.isEvacuationSituation();
    }

    /**
     * ビューを更新する。
     */
    private void updateView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (canStartMarquee(mAlert)) {
                    mMessageView.startMarquee();
                } else {
                    mMessageView.clearMarquee();
                }
            }
        });
    }
}
