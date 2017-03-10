package org.ohutouch.hashcode;

public class Drone {

    public int id;
    public int row;

    public int col;

    public int[] stocks;

    public int usedTurns;

    public Drone(int id, int row, int col, int nbProductTypes) {
        this.id = id;
        this.row = row;
        this.col = col;
        this.stocks = new int[nbProductTypes];
    }

    public void moveTo(Warehouse warehouse) {
        row = warehouse.row;
        col = warehouse.col;
    }

    public void moveTo(Order order) {
        row = order.row;
        col = order.col;
    }

    public int getId() {
        return id;
    }
}
