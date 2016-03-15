package gr.crystalogic.sms.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import gr.crystalogic.sms.R;
import gr.crystalogic.sms.utils.Constants;
import gr.crystalogic.sms.utils.Device;

public class SettingsActivity extends BaseActivity {

    private TextView defaultSmsTextView;
    private Button defaultSmsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initToolbar();
        initControls();
    }

    @Override
    protected void onResume() {
        super.onResume();
        activateControls();
    }

    private void initControls() {
        defaultSmsTextView = (TextView) findViewById(R.id.default_sms_status);
        defaultSmsButton = (Button) findViewById(R.id.default_sms_button);
        defaultSmsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Device.makeDefaultApp(SettingsActivity.this, 0);
            }
        });
    }

    private void activateControls() {
        if (Device.isDefaultApp(this)) {
            defaultSmsTextView.setText(R.string.app_is_default_sms_handler);
            defaultSmsButton.setVisibility(View.GONE);
        } else {
            defaultSmsTextView.setText(R.string.app_is_not_the_default_sms_handler);
            defaultSmsButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constants.DEF_SMS_REQ_CODE:
                activateControls();
                break;
        }
    }

}
