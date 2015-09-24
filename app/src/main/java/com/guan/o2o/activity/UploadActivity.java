package com.guan.o2o.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.guan.o2o.R;
import com.guan.o2o.adapter.FileAdapter;
import com.guan.o2o.common.Contant;
import com.guan.o2o.exception.ServiceRulesException;
import com.guan.o2o.net.AsyncTaskImageLoad;
import com.guan.o2o.utils.BitmapUtil;
import com.guan.o2o.utils.HttpUtil;
import com.guan.o2o.utils.LogUtil;
import com.guan.o2o.view.CircleImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.net.URL;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class UploadActivity extends FrameActivity {

    @InjectView(R.id.iv_back)
    ImageView ivBack;
    @InjectView(R.id.image_logo)
    RelativeLayout imageLogo;
    @InjectView(R.id.iv_head)
    CircleImageView ivHead;
    @InjectView(R.id.btn_upload_album)
    Button btnUploadAlbum;
    @InjectView(R.id.btn_upload_photo)
    Button btnUploadPhoto;

    private Uri imageUri;
    private UploadHandler uploadHandler;
    public static final int REDLY_PHOTO = 0;
    public static final int TAKE_PHOTO = 1;
    public static final int CROP_PHOTO = 2;

    /**
     * UploadHandler:异步更新界面
     */
    private static class UploadHandler extends Handler {

        /**
         * 内部声明一个弱引用，引用外部类.弱引用被GC检查到时回收掉,防止内存泄露
         */
        private WeakReference<UploadActivity> mActivityReference;

        public UploadHandler(UploadActivity activity) {
            mActivityReference = new WeakReference<UploadActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            int flag = msg.what;
            UploadActivity uploadActivity = mActivityReference.get();

            switch (flag) {
                case Contant.FLAG_UPLOAD_ERROR:
                    String errorMsg = (String) msg.getData().getSerializable("ErrorMsg");
                    uploadActivity.showMsg(errorMsg);
                    break;

                case Contant.FLAG_UPLOAD_SUCCESS:
                    uploadActivity.showMsg("上传成功");
                    break;

                default:
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        ButterKnife.inject(this);

        /**
         * 初始化变量
         */
        initVariable();
        /**
         * 绑定数据
         */
        bindData();
    }

    /**
     * 初始化变量
     */
    private void initVariable() {
        imageUri = null;
        uploadHandler = new UploadHandler(this);
    }

    /**
     * 绑定数据
     */
    private void bindData() {
        String httpPath = Contant.HTTPPATH + "GetImageServlet?filename=" + "head";
        // 从本地获取
        Bitmap bitmap = BitmapUtil.getBitmapBySDCARD(Contant.FILELOCATION, "head");
        if (bitmap != null) {
            ivHead.setImageBitmap(bitmap);
        } else {
            //异步加载图片资源
            AsyncTaskImageLoad async = new AsyncTaskImageLoad(ivHead);
            //执行异步加载，并把图片的路径传送过去
            async.execute(httpPath);
        }
    }

    /**
     * 监听实现
     */
    @OnClick({R.id.iv_back, R.id.btn_upload_album, R.id.btn_upload_photo})
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.iv_back:
                openActivity(MainActivity.class);
                this.finish();
                break;

            case R.id.btn_upload_album:
                // 创建File对象,存储选择的照片
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, REDLY_PHOTO);
                break;

            case R.id.btn_upload_photo:
                // 创建File对象,存储拍照后的图片
                File outputImage = file("phone");
                imageUri = Uri.fromFile(outputImage);
                Intent intents = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intents.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                // 启动相机程序
                startActivityForResult(intents, TAKE_PHOTO);
                break;

            default:
                break;
        }
    }

    /**
     * onActivityResult
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            case REDLY_PHOTO:
                if (resultCode == RESULT_OK) {
                    // 裁剪图片 获得图片的绝对uri
                    cropPhoto(data.getData());
                }
                break;

            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    cropPhoto(imageUri);
                }
                break;

            case CROP_PHOTO:
                if (data != null) {
                    if (resultCode == RESULT_OK) {
                        Bitmap bitmap = null;
                        Bundle extras = data.getExtras();
                        bitmap = extras.getParcelable("data");
                        if (bitmap != null) {
                            // 显示裁剪后的照片
                            ivHead.setImageBitmap(bitmap);
                            // 存储图片到SD卡（URL地址保存）
                            BitmapUtil.storeImageToSDCARD(bitmap, "head", Contant.FILELOCATION);
                            // 上传图片
                            requestHttp();
                        }
                    }
                } else {
                    showMsg("图片为空");
                }
                break;

            default:
                break;
        }
    }

    /**
     * @param imageUri
     * @description 裁剪图片
     */
    public void cropPhoto(Uri imageUri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(imageUri, "image/*");
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        // 启动裁剪程序
        startActivityForResult(intent, CROP_PHOTO);
    }

    /**
     * 创建FILE对象
     *
     * @param fileName
     * @return
     */
    private File file(String fileName) {
        File outputImage = new File(Environment.getExternalStorageDirectory(), fileName + ".jpg");

        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return outputImage;
    }

    /**
     * 上传图片线程
     */
    private void requestHttp() {
        try {
            // 上传服务器
            HttpUtil.uploadFileURLRequest(Contant.HTTPPATH + "UploadServlet", Contant.FILELOCATION + "head.jpg");
            uploadHandler.sendEmptyMessage(Contant.FLAG_UPLOAD_SUCCESS);
        } catch (ServiceRulesException e) {
            e.printStackTrace();
            sendErrorMsg(Contant.MSG_SEIVICE_ERROR);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送错误handler消息
     */
    private void sendErrorMsg(String message) {
        Message msg = new Message();
        Bundle bundle = new Bundle();
        bundle.putSerializable("ErrorMsg", message);
        msg.what = Contant.FLAG_UPLOAD_ERROR;
        msg.setData(bundle);
        uploadHandler.sendMessage(msg);
    }
}
