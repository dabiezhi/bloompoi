package com.mao.bloompoi.writer;

import com.mao.bloompoi.enums.ExcelType;
import com.mao.bloompoi.exception.ExcelException;

import javax.servlet.http.HttpServletResponse;

/**
 * ServletResponse出口
 *
 * @author bloom
 * @date 2018/2/25
 */
public class ResponseExcelWriter implements ExcelWriter {

    private ResponseWrapper wrapper;

    public ResponseExcelWriter(ResponseWrapper wrapper) {
        this.wrapper = wrapper;
    }

    @Override
    public <T> void export(Exporter<T> exporter) throws ExcelException {
        HttpServletResponse servletResponse = this.wrapper.getServletResponse();
        try {
            String fileName = wrapper.getFileName();
            servletResponse.setContentType("application/x-xls");
            fileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");
            servletResponse.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            ExcelType excelType = ExcelType.getExcelType(fileName);
            exporter.setExcelType(excelType);
            this.export(exporter, servletResponse.getOutputStream());
        } catch (Exception e) {
            throw new ExcelException(e);
        }
    }

    @Override
    public <T> void exportBySpel(Exporter<T> exporter) throws ExcelException {
        HttpServletResponse servletResponse = this.wrapper.getServletResponse();
        try {
            String fileName = wrapper.getFileName();
            servletResponse.setContentType("application/x-xls");
            fileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");
            servletResponse.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            ExcelType excelType = ExcelType.getExcelType(fileName);
            exporter.setExcelType(excelType);
            this.exportBySpel(exporter, servletResponse.getOutputStream());
        } catch (Exception e) {
            throw new ExcelException(e);
        }
    }

    @Override
    public <T> void exportByResult(Exporter<T> exporter) throws ExcelException {
        HttpServletResponse servletResponse = this.wrapper.getServletResponse();
        try {
            String fileName = wrapper.getFileName();
            servletResponse.setContentType("application/x-xls");
            fileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");
            servletResponse.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            this.exportByResult(exporter, servletResponse.getOutputStream());
        } catch (Exception e) {
            throw new ExcelException(e);
        }
    }
}
