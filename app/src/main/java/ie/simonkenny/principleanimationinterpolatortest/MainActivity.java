package ie.simonkenny.principleanimationinterpolatortest;

import android.graphics.PointF;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BaseInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ie.simonkenny.principleanimationinterpolatortest.interpolators.BezierInterpolator;
import ie.simonkenny.principleanimationinterpolatortest.interpolators.SpringInterpolator;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.edit_text_tension)
    EditText mEtTension;
    @Bind(R.id.edit_text_friction)
    EditText mEtFriction;
    @Bind(R.id.edit_text_duration_spring)
    EditText mEtDurationSpring;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
    }

    //SpringInterpolator

    @OnClick(R.id.button_overshoot_spring)
    public void buttonOvershootSprintClick(View v) {
        startOvershootSpringAnimations();
    }

    private void startOvershootSpringAnimations() {
        String tensionText = mEtTension.getText().toString();
        String frictionText = mEtFriction.getText().toString();
        String durationText = mEtDurationSpring.getText().toString();
        if (tensionText.isEmpty() || frictionText.isEmpty() || durationText.isEmpty()) {
            return;
        }
        float tension;
        float friction;
        float duration;
        try {
            tension = Float.parseFloat(tensionText);
            friction = Float.parseFloat(frictionText);
            duration = Float.parseFloat(durationText);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return;
        }
        Animation animation1 = AnimationUtils.loadAnimation(getBaseContext(), R.anim.translate_in_from_bottom);
        animation1.setInterpolator(new OvershootInterpolator());
        animation1.setDuration((int)duration);
        Animation animation2 = AnimationUtils.loadAnimation(getBaseContext(), R.anim.translate_in_from_bottom);
        //animation2.setInterpolator(new SpringInterpolator(200, 20, false));
        animation2.setInterpolator(new SpringInterpolator(tension, friction, false));
        animation2.setDuration((int)duration);
        //mIv3.startAnimation(animation1);
        //mIv4.startAnimation(animation2);
    }
}
