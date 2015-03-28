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
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import org.apache.commons.lang3.exception.ExceptionUtils;

import jp.co.ipublishing.esnavi.R;

/**
 * テキストのマーキー表示を行うカスタムビュー。
 */
public class MarqueeView extends View {
    private static final String TAG = "MarqueeView";

    @NonNull
    private final Paint mTextPaint = new Paint();

    @NonNull
    private String mText = "";

    private boolean mRunning = true;

    private int mLastX;

    /**
     * リピートした回数
     */
    private int mRepeatCount = 0;

    /**
     * 最大リピート回数。
     */
    private int mRepeatLimit = 1;

    /**
     * 現在のテキストの位置。
     */
    private int mCurrentX;

    /**
     * 1フレームで動く距離。
     */
    private int mTextMoveSpeed = 5;

    /**
     * マーキー表示処理(テキストの移動＋表示)。
     */
    private final Runnable runnable = new Runnable() {
        public void run() {
            // 左端と判断するX座標
            mLastX = getLastX();

            while (mRepeatCount < mRepeatLimit || mRepeatLimit < 0) {
                mCurrentX = getMarqueeStartX(); // テキスト位置を戻す

                final int fps = 30;
                final long frameTime = 1000 / fps;

                long beforeTime = System.currentTimeMillis();
                long afterTime;

                // 1回のマーキー処理
                while (mRunning) {
                    // 左端まで到達したらリピート1回としてカウント
                    if (mCurrentX <= mLastX) {
                        mRepeatCount += 1;
                        break;
                    }

                    mCurrentX -= mTextMoveSpeed;
                    postInvalidate();

                    afterTime = System.currentTimeMillis();
                    final long pastTime = afterTime - beforeTime;
                    final long sleepTime = frameTime - pastTime;

                    if (sleepTime > 0) {
                        try {
                            Thread.sleep(sleepTime);
                        } catch (Exception e) {
                            Log.e(TAG, ExceptionUtils.getStackTrace(e));
                        }
                    }
                    beforeTime = System.currentTimeMillis();
                }
            }
        }
    };

    /**
     * テキストを移動させるスレッド
     */
    @Nullable
    private Thread mThread;

    /**
     * コンストラクタ(XMLを使用しない場合)。
     *
     * @param context コンテキスト
     */
    public MarqueeView(Context context) {
        super(context);

        initMarqueeView();
    }

    /**
     * コンストラクタ(XMLを使用する場合)。
     *
     * @param context コンテキスト
     * @param attrs   XMLで定義した属性
     */
    public MarqueeView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initMarqueeView();

