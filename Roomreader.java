import java.io.*;
import java.util.*;


public class Roomreader {
    public static Map<Integer, String> readRooms(String filename) {
        HashMap<Integer, Room> room = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader("Map.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if(line.startsWith("#") || line.trim().isEmpty()) continue;
                String[] parts = line.split("\\|");
                if (parts.length < 7){
                    System.out.println("Invalid room data: " + line);
                    continue;
                }
                int id = Integer.parseInt(parts[0].trim());
                String name = parts[1].trim();
                StringBuilder description = new StringBuilder();
                for (int i = 2; i < parts.length - 4; i++){
                    if (i > 2) description.append(",");
                    description.append(parts[i].trim());
                }
                String desc = description.toString().trim();
                int n = Integer.parseInt(parts[parts.length - 4].trim());
                int e = Integer.parseInt(parts[parts.length - 3].trim());
                int s = Integer.parseInt(parts[parts.length - 2].trim());
                int w = Integer.parseInt(parts[parts.length - 1].trim());

                room.put(id, new Room(id,name,desc,n,e,s,w));
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Error parsing number: " + e.getMessage());
        }
        return null;
    }
}


