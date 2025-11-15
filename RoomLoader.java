import java.util.*;

public class RoomLoader {
    private List<Room> rooms;
    private Map<Integer, Room> roomMap;
    private Player player;

    public RoomLoader() {
        roomMap = Roomreader.readRooms("Map.txt");
        rooms = new ArrayList<>(roomMap.values());
    }

    public void startGame(Player player, Scanner scanner) {
        if (rooms.isEmpty()) {
            System.out.println("No rooms loaded. Exiting.");
            return;
        }

        this.player = player;

        Room current = roomMap.get(player.getCurrentRoomNumber());
        player.enterRoom(current, scanner);

        boolean playing = true;

        while (playing) {

            System.out.println("\nAvailable exits: [" +
                    String.join(", ", current.getExits().keySet()) + "]");

            System.out.print("Enter command (Quit or direction): ");
            String rawInput = scanner.nextLine().trim();
            String command = rawInput.toUpperCase();

            switch (command) {

                case "NORTH":
                case "SOUTH":
                case "EAST":
                case "WEST":
                    Room next = player.move(command, roomMap);
                    if (next != null) {
                        current = next;
                        player.enterRoom(current, scanner);
                    }
                    break;

                case "QUIT":
                    System.out.println("Thanks for playing!");
                    playing = false;
                    break;

                default:
                    System.out.println("Unknown command: " + rawInput);
            }
        }
    }
}