        // XMLから属性を取得
        final TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.MarqueeView);

        final String marqueeText = attributes.getString(R.styleable.MarqueeView_marquee_text);
        if (marqueeText != null) {
            setText(marqueeText);
        }

        final int textSize = attributes.getDimensionPixelOffset(R.styleable.MarqueeView_marquee_textSize, 0);
        if (textSize > 0) {
            setTextSize(textSize);
        }

        final int padding = attributes.getDimensionPixelOffset(R.styleable.MarqueeView_marquee_padding, 0);
        if (padding > 0) {
            setPadding(padding, padding, padding, padding);
        }

        setTextColor(attributes.getColor(R.styleable.MarqueeView_marquee_textColor, 0xFFFFFFFF));
        setBackgroundColor(attributes.getColor(R.styleable.MarqueeView_marquee_background, 0xFF000000));
        setRepeatLimit(attributes.getInteger(R.styleable.MarqueeView_marquee_repeatLimit, 1));
        setTextMoveSpeed(attributes.getInteger(R.styleable.MarqueeView_marquee_textMoveSpeed, 5));

        attributes.recycle();
    }

    /**
     * 初期化処理。
     * このメソッドは必ずコンストラクタ内で呼び出す必要がある
     */
    private void initMarqueeView() {
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(25);
        mTextPaint.setColor(0xFFFFFFFF);

        setPadding(0, 0, 0, 0);
        setBackgroundColor(0xFF000000);

        requestLayout();
        invalidate();
    }

    /**
     * マーキー処理を停止する。
     */
    public void clearMarquee() {
        mCurrentX = getMarqueeStartX();
        mRepeatCount = 0;
        mRunning = false;
    }

    /**
     * マーキー処理を開始する。
     */
    public void startMarquee() {
        clearMarquee();

        mRunning = true;

        if (mThread == null) {
            mThread = new Thread(runnable);
            mThread.start();
        } else {
            mLastX = getLastX();
            mCurrentX = getMarqueeStartX();
        }
    }

    /**
     * リピート回数を設定する。
     *
     * @param repeatLimit マイナス値だと無限
     */
    public void setRepeatLimit(int repeatLimit) {
        this.mRepeatLimit = repeatLimit;
    }

    /**
     * テキストの移動速度(px)を設定する。
     *
     * @param speed 移動速度(ピクセルで指定)
     */
    public void setTextMoveSpeed(int speed) {
        if (speed > 0) {
            mTextMoveSpeed = speed;
        }
    }

    /**
     * テキストを設定する。
     *
     * @param text 表示するテキスト
     */
    public void setText(@NonNull String text) {
        this.mText = text;

        requestLayout();
        invalidate();
    }

    /**
     * テキストサイズを設定する。
     *
     * @param size フォントサイズ
     */
    public void setTextSize(int size) {
        mTextPaint.setTextSize(size);

        requestLayout();
        invalidate();
    }

    /**
     * テキストカラーを設定する。
     *
     * @param color
     */
    public void setTextColor(int color) {
        mTextPaint.setColor(color);

        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    /**
     * ビューの幅を返す。
     *
     * @param measureSpec
     * @return
     */
    private int measureWidth(int measureSpec) {
        final int result;
        final int specMode = MeasureSpec.getMode(measureSpec);
        final int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            // We were told how big to be
            result = specSize;
        } else {
            final WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
            final Display display = wm.getDefaultDisplay();
            result = display.getWidth();
        }

        return result;
    }

    /**
     * ビューの高さを返す。
     *
     * @param measureSpec
     * @return
     */
    private int measureHeight(int measureSpec) {
        final int result;

        final int specMode = MeasureSpec.getMode(measureSpec);
        final int specSize = MeasureSpec.getSize(measureSpec);
        final int ascent = (int) mTextPaint.ascent();

        if (specMode == MeasureSpec.EXACTLY) {
            // We were told how big to be
            result = specSize;
        } else {
            // Measure the mText (beware: ascent is a negative number)
            final int tmpResult = (int) (-ascent + mTextPaint.descent()) + getPaddingTop() + getPaddingBottom();
            if (specMode == MeasureSpec.AT_MOST) {
                // Respect AT_MOST value if that was what is called for by measureSpec
                result = Math.min(tmpResult, specSize);
            } else {
                result = tmpResult;
            }
        }

        return result;
    }

    /**
     * マーキーの開始位置のX座標を返す。
     *
     * @return
     */
    private int getMarqueeStartX() {
        final WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        final Display display = wm.getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);

        final int measureText = (int) mTextPaint.measureText(mText);
        final int measureWidth = getMeasuredWidth();

        if (size.x == measureWidth) {
            return measureWidth;
        } else if (measureText > size.x) {
            // テキストが画面サイズを超える場合
            return size.x;
        } else if (measureWidth > measureText) {
            return measureWidth;
        } else {
            return measureText;
        }
    }

    /**
     * 左端と判定するX座標。
     *
     * @return
     */
    private int getLastX() {
        final WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        final Display display = wm.getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);

        final int measureText = (int) mTextPaint.measureText(mText);

        if (measureText >= size.x) {
            // テキストが画面サイズを超える場合
            return -measureText;
        } else {
            return -measureText;
        }
    }

    /**
     * 描画処理。
     *
     * @param canvas キャンバス
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        final int ascent = (int) mTextPaint.ascent();

        final int x = getPaddingLeft() + mCurrentX;
        final int y = getPaddingTop() - ascent;

        canvas.drawText(mText, x, y, mTextPaint);
    }
}
