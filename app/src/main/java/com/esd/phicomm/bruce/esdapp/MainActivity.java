package com.esd.phicomm.bruce.esdapp;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Runnable checkVoltage;
    private final String TAG = "MainActivity";
    private TextView txtResult;
    private TextView txtUserId;
    private Button btnCancel;
    private Gpio gpio = new Gpio("PE4");

    private String[] promoteText = {"  等待测试......", "  测试通过", "  测试失败"};
    private int[] promoteColor = {Color.YELLOW, Color.GREEN, Color.RED};

    private enum Promote {
        WAIT,
        PASS,
        FAIL
    }

    Handler handler = new MyHandler();

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case R.id.wait:
                    setPromoteOption(Promote.WAIT.ordinal());
                    break;

                case R.id.pass:
                    setPromoteOption(Promote.PASS.ordinal());
                    break;

                case R.id.fail:
                    setPromoteOption(Promote.FAIL.ordinal());
                    break;
            }
        }
    }

    class TestingThread implements Runnable{

        @Override
        public void run() {
            Message msg = handler.obtainMessage();
            if (isTestOk()) {
                msg.what = R.id.pass;
                handler.handleMessage(msg);
            } else {
                msg.what = R.id.fail;
                handler.handleMessage(msg);
            }
            txtUserId.setText("");
            txtUserId.requestFocus();

            checkVoltage = new Runnable() {
                @Override
                public void run() {
                    Message msg = new Message();
                    msg.what = R.id.wait;
                    handler.sendMessageDelayed(msg, 3000);
                }
            };
            checkVoltage.run();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getControls();
        setListener();

        setPromoteOption(Promote.WAIT.ordinal());
        txtUserId.setFocusable(true);
        txtUserId.setFocusableInTouchMode(true);
    }

    private void getControls() {
        txtUserId = (TextView) this.findViewById(R.id.txtId);
        txtResult = (TextView) this.findViewById(R.id.txtShow);
        btnCancel = (Button) this.findViewById(R.id.btnCancelTest);
    }

    private void setListener() {
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPromoteOption(Promote.WAIT.ordinal());
                txtUserId.requestFocus();
            }
        });

        txtUserId.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_ENTER) {
                    String userId = txtUserId.getText().toString();
                    if (userId.equals("admin")) {
                        finish();
                        return true;
                    }

                    if (userId.equals("")) {
                        txtUserId.requestFocus();
                        return true;
                    }

                    new Thread(new TestingThread()).start();
                    return true;
                }

                return false;
            }
        });
    }

    private boolean isTestOk() {
        long start = System.currentTimeMillis();
        long end = System.currentTimeMillis();
        while (end - start < 15000) {
            int value = gpio.input();
            if (value == 0)
                return true;

            end = System.currentTimeMillis();
        }

        return false;
    }

    private void setPromoteOption(final int index) {
        txtResult.setText(promoteText[index]);
        txtResult.setBackgroundColor(promoteColor[index]);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_HOME:
                return true;
            case KeyEvent.KEYCODE_BACK:
                return true;
            case KeyEvent.KEYCODE_APP_SWITCH:
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}

