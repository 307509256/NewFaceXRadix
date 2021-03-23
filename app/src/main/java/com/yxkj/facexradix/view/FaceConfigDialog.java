package com.yxkj.facexradix.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.baidu.idl.face.main.model.SingleBaseConfig;
import com.baidu.idl.face.main.utils.ConfigUtils;
import com.yxkj.facexradix.R;

import java.math.BigDecimal;

/**
 * TODO: document your custom view class.
 */

public class FaceConfigDialog extends AlertDialog implements View.OnClickListener, View.OnFocusChangeListener {

    private TextView livevalue;
    private TextView facevalue;

    public FaceConfigDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.face_config_dialog);
        ImageButton faceValueLess = findViewById(R.id.faceValueLess);
        faceValueLess.setOnClickListener(this);
        facevalue = findViewById(R.id.facevalue);
        ImageButton faceValuePlus = findViewById(R.id.faceValuePlus);
        faceValuePlus.setOnClickListener(this);
        ImageButton liveValueLess = findViewById(R.id.liveValueLess);
        liveValueLess.setOnClickListener(this);
        livevalue = findViewById(R.id.livevalue);
        ImageButton liveValuePlus = findViewById(R.id.liveValuePlus);
        liveValuePlus.setOnClickListener(this);
        facevalue.setText("" + SingleBaseConfig.getBaseConfig().getThreshold());
        livevalue.setText("" + SingleBaseConfig.getBaseConfig().getRgbLiveScore());
    }

    @Override
    public void onClick(View v) {
        int threshold = SingleBaseConfig.getBaseConfig().getThreshold();
        BigDecimal f1 = new BigDecimal(Float.toString(SingleBaseConfig.getBaseConfig().getRgbLiveScore()));
        BigDecimal f2 = new BigDecimal(Float.toString(0.1f));
        switch (v.getId()) {
            case R.id.faceValueLess:
                if (threshold > 1) {
                    SingleBaseConfig.getBaseConfig().setThreshold(SingleBaseConfig.getBaseConfig().getThreshold() - 1);
                    ConfigUtils.modityJson();
                    facevalue.setText("" + SingleBaseConfig.getBaseConfig().getThreshold());
                }
                break;
            case R.id.faceValuePlus:
                if (threshold < 100) {
                    SingleBaseConfig.getBaseConfig().setThreshold(SingleBaseConfig.getBaseConfig().getThreshold() + 1);
                    ConfigUtils.modityJson();
                    facevalue.setText("" + SingleBaseConfig.getBaseConfig().getThreshold());
                }
                break;
            case R.id.liveValueLess:
                if (f1.floatValue() > 0.1) {
                    SingleBaseConfig.getBaseConfig().setRgbLiveScore(f1.subtract(f2).floatValue());
                    ConfigUtils.modityJson();
                    livevalue.setText("" + SingleBaseConfig.getBaseConfig().getRgbLiveScore());
                }
                break;
            case R.id.liveValuePlus:
                if (f1.floatValue() < 0.9) {
                    SingleBaseConfig.getBaseConfig().setRgbLiveScore(f1.add(f2).floatValue());
                    ConfigUtils.modityJson();
                    livevalue.setText("" + SingleBaseConfig.getBaseConfig().getRgbLiveScore());
                }
                break;
        }
    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {

    }
}
