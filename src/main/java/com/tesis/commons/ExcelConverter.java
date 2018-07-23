package com.tesis.commons;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Convierte un archivo Excel en uno ARFF
 *
 *
 */

public class ExcelConverter {

    private List<ExcelSheet> sheets;

    /**
     * Constructor
     */
    public ExcelConverter() {
        sheets = new ArrayList<ExcelSheet>();
    }

    /**
     * Returns a list of lists with data in columns G (mensaje) and H (conducta)
     * from excel file (only XLS and XLSX files) contained in parameter fileName
     * (except header)
     * 
     * @param fileName
     *            - path and file name to add to current object
     * @return List of lists with data contained in parameter fileName (except
     *         header)
     */
    private ExcelSheet loadExcel(String fileName) {

        ExcelSheet excelSheet = null;
        try {
            FileInputStream fileInputStream = new FileInputStream(fileName);
            Workbook workbook;
            Sheet sheet;

            if (fileName.contains(Constants.XLSX_FILE))
                workbook = new XSSFWorkbook(fileInputStream);
            else {
                POIFSFileSystem fsFileSystem = new POIFSFileSystem(fileInputStream);
                workbook = new HSSFWorkbook(fsFileSystem);
            }

            for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
                sheet = workbook.getSheetAt(sheetIndex);

                Iterator<Row> rowIterator = sheet.rowIterator();
                
                List<List<Cell>> cells = new ArrayList<List<Cell>>();
                excelSheet = new ExcelSheet(sheet.getSheetName());
                while (rowIterator.hasNext()) {
                    Row row = rowIterator.next();
                    
                    if (row.getRowNum() > 1) {
                        Cell cellName = row.getCell(4);
                        Cell cellHour = row.getCell(5);
                        Cell cellMessage = row.getCell(6);
                        Cell cellPattern = row.getCell(8);
                        Cell cellAtribute = row.getCell(9);
                        Cell cellConduct = row.getCell(10);

                        if (cellMessage != null && cellMessage.toString() != "") {
                            List<Cell> rowCells = new ArrayList<Cell>(); 
                            rowCells.add(cellName);
                            rowCells.add(cellHour);
                            rowCells.add(cellMessage);
                            rowCells.add(cellPattern);
                            rowCells.add(cellAtribute);
                            rowCells.add(cellConduct);
                            cells.add(rowCells);
                            excelSheet.setCells(cells);
                        }
                    }
                }
                sheets.add(excelSheet);
            }
            workbook.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return excelSheet;
    }
    
    /**
     * Agrega al String pasado por parámetro los caracteres de escape necesarios
     * @param str Texto para agregarle los caracteres de escape
     * @return String con los caracteres de escape agregados
     */
    private String addEscapeChar(String str) {
        
        if (str != null)
        {
            str = str.replace("'", "\\'");
            str = str.replace('\n', ' ');
            str = str.replace('\u0001', ' ');
            str = str.replace('\u0002', ' ');
        }
        else
            return "";
            
        return str;
    }

    /**
     * Crea el encabezado de los archivos ARFF 
     * @return
     */
    private String getARFFHeader() {
        String header;
        
        header =    "@relation chat" + '\n' + 
                    '\n'+
                    "@attribute nombre string" + '\n' +
                     "@attribute hora string" + '\n' +
                    "@attribute message string" + '\n' +
                    "@attribute patron string" + '\n' +
                    "@attribute atributo int" + '\n' +
                    "@attribute conducta int" + '\n' +
                    '\n' + 
                    "@data" + '\n';
        return header;
    }
    
    /**
     * Saves to a file all data (except rows not-labeled or labeled with value
     * zero) contained in member variable "cells"
     * 
     * @param arffFileName
     *            - file name to be used
     */
    private List<String> saveToArff(String arffFileName) {

        List<String> arffList = new ArrayList<String>(); 
        
        // Guardar los datos en un archivo ARFF
        for (int sheetIndex = 0; sheetIndex < sheets.size(); sheetIndex++) {
            
            ExcelSheet excelSheet = sheets.get(sheetIndex);
            
            if (excelSheet.getName() != null) {
                try {
                    String fileName = arffFileName + " (" + excelSheet.getName() + ")" + Constants.ARFF_FILE;
                    
                    File file = new File(fileName);
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    
                    arffList.add(fileName);
    
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    OutputStream os = (OutputStream) fileOutputStream;
                    OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
                    BufferedWriter bw = new BufferedWriter(osw);
    
                    // Escribir el encabezado del archivo ARFF
                    bw.write(getARFFHeader());
                    
                    String lastName = "";

                    if (excelSheet.getCells() != null) {
                        for (int cellIndex = 0; cellIndex < excelSheet.getCells().size(); cellIndex++) {
                            List<Cell> rowCells = excelSheet.getCells().get(cellIndex);
            
                            Cell cellName = (Cell) rowCells.get(0);
                            Cell cellHour = (Cell) rowCells.get(1);
                            Cell cellMessage = (Cell) rowCells.get(2);
                            Cell cellPattern = (Cell) rowCells.get(3);
                            Cell cellAtribute = (Cell) rowCells.get(4);
                            Cell cellConduct = (Cell) rowCells.get(5);
                            
                            String name, hour, message, pattern, atribute, conduct;
                            Date dt;
                            name = "";
                            
                            if (cellName != null) {
                                name = cellName.toString();
                            }
            
                            if (name == "")
                                name = lastName;
                                
                            message = "";
                            if (cellMessage != null) {
                                message = cellMessage.toString();
                            }

                         /*   hour = "";
                            if (cellHour != null && !cellHour.toString().isEmpty()) {
                                dt = cellHour.getDateCellValue();
                                Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
                                calendar.setTime(dt);   // assigns calendar to given date
                                hour = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)); // gets hour in 24h forma
                                hour = hour + ":" + String.valueOf(calendar.get(Calendar.MINUTE));
                            }*/

                            pattern = "";
                            if (cellPattern != null) {
                                pattern = cellPattern.toString();
                            }

                            atribute = "";
                            if (cellAtribute != null) {
                                atribute = String.valueOf(cellAtribute.getNumericCellValue());
                            }

                            conduct = "";
                            if (cellConduct != null) {
                                conduct = String.valueOf(cellConduct.getNumericCellValue());
                            }
            
                            bw.write("(" + addEscapeChar(name) + ", "+ /*addEscapeChar(hour) +*/ "' ,'" + addEscapeChar(message) + "', "+ addEscapeChar(pattern) +", "+ addEscapeChar(atribute) + ", "+ addEscapeChar(conduct) + ")");
                            bw.newLine();
            
                            lastName = name;
                        }
                    }
    
                    bw.flush();
                    bw.close();
                    
                } catch (UnsupportedEncodingException e) {
                    System.err.println(e.toString());
                } catch (IOException e) {
                    System.err.println(e.toString());
                } catch (Exception e) {
                    System.err.println(e.toString());
                }
            }
        }
        
        return arffList;
    }

    /**
     * Loads data from an Excel file and saves it to a ARFF file
     * 
     * @param in
     *            - File path
     */
    public List<String> excelToARFF(String in) {

        sheets.clear();
        if (loadExcel(in) != null) {
            String out = in.substring(0, in.lastIndexOf(".xlsx"));
            return saveToArff(out);
        }
        return null;
    }
}