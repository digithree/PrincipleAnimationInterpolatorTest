package ie.simonkenny.principleanimationinterpolatortest.interpolators;

import android.graphics.PointF;
import android.view.animation.BaseInterpolator;

import java.util.Arrays;

/**
 * Created by simonkenny on 04/08/2016.
 */
public class SpringInterpolator extends BaseInterpolator {
    private static final int NUM_SAMPLES = 1000;
    private static final int NUM_CALCULATIONS = 10000;

    private static final float MIN_DIFF_THRESHOLD = 0.003f;
    private static final float T_ADJUST_CONST = 0.7f;
    private static final float AMP_ADJUST_CONST_SMALL = 0.7f;
    private static final float FRICTION_ADJUST_CONST = 7.f;
    private static final float HALF_PI = (float) (Math.PI / 2);

    private static final float T_ADJUST_CONST_FIX = 2.f;

    private final float tension;
    private final float friction;
    private final boolean applyMinDiffThreshold;

    private float samples[];

    public SpringInterpolator(float tension, float friction, boolean applyMinDiffThreshold) {
        this.tension = tension;
        this.friction = friction;
        this.applyMinDiffThreshold = applyMinDiffThreshold;
        init();
    }

    public float getFriction() {
        return friction;
    }

    public float getTension() {
        return tension;
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
        for (int i = 0 ; i < NUM_CALCULATIONS ; ++i) {
            float t = ((float)i) / ((float)NUM_CALCULATIONS - 1);
            PointF p = spring(tension, friction, t);
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
                if (samples[i+numUnfilled] == samples[i-1]) {
                    for (int j = 0; j < numUnfilled; j++) {
                        samples[i + j] = samples[i - 1];
                    }
                } else {
                    float diff = (samples[i+numUnfilled] - samples[i-1]) / numUnfilled;
                    for (int j = 0; j < numUnfilled; j++) {
                        samples[i + j] = samples[i - 1] + (diff * (j + 1));
                    }
                }
            }
        }
    }

    PointF spring(float tension, float friction, float t) {
        PointF p = new PointF();
        float safeT = t != 0 ? t : 0.001f;
        p.x = t * T_ADJUST_CONST * T_ADJUST_CONST_FIX;
        float tensionMod = (tension - 1) * (tension < 1 ? AMP_ADJUST_CONST_SMALL : 1 / logMod(tension));
        float frictionMod = friction * (float)Math.pow(safeT * FRICTION_ADJUST_CONST, 2 + ((float)Math.pow(safeT, 4) * 10.f));
        float sinCalc = (((float)Math.sin(-HALF_PI + ((t*2) * (1.f + tensionMod)))) / (2.f + frictionMod));
        if (applyMinDiffThreshold && Math.abs(sinCalc) < MIN_DIFF_THRESHOLD) {
            sinCalc = 0.f;
        }
        p.y = 1f + (sinCalc * 2.f);
        return p;
    }

    float logMod (float x) {
        return ((float)Math.log(x + 1) * (1.5f + (x / 200)));
    }
}