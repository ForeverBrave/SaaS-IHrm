package com.ihrm.common.exception;

import com.ihrm.common.entity.ResultCode;
import com.sun.org.apache.bcel.internal.classfile.Code;
import lombok.Getter;

/**
 * 自定义异常
 */
@Getter
public class CommonException extends Exception  {

    private ResultCode resultCode;

    public CommonException(ResultCode resultCode) {
        this.resultCode = resultCode;
    }
}
