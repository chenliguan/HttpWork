package com.guan.o2o.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.guan.o2o.R;
import com.guan.o2o.common.Contant;
import com.guan.o2o.exception.ServiceRulesException;
import com.guan.o2o.service.UserService;
import com.guan.o2o.service.UserServiceImpl;
import com.guan.o2o.utils.SharedPreferenceUtil;

import java.lang.ref.WeakReference;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class RegisterActivity extends FrameActivity {

    @InjectView(R.id.iv_back)
    ImageView ivBack;
    @InjectView(R.id.image_logo)
    RelativeLayout imageLogo;
    @InjectView(R.id.et_phone)
    EditText etPhone;
    @InjectView(R.id.btn_code)
    Button btnCode;
    @InjectView(R.id.et_code)
    EditText etCode;
    @InjectView(R.id.btn_register_login)
    Button btnRegisterLogin;
    @InjectView(R.id.tv_book1)
    TextView tvBook1;
    @InjectView(R.id.tv_book2)
    TextView tvBook2;

    public static String loginPhone;
    public static String loginCode;
    public static Context context;
    private UserService userService;
    private RegisterHandler registerHandler;

    /**
     * RegisterHandler:判断登录是否成功
     */
    private static class RegisterHandler extends Handler {

        /**
         * 内部声明一个弱引用，引用外部类.弱引用被GC检查到时回收掉,防止内存泄露
         */
        private WeakReference<RegisterActivity> mActivityReference;

        public RegisterHandler(RegisterActivity activity) {
            mActivityReference = new WeakReference<RegisterActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            int flag = msg.what;
            RegisterActivity registerActivity = mActivityReference.get();

            switch (flag) {
                case Contant.FLAG_REGISTER_ERROR:
                    String errorMsg = (String) msg.getData().getSerializable("ErrorMsg");
                    registerActivity.showMsg(errorMsg);
                    break;

                case Contant.FLAG_REGISTER_SUCCESS:
                    SharedPreferenceUtil.sharedPreferences(context, loginPhone, loginCode);
                    registerActivity.openActivity(MainActivity.class);
                    registerActivity.finish();
                    break;

                default:
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.inject(this);

        /**
         * 初始化变量
         */
        initVariable();
    }

    /**
     * 初始化变量
     */
    private void initVariable() {
        context = RegisterActivity.this;
        userService = new UserServiceImpl();
        registerHandler = new RegisterHandler(this);
    }

    /**
     * 监听实现
     */
    @OnClick({R.id.iv_back, R.id.btn_code, R.id.btn_register_login, R.id.tv_book2})
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.iv_back:
                openActivity(LoginActivity.class);
                this.finish();
                break;

            case R.id.btn_code:
                showMsg("13751338740");
                break;

            case R.id.btn_register_login:

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        loginPhone = etPhone.getText().toString();
                        loginCode = etCode.getText().toString();

                        try {
                            userService.userRegister(loginPhone, loginCode);
                            registerHandler.sendEmptyMessage(Contant.FLAG_REGISTER_SUCCESS);
                        } catch (ServiceRulesException e) {
                            e.printStackTrace();
                            sendErrorMsg(e.getMessage());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

                break;

            case R.id.tv_book2:

                break;

            default:
                break;
        }
    }

    /**
     * 发送错误handler消息
     */
    private void sendErrorMsg(String message) {
        Message msg = new Message();
        Bundle bundle = new Bundle();
        bundle.putSerializable("ErrorMsg", message);
        msg.what = Contant.FLAG_REGISTER_ERROR;
        msg.setData(bundle);
        registerHandler.sendMessage(msg);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        registerHandler.removeCallbacksAndMessages(null);
    }

}
