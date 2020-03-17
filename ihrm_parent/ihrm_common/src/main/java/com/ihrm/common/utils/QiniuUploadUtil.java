package com.ihrm.common.utils;

import com.google.gson.Gson;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;

import java.util.Date;

public class QiniuUploadUtil {

    String accessKey = "h4Y9yOXvY6-4Ja3w0sMwp2Wa-30NW8v3MsoDIVi_";
    String secretKey = "yopswFvQ-p_iiUngSKg4FXo2vULxnOt3GGeKa0a3";
    String bucket = "ihrm-bucket-test";
    private static final String prix = "http://q7brpg92z.bkt.clouddn.com/";
    private UploadManager manager;

    public QiniuUploadUtil() {
        //初始化基本配置
        Configuration cfg = new Configuration(Region.region0());
        //创建上传管理器
        manager = new UploadManager(cfg);
    }

	//文件名 = key
	//文件的byte数组
    public String upload(String imgName , byte [] bytes) {
        Auth auth = Auth.create(accessKey, secretKey);
        //构造覆盖上传token
        String upToken = auth.uploadToken(bucket,imgName);
        try {
            Response response = manager.put(bytes, imgName, upToken);
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            //返回请求地址
            return prix+putRet.key+"?t="+System.currentTimeMillis();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
