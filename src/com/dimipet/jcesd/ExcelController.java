package com.dimipet.jcesd;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class ExcelController {

    private Workbook wb;
    private CreationHelper createHelper;
    private Sheet sheet;
    private Row row;
    private InputStream fileIn;
    private FileOutputStream fileOut;

    public ExcelController() {
        try {
            //TODO add app.properties
            this.fileIn = new FileInputStream("results.xls");
            this.wb = WorkbookFactory.create(fileIn);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.createHelper = wb.getCreationHelper();
        this.sheet = wb.getSheet("apotelesmata");
    }

    public void createNewRow() {
        this.row = sheet.createRow(this.sheet.getLastRowNum() + 1);
    }

    public void createAndWriteToCell(int i) {
        if (this.row.getLastCellNum() <= 0) {
            this.row.createCell(this.row.getLastCellNum() + 1).setCellValue(i);
        } else {
            this.row.createCell(this.row.getLastCellNum()).setCellValue(i);
        }
    }

    public void createAndWriteToCell(String s) {
        if (this.row.getLastCellNum() <= 0) {
            this.row.createCell(this.row.getLastCellNum() + 1).setCellValue(createHelper.createRichTextString(s));
        } else {
            this.row.createCell(this.row.getLastCellNum()).setCellValue(createHelper.createRichTextString(s));
        }
    }

    public void createAndWriteToCell(double d) {
        this.row.createCell(this.row.getLastCellNum() + 1).setCellValue(d);
    }

    public void createAndWriteToCell(boolean b) {
        this.row.createCell(this.row.getLastCellNum() + 1).setCellValue(b);
    }

    public void writeToFile() {
        try {
            //TODO add app.properties
            fileOut = new FileOutputStream("results.xls");
            wb.write(fileOut);
            fileOut.close();
        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}
