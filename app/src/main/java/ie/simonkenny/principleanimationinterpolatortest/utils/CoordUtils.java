package ie.simonkenny.principleanimationinterpolatortest.utils;

import android.graphics.PointF;

/**
 * Created by simonkenny on 07/09/2016.
 */
public class CoordUtils {

    public static float dist(PointF p1, PointF p2) {
        float sqX = (float) Math.pow((p1.x - p2.x), 2);
        float sqY = (float) Math.pow((p1.y - p2.y), 2);
        if ((sqX + sqY) > 0) {
            return (float) Math.sqrt(sqX + sqY);
        }
        return 0.f;
    }


    public static float translateFromNormalX(float x, float drawWidth, float offsetX) {
        return offsetX + (x * drawWidth);
    }

    public static float translateFromNormalY(float y, float drawHeight, float offsetY) {
        return offsetY + ((1 - y) * drawHeight);
    }

    public static float reverseTranslateFromNormalX(float x, float drawWidth, float offsetX) {
        float x1 = (x - offsetX);
        return x1 == 0.f ? 0.f : (x1 / drawWidth);
    }

    public static float reverseTranslateFromNormalY(float y, float drawHeight, float offsetY) {
        float y1 = (y - offsetY);
        return y1 == 0.f ? 0.f : (1.f - (y1 / drawHeight));
    }

    public static PointF getNormalizedPoint(PointF pointF, float width, float height, float offsetX, float offsetY) {
        // normalize point
        PointF normPoint = new PointF(
                CoordUtils.reverseTranslateFromNormalX(pointF.x, width, offsetX),
                CoordUtils.reverseTranslateFromNormalY(pointF.y, height, offsetY)
        );
        // make sure point if within bounds
        normPoint.x = capNormalVal(normPoint.x);
        normPoint.y = capNormalVal(normPoint.y);
        return normPoint;
    }

    /**
     * Scales values logarithmically
     *
     * @param val value to scale, should be in range 0.0 to 1.0, if not will be capped
     * @return scaled value
     */
    public static float logScale(float val) {
        return (float) Math.log10((double) ((capNormalVal(val) * 9.f) + 1.f));
    }

    public static float powScale(float val, float pow) {
        return (float) Math.pow((double) capNormalVal(val), pow);
    }

    public static float capNormalVal(float val) {
        float cappedVal;
        if (val < 0.f) {
            cappedVal = 0.f;
        } else if (val > 1.f) {
            cappedVal = 1.f;
        } else {
            cappedVal = val;
        }
        return cappedVal;
    }
}
