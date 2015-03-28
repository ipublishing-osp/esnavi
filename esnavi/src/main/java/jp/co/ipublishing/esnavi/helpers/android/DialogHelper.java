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

import android.app.FragmentManager;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import com.afollestad.materialdialogs.MaterialDialog;

import jp.co.ipublishing.esnavi.R;

/**
 * ダイアログを簡単に使用するためのヘルパー。
 */
public class DialogHelper {
    /**
     * エラーダイアログを表示する。
     * OKボタンのみ。
     *
     * @param context         コンテキスト
     * @param fragmentManager フラグメントマネージャー
     * @param titleResId      タイトルのリソースID
     * @param messageResId    本文のリソースID
     * @param error           例外オブジェクト
     */
    public void showErrorDialog(Context context, FragmentManager fragmentManager, @StringRes int titleResId, @StringRes int messageResId, @Nullable Throwable error) {
        new MaterialDialog.Builder(context)
                .title(titleResId)
                .content(messageResId)
                .positiveText(R.string.dialog_button_ok)
                .show();
    }

    /**
     * ダイアログを表示する。
     * OKボタンのみ。
     *
     * @param context         コンテキスト
     * @param fragmentManager フラグメントマネージャー
     * @param titleResId      タイトルのリソースID
     * @param messageResId    本文のリソースID
     */
    public void showDialog(Context context, FragmentManager fragmentManager, @StringRes int titleResId, @StringRes int messageResId) {
        new MaterialDialog.Builder(context)
                .title(titleResId)
                .content(messageResId)
                .positiveText(R.string.dialog_button_ok)
                .show();
    }

}
