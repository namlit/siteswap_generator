/*
* Siteswap Generator: Android App for generating juggling siteswaps
* Copyright (C) 2018 Tilman Sinning
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/

package namlit.siteswapgenerator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

import siteswaplib.Siteswap;

/**
 * Created by tilman on 11.04.18.
 */

public class CausalDiagram extends View {

    private Paint mTextPaint;
    private Paint mCirclePaint;
    private Siteswap mSiteswap = null;
    private float mDensity;
    private float mTextSize;
    private float mNodeRadius;
    private float mNodeDistance;
    private float mStrokeWidth;

    public CausalDiagram(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setSiteswap(Siteswap siteswap) {
        this.mSiteswap = siteswap;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Try for a width based on our minimum
        int rowLengthCenterPoints = (mSiteswap.getNonMirroredPeriod() /
                mSiteswap.getNumberOfJugglers()) * (int) mNodeDistance;
        int circleSize = 2 * (int) mNodeRadius + (int) mStrokeWidth;
        int rowOffset = (int) mNodeDistance / mSiteswap.getNumberOfJugglers();
        int minw = getPaddingLeft() + getPaddingRight() + rowLengthCenterPoints +
                circleSize - rowOffset;
        int w = resolveSizeAndState(minw, widthMeasureSpec, 1);

        // Whatever the width ends up being, ask for a height that would let the pie
        // get as big as it can
        //int minh = getPaddingBottom() + getPaddingTop() + 1000;
        int minh = 2 * (int) (mNodeRadius + mTextPaint.descent() - mTextPaint.ascent()) +
                (mSiteswap.getNumberOfJugglers() - 1) * (int) mNodeDistance;
        int h = resolveSizeAndState(minh, heightMeasureSpec, 0);

        setMeasuredDimension(w, h);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for(int i = 0; i < mSiteswap.getNonMirroredPeriod(); ++i) {
            int row = i % mSiteswap.getNumberOfJugglers();
            boolean isRightHand = (i / mSiteswap.getNumberOfJugglers()) % 2 == 0;
            boolean isTextAbove = row == 0 ? true : false;

            drawNode(canvas, getNodePosition(i).x, getNodePosition(i).y, String.valueOf(mSiteswap.at(i)),
                            isRightHand, isTextAbove);

        }

    }

    private void drawNode(Canvas canvas, int x, int y, String number,
                          boolean isRightHand, boolean isTextAbove) {
        String handText = isRightHand ? new String("R") : new String("L");
        float handTextPosY = isTextAbove?
                y - mTextPaint.descent() - mNodeRadius : y - mTextPaint.ascent() + mNodeRadius;
        float numberTextPosY = y - (mTextPaint.ascent() + mTextPaint.descent()) / 2;

        canvas.drawText(number, x, numberTextPosY, mTextPaint);
        canvas.drawCircle(x, y, mNodeRadius, mCirclePaint);
        canvas.drawText(handText, x, handTextPosY, mTextPaint);
    }

    private Point getNodePosition(int nodeIndex) {

        int yPosFirstRow = (int) (mNodeRadius - mTextPaint.ascent() + mTextPaint.descent() + mStrokeWidth / 2);
        int xPosStart = (int) (mNodeRadius + mStrokeWidth / 2) + getPaddingLeft();

        int row = nodeIndex % mSiteswap.getNumberOfJugglers();
        int column = nodeIndex / mSiteswap.getNumberOfJugglers();
        int rowOffset = (int) mNodeDistance / mSiteswap.getNumberOfJugglers();
        int xPos = xPosStart + column * (int) mNodeDistance + row * rowOffset;
        int yPos = yPosFirstRow + row * (int) mNodeDistance;
        return new Point(xPos, yPos);
    }

    private void init() {
        mSiteswap = new Siteswap();
        mDensity = getResources().getDisplayMetrics().density;
        mTextSize = mDensity * 40;
        mNodeRadius = mDensity * 30;
        mNodeDistance = mDensity * 100;
        mStrokeWidth = mDensity * 2;
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setColor(Color.BLACK);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setStrokeWidth(mStrokeWidth);
    }
}
