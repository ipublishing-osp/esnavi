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

package jp.co.ipublishing.esnavi.impl.user;

import android.support.annotation.NonNull;

import jp.co.ipublishing.esnavi.helpers.network.ApiMethod;

/**
 * ユーザ関連のAPIインターフェース。
 *
 * @see jp.co.ipublishing.esnavi.impl.user.UserClient
 */
public interface UserApi {
    /**
     * 通知用のキーを登録するAPI情報を取得する。
     *
     * @return 通知用のキーを登録するAPI情報
     * @see UserClient#registerNotificationKey(String)
     */
    @NonNull
    ApiMethod registerNotificationKey();
}
