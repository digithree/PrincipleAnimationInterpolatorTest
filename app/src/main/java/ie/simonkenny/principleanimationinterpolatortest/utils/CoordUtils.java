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
}
