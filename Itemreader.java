import java.io.*;
import java.util.*;

public class Itemreader {
    
    

    // Now returns a map of item ID -> Item for use by MonsterReader
    public static Map<String, Item> loadItems(String filename, Map<Integer, Room> roomMap) {
        Map<String, Item> itemMap = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
<<<<<<< HEAD
                if (line.startsWith("#") || line.isEmpty()) continue; // Skip comments and empty lines
=======
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

>>>>>>> 94f7c2d2e71ff7e61f42724a3fe7df2c3ef2abca
                String[] parts = line.split("\\|");
                if (parts.length < 10) {
                    System.out.println("Skipping malformed item line: " + line);
                    continue;
                }

                String itemID = parts[0].trim();
                String name = parts[1].trim();
                String desc = parts[2].trim();
                String type = parts[3].trim();
                int hp = parseIntSafe(parts[4].trim());
                int damage = parseIntSafe(parts[5].trim());
                double armor = parseDoubleSafe(parts[6].trim());
                String dice = parts[7].trim().equals("-") ? null : parts[7].trim();
                String rarity = parts[8].trim();
                int roomId = parseIntSafe(parts[9].trim());

<<<<<<< HEAD
                Item item =new Item(itemID,name, desc, type, hp, damage, armor, dice, rarity);
                

                Room room = roomMap.get(roomId);
                if (room != null || roomId != 0) room.addItem(item);
                System.out.println("Properly loaded item: " + name + " into room ID: " + roomId);
=======
                Item item = new Item(itemID, name, desc, type, hp, damage, armor, dice, rarity);

                // store in global item map
                itemMap.put(itemID, item);

                // put in room if valid roomId > 0 and room exists
                if (roomId > 0) {
                    Room room = roomMap.get(roomId);
                    if (room != null) {
                        room.addItem(item);
                       
                    } else {
                        System.out.println("Item " + name + " has room ID " + roomId + " but no such room exists.");
                    }
                } else {
                    System.out.println("Loaded item: " + name + " (no room assigned)");
                }
>>>>>>> 94f7c2d2e71ff7e61f42724a3fe7df2c3ef2abca
            }
        } catch (Exception e) {
            System.out.println("Error loading items: " + e.getMessage());
            e.printStackTrace();
        }

        return itemMap;
    }

    private static int parseIntSafe(String s) {
        try {
            return s.equals("-") ? 0 : Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static double parseDoubleSafe(String s) {
        try {
            return s.equals("-") ? 0 : Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}