package ie.simonkenny.principleanimationinterpolatortest.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.BaseInterpolator;

import ie.simonkenny.principleanimationinterpolatortest.R;
import ie.simonkenny.principleanimationinterpolatortest.interfaces.IInterpolatorRenderView;
import ie.simonkenny.principleanimationinterpolatortest.interpolators.SpringInterpolator;

/**
 * Created by simonkenny on 07/09/2016.
 */
public class SpringCurveView extends View implements IInterpolatorRenderView {

    private final static int NUM_POINTS = 100;

    private SpringInterpolator interpolator;

    private Paint mPaintMainLine;
    private Paint mPaintCentreLine;


    public SpringCurveView(Context context) {
        super(context);
        init();
    }

    public SpringCurveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SpringCurveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public SpringCurveView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }


    // init

    private void init() {
        mPaintMainLine = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintMainLine.setStyle(Paint.Style.STROKE);
        mPaintMainLine.setColor(ContextCompat.getColor(getContext(), R.color.curve_main_line));
        mPaintMainLine.setStrokeWidth(2);

        mPaintCentreLine = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintCentreLine.setStyle(Paint.Style.STROKE);
        mPaintCentreLine.setColor(ContextCompat.getColor(getContext(), R.color.curve_centre_line));
        mPaintCentreLine.setStrokeWidth(2);
    }


    // IInterpolatorRenderView implementation


    @Override
    public void setInterpolator(BaseInterpolator interpolator) {
        if (interpolator instanceof SpringInterpolator) {
            this.interpolator = (SpringInterpolator) interpolator;
            invalidate();
        }
    }


    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        if (interpolator == null) {
            return;
        }

        PointF canvasDim = new PointF(canvas.getWidth(), canvas.getHeight());
        float size = Math.min(canvasDim.x, canvasDim.y) - getContext().getResources().getDimensionPixelSize(R.dimen.size_standard);
        //float size = 300.f;
        float offsetX = (canvasDim.x / 2) - (size / 2);
        float offsetY = (canvasDim.y / 2) - (size / 2);

        // draw centre line
        canvas.drawLine(
                translateFromNormalX(0, size, offsetX),
                translateFromNormalY(0.5f, size, offsetY),
                translateFromNormalX(1, size, offsetX),
                translateFromNormalY(0.5f, size, offsetY),
                mPaintCentreLine);

        // draw main curve
        PointF lastPoint = new PointF(0, 0);
        for (int i = 1 ; i <= NUM_POINTS ; i++) {
            float xNorm = ((float)i) / NUM_POINTS;
            float yNorm = interpolator.getInterpolation(xNorm) * 0.5f;
            canvas.drawLine(
                    translateFromNormalX(lastPoint.x, size, offsetX),
                    translateFromNormalY(lastPoint.y, size, offsetY),
                    translateFromNormalX(xNorm, size, offsetX),
                    translateFromNormalY(yNorm, size, offsetY),
                    mPaintMainLine);
            lastPoint.x = xNorm;
            lastPoint.y = yNorm;
        }
    }


    // helper

    float translateFromNormalX(float x, float drawWidth, float offsetX) {
        return offsetX + (x * drawWidth);
    }

    float translateFromNormalY(float y, float drawHeight, float offsetY) {
        return offsetY + ((1 - y) * drawHeight);
    }
}
