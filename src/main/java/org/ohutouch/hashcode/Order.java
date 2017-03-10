package org.ohutouch.hashcode;

import java.util.Arrays;

public class Order {

    public final int id;
    public final int row;
    public final int col;
    public final int[] quantities;

    public Order(int id, int row, int col, int nProductTypes) {
        this.id = id;
        this.row = row;
        this.col = col;
        this.quantities = new int[nProductTypes]; // filled with 0 initially
    }

    public int getTotalItemCount() {
        return Arrays.stream(quantities).sum();
    }

    public int getId() {
        return id;
    }
}
