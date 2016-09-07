package ie.simonkenny.principleanimationinterpolatortest.activities;

import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;

import java.lang.ref.WeakReference;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import ie.simonkenny.principleanimationinterpolatortest.R;
import ie.simonkenny.principleanimationinterpolatortest.interpolators.BezierInterpolator;
import ie.simonkenny.principleanimationinterpolatortest.views.BezierCurveView;

/**
 * Created by simonkenny on 07/09/2016.
 */
public class BezierInterpolatorActivity extends AppCompatActivity {

    private static final int TIMER_WAIT = 1000;

    @Bind(R.id.seek_bar_value_edit)
    AppCompatSeekBar mSbValueEdit;
    @Bind(R.id.edit_text_c1x)
    EditText mEtC1x;
    @Bind(R.id.edit_text_c1y)
    EditText mEtC1y;
    @Bind(R.id.edit_text_c2x)
    EditText mEtC2x;
    @Bind(R.id.edit_text_c2y)
    EditText mEtC2y;
    @Bind(R.id.edit_text_duration)
    EditText mEtDuration;

    @Bind(R.id.bezier_curve_view)
    BezierCurveView mBezierCurveView;

    @Bind(R.id.animation_container)
    ViewGroup mVgAnimationContainer;
    @Bind(R.id.image_view_animate)
    ImageView mIvAnimate;

    private final int []ANIMATION_RES_IDS = {
            R.anim.animate_step_1,
            R.anim.animate_step_2,
            R.anim.animate_step_3,
            R.anim.animate_step_4,
            R.anim.animate_step_5
    };


    private WeakReference<EditText> mCurrentEditTextWeakRef;
    private boolean mSeekBarChangeFreeze = false;

    private Handler mHandler = new Handler();

    private BezierInterpolator mBezierInterpolator;
    private float mDuration = 0.f;

    private int mAnimationStep = -1;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bezier_interpolator);
        ButterKnife.bind(this);

        mSbValueEdit.setMax(1000);

        mEtC1x.setOnFocusChangeListener(mCoordinateFocusChangeListener);
        mEtC1y.setOnFocusChangeListener(mCoordinateFocusChangeListener);
        mEtC2x.setOnFocusChangeListener(mCoordinateFocusChangeListener);
        mEtC2y.setOnFocusChangeListener(mCoordinateFocusChangeListener);
        mEtDuration.setOnFocusChangeListener(mDurationFocusChangeListener);

        mSbValueEdit.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (!mSeekBarChangeFreeze && mCurrentEditTextWeakRef != null && mCurrentEditTextWeakRef.get() != null) {
                    EditText editText = mCurrentEditTextWeakRef.get();
                    if (editText == mEtDuration) {
                        editText.setText(String.format(Locale.getDefault(), "%d", i));
                    } else {
                        editText.setText(String.format(Locale.getDefault(), "%.2f", (((float)i) / 1000.f)));
                    }
                    restartTimer();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // first time creation of BezierInterpolator with default values
        updateBezierInterpolator();
    }


    private View.OnFocusChangeListener mCoordinateFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean b) {
            if (!(view instanceof EditText)) {
                return;
            }
            EditText editText = (EditText) view;
            mSeekBarChangeFreeze = true;
            try {
                float value = Float.parseFloat(editText.getText().toString());
                mSbValueEdit.setProgress((int)(value * 1000));
            } catch (NumberFormatException e) {
                e.printStackTrace();
                mSeekBarChangeFreeze = false;
                return;
            }
            mSeekBarChangeFreeze = false;
            mCurrentEditTextWeakRef = new WeakReference<>(editText);
        }
    };

    private View.OnFocusChangeListener mDurationFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean b) {
            if (!(view instanceof EditText)) {
                return;
            }
            EditText editText = (EditText) view;
            mSeekBarChangeFreeze = true;
            try {
                int value = Integer.parseInt(editText.getText().toString());
                mSbValueEdit.setProgress(value);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                mSeekBarChangeFreeze = false;
                return;
            }
            mSeekBarChangeFreeze = false;
            mCurrentEditTextWeakRef = new WeakReference<>(editText);
        }
    };

    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d("BezierInterpolatorTimer", "timer elapsed at "+ SystemClock.currentThreadTimeMillis());
            updateBezierInterpolator();
        }
    };

    private void restartTimer() {
        mHandler.removeCallbacks(timerRunnable);
        mHandler.postDelayed(timerRunnable, TIMER_WAIT);
    }

    private void updateBezierInterpolator() {
        String c1xText = mEtC1x.getText().toString();
        String c1yText = mEtC1y.getText().toString();
        String c2xText = mEtC2x.getText().toString();
        String c2yText = mEtC2y.getText().toString();
        String durationText = mEtDuration.getText().toString();
        if (c1xText.isEmpty() || c1yText.isEmpty() || c2xText.isEmpty() || c2yText.isEmpty() || durationText.isEmpty()) {
            return;
        }
        float c1x;
        float c1y;
        float c2x;
        float c2y;
        mDuration = 0.f;
        try {
            c1x = Float.parseFloat(c1xText);
            c1y = Float.parseFloat(c1yText);
            c2x = Float.parseFloat(c2xText);
            c2y = Float.parseFloat(c2yText);
            mDuration = Float.parseFloat(durationText);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return;
        }
        mBezierInterpolator = new BezierInterpolator(new PointF(c1x, c1y), new PointF(c2x, c2y));
        mBezierCurveView.setInterpolator(mBezierInterpolator);
    }

    @OnClick(R.id.button_visualize)
    public void onVisualizeButtonClick() {
        mBezierCurveView.setVisibility(View.VISIBLE);
        mVgAnimationContainer.setVisibility(View.GONE);
    }

    @OnClick(R.id.button_test)
    public void onTestButtonClick() {
        mBezierCurveView.setVisibility(View.GONE);
        mVgAnimationContainer.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.animation_container)
    public void onAnimationContainerClick() {
        doNextAnimationStep();
    }

    @OnLongClick(R.id.animation_container)
    public boolean onAnimationContainerLongClick() {
        mAnimationStep = -1;
        doNextAnimationStep();
        return true;
    }

    private void doNextAnimationStep() {
        if (mBezierInterpolator != null) {
            mAnimationStep = (mAnimationStep + 1) % ANIMATION_RES_IDS.length;
            // do animation
            mIvAnimate.setVisibility(View.VISIBLE);
            Animation animation = AnimationUtils.loadAnimation(getBaseContext(), ANIMATION_RES_IDS[mAnimationStep]);
            animation.setInterpolator(mBezierInterpolator);
            animation.setDuration((int) mDuration);
            animation.setFillAfter(true);
            mIvAnimate.startAnimation(animation);
        }
    }
}
