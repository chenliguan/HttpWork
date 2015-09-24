package com.guan.o2o.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.guan.o2o.R;
import com.guan.o2o.common.Contant;
import com.guan.o2o.model.FileBean;
import com.guan.o2o.net.AsyncTaskImageLoad;
import com.guan.o2o.utils.BitmapUtil;
import com.guan.o2o.utils.HttpUtil;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * @author Guan
 * @file com.guan.o2o.adapter
 * @date 2015/9/21
 * @Version 1.0
 */
public class FileAdapter extends BaseToAdapter {

    private List<FileBean> mList;
    private Context mContext;
    private FileBean mFileBean;

    public FileAdapter(Context context, List<FileBean> list) {
        super(context, list);
        mContext = context;
        mList = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_line, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        // 获取数据对象
        mFileBean = (FileBean) getItem(position);
        //加载图片资源
        loadImage(holder.ivFile, mFileBean.getPath());
        holder.tvFileName.setText(mFileBean.getName());

        return convertView;
    }

    static class ViewHolder {
        @InjectView(R.id.iv_file)
        ImageView ivFile;
        @InjectView(R.id.tv_file_name)
        TextView tvFileName;
        @InjectView(R.id.tv_down)
        TextView tvDown;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    /**
     * 加载图片方法
     *
     * @param imageView
     * @param httpPath
     */
    private static void loadImage(ImageView imageView, String httpPath) {

        Bitmap bitmap = BitmapUtil.getBitmapBySDCARD(Contant.FILELOCATION, String.valueOf(httpPath.hashCode()));
        if (bitmap != null) {
            // 本地存在图片直接显示
            imageView.setImageBitmap(bitmap);
        } else {
            // 否则，异步加载图片资源
            AsyncTaskImageLoad async = new AsyncTaskImageLoad(imageView);
            //执行异步加载，并把图片的路径传送过去
            async.execute(httpPath);
        }
    }
}
