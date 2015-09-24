package com.guan.o2o.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import com.guan.o2o.R;
import com.guan.o2o.common.Contant;
import com.guan.o2o.exception.ServiceRulesException;
import com.guan.o2o.service.UserService;
import com.guan.o2o.service.UserServiceImpl;

import java.lang.ref.WeakReference;

public class SplashActivity extends FrameActivity {

    /**
     * 记录是否第一次启动
     */
    private boolean mIsFirstIn;
    private UserService mUserService;
    private SplashHandler mSplashHandler;
    public static String loginPhone;
    public static String loginCode;

    /**
     * Handler:跳转到不同界
     */
    private static class SplashHandler extends Handler {

        /**
         * 内部声明一个弱引用，引用外部类.弱引用被GC检查到时回收掉,防止内存泄露
         */
        private WeakReference<SplashActivity> mActivityReference;

        public SplashHandler(SplashActivity activity) {
            mActivityReference = new WeakReference<SplashActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            int flag = msg.what;
            SplashActivity splashActivity = mActivityReference.get();

            switch (flag) {

                case Contant.FLAG_LOGN_FIRST:
                    splashActivity.openActivity(GuideActivity.class);
                    splashActivity.finish();
                    break;

                case Contant.FLAG_LOGN_ERROR:
                    splashActivity.openActivity(LoginActivity.class);
                    splashActivity.finish();
                    splashActivity.showMsg((String) msg.getData().getSerializable("ErrorMsg"));
                    break;

                case Contant.FLAG_LOGN_SUCCESS:
                    splashActivity.openActivity(MainActivity.class);
                    splashActivity.finish();
                    break;

                default:
                    break;
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);

        /**
         * 初始化数据
         */
        initVariable();

        /**
         * 判断选择跳转Activity
         */
        switchActivity();
    }

    /**
     * 初始化数据
     */
    private void initVariable() {

        loginCode = null;
        loginPhone = null;
        mIsFirstIn = false;
        mUserService = new UserServiceImpl();
        mSplashHandler = new SplashHandler(this);

        // 读取SHAREDPREFERENCES_NAME_FIRST中的数据
        SharedPreferences preferences_first = getSharedPreferences(
                Contant.SHAREDPREFERENCES_NAME_FIRST, MODE_PRIVATE);

        // 读取SHAREDPREFERENCES_NAME_LOGIN中的数据
        SharedPreferences preferences_login = getSharedPreferences(
                Contant.SHAREDPREFERENCES_NAME_LOGIN, MODE_PRIVATE);

        // 取得相应的值，如果没有该值用true作为默认值
        mIsFirstIn = preferences_first.getBoolean("isFirstIn", true);
        loginPhone = preferences_login.getString("loginPhone", "");
        loginCode = preferences_login.getString("loginCode", "");
    }

    /**
     * 判断选择跳转Activity
     */
    private void switchActivity() {
        // 判断程序与第几次运行
        if (!mIsFirstIn) {

            new Thread(new Runnable() {
                @Override
                public void run() {

                    try {
                        mUserService.userLogin(loginPhone, loginCode);
                        mSplashHandler.sendEmptyMessageDelayed(Contant.FLAG_LOGN_SUCCESS, Contant.SPLASH_DELAY_MILLIS);
                    } catch (ServiceRulesException e) {
                        e.printStackTrace();
                        sendErrorMsg(e.getMessage());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else {
            mSplashHandler.sendEmptyMessageDelayed(Contant.FLAG_LOGN_FIRST, Contant.SPLASH_DELAY_MILLIS);
        }
    }

    /**
     * 发送错误handler消息
     */
    private void sendErrorMsg(String message) {
        Message msg = new Message();
        Bundle bundle = new Bundle();
        bundle.putSerializable("ErrorMsg", message);
        msg.what = Contant.FLAG_LOGN_ERROR;
        msg.setData(bundle);
        mSplashHandler.sendMessageDelayed(msg, Contant.SPLASH_DELAY_MILLIS);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSplashHandler.removeCallbacksAndMessages(null);
    }
}
