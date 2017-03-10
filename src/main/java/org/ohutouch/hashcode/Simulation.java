package org.ohutouch.hashcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Simulation {

    public final int nRows;

    public final int nCols;

    public int nTurns;

    public final int nProductTypes;

    public final int maxLoad;

    public final Drone[] drones;

    public final int[] productTypeWeights;

    public final Warehouse[] warehouses;

    public Order[] orders;

    public Simulation(int nRows, int nCols, int nDrones, int nTurns, int maxLoad, int nProductTypes, int nWarehouses) {
        this.nRows = nRows;
        this.nCols = nCols;
        this.nTurns = nTurns;
        this.nProductTypes = nProductTypes;
        this.maxLoad = maxLoad;
        this.productTypeWeights = new int[nProductTypes];
        this.warehouses = new Warehouse[nWarehouses];
        this.drones = new Drone[nDrones];
    }

    public void initDrones() {
        for (int d = 0; d < drones.length; d++) {
            drones[d] = new Drone(d, warehouses[0].row, warehouses[0].col, nProductTypes);
        }
    }

    public List<String> run() {
        List<String> commands = new ArrayList<>(128);

        List<Integer> orderIds = prioritizedOrders();
        int n = 0;
        for (Integer orderId : orderIds) {

            int droneId = getClosestDrone(orders[orderId]);
            addOrderCommands(commands, orderId, droneId);
            n++;
        }
        return commands;
    }

    private int getClosestDrone(Order order) {
        return Arrays.stream(drones).sorted((d1, d2) -> distance(d1, order) - distance(d2, order))
                .findFirst().orElse(null).getId();
    }

    private List<Integer> prioritizedOrders() {
        return Arrays.stream(orders).sorted((o1, o2) -> orderPriority(o1) - orderPriority(o2))
                .map(Order::getId).collect(Collectors.toList());
    }

    /** lower is better */
    private int orderPriority(Order order) {
        int minDDistance = Arrays.stream(drones).mapToInt(d -> distance(d, order)).min().orElse(Integer.MAX_VALUE);
        int minWDistance = Arrays.stream(warehouses).mapToInt(w -> distance(w, order)).min().orElse(Integer.MAX_VALUE);
        int itemsCount = order.getTotalItemCount();
        return 10 * minDDistance + minWDistance + 5 * itemsCount;
    }

    private void addOrderCommands(List<String> commands, int orderId, int droneId) {
        Drone drone = drones[droneId];
        int initialRow = drone.row;
        int initialCol = drone.col;
        if (drone.usedTurns < nTurns) {
            Delivery delivery = executeOrder(droneId, orderId);
            drones[droneId].usedTurns += delivery.turns;
            if (drone.usedTurns <= nTurns) {
                commands.addAll(delivery.commands);
            }
        } else {
            drone.row = initialRow;
            drone.col = initialCol;
        }
    }

    private Delivery executeOrder(int droneId, int orderId) {
        Delivery delivery = new Delivery();
        Order order = orders[orderId];
        Drone drone = drones[droneId];
        for (int p = 0; p < nProductTypes; p++) {
            while (order.quantities[p] > 0) {
                int requiredQty = order.quantities[p];
                int qtyInOneFlight = Math.min(requiredQty, getMaxCarriableQty(p));
                int[] closest = getClosestWarehouse(droneId, p, qtyInOneFlight);
                int warehouseId = closest[0];
                int distance = closest[1];
                Warehouse warehouse = warehouses[warehouseId];
                int qty = Math.min(qtyInOneFlight, warehouse.stocks[p]);
                delivery.commands.add(load(droneId, warehouseId, p, qty));
                delivery.turns += distance + 1;
                drone.moveTo(warehouse);
                warehouse.stocks[p] -= qty;
                delivery.commands.add(deliver(droneId, orderId, p, qty));
                delivery.turns += distance(drone, order) + 1;
                order.quantities[p] -= qty;
                drone.moveTo(order);
            }
        }
        return delivery;
    }

    private int getMaxCarriableQty(int product) {
        return maxLoad / productTypeWeights[product];
    }

    private int[] getClosestWarehouse(int droneId, int product, int quantity) {
        Drone drone = drones[droneId];
        int minDistance = Integer.MAX_VALUE;
        int closestWarehouse = 0;
        int minDistanceFull = Integer.MAX_VALUE;
        int closestWarehouseFull = 0;
        boolean foundFull = false;
        for (int w = 0; w < warehouses.length; w++) {
            Warehouse warehouse = warehouses[w];
            int distance = distance(drone, warehouse);
            if (warehouse.stocks[product] >= quantity) {
                if (distance < minDistanceFull) {
                    minDistanceFull = distance;
                    closestWarehouseFull = w;
                    foundFull = true;
                }
            } else if (warehouse.stocks[product] > 0) {
                if (distance < minDistance) {
                    minDistance = distance;
                    closestWarehouse = w;
                }
            }
        }
        return foundFull ?
                new int[] {closestWarehouseFull, minDistanceFull} :
                new int[] {closestWarehouse, minDistance};
    }

    private static int distance(Warehouse d, Order c) {
        return distance(d.row, d.col, c.row, c.col);
    }

    private static int distance(Drone d, Order c) {
        return distance(d.row, d.col, c.row, c.col);
    }

    private static int distance(Drone d, Warehouse w) {
        return distance(d.row, d.col, w.row, w.col);
    }

    private static int distance(int row1, int col1, int row2, int col2) {
        int sqDiffRow = (row2 - row1) * (row2 - row1);
        int sqDiffCol = (col2 - col1) * (col2 - col1);
        return (int)Math.ceil(Math.sqrt(sqDiffRow + sqDiffCol));
    }

    private static String load(int drone, int warehouse, int product, int quantity) {
        return String.format("%d L %d %d %d", drone, warehouse, product, quantity);
    }

    private static String unload(int drone, int warehouse, int product, int quantity) {
        return String.format("%d U %d %d %d", drone, warehouse, product, quantity);
    }

    private static String deliver(int drone, int order, int product, int quantity) {
        return String.format("%d D %d %d %d", drone, order, product, quantity);
    }

    private static String wait(int drone, int turns) {
        return String.format("%d W %d", drone, turns);
    }

    @Override
    public String toString() {
        return "Simulation{" +
                "nRows=" + nRows +
                ", nCols=" + nCols +
                ", nDrones=" + drones.length +
                ", nTurns=" + nTurns +
                ", maxLoad=" + maxLoad +
                ", nProductTypes=" + nProductTypes +
                ", productTypeWeights=" + Arrays.toString(productTypeWeights) +
                ", warehouses=" + Arrays.toString(warehouses) +
                ", orders=" + Arrays.toString(orders) +
                '}';
    }
}
