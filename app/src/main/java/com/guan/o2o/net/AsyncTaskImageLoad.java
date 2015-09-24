package com.guan.o2o.net;

/**
 * @author Guan
 * @file com.guan.o2o.net
 * @date 2015/9/22
 * @Version 1.0
 */

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.guan.o2o.common.Contant;
import com.guan.o2o.utils.BitmapUtil;
import com.guan.o2o.utils.HttpUtil;
import com.guan.o2o.utils.LogUtil;

import static com.guan.o2o.utils.BitmapUtil.storeImageToSDCARD;

/**
 * 异步请求异步类
 */
public class AsyncTaskImageLoad extends AsyncTask<String, Integer, Bitmap> {

    private String httpPath = null;
    private ImageView imageView = null;

    public AsyncTaskImageLoad(ImageView imageView) {
        this.imageView = imageView;
    }

    //运行在子线程中
    protected Bitmap doInBackground(String... params) {
        try {
            httpPath = params[0];
            // 网络获取bitmap
            Bitmap bitmap  = HttpUtil.getBitmapURLRequest(params[0]);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);
        if (imageView != null && result != null) {
            imageView.setImageBitmap(result);
            // 存储图片到SD卡（URL地址保存）
            BitmapUtil.storeImageToSDCARD(result, httpPath.hashCode() + "", Contant.FILELOCATION);
        }
    }
}