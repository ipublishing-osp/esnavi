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

import jp.co.ipublishing.esnavi.helpers.network.ApiMethod;

/**
 * 警報関係のAPIインターフェース。
 *
 * @see jp.co.ipublishing.esnavi.impl.alert.AlertClient
 */
public interface AlertApi {
    /**
     * 警報情報ダウンロードのAPI情報を取得する。
     *
     * @return 警報情報ダウンロードのAPI情報
     * @see AlertClient#downloadAlert()
     */
    @NonNull
    ApiMethod downloadAlert();
}
