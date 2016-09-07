package ie.simonkenny.principleanimationinterpolatortest.activities;

import android.content.SharedPreferences;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;

import java.lang.ref.WeakReference;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import butterknife.OnTextChanged;
import ie.simonkenny.principleanimationinterpolatortest.R;
import ie.simonkenny.principleanimationinterpolatortest.interpolators.SpringInterpolator;
import ie.simonkenny.principleanimationinterpolatortest.utils.SPrefUtils;
import ie.simonkenny.principleanimationinterpolatortest.views.SpringCurveView;

/**
 * Created by simonkenny on 07/09/2016.
 */
public class SpringInterpolatorActivity extends AppCompatActivity {

    private static final String PREF_TENSION = "pref_spring_tension";
    private static final String PREF_FRICTION = "pref_spring_friction";
    private static final String PREF_DURATION = "pref_spring_duration";

    private static final String DEFAULT_TENSION = "250";
    private static final String DEFAULT_FRICTION = "5";
    private static final String DEFAULT_DURATION = "1000";

    private static final int TIMER_WAIT = 1000;


    @Bind(R.id.seek_bar_value_edit)
    AppCompatSeekBar mSbValueEdit;

    @Bind(R.id.edit_text_tension)
    EditText mEtTension;
    @Bind(R.id.edit_text_friction)
    EditText mEtFriction;
    @Bind(R.id.edit_text_duration)
    EditText mEtDuration;

    @Bind(R.id.bezier_curve_view)
    SpringCurveView mSpringCurveView;

    @Bind(R.id.animation_container)
    ViewGroup mVgAnimationContainer;
    @Bind(R.id.image_view_animate)
    ImageView mIvAnimate;

    @Bind(R.id.button_visualize)
    Button mBtnVisualize;
    @Bind(R.id.button_test)
    Button mBtnTest;


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

    private SpringInterpolator mSpringInterpolator;
    private float mDuration = 0.f;

    private int mAnimationStep = -1;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_spring_interpolator);
        ButterKnife.bind(this);

        mSbValueEdit.setMax(1000);

        mEtTension.setOnFocusChangeListener(mValueFocusChangeListener);
        mEtFriction.setOnFocusChangeListener(mValueFocusChangeListener);
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

        // load values if exist
        initValuesFromPrefs();

        // first time creation of BezierInterpolator with default values
        updateSpringInterpolator();
    }

    private void initValuesFromPrefs() {
        SharedPreferences sharedPreferences = SPrefUtils.getPrefManager(getApplicationContext());
        mEtTension.setText(sharedPreferences.getString(PREF_TENSION, DEFAULT_TENSION));
        mEtFriction.setText(sharedPreferences.getString(PREF_FRICTION, DEFAULT_FRICTION));
        mEtDuration.setText(sharedPreferences.getString(PREF_DURATION, DEFAULT_DURATION));
    }

    @OnTextChanged(R.id.edit_text_tension)
    void onTensionTextChanged(CharSequence text) {
        SPrefUtils.saveString(getApplicationContext(), PREF_TENSION, text.toString());
    }

    @OnTextChanged(R.id.edit_text_friction)
    void onFrictionTextChanged(CharSequence text) {
        SPrefUtils.saveString(getApplicationContext(), PREF_FRICTION, text.toString());
    }

    @OnTextChanged(R.id.edit_text_duration)
    void onDurationTextChanged(CharSequence text) {
        SPrefUtils.saveString(getApplicationContext(), PREF_DURATION, text.toString());
    }

    private View.OnFocusChangeListener mValueFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean b) {
            if (!(view instanceof EditText)) {
                return;
            }
            EditText editText = (EditText) view;
            mSeekBarChangeFreeze = true;
            try {
                int value = Integer.parseInt(editText.getText().toString());
                mSbValueEdit.setProgress(value * 10);
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
            updateSpringInterpolator();
        }
    };

    private void restartTimer() {
        mHandler.removeCallbacks(timerRunnable);
        mHandler.postDelayed(timerRunnable, TIMER_WAIT);
    }

    private void updateSpringInterpolator() {
        String tensionText = mEtTension.getText().toString();
        String frictionText = mEtFriction.getText().toString();
        String durationText = mEtDuration.getText().toString();
        if (tensionText.isEmpty() || frictionText.isEmpty() || durationText.isEmpty()) {
            return;
        }
        float tension;
        float friction;
        mDuration = 0.f;
        try {
            tension = Float.parseFloat(tensionText);
            friction = Float.parseFloat(frictionText);
            mDuration = Float.parseFloat(durationText);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return;
        }
        mSpringInterpolator = new SpringInterpolator(tension, friction, false);
    }

    @OnClick(R.id.button_visualize)
    public void onVisualizeButtonClick() {
        mBtnVisualize.setEnabled(false);
        mBtnTest.setEnabled(true);
        mSpringCurveView.setVisibility(View.VISIBLE);
        mVgAnimationContainer.setVisibility(View.GONE);
    }

    @OnClick(R.id.button_test)
    public void onTestButtonClick() {
        mBtnVisualize.setEnabled(true);
        mBtnTest.setEnabled(false);
        mSpringCurveView.setVisibility(View.GONE);
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
        if (mSpringInterpolator != null) {
            mAnimationStep = (mAnimationStep + 1) % ANIMATION_RES_IDS.length;
            // do animation
            mIvAnimate.setVisibility(View.VISIBLE);
            Animation animation = AnimationUtils.loadAnimation(getBaseContext(), ANIMATION_RES_IDS[mAnimationStep]);
            animation.setInterpolator(mSpringInterpolator);
            animation.setDuration((int) mDuration);
            animation.setFillAfter(true);
            mIvAnimate.startAnimation(animation);
        }
    }
}
