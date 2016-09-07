package ie.simonkenny.principleanimationinterpolatortest;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by simonkenny on 07/09/2016.
 */
public class MainMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_menu);
        ButterKnife.bind(this);
    }


    @OnClick(R.id.thumb_button_bezier)
    public void onBezierThumbButtonClick() {
        startActivity(new Intent(this, BezierInterpolatorActivity.class));
    }

    @OnClick(R.id.thumb_button_spring)
    public void onSpringThumbButtonClick() {
        startActivity(new Intent(this, SpringInterpolatorActivity.class));
    }
}
