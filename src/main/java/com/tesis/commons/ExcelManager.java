package com.tesis.commons;

import java.io.FileOutputStream;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class ExcelManager {

    private String fileName;
    private HSSFWorkbook workbook;
    private int rowCount;
    private HSSFSheet currentSheet;
    
    public ExcelManager(String fileName) {
        
        this.fileName = fileName;
        workbook = new HSSFWorkbook();
        rowCount = 0;
    }
    
    private boolean existSheetName(String newSheetName){
        
        for(int i = 0; i < workbook.getNumberOfSheets(); i++) {
            if (workbook.getSheetName(i).equals(newSheetName)) {
                return true;
            }
        }
        
        return false;
    }
    
    public void addSheet(String sheetName) {
        
        rowCount = 0;
        String newSheetName = sheetName;
        
        if (existSheetName(sheetName)) {
            int index = 1;
            newSheetName = sheetName.substring(0, sheetName.lastIndexOf(Constants.ARFF_FILE)) + "(" + index + ")" + Constants.ARFF_FILE;
            while(existSheetName(newSheetName)) {
                index++;
            }
        }

        currentSheet = workbook.createSheet(newSheetName);  
    }
    
    public void addRow(List<String> cellsValues) {
        
        HSSFRow rowhead = currentSheet.createRow((short)rowCount++);
        int cellIndex = 0;
        for (String cellValue : cellsValues) {
            rowhead.createCell(cellIndex++).setCellValue(cellValue);
        }
    }
    
    public void saveExcel() {
        
        FileOutputStream fileOut;
        try {
            fileOut = new FileOutputStream(fileName);
            workbook.write(fileOut);
            fileOut.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
