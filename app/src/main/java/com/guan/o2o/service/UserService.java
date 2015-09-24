package com.guan.o2o.service;

import com.guan.o2o.exception.ServiceRulesException;
import com.guan.o2o.model.FileBean;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Guan
 * @file com.guan.o2o.service
 * @date 2015/9/18
 * @Version 1.0
 */
public interface UserService {

    public boolean userLogin(String loginPhone,String loginCode) throws ServiceRulesException;

    public boolean userRegister(String registerPhone,String registerCode) throws ServiceRulesException;

    public List<FileBean> getFile() throws ServiceRulesException;
}
