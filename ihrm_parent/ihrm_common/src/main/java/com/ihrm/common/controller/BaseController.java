package com.ihrm.common.controller;

import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author : Brave
 * @Version : 1.0
 * @Date : 2020/3/2 11:01
 */
public class BaseController {

    protected HttpServletRequest request;
    protected HttpServletResponse response;
    protected String companyId;
    protected String companyName;

    @ModelAttribute
    public void setResAndReq(HttpServletRequest request,HttpServletResponse response){
        this.request = request;
        this.response = response;
        /**
         * companyId,companyName暂设固定值
         */
        this.companyId = "1";
        this.companyName = "青春年华";
    }
}
