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

package jp.co.ipublishing.esnavi.dependence.map;

import com.google.android.gms.maps.model.CameraPosition;

import jp.co.ipublishing.esnavi.fragments.MapFragment;
import jp.co.ipublishing.esnavi.dependence.Config;

/**
 * 地図の設定。
 */
public class MapSettings implements MapFragment.MapSettings {
    @Override
    public CameraPosition getDefaultCameraPosition() {
        return Config.DEFAULT_MAP_POSITION;
    }
}
