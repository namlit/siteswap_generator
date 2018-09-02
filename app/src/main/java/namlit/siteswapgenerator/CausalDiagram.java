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
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

import siteswaplib.Siteswap;

/**
 * Created by tilman on 11.04.18.
 */

public class CausalDiagram extends View {

    private boolean mIsLadderDiagram;
    private Paint mTextPaint;
    private Paint mHandTextPaint;
    private Paint mJugglerNamePaint;
    private Paint mCirclePaint;
    private Paint mConnectionPaint;
    private Paint mArrowHeadPaint;
    private Path mArrowHeadPath;
    private Siteswap mSiteswap = null;
    private float mDensity;
    private float mSmallTextSize;
    private float mMediumTextSize;
    private float mLargeTextSize;
    private float mNodeRadius;
    private float mNodeLocalBeatXDistance;
    private float mNodeYDistance;
    private float mStrokeWidth;
    private float mJugglerNameDist;
    private float mArrowHeadSize;
    private int mNumberOfNodes;

    public CausalDiagram(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.CausalDiagram,
                0, 0);
        try {
            mIsLadderDiagram = a.getBoolean(R.styleable.CausalDiagram_isLadderDiagram, false);
        } finally {
            a.recycle();
        }

        init();
    }

    public void setSiteswap(Siteswap siteswap) {
        this.mSiteswap = siteswap;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mNumberOfNodes = mSiteswap.getNonMirroredPeriod();
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        int rowLengthCenterPoints = (mNumberOfNodes /
                mSiteswap.getNumberOfJugglers()) * (int) mNodeLocalBeatXDistance;
        int circleSize = 2 * (int) mNodeRadius + (int) mStrokeWidth;
        int rowOffset = (int) mNodeLocalBeatXDistance / mSiteswap.getNumberOfJugglers();
        if (mSiteswap.isSynchronous()) {
            if (mSiteswap.getmSynchronousStartPosition() == 0)
                rowOffset = (int) mNodeLocalBeatXDistance;
            else
                rowOffset = 0;
        }
        int minw = getPaddingLeft() + getPaddingRight() + (int) mJugglerNameDist +
                rowLengthCenterPoints + circleSize - rowOffset;
        if (minw < parentWidth) {
            minw = parentWidth;
            mNumberOfNodes = (int) ((minw / mNodeLocalBeatXDistance * mSiteswap.getNumberOfJugglers()) - 1);
        }
        int w = resolveSizeAndState(minw, widthMeasureSpec, 1);

        int minh = getPaddingTop() + getPaddingBottom() +
                2 * (int) (mNodeRadius + mHandTextPaint.descent() - mHandTextPaint.ascent()) +
                (mSiteswap.getNumberOfJugglers() - 1) * (int) mNodeYDistance;
        int h = resolveSizeAndState(minh, heightMeasureSpec, 0);

        setMeasuredDimension(w, h);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (int i = 0; i < mSiteswap.getNumberOfJugglers(); ++i) {

            float numberTextPosY = getNodePosition(i).y -
                    (mJugglerNamePaint.ascent() + mJugglerNamePaint.descent()) / 2;
            canvas.drawText(Character.toString((char) ('A' + i)) + ":",
                    getPaddingLeft(), numberTextPosY, mJugglerNamePaint);
        }

        for(int i = 0; i < mNumberOfNodes; ++i) {
            boolean isRightHand = (i / mSiteswap.getNumberOfJugglers()) % 2 == 0;

            drawNode(canvas, getNodePosition(i).x, getNodePosition(i).y, mSiteswap.stringAt(i),
                            isRightHand, isTextAbove(i));

            drawConnection(canvas, i);

        }
    }

    private boolean isTextAbove(int index) {
        int row = index % mSiteswap.getNumberOfJugglers();
        return row == 0 ? true : false;
    }

    private void drawNode(Canvas canvas, int x, int y, String number,
                          boolean isRightHand, boolean isTextAbove) {
        String handText = isRightHand ? new String("R") : new String("L");
        float handTextPosY = isTextAbove?
                y - mHandTextPaint.descent() - mNodeRadius :
                y - mHandTextPaint.ascent() + mNodeRadius;
        float numberTextPosY = y - (mTextPaint.ascent() + mTextPaint.descent()) / 2;
        canvas.drawText(number, x, numberTextPosY, mTextPaint);
        canvas.drawCircle(x, y, mNodeRadius, mCirclePaint);
        canvas.drawText(handText, x, handTextPosY, mHandTextPaint);
    }

    private int getStepIndex(int startNodeIndex){
        if (mIsLadderDiagram) {
            return mSiteswap.at(startNodeIndex);
        }
        return mSiteswap.at(startNodeIndex) - 2 * mSiteswap.getNumberOfJugglers();
    }

    private void drawConnection(Canvas canvas, int startNodeIndex) {
        int stepIndex = getStepIndex(startNodeIndex);
        int stopNodeIndex = startNodeIndex + stepIndex;
        if (stopNodeIndex < 0)
            return;
        int row_distance = Math.abs(stopNodeIndex % mSiteswap.getNumberOfJugglers() -
                startNodeIndex % mSiteswap.getNumberOfJugglers());
        float offsetFromNode = mNodeRadius + mStrokeWidth / 2;

        if (stepIndex == 0) {

        }
        if (row_distance == 1 ||
                stepIndex == mSiteswap.getNumberOfJugglers())
            drawStraightConnection(canvas, startNodeIndex, stopNodeIndex, offsetFromNode);
        else
            drawBezierConnection(canvas, startNodeIndex, stopNodeIndex, offsetFromNode);

    }

    private void drawBezierConnection(Canvas canvas, int startNodeIndex,
                                      int stopNodeIndex, float offsetFromNode) {
        float startNodeX = getNodePosition(startNodeIndex).x;
        float startNodeY = getNodePosition(startNodeIndex).y;
        float stopNodeX = getNodePosition(stopNodeIndex).x;
        float stopNodeY = getNodePosition(stopNodeIndex).y;

        float x_direction_start = 1 / (float) Math.sqrt(2);
        if (stopNodeIndex < startNodeIndex)
            x_direction_start *= -1;
        float y_direction_start = 1 / (float) Math.sqrt(2);
        if (!isTextAbove(startNodeIndex))
            y_direction_start *= -1;
        if (stopNodeIndex - startNodeIndex == -mSiteswap.getNumberOfJugglers())
            y_direction_start *= -1;
        float x_direction_stop = 1 / (float) Math.sqrt(2);
        if (stopNodeIndex < startNodeIndex)
            x_direction_stop *= -1;
        float y_direction_stop = 1 / (float) Math.sqrt(2);
        if (!isTextAbove(stopNodeIndex))
            y_direction_stop *= -1;
        if (stopNodeIndex - startNodeIndex == -mSiteswap.getNumberOfJugglers())
            y_direction_stop *= -1;
        float rotation = (float) Math.toDegrees(Math.atan2(-y_direction_stop, x_direction_stop));

        float startX = startNodeX + x_direction_start * offsetFromNode;
        float startY = startNodeY + y_direction_start * offsetFromNode;
        float stopX = stopNodeX - x_direction_stop * (mArrowHeadSize + offsetFromNode);
        float stopY = stopNodeY + y_direction_stop * (mArrowHeadSize + offsetFromNode);

        float control_point_factor = mNodeRadius;
        if (Math.abs(stopNodeIndex - startNodeIndex) == mSiteswap.getNumberOfJugglers()) {
            control_point_factor *= 1.2;
            y_direction_stop /= 2;
        }
        else if (stopNodeIndex == startNodeIndex) {
            control_point_factor *= 1.5;
            y_direction_start *= 1.5;
        }
        else
            control_point_factor *= 3;

        Path path = new Path();
        path.moveTo(startX, startY);
        path.cubicTo(startX + x_direction_start * control_point_factor,
                startY + y_direction_start * control_point_factor,
                stopX - x_direction_stop * control_point_factor,
                stopY + y_direction_stop * control_point_factor,
                stopX, stopY);
        canvas.drawPath(path, mConnectionPaint);
        drawArrowHead(canvas, stopX, stopY, rotation);


    }

    private void drawStraightConnection(Canvas canvas, int startNodeIndex,
                                        int stopNodeIndex, float offsetFromNode) {
        float startNodeX = getNodePosition(startNodeIndex).x;
        float startNodeY = getNodePosition(startNodeIndex).y;
        float stopNodeX = getNodePosition(stopNodeIndex).x;
        float stopNodeY = getNodePosition(stopNodeIndex).y;
        float length = new PointF(stopNodeX - startNodeX, stopNodeY - startNodeY).length();

        float dxStart = (stopNodeX - startNodeX) / length * offsetFromNode;
        float dyStart = (stopNodeY - startNodeY) / length * offsetFromNode;
        float dxEnd = (stopNodeX - startNodeX) / length * (offsetFromNode + mArrowHeadSize);
        float dyEnd = (stopNodeY - startNodeY) / length * (offsetFromNode + mArrowHeadSize);
        float rotation = (float) Math.toDegrees(Math.atan2(dyStart, dxStart));

        canvas.drawLine(startNodeX + dxStart, startNodeY + dyStart,
                stopNodeX - dxEnd, stopNodeY - dyEnd,
                mConnectionPaint);
        drawArrowHead(canvas, stopNodeX - dxEnd, stopNodeY - dyEnd, rotation);
    }

    private void drawArrowHead(Canvas canvas, float x, float y, float rotationDegree) {
        Matrix transform = new Matrix();
        transform.setRotate(rotationDegree);
        transform.postTranslate(x, y);
        Path arrowHead = new Path();
        mArrowHeadPath.transform(transform, arrowHead);
        canvas.drawPath(arrowHead, mArrowHeadPaint);
    }

    private Point getNodePosition(int nodeIndex) {

        int yPosFirstRow = getPaddingTop() + (int) (mNodeRadius - mHandTextPaint.ascent() +
                mHandTextPaint.descent() + mStrokeWidth / 2);
        int xPosStart = (int) (mNodeRadius + mStrokeWidth / 2) +
                getPaddingLeft() + (int) mJugglerNameDist;

        int row = nodeIndex % mSiteswap.getNumberOfJugglers();
        int column = nodeIndex;
        if (mSiteswap.getmSynchronousStartPosition() != 0) {
            column += (mSiteswap.getNumberOfSynchronousHands() - 1);
        }
        column -= mSiteswap.getSynchronousPosition(nodeIndex);
        float columnXDistance = mNodeLocalBeatXDistance / mSiteswap.getNumberOfJugglers();
        int rowOffset = (int) mNodeLocalBeatXDistance / mSiteswap.getNumberOfJugglers();
        int xPos = xPosStart + column * (int) columnXDistance;
        int yPos = yPosFirstRow + row * (int) mNodeYDistance;
        return new Point(xPos, yPos);
    }

    private void init() {
        mSiteswap = new Siteswap();
        mDensity = getResources().getDisplayMetrics().density;
        mSmallTextSize = mDensity * 20;
        mMediumTextSize = mDensity * 25;
        mLargeTextSize = mDensity * 30;
        mNodeRadius = mSmallTextSize * 2 / 3;
        mNodeLocalBeatXDistance = mDensity * 60;
        mNodeYDistance = mDensity * 80;
        mStrokeWidth = mDensity * 2;
        mArrowHeadSize = mDensity * 12;
        mJugglerNameDist = mDensity * 40;
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setTextSize(mSmallTextSize);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mHandTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHandTextPaint.setColor(Color.BLACK);
        mHandTextPaint.setTextSize(mMediumTextSize);
        mHandTextPaint.setTextAlign(Paint.Align.CENTER);
        mJugglerNamePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mJugglerNamePaint.setColor(Color.BLACK);
        mJugglerNamePaint.setTextSize(mLargeTextSize);
        mJugglerNamePaint.setTextAlign(Paint.Align.LEFT);
        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setColor(Color.BLACK);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setStrokeWidth(mStrokeWidth);
        mConnectionPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        if(mIsLadderDiagram) {
            mConnectionPaint.setColor(0xff00aa00);
        }
        else {
            mConnectionPaint.setColor(Color.BLUE);
        }
        mConnectionPaint.setStyle(Paint.Style.STROKE);
        mConnectionPaint.setStrokeWidth(mStrokeWidth);
        mArrowHeadPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        if(mIsLadderDiagram) {
            mArrowHeadPaint.setColor(0xff00aa00);
        }
        else {
            mArrowHeadPaint.setColor(Color.BLUE);
        }
        mArrowHeadPaint.setStyle(Paint.Style.FILL);
        mArrowHeadPaint.setStrokeWidth(mStrokeWidth);
        mArrowHeadPath = new Path();
        mArrowHeadPath.moveTo(mArrowHeadSize, 0);
        mArrowHeadPath.lineTo(0, -0.4f * mArrowHeadSize);
        mArrowHeadPath.lineTo(0, +0.4f * mArrowHeadSize);
        mArrowHeadPath.close();
    }
}
