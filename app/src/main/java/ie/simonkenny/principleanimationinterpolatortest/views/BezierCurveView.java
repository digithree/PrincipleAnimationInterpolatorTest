package ie.simonkenny.principleanimationinterpolatortest.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.BaseInterpolator;

import ie.simonkenny.principleanimationinterpolatortest.R;
import ie.simonkenny.principleanimationinterpolatortest.interfaces.IBezierCurveViewControlPointChange;
import ie.simonkenny.principleanimationinterpolatortest.interfaces.IInterpolatorRenderView;
import ie.simonkenny.principleanimationinterpolatortest.interpolators.BezierInterpolator;
import ie.simonkenny.principleanimationinterpolatortest.utils.CoordUtils;

/**
 * Created by simonkenny on 07/09/2016.
 */
public class BezierCurveView extends View implements IInterpolatorRenderView {

    private final static int NUM_POINTS = 100;

    private enum TrackedPoint {
        NONE,
        P1,
        P2
    }

    private BezierInterpolator interpolator;

    private Paint mPaintMainLine;
    private Paint mPaintControlLine;
    private Paint mPaintControlCircle1;
    private Paint mPaintControlCircle2;

    private PointF controlPoint1Coord;
    private PointF controlPoint2Coord;
    private PointF currentTrackedPoint;
    private TrackedPoint mTrackedPoint;

    private float mMinTrackingDist = 0.f;

    private float lastDrawWidth = 0.f;
    private float lastDrawHeight = 0.f;
    private float lastOffsetX = 0.f;
    private float lastOffsetY = 0.f;

    private IBezierCurveViewControlPointChange mListener;


    public BezierCurveView(Context context) {
        super(context);
        init();
    }

