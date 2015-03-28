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

package jp.co.ipublishing.esnavi.impl.alert;

import android.support.annotation.NonNull;

import java.util.Date;
import java.util.List;

import jp.co.ipublishing.aeskit.alert.models.AlertLevel;
import jp.co.ipublishing.aeskit.alert.models.figure.Figure;

/**
 * 警報の実装。
 */
public class Alert implements jp.co.ipublishing.aeskit.alert.models.Alert {
    /**
     * 警報レベル。
     */
    private AlertLevel mLevel;

    /**
     * 発生エリア。
     */
    private String mArea;

    /**
     * メッセージタイトル。
     */
    private String mMessageTitle;

    /**
     * メッセージ本文。
     */
    private String mMessageBody;

    /**
     * ヘッドライン本文。
     */
    private String mHeadlineBody;

    /**
     * 発生日時。
     */
    private Date mTime;

    /**
     * 危険区域。
     */
    private List<Figure> mWarningAreas;

    @NonNull
    @Override
    public AlertLevel getLevel() {
        return mLevel;
    }

    public void setLevel(@NonNull AlertLevel level) {
        mLevel = level;
    }

    @NonNull
    @Override
    public String getArea() {
        return mArea;
    }

    public void setArea(@NonNull String area) {
        mArea = area;
    }

    @NonNull
    @Override
    public String getMessageTitle() {
        return mMessageTitle;
    }

    public void setMessageTitle(@NonNull String messageTitle) {
        mMessageTitle = messageTitle;
    }

    @NonNull
    @Override
    public String getMessageBody() {
        return mMessageBody;
    }

    public void setMessageBody(@NonNull String messageBody) {
        mMessageBody = messageBody;
    }

    @NonNull
    @Override
    public String getHeadlineBody() {
        return mHeadlineBody;
    }

    public void setHeadlineBody(@NonNull String headlineBody) {
        mHeadlineBody = headlineBody;
    }

    @NonNull
    @Override
    public Date getTime() {
        return mTime;
    }

    public void setTime(@NonNull Date time) {
        mTime = time;
    }

    @NonNull
    @Override
    public List<Figure> getWarningAreas() {
        return mWarningAreas;
    }

    public void setWarningAreas(@NonNull List<Figure> warningAreas) {
        mWarningAreas = warningAreas;
    }

    @Override
    public boolean isEvacuationSituation() {
        // 緊急時に避難状況とする。
        return mLevel == AlertLevel.Emergency;
    }
}
