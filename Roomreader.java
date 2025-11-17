/* 
 * Authors: David and Mohammed
 */

import java.io.*;
import java.util.*;

public class Roomreader {

    public static Map<Integer, Room> readRooms(String filename) {
        Map<Integer, Room> roomMap = new HashMap<>(); // declare once here

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;

            while ((line = br.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split("\\|");
                if (parts.length < 7) {
                    System.out.println("Invalid room data: " + line);
                    continue;
                }

                int id = Integer.parseInt(parts[0].trim());
                String name = parts[1].trim();
                String description = parts[2].trim();

                int north = Integer.parseInt(parts[3].trim());
                int east  = Integer.parseInt(parts[4].trim());
                int south = Integer.parseInt(parts[5].trim());
                int west  = Integer.parseInt(parts[6].trim());

                Room room = new Room(id, name, description); // only declare once

                if (north > 0) room.addExit("NORTH", north);
                if (east  > 0) room.addExit("EAST", east);
                if (south > 0) room.addExit("SOUTH", south);
                if (west  > 0) room.addExit("WEST", west);

            

                roomMap.put(id, room); // add room to map
            }
        } catch (IOException e) {
            System.out.println("Error reading rooms from file: " + e.getMessage());
        }

        return roomMap; // return the map
    }
}
