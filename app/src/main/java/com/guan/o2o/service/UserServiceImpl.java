package com.guan.o2o.service;

import android.util.Log;

import com.guan.o2o.common.Contant;
import com.guan.o2o.exception.ServiceRulesException;
import com.guan.o2o.model.FileBean;
import com.guan.o2o.model.ResultBean;
import com.guan.o2o.model.UserBean;
import com.guan.o2o.utils.HttpUtil;
import com.guan.o2o.utils.LogUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.guan.o2o.utils.RegularExpressUtil.isChineseNo;
import static com.guan.o2o.utils.RegularExpressUtil.isMobileNO;

/**
 * @author Guan
 * @file com.guan.o2o.service
 * @date 2015/9/18
 * @Version 1.0
 */
public class UserServiceImpl implements UserService {

    /**
     * 登录业务
     *
     * @param loginPhone
     * @param loginCode
     * @return
     * @throws Exception
     */
    @Override
    public boolean userLogin(String loginPhone, String loginCode) throws ServiceRulesException {

        String path = null;
        String response = null;
        if (isMobileNO(loginPhone)) {
            if (!isChineseNo(loginCode)) {
                path = Contant.HTTPPATH + "LoginServlet?loginPhone=" + loginPhone + "&loginCode=" + loginCode;

                // 请求网络
                try {
                    response = HttpUtil.getStringURLRequest(path);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new ServiceRulesException(Contant.MSG_SEIVICE_ERROR);
                }
                
            } else {
                throw new ServiceRulesException(Contant.MSG_PASSWORD_ERROR);
            }
        } else {
            throw new ServiceRulesException(Contant.MSG_PHONE_ERROR);
        }

        // 登录业务判断
        if (response.equals("success")) {
            return true;
        } else {
            throw new ServiceRulesException(Contant.MSG_LOGIN_FAILED);
        }
    }


    /**
     * 注册业务
     *
     * @param registerPhone
     * @param registerCode
     * @return
     * @throws Exception
     */
    @Override
    public boolean userRegister(String registerPhone, String registerCode) throws ServiceRulesException {

        String path = null;
        ResultBean resultBean;

        if (isMobileNO(registerPhone)) {
            if (!isChineseNo(registerCode)) {
                path = Contant.HTTPPATH + "RegisterServlet";
                // 封装json数据
                String registerData = UserBean.toJson(registerPhone, registerCode);

                // 请求网络
                String response = null;
                try {
                    response = HttpUtil.getJsonStringRequest(path, registerData);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new ServiceRulesException(Contant.MSG_SEIVICE_ERROR);
                }

                // 解析json数据
                resultBean = ResultBean.praseJson(response);
            } else {
                throw new ServiceRulesException(Contant.MSG_PASSWORD_ERROR);
            }
        } else {
            throw new ServiceRulesException(Contant.MSG_PHONE_ERROR);
        }

        // 注册业务判断
        if (resultBean.getResult().equals("success")) {
            return true;
        } else {
            // 返回服务器传递的信息
            throw new ServiceRulesException(resultBean.getMsg());
        }
    }

    /**
     * 获取服务器文件列表
     * @return
     * @throws ServiceRulesException
     */
    @Override
    public List<FileBean> getFile() throws ServiceRulesException {

        String response = null;
        List<FileBean> list = new ArrayList<FileBean>();
        String path = Contant.HTTPPATH + "StudentServlet";

        // 请求网络
        try {
            response = HttpUtil.getStringURLRequest(path);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceRulesException(Contant.MSG_SEIVICE_ERROR);
        }

        // 解析json数据
        list = FileBean.praseJson(response);

        return list;
    }
}
