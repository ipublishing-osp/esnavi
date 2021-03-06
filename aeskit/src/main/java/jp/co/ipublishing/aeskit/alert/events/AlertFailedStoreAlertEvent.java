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

package jp.co.ipublishing.aeskit.alert.events;

import android.support.annotation.NonNull;

import jp.co.ipublishing.aeskit.helpers.eventbus.FailedEvent;

/**
 * 警報情報の保存失敗のイベント。
 * 警報情報の保存に失敗した際に発生する。
 */
public class AlertFailedStoreAlertEvent extends FailedEvent {

    /**
     * コンストラクタ。
     *
     * @param error エラー情報
     */
    public AlertFailedStoreAlertEvent(@NonNull Throwable error) {
        super(error);
    }
}
