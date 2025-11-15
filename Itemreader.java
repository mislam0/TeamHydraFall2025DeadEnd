import java.io.*;
import java.util.*;

public class Itemreader {
    
    

    public static void loadItems(String filename, Map<Integer, Room> roomMap) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("#") || line.isEmpty()) continue; // Skip comments and empty lines
                String[] parts = line.split("\\|");
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

                Item item =new Item(itemID,name, desc, type, hp, damage, armor, dice, rarity);
                

                Room room = roomMap.get(roomId);
                if (room != null || roomId != 0) room.addItem(item);
                System.out.println("Properly loaded item: " + name + " into room ID: " + roomId);
            }
        } catch (Exception e) {
            System.out.println("Error loading items: " + e.getMessage());
        }
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




