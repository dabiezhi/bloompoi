package com.mao.bloompoi.exception;

/**
 * Excel读写异常
 *
 * @author bloom
 * @date 2018/2/25
 */
public class ExcelException extends Exception{

    public ExcelException(String message){
        super(message);
    }

    public ExcelException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExcelException(Throwable cause) {
        super(cause);
    }
}
