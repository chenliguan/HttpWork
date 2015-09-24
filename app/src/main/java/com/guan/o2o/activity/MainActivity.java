package com.guan.o2o.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.guan.o2o.R;
import com.guan.o2o.adapter.FileAdapter;
import com.guan.o2o.common.Contant;
import com.guan.o2o.exception.ServiceRulesException;
import com.guan.o2o.model.FileBean;
import com.guan.o2o.service.UserService;
import com.guan.o2o.service.UserServiceImpl;
import com.guan.o2o.utils.SharedPreferenceUtil;

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class MainActivity extends FrameActivity {

    @InjectView(R.id.iv_back)
    ImageView ivBack;
    @InjectView(R.id.tv_name)
    TextView tvName;
    @InjectView(R.id.tv_backout)
    TextView tvBackout;
    @InjectView(R.id.btn_upload_local)
    Button btnUploadLocal;
    @InjectView(R.id.image_logo)
    RelativeLayout imageLogo;

    private String mLoginPhone;
    private UserService mUserService;
    private MainHandler mMainHandler;
    private static ListView sLvFile;
    private static List<FileBean> sList;
    private static FileAdapter sFileAdapter;

    /**
     * MainHandler:异步更新界面
     */
    private static class MainHandler extends Handler {

        /**
         * 内部声明一个弱引用，引用外部类.弱引用被GC检查到时回收掉,防止内存泄露
         */
        private WeakReference<MainActivity> mActivityReference;

        public MainHandler(MainActivity activity) {
            mActivityReference = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            int flag = msg.what;
            MainActivity mainActivity = mActivityReference.get();

            switch (flag) {
                case Contant.FLAG_MAIN_ERROR:
                    String errorMsg = (String) msg.getData().getSerializable("ErrorMsg");
                    mainActivity.showMsg(errorMsg);
                    break;

                case Contant.FLAG_MAIN_SUCCESS:
                    sFileAdapter = new FileAdapter(mainActivity, sList);
                    sLvFile.setAdapter(sFileAdapter);
                    break;

                default:
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        /**
         * 初始化变量
         */
        initVariable();
        /**
         * 初始化界面
         */
        initView();
        /**
         * 启动线程请求网络
         */
        requestHttp();
        /**
         * 绑定数据
         */
        bindData();
    }

    /**
     * 初始化变量
     */
    private void initVariable() {
        sList = null;
        mUserService = new UserServiceImpl();
        mMainHandler = new MainHandler(this);

        // 读取SHAREDPREFERENCES_NAME_LOGIN中的数据
        SharedPreferences preferences_login = getSharedPreferences(
                Contant.SHAREDPREFERENCES_NAME_LOGIN, MODE_PRIVATE);
        mLoginPhone = preferences_login.getString("loginPhone", "guan");
    }

    /**
     * 初始化界面
     */
    private void initView() {
        sLvFile = (ListView)findViewById(R.id.lv_file_down);
        tvName.setText(mLoginPhone);
    }

    /**
     * 启动线程请求网络
     */
    private void requestHttp() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    sList = mUserService.getFile();
                    mMainHandler.sendEmptyMessage(Contant.FLAG_MAIN_SUCCESS);
                } catch (ServiceRulesException e) {
                    e.printStackTrace();
                    sendErrorMsg(e.getMessage());
                }
            }
        }).start();
    }

    /**
     * 绑定数据
     */
    private void bindData() {

    }

    /**
     * 监听实现
     */
    @OnClick({R.id.iv_back, R.id.tv_backout, R.id.btn_upload_local})
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.iv_back:
                showMsg("back");
                break;

            case R.id.tv_backout:
                // 将配置文件信息设为空
                SharedPreferenceUtil.sharedPreferences(MainActivity.this,"","");
                // 返回登陆页面
                openActivity(LoginActivity.class);
                this.finish();
                break;

            case R.id.btn_upload_local:
                openActivity(UploadActivity.class);
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
        msg.what = Contant.FLAG_MAIN_ERROR;
        msg.setData(bundle);
        mMainHandler.sendMessage(msg);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMainHandler.removeCallbacksAndMessages(null);
    }
}
