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

package jp.co.ipublishing.esnavi.helpers.android;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

/**
 * 共通の親Fragmentクラス。
 */
public class AppFragment extends Fragment {
    /**
     * メインハンドラー。
     */
    @NonNull
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    /**
     * UIスレッド。
     */
    @NonNull
    private final Thread mUiThread = Thread.currentThread();

    /**
     * ダイアログヘルパー。
     */
    @NonNull
    private final DialogHelper mDialogHelper = new DialogHelper();

    /**
     * UIスレッドで実行する。
     *
     * @param action UIスレッドで実行したい処理
     */
    protected final void runOnUiThread(@NonNull Runnable action) {
        if (Thread.currentThread() != mUiThread) {
            mHandler.post(action);
        } else {
            action.run();
        }
    }

    /**
     * エラーダイアログを表示する。
     *
     * @param titleResId   タイトルのリソースID
     * @param messageResId 本文のリソースID
     * @param error        例外オブジェクト
     */
    protected void showErrorDialog(@StringRes int titleResId, @StringRes int messageResId, @Nullable Throwable error) {
        final Activity activity = getActivity();
        final FragmentManager fragmentManager = getFragmentManager();

        if (activity != null && fragmentManager != null) {
            mDialogHelper.showErrorDialog(activity, fragmentManager, titleResId, messageResId, error);
        }
    }

    /**
     * ダイアログを表示する。
     *
     * @param titleResId   タイトルのリソースID
     * @param messageResId 本文のリソースID
     */
    protected void showDialog(@StringRes int titleResId, @StringRes int messageResId) {
        final Activity activity = getActivity();
        final FragmentManager fragmentManager = getFragmentManager();

        if (activity != null && fragmentManager != null) {
            mDialogHelper.showDialog(activity, fragmentManager, titleResId, messageResId);
        }
    }
}
