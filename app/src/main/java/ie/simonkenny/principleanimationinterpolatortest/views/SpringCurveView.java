package ie.simonkenny.principleanimationinterpolatortest.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.BaseInterpolator;

import ie.simonkenny.principleanimationinterpolatortest.R;
import ie.simonkenny.principleanimationinterpolatortest.interfaces.IInterpolatorRenderView;
import ie.simonkenny.principleanimationinterpolatortest.interfaces.ISpringCurveViewParameterChange;
import ie.simonkenny.principleanimationinterpolatortest.interpolators.SpringInterpolator;
import ie.simonkenny.principleanimationinterpolatortest.utils.CoordUtils;

/**
 * Created by simonkenny on 07/09/2016.
 */
public class SpringCurveView extends View implements IInterpolatorRenderView {

    private final static int NUM_POINTS = 100;

    private SpringInterpolator interpolator;

    private Paint mPaintMainLine;
    private Paint mPaintCentreLine;
    private Paint mPaintControlLine1;
    private Paint mPaintControlLine2;

    private PointF currentTrackedPoint;

    private float lastDrawWidth = 0.f;
    private float lastDrawHeight = 0.f;
    private float lastOffsetX = 0.f;
    private float lastOffsetY = 0.f;

    private ISpringCurveViewParameterChange mListener;


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
        mPaintCentreLine.setColor(ContextCompat.getColor(getContext(), R.color.curve_control_line));
        mPaintCentreLine.setStrokeWidth(2);

        mPaintControlLine1 = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintControlLine1.setStyle(Paint.Style.FILL);
        mPaintControlLine1.setColor(ContextCompat.getColor(getContext(), R.color.curve_control_circle1));

        mPaintControlLine2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintControlLine2.setStyle(Paint.Style.FILL);
        mPaintControlLine2.setColor(ContextCompat.getColor(getContext(), R.color.curve_control_circle2));
    }

    public void setListener(ISpringCurveViewParameterChange listener) {
        mListener = listener;
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
        float offsetX = (canvasDim.x / 2) - (size / 2);
        float offsetY = (canvasDim.y / 2) - (size / 2);

        lastDrawWidth = size;
        lastDrawHeight = size;
        lastOffsetX = offsetX;
        lastOffsetY = offsetY;

        // draw centre line
        canvas.drawLine(
                CoordUtils.translateFromNormalX(0, size, offsetX),
                CoordUtils.translateFromNormalY(0.5f, size, offsetY),
                CoordUtils.translateFromNormalX(1, size, offsetX),
                CoordUtils.translateFromNormalY(0.5f, size, offsetY),
                mPaintCentreLine);

        // draw main curve
        PointF lastPoint = new PointF(0, 0);
        for (int i = 1 ; i <= NUM_POINTS ; i++) {
            float xNorm = ((float)i) / NUM_POINTS;
            float yNorm = interpolator.getInterpolation(xNorm) * 0.5f;
            canvas.drawLine(
                    CoordUtils.translateFromNormalX(lastPoint.x, size, offsetX),
                    CoordUtils.translateFromNormalY(lastPoint.y, size, offsetY),
                    CoordUtils.translateFromNormalX(xNorm, size, offsetX),
                    CoordUtils.translateFromNormalY(yNorm, size, offsetY),
                    mPaintMainLine);
            lastPoint.x = xNorm;
            lastPoint.y = yNorm;
        }

        // draw edit point if exists
        if (currentTrackedPoint != null) {
            canvas.drawLine(
                    CoordUtils.translateFromNormalX(0.f, size, offsetX),
                    currentTrackedPoint.y,
                    CoordUtils.translateFromNormalX(1.f, size, offsetX),
                    currentTrackedPoint.y,
                    mPaintControlLine1
                    );
            canvas.drawLine(
                    currentTrackedPoint.x,
                    CoordUtils.translateFromNormalY(0.f, size, offsetY),
                    currentTrackedPoint.x,
                    CoordUtils.translateFromNormalY(1.f, size, offsetY),
                    mPaintControlLine2
            );
        }
    }


    // touch input

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (currentTrackedPoint == null) {
                currentTrackedPoint = new PointF(event.getX(), event.getY());
                // check point in normal area
                invalidate();
                return true;
            }
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (currentTrackedPoint != null) {
                currentTrackedPoint.x = event.getX();
                currentTrackedPoint.y = event.getY();
                updateTrackedPointToListener();
                invalidate();
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            if (currentTrackedPoint != null) {
                updateTrackedPointToListener();
                // reset point tracking when done
                currentTrackedPoint = null;
                invalidate();
            }
        }
        return false;
    }

    private void updateTrackedPointToListener() {
        PointF normPoint = CoordUtils.getNormalizedPoint(currentTrackedPoint, lastDrawWidth, lastDrawHeight, lastOffsetX, lastOffsetY);
        // report change back to listener if exists
        if (mListener != null) {
            mListener.changeParametersNormal(normPoint.x, normPoint.y);
        }
    }
}
