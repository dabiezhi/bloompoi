package com.mao.bloompoi.writer;

import com.mao.bloompoi.enums.ExcelType;
import com.mao.bloompoi.exception.ExcelException;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by mao on 2018/2/14.
 */
public class FileExcelWriter implements ExcelWriter {

    private File savePath;

    public FileExcelWriter(File file) {
        this.savePath = file;
    }

    @Override
    public <T> void export(Exporter<T> exporter) throws ExcelException {
        if (null == savePath) {
            throw new ExcelException("Save the Excel path can not be null.");
        }
        try {
            ExcelType excelType = ExcelType.getExcelType(savePath.getName());
            exporter.setExcelType(excelType);
            this.export(exporter, new FileOutputStream(savePath));
        } catch (Exception e) {
            throw new ExcelException(e);
        }
    }

    @Override
    public <T> void exportBySpel(Exporter<T> exporter) throws ExcelException {
        if (null == savePath) {
            throw new ExcelException("Save the Excel path can not be null.");
        }
        try {
            ExcelType excelType = ExcelType.getExcelType(savePath.getName());
            exporter.setExcelType(excelType);
            this.exportBySpel(exporter, new FileOutputStream(savePath));
        } catch (Exception e) {
            throw new ExcelException(e);
        }
    }
}
