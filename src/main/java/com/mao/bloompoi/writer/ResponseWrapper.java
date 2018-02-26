package com.mao.bloompoi.writer;

import javax.servlet.http.HttpServletResponse;

/**
 * 用于包装HttpServletResponse对象和下载文件名
 *
 * @author bloom
 * @date 2018/2/25
 */
public class ResponseWrapper {

    private HttpServletResponse servletResponse;
    private String fileName;

    public ResponseWrapper(HttpServletResponse servletResponse, String fileName) {
        this.servletResponse = servletResponse;
        this.fileName = fileName;
    }

    public static ResponseWrapper create(HttpServletResponse servletResponse, String fileName) {
        return new ResponseWrapper(servletResponse, fileName);
    }

    public HttpServletResponse getServletResponse() {
        return servletResponse;
    }

    public String getFileName() {
        return fileName;
    }
}
