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

package jp.co.ipublishing.esnavi.dependence.shelter;

import android.content.Context;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/**
 * 避難所DBのOpenHelper。
 *
 * DBの構成を変えた場合は以下のサイトを参考にしてファイルを配置する。
 * https://github.com/jgilfelt/android-sqlite-asset-helper
 */
public class ShelterDbOpenHelper extends SQLiteAssetHelper {
    /**
     * データベースファイル名。
     */
    private static final String DATABASE_NAME = "hinan.db";

    /**
     * データベースのバージョン。
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * コンストラクタ。
     *
     * @param context コンテキスト
     */
    public ShelterDbOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
}
