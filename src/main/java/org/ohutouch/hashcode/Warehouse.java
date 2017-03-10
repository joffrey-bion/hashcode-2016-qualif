package org.ohutouch.hashcode;

public class Warehouse {

    public final int row;
    public final int col;
    public final int[] stocks;

    public Warehouse(int row, int col, int nProductTypes) {
        this.row = row;
        this.col = col;
        this.stocks = new int[nProductTypes];
    }
}
