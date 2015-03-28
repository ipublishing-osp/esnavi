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

package jp.co.ipublishing.aeskit.alert.models;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import jp.co.ipublishing.aeskit.alert.models.figure.Figure;

/**
 * 警報情報インターフェース。
 */
public interface Alert extends Serializable {
    /**
     * 警報レベルを取得する。
     *
     * @return 警報レベル
     */
    @NonNull
    AlertLevel getLevel();

    /**
     * 発生したエリア名を取得する。
     *
     * @return 警報が発生したエリア名
     */
    @NonNull
    String getArea();

    /**
     * メッセージの件名を取得する。
     *
     * @return 警報メッセージの件名
     */
    @NonNull
    String getMessageTitle();

    /**
     * メッセージの本文を取得する。
     *
     * @return 警報メッセージの本文
     */
    @NonNull
    String getMessageBody();

    /**
     * ヘッドラインの本文を取得する。
     *
     * @return 警報ヘッドラインの本文
     */
    @NonNull
    String getHeadlineBody();

    /**
     * 発生時刻を取得する。
     *
     * @return 警報の発生時刻
     */
    @NonNull
    Date getTime();

    /**
     * 危険区域を取得する。
     *
     * @return 危険区域
     */
    @NonNull
    List<Figure> getWarningAreas();

    /**
     * 避難状況かどうかを取得する。
     *
     * @return 避難状況であればtrue、そうでなければfalseを返す。
     */
    boolean isEvacuationSituation();
}
