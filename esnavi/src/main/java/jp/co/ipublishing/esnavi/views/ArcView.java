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

package jp.co.ipublishing.esnavi.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;

import jp.co.ipublishing.esnavi.R;

/**
 * 弧を表示するカスタムビュー.
 */
public class ArcView extends View {
    @NonNull
    private final Paint mBackPaint = new Paint();

    @NonNull
    private final Paint mFillPaint = new Paint();

    @NonNull
    private final Paint mValuePaint = new Paint();

    @NonNull
    private final Paint mTextPaint = new Paint();

    /**
     * グラフのパディング。
     */
    private float mPadding;

    /**
     * 円弧の幅。
     */
    private float mStrokeWidth;

    /**
     * 収容率。
     */
    private int mPercent = 83;

    /**
     * 収容率を設定する。
     *
     * @param percent 収容率
     */
    public void setPercent(int percent) {
        mPercent = percent;
    }

    /**
     * コンストラクタ。
     *
     * @param context コンテキスト
     */
    public ArcView(Context context) {
        super(context);
    }

    /**
     * コンストラクタ。
     *
     * @param context コンテキスト
     * @param attrs   属性
     */
    public ArcView(Context context, AttributeSet attrs) {
        super(context, attrs);

        final TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.ArcView);

        setBackgroundColor(getResources().getColor(android.R.color.transparent));

        mBackPaint.setAntiAlias(true);
        mBackPaint.setColor(attributes.getColor(R.styleable.ArcView_arc_background, 0xFFCCCCCC));

        mFillPaint.setAntiAlias(true);
        mFillPaint.setColor(attributes.getColor(R.styleable.ArcView_arc_gaugeFillColor, 0xFFCCCCCC));

        mValuePaint.setAntiAlias(true);
        mValuePaint.setColor(attributes.getColor(R.styleable.ArcView_arc_gaugeValueColor, 0xFFCCCCCC));

        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(attributes.getColor(R.styleable.ArcView_arc_gaugeFillColor, 0xFFCCCCCC));
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(attributes.getDimension(R.styleable.ArcView_arc_textSize, 10.0f));

        mStrokeWidth = attributes.getDimension(R.styleable.ArcView_arc_strokeWidth, 10.0f);
        mPadding = attributes.getDimension(R.styleable.ArcView_arc_padding, 8.0f);

        attributes.recycle();
    }

    /**
     * Canvasのbounds。
     */
    @NonNull
    private final Rect mClip = new Rect();

    /**
     * 円弧描画用のrect。
     */
    @NonNull
    private final RectF mArcRect = new RectF();

    private final Rect mTextBounds = new Rect();

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.getClipBounds(mClip);

        final float centerX = mClip.centerX();
        final float centerY = mClip.centerY();
        final float length = Math.min(mClip.width(), mClip.height());

        // 白背景を描画する。
        canvas.drawCircle(centerX, centerY, length / 2.0f, mBackPaint);

        // 灰色の円を描画する。
        canvas.drawCircle(centerX, centerY, length / 2.0f - mPadding, mFillPaint);

        // パーセンテージに合わせて円(弧)を描画する
        if (mPercent != -1) {
            final float arcRadius = length / 2.0f - mPadding;
            final float left = centerX - arcRadius;
            final float top = centerY - arcRadius;
            final float right = centerX + arcRadius;
            final float bottom = centerY + arcRadius;

            mArcRect.set(left, top, right, bottom);

            final int angle = (int) (360.0f * 0.01f * mPercent);
            canvas.drawArc(mArcRect, -90.0f, angle, true, mValuePaint);
        }

        // 真ん中の白円を描画する。
        canvas.drawCircle(centerX, centerY, length / 2.0f - mPadding - mStrokeWidth, mBackPaint);

        // パーセンテージを描画する。
        {
            final String text;

            if (mPercent == -1) {
                text = getResources().getString(R.string.default_percent);
            } else {
                text = String.format(getResources().getString(R.string.format_percent), mPercent);
            }

            mTextPaint.getTextBounds(text, 0, text.length(), mTextBounds);

            canvas.drawText(text, centerX, centerY + (mTextBounds.bottom - mTextBounds.top) / 2.0f, mTextPaint);
        }
    }
}
