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

import android.support.annotation.NonNull;

import dagger.Module;
import dagger.Provides;
import jp.co.ipublishing.esnavi.activities.ShelterDetailActivity;
import jp.co.ipublishing.esnavi.activities.ShelterListActivity;
import jp.co.ipublishing.esnavi.dependence.shelter.ShelterImageFactory;

/**
 * 避難所関連の依存モジュール。
 */
@Module(
        injects = {
                ShelterDetailActivity.class,
                ShelterListActivity.class,
        },
        complete = false
)
public class ShelterModule {
    /**
     * ShelterImageFactoryの注入。
     *
     * @return ShelterImageFactoryインスタンス
     */
    @NonNull
    @Provides
    public jp.co.ipublishing.esnavi.factories.ShelterImageFactory provideShelterImageFactory() {
        return new ShelterImageFactory();
    }
}
