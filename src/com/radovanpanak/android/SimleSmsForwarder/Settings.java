package com.radovanpanak.android.SimleSmsForwarder;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class Settings extends Activity {

    public static final String SETTINGS = "SimpleSmsForwarderSettings";
    public static final String SETTINGS_ENABLED = SETTINGS + "_enabled";
    public static final String SETTINGS_TARGET_NO = SETTINGS + "_targetNo";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        final EditText targetNo = (EditText) findViewById(R.id.targetNo);
        final CheckBox enabled = (CheckBox) findViewById(R.id.enabled);
        final Button ok = (Button) findViewById(R.id.ok);
        final Button cancel = (Button) findViewById(R.id.cancel);

        final SharedPreferences settings = getSharedPreferences(SETTINGS, MODE_PRIVATE);
        final String targetNoSetting = settings.getString(SETTINGS_TARGET_NO, "");
        final boolean enabledSetting = settings.getBoolean(SETTINGS_ENABLED, false);

        targetNo.setText(targetNoSetting);
        enabled.setChecked(enabledSetting);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final SharedPreferences.Editor editor = settings.edit();
                editor.putString(SETTINGS_TARGET_NO, targetNo.getText().toString());
                editor.putBoolean(SETTINGS_ENABLED, enabled.isChecked());
                editor.commit();
                finish();
            }
        });
    }
}
