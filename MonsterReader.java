import java.io.*;
import java.util.*;

public class MonsterReader {

    // Load monsters from a file and assign them to rooms
    public static void loadMonsters(String filePath, Map<Integer, Room> roomMap, Map<String, Item> itemMap) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                // Format: ID|Name|Description|Damage|HP|Type|DropItemID|RoomNumber
                String[] parts = line.split("\\|");
                if (parts.length < 8) {
                    System.out.println("Skipping malformed monster line: " + line);
                    continue;
                }

                String id = parts[0].trim();
                String name = parts[1].trim();
                String description = parts[2].trim();

                // Remove non-digit chars (e.g., "3 DMG" -> "3")
                int damage = parseIntSafe(parts[4].trim()) == 0 ? parseIntSafe(parts[3].trim()) : parseIntSafe(parts[3].trim());

                try {
                    damage = Integer.parseInt(parts[3].replaceAll("[^0-9]", ""));
                } catch (Exception ex) {
                    damage = parseIntSafe(parts[3].trim());
                }
                int hp = 0;
                try {
                    hp = Integer.parseInt(parts[4].replaceAll("[^0-9]", ""));
                } catch (Exception ex) {
                    hp = parseIntSafe(parts[4].trim());
                }

                String type = parts[5].trim();

                String dropItemId = parts[6].trim();
                if (dropItemId.equals("-")) dropItemId = null;

                int roomNumber = parseIntSafe(parts[7].trim());

                Item dropItem = null;
                if (dropItemId != null) {
                    dropItem = itemMap.get(dropItemId); // fetch actual Item object
                    if (dropItem == null) {
                        System.out.println("Drop item " + dropItemId + " not found for monster " + name);
                    }
                }
                Monster monster = new Monster(id,name, description, hp, damage, type, dropItem);

                Room room = roomMap.get(roomNumber);
                if (room != null) {
                    room.getMonster();
                    room.setMonster(monster);
                    System.out.println("Loaded monster: " + name + " into room : " + roomNumber + "with drops: "+dropItemId);
                   
                } else {
                    System.out.println("Room " + roomNumber + " not found for monster " + name);
                }
            }
        } catch (Exception e) {
            System.out.println("Error reading monsters: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static int parseIntSafe(String s) {
        try {
            return s.equals("-") ? 0 : Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
