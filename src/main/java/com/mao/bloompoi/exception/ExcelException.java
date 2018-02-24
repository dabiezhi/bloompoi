package com.mao.bloompoi.exception;

import com.mao.bloompoi.writer.Exporter;

/**
 * Created by mao on 2018/2/14.
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
