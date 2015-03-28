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

package jp.co.ipublishing.esnavi.dependence.moduels;

import android.content.Context;
import android.support.annotation.NonNull;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import jp.co.ipublishing.aeskit.alert.AlertManager;
import jp.co.ipublishing.aeskit.alert.AlertService;
import jp.co.ipublishing.aeskit.shelter.ShelterManager;
import jp.co.ipublishing.aeskit.shelter.ShelterService;
import jp.co.ipublishing.aeskit.user.UserManager;
import jp.co.ipublishing.esnavi.activities.MapActivity;
import jp.co.ipublishing.esnavi.activities.ShelterDetailActivity;
import jp.co.ipublishing.esnavi.activities.ShelterListActivity;
import jp.co.ipublishing.esnavi.fragments.MapFragment;
import jp.co.ipublishing.esnavi.impl.alert.AlertClient;
import jp.co.ipublishing.esnavi.impl.alert.AlertStore;
import jp.co.ipublishing.esnavi.impl.shelter.ShelterClient;
import jp.co.ipublishing.esnavi.impl.shelter.ShelterStore;
import jp.co.ipublishing.esnavi.impl.user.UserClient;
import jp.co.ipublishing.esnavi.dependence.Config;
import jp.co.ipublishing.esnavi.dependence.alert.AlertApi;
import jp.co.ipublishing.esnavi.dependence.gcm.GcmIntentService;
import jp.co.ipublishing.esnavi.dependence.shelter.ShelterApi;
import jp.co.ipublishing.esnavi.dependence.shelter.ShelterDbOpenHelper;
import jp.co.ipublishing.esnavi.dependence.user.UserApi;

/**
 * 管理クラス関連の依存モジュール。
 */
@Module(
        injects = {
                AlertService.class,
                ShelterService.class,
                MapActivity.class,
                ShelterDetailActivity.class,
                ShelterListActivity.class,
                MapFragment.class,
                GcmIntentService.class,
        },
        complete = false
)
public class ManagerModule {
    /**
     * コンテキスト。
     */
    @NonNull
    private final Context mContext;

    /**
     * コンストラクタ。
     *
     * @param context コンテキスト
     */
    public ManagerModule(@NonNull Context context) {
        mContext = context;
    }

    /**
     * AlertManagerインスタンスの注入。
     *
     * @return AlertManagerインスタンス
     */
    @NonNull
    @Provides
    @Singleton
    public AlertManager provideAlertManager() {
        return new AlertManager(
                new AlertStore(mContext), // ストア
                new AlertClient(new AlertApi()) // クライアント
        );
    }

    /**
     * ShelterManagerインスタンスの注入。
     *
     * @return ShelterManagerインスタンス
     */
    @NonNull
    @Provides
    @Singleton
    public ShelterManager provideShelterManager() {
        return new ShelterManager(
                new ShelterStore(new ShelterDbOpenHelper(mContext)), // ストア
                new ShelterClient(new ShelterApi()) // クライアント
        );
    }

    /**
     * UserManagerインスタンスの注入。
     *
     * @return UserManagerインスタンス
     */
    @NonNull
    @Provides
    @Singleton
    public UserManager provideUserManager() {
        return new UserManager(
                new UserClient(mContext, new UserApi()),
                Config.GCM_SENDER_ID
        );
    }
}
