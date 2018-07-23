package com.tesis.commons;

import java.util.List;

import org.apache.poi.ss.usermodel.Cell;

public class ExcelSheet {

    private String name;
    private List<List<Cell>> cells;
    
    public ExcelSheet(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<List<Cell>> getCells() {
        return cells;
    }
    public void setCells(List<List<Cell>> cells) {
        this.cells = cells;
    }

}
