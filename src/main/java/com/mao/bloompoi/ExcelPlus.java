package com.mao.bloompoi;

import com.mao.bloompoi.exception.ExcelException;
import com.mao.bloompoi.reader.ExcelReader;
import com.mao.bloompoi.writer.Exporter;
import com.mao.bloompoi.writer.FileExcelWriter;
import com.mao.bloompoi.writer.ResponseExcelWriter;
import com.mao.bloompoi.writer.ResponseWrapper;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collection;

/**
 * Created by mao on 2018/2/14.
 */
public class ExcelPlus {

    private Exporter exporter;

    public <T> ExcelPlus export(Collection<T> data) {
        return this.export(Exporter.create(data));
    }

    public <T> ExcelPlus export(Exporter<T> exporter) {
        this.exporter = exporter;
        return this;
    }

    @SuppressWarnings({"unchecked"})
    public void writeAsFile(File file) throws ExcelException {
        new FileExcelWriter(file).export(exporter);
    }

    @SuppressWarnings({"unchecked"})
    public void writeAsFileBySpel(File file) throws ExcelException {
        new FileExcelWriter(file).exportBySpel(exporter);
    }

    @SuppressWarnings({"unchecked"})
    public void writeAsResponse(ResponseWrapper wrapper) throws ExcelException {
        new ResponseExcelWriter(wrapper).export(exporter);
    }

    @SuppressWarnings({"unchecked"})
    public void writeAsResponseBySpel(ResponseWrapper wrapper) throws ExcelException {
        new ResponseExcelWriter(wrapper).exportBySpel(exporter);
    }

    public <T> ExcelReader<T> read(File file, Class<T> type) throws ExcelException {
        try {
            return this.read(new FileInputStream(file), type);
        } catch (Exception e) {
            throw new ExcelException(e);
        }
    }

    public <T> ExcelReader<T> read(InputStream inputStream, Class<T> type) throws ExcelException {
        try {
            Workbook workbook = WorkbookFactory.create(inputStream);
            return new ExcelReader<>(workbook, type);
        } catch (Exception e) {
            throw new ExcelException(e);
        }
    }

    public static ExcelPlus me() {
        return new ExcelPlus();
    }
}
