package com.guan.o2o.common;

/**
 * @author Guan
 * @file com.guan.o2o.common
 * @date 2015/9/19
 * @Version 1.0
 */
public class Contant {

    /**
     * 请求网路地址
     */
    public final static String HTTPPATH = "http://172.28.89.98:8080/Server/servlet/";

    /**
     * 文件内存路径
     */
    public static String FILELOCATION = "/storage/sdcard0/files/";
    /**
     * TAG
     */
    public final static String TAG = "TAG";

    /**
     * 第一次登录标志
     */
    public final static int FLAG_LOGN_FIRST = 0000;
    /**
     * 注册失败标志
     */
    public final static int FLAG_REGISTER_ERROR = 0002;
    /**
     * 注册成功标志
     */
    public final static int FLAG_REGISTER_SUCCESS = 2222;
    /**
     * 登录失败标志
     */
    public final static int FLAG_LOGN_ERROR = 0001;
    /**
     * 登录成功标志
     */
    public final static int FLAG_LOGN_SUCCESS = 1111;
    /**
     * 上传文件失败标志
     */
    public final static int FLAG_UPLOAD_ERROR = 0004;
    /**
     * 上传文件成功标志
     */
    public final static int FLAG_UPLOAD_SUCCESS = 4444;
    /**
     * 请求获取文件失败标志
     */
    public final static int FLAG_MAIN_ERROR = 0003;
    /**
     * 请求获取文件成功标志
     */
    public final static int FLAG_MAIN_SUCCESS = 3333;
    /**
     * 延迟3秒
     */
    public static final long SPLASH_DELAY_MILLIS = 3000;


    /**
     * 手机号码格式错误
     */
    public final static String MSG_PHONE_ERROR = "手机号码格式错误";
    /**
     * 密码存在汉字
     */
    public final static String MSG_PASSWORD_ERROR = "密码存在汉字";
    /**
     * 登录失败
     */
    public final static String MSG_LOGIN_FAILED = "登录失败";
    /**
     * 注册失败
     */
    public final static String MSG_REGISTER_FAILED = "注册失败";
    /**
     * 请求服务器出错
     */
    public final static String MSG_SEIVICE_ERROR = "请求服务器出错";

    /**
     * 文件路径为空
     */
    public final static String MSG_FILEPATH_NULL = "文件路径不能为空";

    /**
     * 文件不存在
     */
    public final static String MSG_FILE_NULL = "文件不存在";
    /**
     * 上传失败
     */
    public final static String MSG_UPLOAD_ERROR = "上传失败";
    /**
     * 保存是否第一次登陆客户端
     */
    public static final String SHAREDPREFERENCES_NAME_FIRST = "first_pref";
    /**
     * 保存用户登录信息
     */
    public static final String SHAREDPREFERENCES_NAME_LOGIN = "login_data";

}
