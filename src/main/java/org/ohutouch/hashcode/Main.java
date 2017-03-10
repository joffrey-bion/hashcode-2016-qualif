package org.ohutouch.hashcode;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

public class Main {

    private static final String busyDay =
            "D:\\git_clones\\hashcode2016\\src\\main\\resources\\org\\ohutouch" + "\\hashcode\\busy_day.in";

    private static final String motherOfAllWarehouses =
            "D:\\git_clones\\hashcode2016\\src\\main\\resources\\org\\ohutouch\\hashcode" +
                    "\\mother_of_all_warehouses.in";

    private static final String redundancy =
            "D:\\git_clones\\hashcode2016\\src\\main\\resources\\org\\ohutouch\\hashcode\\redundancy.in";

    public static void main(String[] args) {
        for (String filename : Arrays.asList(busyDay, motherOfAllWarehouses, redundancy)) {
            Simulation simulation = InputReader.readFile(filename);
            List<String> commands = simulation.run();
            try (BufferedWriter w = Files.newBufferedWriter(Paths.get(filename.replace(".in", ".out")))) {
                w.write(commands.size() + "\n");
                commands.forEach(c -> {
                    try {
                        w.write(c + "\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                w.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
