package ie.simonkenny.principleanimationinterpolatortest;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;

import java.lang.ref.WeakReference;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by simonkenny on 07/09/2016.
 */
public class BezierInterpolatorActivity extends AppCompatActivity {

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

    WeakReference<EditText> mCurrentEditTextWeakRef;

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
                if (mCurrentEditTextWeakRef != null && mCurrentEditTextWeakRef.get() != null) {
                    EditText editText = mCurrentEditTextWeakRef.get();
                    if (editText == mEtDuration) {
                        editText.setText(String.format(Locale.getDefault(), "%d", i));
                    } else {
                        editText.setText(String.format(Locale.getDefault(), "%.2f", (((float)i) / 1000.f)));
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    private View.OnFocusChangeListener mCoordinateFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean b) {
            if (!(view instanceof EditText)) {
                return;
            }
            EditText editText = (EditText) view;
            try {
                float value = Float.parseFloat(editText.getText().toString());
                mSbValueEdit.setProgress((int)(value * 1000));
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return;
            }
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
            try {
                int value = Integer.parseInt(editText.getText().toString());
                mSbValueEdit.setProgress(value);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return;
            }
            mCurrentEditTextWeakRef = new WeakReference<>(editText);
        }
    };



    /*
    @OnClick(R.id.button_ease_in)
    public void buttonEaseInClick(View v) {
        startEaseInAnimations();
    }

    private void startEaseInAnimations() {
        String c1xText = mEtC1x.getText().toString();
        String c1yText = mEtC1y.getText().toString();
        String c2xText = mEtC2x.getText().toString();
        String c2yText = mEtC2y.getText().toString();
        String durationText = mEtDurationEase.getText().toString();
        if (c1xText.isEmpty() || c1yText.isEmpty() || c2xText.isEmpty() || c2yText.isEmpty() || durationText.isEmpty()) {
            return;
        }
        float c1x;
        float c1y;
        float c2x;
        float c2y;
        float duration;
        try {
            c1x = Float.parseFloat(c1xText);
            c1y = Float.parseFloat(c1yText);
            c2x = Float.parseFloat(c2xText);
            c2y = Float.parseFloat(c2yText);
            duration = Float.parseFloat(durationText);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return;
        }
        Animation animation1 = AnimationUtils.loadAnimation(getBaseContext(), R.anim.translate_in_from_bottom);
        animation1.setInterpolator(new FastOutSlowInInterpolator());
        animation1.setDuration((int)duration);
        Animation animation2 = AnimationUtils.loadAnimation(getBaseContext(), R.anim.translate_in_from_bottom);
        animation2.setInterpolator(new BezierInterpolator(new PointF(c1x, c1y), new PointF(c2x, c2y)));
        animation2.setDuration((int)duration);
        mIv1.startAnimation(animation1);
        mIv2.startAnimation(animation2);
    }
    */
}
