package com.guan.o2o.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.guan.o2o.common.Contant;

/**
 * @author Guan
 * @file com.guan.o2o.utils
 * @date 2015/9/19
 * @Version 1.0
 */
public class SharedPreferenceUtil {

    /**
     * 持久化用户信息
     */
    public static void sharedPreferences(Context context, String loginPhone, String loginCode) {
        //1、实例化SharedPreferences对象
        SharedPreferences preferences = context.getSharedPreferences(
                Contant.SHAREDPREFERENCES_NAME_LOGIN, Context.MODE_PRIVATE);
        //2、实例化SharedPreferences.Editor对象
        SharedPreferences.Editor editor = preferences.edit();
        //3、editor.put()存入数据
        editor.putString("loginPhone", loginPhone);
        editor.putString("loginCode", loginCode);
        //4、commit()提交修改
        editor.commit();
    }
}
