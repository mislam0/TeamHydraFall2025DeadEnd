import java.util.*;

public class RoomLoader {
    private List<Room> rooms;
    public Map<Integer, Room> roomMap;
    private Player player;
    

    public RoomLoader() {
        roomMap = Roomreader.readRooms("Map.txt");
        Map<String, Item> itemMap = Itemreader.loadItems("Items.txt", roomMap);
        MonsterReader.loadMonsters("Monsters.txt", roomMap, itemMap);

        rooms = new ArrayList<>(roomMap.values());
    }

    public void startGame(Player player, Scanner scanner) {
        if (rooms.isEmpty()) {
            System.out.println("No rooms loaded. Exiting.");
            return;
        }

        this.player = player;

        Room current = roomMap.get(player.getCurrentRoomNumber());
        player.enterRoom(current, scanner, roomMap);
        

        boolean playing = true;

        while (playing) {

            System.out.println("\nAvailable exits: [" +
                    String.join(", ", current.getExits().keySet()) + "]");

            System.out.print("Enter command (Quit, Direction, or Help): ");
            String rawInput = scanner.nextLine().trim();
            String[] tokens = rawInput.split(" ", 2);
            String command = tokens[0].toUpperCase();
            String argument = tokens.length > 1 ? tokens[1].trim() : "";

            switch (command) {

                case "NORTH":
                case "SOUTH": 
                case "EAST": 
                case "WEST": 
                    Room next = player.move(command, roomMap);
                    if (next != null) {
                        current = next;
                        player.enterRoom(current, scanner, roomMap);
                       
                    }
                    player.handleGameWin(roomMap);
                    break;

                    case "EXPLORE":
                    if (current.getItems().isEmpty()) System.out.println("No items here.");
                    else current.getItems().forEach(i -> System.out.println("- " + i.getName()));
                    break;

                    case "PICKUP":
                    Item toPick = current.getItemByName(argument);
                    if (toPick != null) {
                        player.pickUp(toPick);
                        current.removeItem(toPick);
                        
                    } else System.out.println("Item not found.");
                    break;

                    case "DROP":
                    Item toDrop = player.getItemByName(argument);
                    if (toDrop != null) {
                        player.drop(toDrop);
                        current.addItem(toDrop);
                        System.out.println("Dropped " + toDrop.getName());
                    } else System.out.println("Item not in inventory.");
                    break;
                    
                    case "INSPECT":
                    Item toInspect = player.getItemByName(argument);
                    if (toInspect != null) System.out.println(toInspect.getDescription());
                    else System.out.println("Item not in inventory.");
                    break;

                case "EQUIP":
                    Item equipItem = player.getItemByName(argument);
                    if (equipItem != null) player.equip(equipItem);
                    else System.out.println("Item not in inventory.");
                    break;

                case "UNEQUIP":
                    Item unequipItem = player.getItemByName(argument);
                    if (unequipItem != null) player.unequip(unequipItem);
                    else System.out.println("Item not in inventory.");
                    break;

                    case "USE":
                    Item healItem = player.getItemByName(argument);
                    if (healItem != null) player.heal(healItem);
                    else System.out.println("Item not in inventory or cannot heal.");
                    break;


                    case "INVENTORY":
                    if (player.getInventory().isEmpty()) System.out.println("No items in inventory.");
                    else player.getInventory().forEach(i -> System.out.println("- " + i.getName()));
                    break;

                    case "HELP":
                    player.help();
                    break;

                    case "STATUS":
                    player.printStatus();
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
    

        