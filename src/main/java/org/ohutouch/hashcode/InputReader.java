package org.ohutouch.hashcode;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class InputReader {

  private static final int BASIC_SETTINGS_LINE = 0;

  private static final int PRODUCT_TYPES_COUNT_LINE = 1;

  private static final int PRODUCT_TYPES_WEIGHTS_LINE = 2;

  private static final int WAREHOUSES_COUNT_LINE = 3;

  private static final int FIRST_WAREHOUSE_LINE = 4;

  private static final int WAREHOUSE_LINES_COUNT = 2;

  private static final int ORDER_LINES_COUNT = 3;

  public static Simulation readFile(String filename) {
    try {
      List<String> lines = Files.readAllLines(Paths.get(filename));
      String[] globalSettings = lines.get(BASIC_SETTINGS_LINE).split(" ");
      int nRows = Integer.parseInt(globalSettings[0]);
      int nCols = Integer.parseInt(globalSettings[1]);
      int nDrones = Integer.parseInt(globalSettings[2]);
      int nTurns = Integer.parseInt(globalSettings[3]);
      int maxLoad = Integer.parseInt(globalSettings[4]);

      int nProductTypes = Integer.parseInt(lines.get(PRODUCT_TYPES_COUNT_LINE));
      int nWarehouses = Integer.parseInt(lines.get(WAREHOUSES_COUNT_LINE));

      Simulation simulation = new Simulation(nRows, nCols, nDrones, nTurns, maxLoad, nProductTypes, nWarehouses);

      String[] weights = lines.get(PRODUCT_TYPES_WEIGHTS_LINE).split(" ");
      for (int i = 0; i < nProductTypes; i++) {
        simulation.productTypeWeights[i] = Integer.parseInt(weights[i]);
      }

      for (int w = 0; w < nWarehouses; w++) {
        String[] coords = lines.get(FIRST_WAREHOUSE_LINE + w * WAREHOUSE_LINES_COUNT).split(" ");
        int wRow = Integer.parseInt(coords[0]);
        int wCol = Integer.parseInt(coords[1]);
        simulation.warehouses[w] = new Warehouse(wRow, wCol, nProductTypes);
        String[] items = lines.get(FIRST_WAREHOUSE_LINE + 1 + w * WAREHOUSE_LINES_COUNT).split(" ");
        for (int p = 0; p < nProductTypes; p++) {
          simulation.warehouses[w].stocks[p] = Integer.parseInt(items[p]);
        }
      }

      final int firstOrderLine = FIRST_WAREHOUSE_LINE + nWarehouses * 2 + 1;
      int nOrders = Integer.parseInt(lines.get(firstOrderLine - 1));
      simulation.orders = new Order[nOrders];
      for (int c = 0; c < nOrders; c++) {
        String[] coords = lines.get(firstOrderLine + c * ORDER_LINES_COUNT).split(" ");
        int row = Integer.parseInt(coords[0]);
        int col = Integer.parseInt(coords[1]);
        simulation.orders[c] = new Order(c, row, col, nProductTypes);
        String[] items = lines.get(firstOrderLine + 2 + c * ORDER_LINES_COUNT).split(" ");
        for (String itemType : items) {
          simulation.orders[c].quantities[Integer.parseInt(itemType)]++;
        }
      }

      simulation.initDrones();
      return simulation;
    } catch (Exception e) {
      throw new RuntimeException("Wrong input format", e);
    }
  }

}
