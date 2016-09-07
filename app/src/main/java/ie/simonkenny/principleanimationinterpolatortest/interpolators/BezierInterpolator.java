package ie.simonkenny.principleanimationinterpolatortest.interpolators;

import android.graphics.PointF;
import android.view.animation.BaseInterpolator;

import java.util.Arrays;

/**
 * Created by simonkenny on 04/08/2016.
 */
public class BezierInterpolator extends BaseInterpolator {
    private static final int NUM_SAMPLES = 1000;
    private static final int NUM_CALCULATIONS = 10000;

    private final PointF controlPoint1;
    private final PointF controlPoint2;

    private float samples[];

    public BezierInterpolator(PointF controlPoint1, PointF controlPoint2) {
        this.controlPoint1 = controlPoint1;
        this.controlPoint2 = controlPoint2;
        init();
    }

    public PointF getControlPoint1() {
        return controlPoint1;
    }

    public PointF getControlPoint2() {
        return controlPoint2;
    }

    @Override
    public float getInterpolation(float v) {
        return samples[(int)(v * (float)(NUM_SAMPLES-1))];
    }

    private void init() {
        samples = new float[NUM_SAMPLES];
        Arrays.fill(samples, -1.f);
        samples[0] = 0.f;
        samples[NUM_SAMPLES-1] = 1.f;
        PointF p1 = new PointF(0.f, 0.f);
        PointF p2 = new PointF(1.f, 1.f);
        for (int i = 0 ; i < NUM_CALCULATIONS ; ++i) {
            float t = ((float)i) / ((float)NUM_CALCULATIONS - 1);
            PointF p = bezier(p1, controlPoint1, controlPoint2, p2, t);
            if (p.x > 0 && p.x < 1.f) {
                int idx = (int) (p.x * (float)(NUM_SAMPLES-1));
                if (samples[idx] < 0) {
                    samples[idx] = p.y;
                }
            }
        }
        for (int i = 0 ; i < NUM_SAMPLES; i++) {
            if (samples[i] < 0) {
                int numUnfilled = 1;
                for (int j = (i+1) ; j < NUM_SAMPLES ; j++) {
                    if (samples[j] < 0) {
                        numUnfilled++;
                    } else {
                        break;
                    }
                }
                float diff = (samples[i+numUnfilled] - samples[i]) / numUnfilled;
                for (int j = 0 ; j < numUnfilled ; j++) {
                    samples[i+j] = samples[i-1] + (diff * (j+1));
                }
            }
        }
    }

    private PointF lerp(PointF a, PointF b, float t) {
        PointF dest = new PointF();
        dest.x = a.x + (b.x-a.x)*t;
        dest.y = a.y + (b.y-a.y)*t;
        return dest;
    }

    // evaluate a point on a bezier-curve. t goes from 0 to 1.0
    private PointF bezier(PointF a, PointF b, PointF c, PointF d, float t) {
        PointF ab, bc, cd, abbc, bccd, dest;
        ab = lerp(a,b,t);           // point between a and b (green)
        bc = lerp(b,c,t);           // point between b and c (green)
        cd = lerp(c,d,t);           // point between c and d (green)
        abbc = lerp(ab,bc,t);       // point between ab and bc (blue)
        bccd = lerp(bc,cd,t);       // point between bc and cd (blue)
        dest = lerp(abbc,bccd,t);   // point on the bezier-curve (black)
        return dest;
    }
}