# ESNavi
====

[かなざわ避難支援ナビ](https://play.google.com/store/apps/details?id=jp.ipublishing.hinan_navi_android&hl=ja)をオープンソースにしたものです。

## Description

GooglePlay で公開中のかなざわ避難支援ナビのソースコード一式です。少しの修正で他地域の避難支援ナビを作成することができます。

## Requirement

### 動作環境
* Android 4.0 以降

### 開発環境
* Java8 以降
* Android SDK 21 以降
* Android Studio

## Getting Started

プログラムをそのまま使用することを想定していません。以下を参考に、プログラムの修正やサーバ環境の構築を行って下さい。

### 1. GCM送信者IDを取得する。

下記ページを参考にして、GCM送信者ID(プロジェクト番号)を取得して下さい。

[Google Cloud メッセージング（GCM）の使用](https://support.google.com/googleplay/android-developer/answer/2663268?hl=ja)

### 2. Google Maps APIキーを取得する。

下記ページを参考にして、Google Maps APIキーを取得して下さい。リリース用と開発用の2種類が必要になります。

[Obtain a Google Maps API key](https://developers.google.com/maps/documentation/android/start#obtain_a_google_maps_api_key)

### 3. サーバAPI仕様書に従い、サーバAPIを作成する。

### 4. GCMによるメッセージ送信プログラムを作成する。

### 5. 本プログラム(ESNavi)をcloneする。

`git clone httsp://github.com/ipublishing-osp/esnavi.git`

### 6. appモジュールのjp.co.ipublishing.esnavi_sampleを適切な固有のパッケージ名に変更する。

※jp.co.ipublishing.esnavi_sampleからjp.co.foo.barに変更するとする。

* app/src/main/java/jp/co/ipublishing/esnavi_sample/Application.javaのパッケージ名を変更する。
* 以下のファイルの中に記述されているパッケージ名を変更する。
    * app/src/main/AndroidManifest.xml
    * app/src/release/AndroidManifest.xml
    * app/src/debug/AndroidManifest.xml

### 7. アプリ固有の情報を編集する。
* app/src/main/java/jp/co/ipublishing/esnavi/dependence/Config.java の各定数の値を設定する。
* app/src/main/res/values/strings.xml の app_name にアプリ名を設定する。
* app/src/debug/AndroidManifest.xml の com.google.android.maps.v2.API_KEY の値に 開発用のGoogle Maps APIキーを設定する。
* app/src/release/AndroidManifest.xml の com.google.android.maps.v2.API_KEY の値に リリース用のGoogle Maps APIキーを設定する。

### 8. 避難所のデータ(データベース、写真)を更新する。

* データベース(app/src/main/assets/databases/hinan.db)
    * データベース定義を参考にして下さい。
* 写真(app/src/main/assets/shelter_images/) ※ファイル名は避難所ID。

## Contribution

1. まずforkして下さい。
2. featureブランチを作って下さい。 `git checkout -b my-new-feature`
3. 変更をcommitして下さい。 `git commit -am 'Add some feature'`
4. ブランチをpushして下さい。 `git push origin my-new-feature`
5. プルリクエストを送って下さい！

## History

### v1.0.0
* ファーストリリース。

## To Do
* 警報情報以外の情報を通知できる。
* Activityからロジックを切り離す。
* コア部分(AESKit)の汎用化。
* テストコード

## Libraries

* Material Design Icons - <http://google.github.io/material-design-icons/>
* Apache Commons Lang - <https://commons.apache.org/proper/commons-lang/>
* RxJava - <https://github.com/ReactiveX/RxJava/>
* RxAndroid - <https://github.com/ReactiveX/RxAndroid/>
* EventBus - <https://github.com/greenrobot/EventBus/>
* Dagger - <http://square.github.io/dagger/>
* OkHttp - <http://square.github.io/okhttp/>
* Icepick - <https://github.com/frankiesardo/icepick/>
* Material Dialogs - <https://github.com/afollestad/material-dialogs/>
* Android SQLiteAssetHelper - <http://jgilfelt.github.io/android-sqlite-asset-helper/>

## Licence

[Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0)

    Copyright 2012 Square, Inc.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

## Authors

* Nao Sekimori / iPublishing Co., Ltd.
* [Yukimune Takagi](https://github.com/yukimunet) / iPublishing Co., Ltd.

