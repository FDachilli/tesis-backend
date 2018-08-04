package com.tesis.commons;

import org.apache.poi.ss.usermodel.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

public class Util {

        public static String convertExcelToJson (String inputFile, String outputFile) throws IOException, JSONException, org.apache.poi.openxml4j.exceptions.InvalidFormatException {
            FileInputStream inp = new FileInputStream(inputFile);
            Workbook workbook = WorkbookFactory.create( inp );

            // Get the first Sheet.
            Sheet sheet = workbook.getSheetAt( 0 );

            // Start constructing JSON.
            JSONObject json = new JSONObject();

            // Iterate through the rows.
            JSONArray rows = new JSONArray();
            for (Iterator<Row> rowsIT = sheet.rowIterator(); rowsIT.hasNext(); )
            {
                Row row = rowsIT.next();
                JSONObject jRow = new JSONObject();

                // Iterate through the cells.
                JSONArray cells = new JSONArray();
                for (Iterator<Cell> cellsIT = row.cellIterator(); cellsIT.hasNext(); )
                {
                    DataFormatter formatter = new DataFormatter(); //creating formatter using the default locale
                    Cell cell = cellsIT.next();
                    cells.put( formatter.formatCellValue(cell));
                }
                jRow.put( "cell", cells );
                rows.put( jRow );
            }

            // Create the JSON.
            json.put( "rows", rows );

            FileWriter file = new FileWriter(outputFile);
            file.write(json.toString());
            file.close();

            return json.toString();
        }


        /**
         * Agrega al String pasado por parámetro los caracteres de escape necesarios
         * @return String con los caracteres de escape agregados
         */

        public static String addEscapeChar(String str) {

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
        
        public void deleteFolder(String path) {
        	File index = new File("/home/Work/Indexer1");
        	String[]entries = index.list();
        	for(String s: entries){
        	    File currentFile = new File(index.getPath(),s);
        	    currentFile.delete();
        	}
        	index.delete();
        }
}
