package com.nuwarobotics.example.custombehavior.behavior;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.nuwarobotics.example.custombehavior.R;

/**
 * FOR DEMO USEGE
 * This Activity use to start Custom BehaviorService
 */

public class CustomBehaviorActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();

    EditText mResult;
    Button mStartBtn;
    Button mStopBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(TAG);

        mResult = (EditText) findViewById(R.id.text_result);
        mResult.setText("");
        mStartBtn = (Button) findViewById(R.id.btn_start);
        mStartBtn.setEnabled(true);
        mStopBtn = (Button) findViewById(R.id.btn_stop);
        mStopBtn.setEnabled(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void BtnStart(View view) {
        setText("Start Service: " + this.getPackageName() + ".behavior.CustomService", false);
        Intent intent = new Intent();
        intent.setClassName(this.getPackageName(), this.getPackageName() + ".behavior.CustomService");
        startForegroundService(intent);
    }

    public void BtnStop(View view) {
        setText("Stop Service: " + this.getPackageName() + ".behavior.CustomService", false);
        Intent intent = new Intent();
        intent.setClassName(this.getPackageName(), this.getPackageName() + ".behavior.CustomService");
        stopService(intent);
    }

    private void setText(final String text, final boolean append) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mResult.append(text);
                if (!append)
                    mResult.append("\n");
                mResult.setMovementMethod(ScrollingMovementMethod.getInstance());
                mResult.setSelection(mResult.getText().length(), mResult.getText().length());
            }
        });
    }

}
