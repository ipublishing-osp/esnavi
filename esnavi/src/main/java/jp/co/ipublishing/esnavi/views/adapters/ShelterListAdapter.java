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

package jp.co.ipublishing.esnavi.views.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.lang3.exception.ExceptionUtils;

import jp.co.ipublishing.aeskit.shelter.models.Shelter;
import jp.co.ipublishing.esnavi.R;
import jp.co.ipublishing.esnavi.factories.ShelterImageFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 避難所一覧のAdapter。
 */
public class ShelterListAdapter extends ArrayAdapter<Shelter> {
    private static final String TAG = "ShelterListAdapter";

    private class ViewHolder {
        /**
         * サムネイルビュー。
         */
        @NonNull
        private final ImageView mThumbnailView;

        /**
         * 名前ビュー。
         */
        @NonNull
        private final TextView mNameTextView;

        /**
         * コンストラクタ。
         *
         * @param view ListItemのビュー。
         */
        public ViewHolder(@NonNull View view) {
            mThumbnailView = (ImageView) view.findViewById(R.id.thumbnail);
            mNameTextView = (TextView) view.findViewById(R.id.name);
        }

        /**
         * データをバインドする。
         *
         * @param shelter 避難所情報
         */
        public void bind(@NonNull Shelter shelter) {
            mNameTextView.setText(shelter.getName());

            getThumbnail(shelter.getId())
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<Bitmap>() {
                        @Override
                        public void onCompleted() {
                            // Nothing to do
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e(TAG, ExceptionUtils.getStackTrace(e));
                        }

                        @Override
                        public void onNext(Bitmap bitmap) {
                            mThumbnailView.setImageBitmap(bitmap);
                        }
                    });
        }

        @NonNull
        private Observable<Bitmap> getThumbnail(final int shelterId) {
            return Observable.create(new Observable.OnSubscribe<Bitmap>() {
                @Override
                public void call(Subscriber<? super Bitmap> subscriber) {
                    subscriber.onNext(mShelterImageFactory.getThumbnail(getContext(), shelterId));
                    subscriber.onCompleted();
                }
            });
        }

    }

    @NonNull
    private final ShelterImageFactory mShelterImageFactory;

    /**
     * コンストラクタ。
     *
     * @param context  コンテキスト
     * @param resource リソースID
     */
    public ShelterListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull ShelterImageFactory shelterImageFactory) {
        super(context, resource);

        mShelterImageFactory = shelterImageFactory;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(getContext(), R.layout.list_item_shelter, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Shelter shelter = getItem(position);

        holder.bind(shelter);

        return convertView;
    }
}