    public BezierCurveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BezierCurveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public BezierCurveView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }


    // init

    private void init() {
        mPaintMainLine = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintMainLine.setStyle(Paint.Style.STROKE);
        mPaintMainLine.setColor(ContextCompat.getColor(getContext(), R.color.curve_main_line));
        mPaintMainLine.setStrokeWidth(2);

        mPaintControlLine = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintControlLine.setStyle(Paint.Style.STROKE);
        mPaintControlLine.setColor(ContextCompat.getColor(getContext(), R.color.curve_control_line));
        mPaintControlLine.setStrokeWidth(2);

        mPaintControlCircle1 = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintControlCircle1.setStyle(Paint.Style.FILL);
        mPaintControlCircle1.setColor(ContextCompat.getColor(getContext(), R.color.curve_control_circle1));

        mPaintControlCircle2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintControlCircle2.setStyle(Paint.Style.FILL);
        mPaintControlCircle2.setColor(ContextCompat.getColor(getContext(), R.color.curve_control_circle2));

        mMinTrackingDist = getContext().getResources().getDimensionPixelSize(R.dimen.bezier_edit_min_tracking_dist);
    }


    // set listener

    public void setListener(IBezierCurveViewControlPointChange listener) {
        mListener = listener;
    }


    // IInterpolatorRenderView implementation

    @Override
    public void setInterpolator(BaseInterpolator interpolator) {
        if (interpolator instanceof BezierInterpolator) {
            this.interpolator = (BezierInterpolator) interpolator;
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

        // draw main curve
        PointF lastPoint = new PointF(0, 0);
        for (int i = 1 ; i <= NUM_POINTS ; i++) {
            float xNorm = ((float)i) / NUM_POINTS;
            float yNorm = interpolator.getInterpolation(xNorm);
            canvas.drawLine(
                    translateFromNormalX(lastPoint.x, size, offsetX),
                    translateFromNormalY(lastPoint.y, size, offsetY),
                    translateFromNormalX(xNorm, size, offsetX),
                    translateFromNormalY(yNorm, size, offsetY),
                    mPaintMainLine);
            lastPoint.x = xNorm;
            lastPoint.y = yNorm;
        }

        // draw control lines and circles
        float controlPointRadius = getContext().getResources().getDimensionPixelSize(R.dimen.bezier_control_point_circle_radius);
        // c1
        PointF c1 = interpolator.getControlPoint1();
        controlPoint1Coord = new PointF(translateFromNormalX(c1.x, size, offsetX),
                translateFromNormalY(c1.y, size, offsetY));
        canvas.drawLine(
                translateFromNormalX(0, size, offsetX),
                translateFromNormalY(0, size, offsetY),
                translateFromNormalX(c1.x, size, offsetX),
                translateFromNormalY(c1.y, size, offsetY),
                mPaintControlLine);
        canvas.drawCircle(
                translateFromNormalX(c1.x, size, offsetX),
                translateFromNormalY(c1.y, size, offsetY),
                controlPointRadius,
                mPaintControlCircle1);
        // c2
        PointF c2 = interpolator.getControlPoint2();
        controlPoint2Coord = new PointF(translateFromNormalX(c2.x, size, offsetX),
                translateFromNormalY(c2.y, size, offsetY));
        canvas.drawLine(
                translateFromNormalX(1, size, offsetX),
                translateFromNormalY(1, size, offsetY),
                translateFromNormalX(c2.x, size, offsetX),
                translateFromNormalY(c2.y, size, offsetY),
                mPaintControlLine);
        canvas.drawCircle(
                translateFromNormalX(c2.x, size, offsetX),
                translateFromNormalY(c2.y, size, offsetY),
                controlPointRadius,
                mPaintControlCircle2);

        // draw edit point if exists
        float editPointRadius = getContext().getResources().getDimensionPixelSize(R.dimen.bezier_edit_point_circle_radius);
        if (currentTrackedPoint != null) {
            canvas.drawCircle(
                    currentTrackedPoint.x,
                    currentTrackedPoint.y,
                    editPointRadius,
                    mTrackedPoint == TrackedPoint.P1 ? mPaintControlCircle1 : mPaintControlCircle2);
        }
    }


    // touch input

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (currentTrackedPoint == null && controlPoint1Coord != null && controlPoint2Coord != null) {
                currentTrackedPoint = new PointF(event.getX(), event.getY());
                float dist1 = CoordUtils.dist(currentTrackedPoint, controlPoint1Coord);
                float dist2 = CoordUtils.dist(currentTrackedPoint, controlPoint2Coord);
                if (dist1 > 0 && dist2 > 0 && (dist1 < mMinTrackingDist || dist2 < mMinTrackingDist)) {
                    if (dist1 < dist2) {
                        mTrackedPoint = TrackedPoint.P1;
                        Log.d("BezierCurveView", "onTouchEvent, ACTION_DOWN, point 1 nearest");
                    } else {
                        mTrackedPoint = TrackedPoint.P2;
                        Log.d("BezierCurveView", "onTouchEvent, ACTION_DOWN, point 2 nearest");
                    }
                    invalidate();
                    return true;
                } else {
                    mTrackedPoint = TrackedPoint.NONE;
                    currentTrackedPoint = null;
                    Log.e("BezierCurveView", "onTouchEvent, ACTION_DOWN, error");
                    return false;
                }
            }
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (currentTrackedPoint != null
                    && (mTrackedPoint == TrackedPoint.P1 || mTrackedPoint == TrackedPoint.P2)) {
                currentTrackedPoint.x = event.getX();
                currentTrackedPoint.y = event.getY();
                invalidate();
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            if (currentTrackedPoint != null
                    && (mTrackedPoint == TrackedPoint.P1 || mTrackedPoint == TrackedPoint.P2)) {
                // normalize point
                PointF normTrackedPoint = new PointF(
                        reverseTranslateFromNormalX(currentTrackedPoint.x, lastDrawWidth, lastOffsetX),
                        reverseTranslateFromNormalY(currentTrackedPoint.y, lastDrawHeight, lastOffsetY)
                );
                // make sure point if within bounds
                if (normTrackedPoint.x < 0.f) {
                    normTrackedPoint.x = 0.f;
                } else if (normTrackedPoint.x > 1.f) {
                    normTrackedPoint.x = 1.f;
                }
                if (normTrackedPoint.y < 0.f) {
                    normTrackedPoint.y = 0.f;
                } else if (normTrackedPoint.y > 1.f) {
                    normTrackedPoint.y = 1.f;
                }
                // report change back to listener if exists
                if (mListener != null) {
                    if (mTrackedPoint == TrackedPoint.P1) {
                        mListener.changeControlPoint1(normTrackedPoint);
                    } else {
                        mListener.changeControlPoint2(normTrackedPoint);
                    }
                }
                Log.d("BezierCurveView", "onTouchEvent, ACTION_UP, point 1 or 2");
                // reset point tracking when done
                mTrackedPoint = TrackedPoint.NONE;
                currentTrackedPoint = null;
                invalidate();
            } else {
                Log.d("BezierCurveView", "onTouchEvent, ACTION_UP, no point captured");
            }
        }
        return false;
    }


    // helper

    float translateFromNormalX(float x, float drawWidth, float offsetX) {
        return offsetX + (x * drawWidth);
    }

    float translateFromNormalY(float y, float drawHeight, float offsetY) {
        return offsetY + ((1 - y) * drawHeight);
    }

    float reverseTranslateFromNormalX(float x, float drawWidth, float offsetX) {
        float x1 = (x - offsetX);
        return x1 == 0.f ? 0.f : (x1 / drawWidth);
    }

    float reverseTranslateFromNormalY(float y, float drawHeight, float offsetY) {
        float y1 = (y - offsetY);
        return y1 == 0.f ? 0.f : (1.f - (y1 / drawHeight));
    }
}
