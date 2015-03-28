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

package jp.co.ipublishing.aeskit.helpers.dagger;

import android.app.Activity;
import android.app.Fragment;
import android.app.Service;
import android.content.Context;
import android.support.annotation.NonNull;

/**
 * Daggerを利用するためのユーティリティ。
 * @link <a href="https://gist.github.com/imminent/5222625">imminent / ObjectGraph.java</a>
 */
public final class ObjectGraph {
    /**
     * このクラスを利用するために必要なApplicationのinterface。
     */
    public interface ObjectGraphApplication {
        /**
         * DIする。
         * @param dependent DI対象オブジェクト
         */
        void inject(@NonNull Object dependent);
    }

    /**
     * デフォルトコンストラクタ。
     */
    private ObjectGraph() {
    }

    /**
     * ActivityにDIする。
     * @param activity DI対象Activity
     */
    public static void inject(@NonNull Activity activity) {
        ((ObjectGraphApplication) activity.getApplication()).inject(activity);
    }

    /**
     * FragmentにDIする。
     * @param fragment DI対象Fragment
     */
    public static void inject(@NonNull Fragment fragment) {
        final Activity activity = fragment.getActivity();
        if (activity == null) {
            throw new IllegalStateException("Attempting to get Activity before it has been attached to " + fragment.getClass().getName());
        }
        ((ObjectGraphApplication) activity.getApplication()).inject(fragment);
    }

    /**
     * ServiceにDIする。
     * @param service DI対象Service
     */
    public static void inject(@NonNull Service service) {
        ((ObjectGraphApplication) service.getApplication()).inject(service);
    }

    /**
     * オブジェクトにDIする。
     * @param context コンテキスト
     * @param object DI対象オブジェクト
     */
    public static void inject(@NonNull Context context, @NonNull Object object) {
        ((ObjectGraphApplication) context.getApplicationContext()).inject(object);
    }
}
