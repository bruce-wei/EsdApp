package com.esd.phicomm.bruce.esdapp;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private View titleBar;
    private TextView txtResult;
    private TextView txtResultEng;
    private TextView txtUserId;
    private Button btnCancel;
    private Gpio gpio = new Gpio("PE4");
    private boolean enterFinished = true;
    private boolean bCancel;

    private String[] promoteText = {"等待测试...", "测试通过！",
            "测试失败！", "测试中...", "用户不存在", "无法连接服务器"};
    private String[] promoteTextEng = {"Waiting......", "P A S S......",
            "F A I L......", "Testing...", "No User!", "ShopFloorError!"};
    private int[] promoteColor = {Color.BLACK, Color.GREEN,
            Color.RED, Color.BLUE, Color.RED, Color.RED};
    private int[] promoteColorEng = {Color.argb(70, 0, 0, 0), Color.argb(70, 0, 255, 0),
            Color.argb(70, 255, 0, 0), Color.argb(70, 0, 0, 255), Color.argb(70, 255, 0, 0), Color.argb(70, 255, 0, 0)};

    private enum Promote {
        WAIT,
        PASS,
        FAIL,
        TESTING,
        NOUSER,
        SHOPFLOORERROR
    }

    private enum TestResult{
        FAIL,
        PASS
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case R.id.wait:
                    setPromoteOption(Promote.WAIT.ordinal());
                    setBarColor(Color.argb(255, 254, 162, 87));
                    break;

                case R.id.pass:
                    setPromoteOption(Promote.PASS.ordinal());
                    setBarColor(Color.GREEN);
                    waitTesting();
                    break;

                case R.id.fail:
                    setPromoteOption(Promote.FAIL.ordinal());
                    setBarColor(Color.RED);
                    waitTesting();
                    break;

                case R.id.testing:
                    setPromoteOption(Promote.TESTING.ordinal());
                    break;

                case R.id.no_user:
                    setPromoteOption(Promote.NOUSER.ordinal());
                    setBarColor(Color.RED);
                    waitTesting();
                    break;

                case R.id.shopfloor_error:
                    setPromoteOption(Promote.SHOPFLOORERROR.ordinal());
                    setBarColor(Color.RED);
                    waitTesting();
                    break;
            }
        }
    };

    class TestingThread extends Thread{

        private String userId;

        TestingThread(String userId) {
            this.userId = userId;
        }
        @Override
        public void run() {
            Message msg = handler.obtainMessage();
            UserInfo user;
            try {
                user = UserBll.getModel(userId);
            } catch (Exception e) {
                String error = e.getMessage();
                switch (error) {
                    case "无法连接ShopFloor":
                        msg.what = R.id.shopfloor_error;
                        break;
                    case "员工信息不存在":
                        msg.what = R.id.no_user;
                        break;
                    default:
                        msg.what = R.id.wait;
                        break;
                }

                handler.sendMessage(msg);
                enterFinished = true;
                return;
            }

            if (isTestOk()) {
                user.setHandResult(TestResult.PASS.toString());
                user.setFootResult(TestResult.PASS.toString());
                msg.what = R.id.pass;
            } else {
                user.setHandResult(TestResult.FAIL.toString());
                user.setFootResult(TestResult.FAIL.toString());
                msg.what = R.id.fail;
            }

            if (!UserBll.insertModel(user))
                msg.what = R.id.fail;

            handler.sendMessage(msg);
            enterFinished = true;
        }
    }

    private void setBarColor(int color) {
        titleBar.setBackgroundColor(color);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();
        getControls();
        setListener();

        setPromoteOption(Promote.WAIT.ordinal());
        txtUserId.setFocusable(true);
        txtUserId.setFocusableInTouchMode(true);
    }

    private void getControls() {
        txtUserId = (TextView) this.findViewById(R.id.txtId);
        txtResult = (TextView) this.findViewById(R.id.txtShow);
        txtResultEng = (TextView) this.findViewById(R.id.txtShowEng);
        btnCancel = (Button) this.findViewById(R.id.btnCancelTest);
        titleBar = this.findViewById(R.id.barTitle);
    }

    private void setListener() {
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPromoteOption(Promote.WAIT.ordinal());
                clearUserId();
                bCancel = true;
            }
        });

        txtUserId.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {

                if (!enterFinished)
                    return true;

                if (i == KeyEvent.KEYCODE_ENTER) {
                    enterFinished = false;
                    bCancel = false;
                    String userId = txtUserId.getText().toString();
                    if (userId.equals("admin")) {
                        finish();
                        return true;
                    }

                    if (userId.equals("")) {
                        txtUserId.requestFocus();
                        return true;
                    }

                    Message msg = new Message();
                    msg.what = R.id.testing;
                    handler.sendMessage(msg);
                    new TestingThread(userId).start();
                    return true;
                }

                return false;
            }
        });
    }

    private boolean isTestOk() {
        long start = System.currentTimeMillis();
        long end = System.currentTimeMillis();
        while (end - start < 10000 && !bCancel) {
            int value = gpio.input();
            if (value == 0)
                return true;

            end = System.currentTimeMillis();
        }

        return false;
    }

    private void setPromoteOption(final int index) {
        txtResult.setText(promoteText[index]);
        txtResult.setTextColor(promoteColor[index]);
        txtResultEng.setText(promoteTextEng[index]);
        txtResultEng.setTextColor(promoteColorEng[index]);
    }

    private void waitTesting() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = R.id.wait;
                handler.sendMessageDelayed(msg, 3000);
            }
        }).start();
        clearUserId();
    }

    private void clearUserId() {
        txtUserId.setText("");
        txtUserId.requestFocus();
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

