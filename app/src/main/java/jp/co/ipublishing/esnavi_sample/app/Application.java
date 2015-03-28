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

package jp.co.ipublishing.esnavi_sample.app;

import android.content.Intent;
import android.support.annotation.NonNull;

import jp.co.ipublishing.aeskit.alert.AlertService;
import jp.co.ipublishing.aeskit.helpers.dagger.ObjectGraph;
import jp.co.ipublishing.aeskit.shelter.ShelterService;
import jp.co.ipublishing.esnavi.dependence.moduels.ManagerModule;
import jp.co.ipublishing.esnavi.dependence.moduels.MapModule;
import jp.co.ipublishing.esnavi.dependence.moduels.ShelterModule;

/**
 * アプリケーションクラス。
 */
public class Application extends android.app.Application implements ObjectGraph.ObjectGraphApplication {
    /**
     * オブジェクト・グラフ。
     * 詳細はDaggerのマニュアルを参照。
     */
    private dagger.ObjectGraph mObjectGraph;

    @Override
    public void onCreate() {
        super.onCreate();

        // 依存するModuleを追加したら、ここにも追記する。
        mObjectGraph = dagger.ObjectGraph.create(new ManagerModule(this), new ShelterModule(), new MapModule());

        startService();
    }

    @Override
    public void inject(@NonNull Object dependent) {
        mObjectGraph.inject(dependent);
    }

    /**
     * サービスを起動する。
     */
    private void startService() {
        startService(new Intent(this, AlertService.class));
        startService(new Intent(this, ShelterService.class));
    }
}
