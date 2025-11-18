/* 
 * Authors: David and Mohammed
 */

import java.io.IOException;
import java.util.*;

public class RoomLoader {
    private List<Room> rooms;
    public Map<Integer, Room> roomMap;
    private Player player;

    // *** For items, including puzzle rewards ***
    private Map<String, Item> itemMap;
    // NEW: itemNameMap: keyed by item NAME – used by GameSerializer for SAVE/LOAD
    private Map<String, Item> itemNameMap;

    public RoomLoader() {
        roomMap = Roomreader.readRooms("Map.txt");

        // Store globally
        itemMap = Itemreader.loadItems("Items.txt", roomMap);

        MonsterReader.loadMonsters("Monsters.txt", roomMap, itemMap);

        rooms = new ArrayList<>(roomMap.values());
        // Build name-based map for serializer
        itemNameMap = new HashMap<>();
        for (Item it : itemMap.values()) {
            itemNameMap.put(it.getName(), it);
        }
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

                    // Guardian Key lock: room 39 -> 40 (south)
                    if (command.equals("SOUTH") && current.getId() == 39) {
                        Integer targetRoom = current.getExit("SOUTH");
                        if (targetRoom != null && targetRoom == 40 && !current.isGuardianDoorUnlocked()) {
                            System.out.println("You need Guardian Key to proceed. Would you like to use it? Enter \"Use guardian key\" to proceed.");
                            break; // do not move
                        }
                    }

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
                    // New: statue inspect in room 18
                    if (argument.equalsIgnoreCase("statue")) {
                        if (current.getId() == 18) {
                            System.out.println("The left statue faces " + current.getStatueDir1() + ".");
                            System.out.println("The middle statue faces " + current.getStatueDir2() + ".");
                            System.out.println("The right statue faces " + current.getStatueDir3() + ".");
                        } else {
                            System.out.println("There is no statue to inspect here.");
                        }
                    } else {
                        Item toInspect = player.getItemByName(argument);
                        if (toInspect != null) System.out.println(toInspect.getDescription());
                        else System.out.println("Item not in inventory.");
                    }
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

                    // Save game from rah
                case "SAVE":
                    if (argument.isEmpty()) {
                        System.out.println("Usage: SAVE <name>");
                    } else {
                        String saveName = argument;
                        try {
                            // Build monster registry basically from current rooms (by id or name or so)
                            Map<String, Monster> monstersById = buildMonsterMap();
                            Map<String, String> state =
                                    GameSerializer.serialize(player, roomMap, itemNameMap, monstersById);
                            SaveManager.save(saveName, state);
                            System.out.println("Game saved as: " + saveName);
                        } catch (IOException e) {
                            System.err.println("Failed to save: " + e.getMessage());
                        } catch (Exception e) {
                            System.err.println("Unexpected error while saving: " + e.getMessage());
                        }
                    }
                    break;

                // LOAD game
                case "LOAD":
                    if (argument.isEmpty()) {
                        System.out.println("Usage: LOAD <name>");
                    } else {
                        String loadName = argument;
                        try {
                            Map<String, String> loaded = SaveManager.load(loadName);
                            Map<String, Monster> monstersById = buildMonsterMap();
                            GameSerializer.deserialize(loaded, player, roomMap, itemNameMap, monstersById);
                            System.out.println("Game loaded: " + loadName);

                            // Refresh current room based on player's restored room number
                            current = roomMap.get(player.getCurrentRoomNumber());
                            if (current != null) {
                                player.enterRoom(current, scanner);
                            } else {
                                System.out.println("Warning: loaded room not found. Staying in previous room.");
                            }
                        } catch (IOException e) {
                            System.err.println("Failed to load: " + e.getMessage());
                        } catch (Exception e) {
                            System.err.println("Unexpected error while loading: " + e.getMessage());
                        }
                    }
                    break;

                // Examine commands (door + panel)
                case "EXAMINE":
                    if (argument.equalsIgnoreCase("door")) {
                        if (current.getId() == 3 && !current.isDoorPuzzleSolved()) {
                            System.out.println("RIDDLE: What has to be broken before you can use it?");
                        } else {
                            System.out.println("There is no door to examine.");
                        }
                    } 
                    else if (argument.equalsIgnoreCase("panel")) {
                        if (current.getId() == 5) {
                            if (current.getLeverSeq1() == 0 &&
                                current.getLeverSeq2() == 0 &&
                                current.getLeverSeq3() == 0) {
                                System.out.println("You have not attempted to pull the lever yet.");
                            } else {
                                System.out.println("Your last lever sequence was: " +
                                        current.getLeverSeq1() + ", " +
                                        current.getLeverSeq2() + ", " +
                                        current.getLeverSeq3() + ".");
                            }
                            System.out.println("You have reset the panel " +
                                    current.getLeverResetCount() + " times.");
                        } else {
                            System.out.println("There is no panel to examine.");
                        }
                    } 
                    else {
                        System.out.println("Examine what?");
                    }
                    break;

                case "CHECK":
                    if (argument.equalsIgnoreCase("components")) {

                        if (current.getId() == 6) {

                            if (!current.isDM1Placed() && !current.isDM4Placed()) {
                                System.out.println("Scale Fragment and Spirit Essence are both missing.");
                            } else if (current.isDM1Placed() && !current.isDM4Placed()) {
                                System.out.println("Only Spirit Essence is missing.");
                            } else if (!current.isDM1Placed() && current.isDM4Placed()) {
                                System.out.println("Only Scale Fragment is missing.");
                            } else {
                                System.out.println("All components are placed.");
                            }

                        } else {
                            System.out.println("There is nothing to check here.");
                        }

                    } else {
                        System.out.println("Check what?");
                    }
                    break;

                // Door puzzle answer
                case "ANSWER":
                    if (argument.equalsIgnoreCase("door")) {
                        if (current.getId() == 3 && !current.isDoorPuzzleSolved()) {

                            System.out.print("Your answer: ");
                            String ans = scanner.nextLine().trim().toLowerCase();

                            if (ans.equals("egg")) {
                                System.out.println("The lock clicks open.");
                                current.setDoorPuzzleSolved(true);

                                // Getting that item for this puzzle (Shard of Bone, DM7)
                                Item reward = itemMap.get("DM7");
                                if (reward != null) {
                                    player.pickUp(reward);
                                    System.out.println("You obtained: " + reward.getName() + "!");
                                }

                            } else {
                                System.out.println("Incorrect answer.");
                            }
                        } else {
                            System.out.println("Nothing to answer here.");
                        }
                    } else {
                        System.out.println("Answer what?");
                    }
                    break;

                // Lever puzzle: Pull Lever
                case "PULL":
                    if (argument.equalsIgnoreCase("lever")) {
                        if (current.getId() == 5) {
                            if (current.isLeverPuzzleSolved()) {
                                System.out.println("The lever is already set in place. Nothing more happens.");
                            } else if (current.isLeverRequiresReset()) {
                                System.out.println("You have to reset the panel first.");
                            } else {
                                System.out.print("What is your first sequence? (1-5) ");
                                int s1 = parseIntOrDefault(scanner.nextLine().trim(), -1);

                                System.out.print("What is your second sequence? (1-5) ");
                                int s2 = parseIntOrDefault(scanner.nextLine().trim(), -1);

                                System.out.print("What is your third sequence? (1-5) ");
                                int s3 = parseIntOrDefault(scanner.nextLine().trim(), -1);

                                current.setLeverSequence(s1, s2, s3);
                                current.setLeverRequiresReset(true);

                                if (s1 == 1 && s2 == 2 && s3 == 3) {
                                    System.out.println("You hear a heavy click from within the walls.");
                                    current.setLeverPuzzleSolved(true);
                                    current.setLeverRequiresReset(false);

                                    // Reward: DM1
                                    Item reward2 = itemMap.get("DM1");
                                    if (reward2 != null) {
                                        player.pickUp(reward2);
                                        System.out.println("You obtained: " + reward2.getName() + "!");
                                    }
                                }
                            }
                        } else {
                            System.out.println("There is no lever to pull here.");
                        }
                    } else {
                        System.out.println("Pull what?");
                    }
                    break;

                // Lever puzzle: Reset Panel
                case "RESET":
                    if (argument.equalsIgnoreCase("panel")) {
                        if (current.getId() == 5) {
                            current.resetLeverPanel();
                            System.out.println("You reset the panel. You can pull the lever again.");
                        } else {
                            System.out.println("There is no panel to reset.");
                        }
                    } 
                    else {
                        System.out.println("Reset what?");
                    }
                    break;

                // Use guardian key (room 39 lock)
                case "USE KEY":
                    if (argument.equalsIgnoreCase("guardian key")) {

                        if (current.getId() == 39) {

                            Item guardianKey = null;
                            for (Item it : player.getInventory()) {
                                if ("DM6".equalsIgnoreCase(it.getId())) {
                                    guardianKey = it;
                                    break;
                                }
                            }

                            if (guardianKey != null) {
                                System.out.println("You use the Guardian Key. The way south is now open.");
                                current.setGuardianDoorUnlocked(true);
                                // Key is not consumed unless you want it to be
                            } else {
                                System.out.println("You don't have the Guardian Key.");
                            }

                        } else {
                            System.out.println("You can't use that here.");
                        }

                    } else {
                        System.out.println("Use what?");
                    }
                    break;

                // Tiles puzzle: Move Tiles
                case "MOVE":
                    if (argument.equalsIgnoreCase("tiles")) {

                        if (current.getId() != 17) {
                            System.out.println("There are no tiles to move here.");
                            break;
                        }

                        if (current.isTilesPuzzleSolved()) {
                            System.out.println("The tiles already seem to be arranged correctly.");
                            break;
                        }

                        System.out.println("Where would you like to move the red tile to? (First slot, Second slot, Third slot)");
                        String red = normalizeSlot(scanner.nextLine().trim());

                        System.out.println("Where would you like to move the blue tile to? (First slot, Second slot, Third slot)");
                        String blue = normalizeSlot(scanner.nextLine().trim());

                        System.out.println("Where would you like to move the green tile to? (First slot, Second slot, Third slot)");
                        String green = normalizeSlot(scanner.nextLine().trim());

                        // Correct order: red = second, blue = third, green = first
                        if ("second".equals(red) && "third".equals(blue) && "first".equals(green)) {
                            System.out.println("You hear a soft chime as the tiles click into place.");
                            current.setTilesPuzzleSolved(true);

                            Item reward3 = itemMap.get("DM3"); // small potion
                            if (reward3 != null) {
                                player.pickUp(reward3);
                                System.out.println("You obtained: " + reward3.getName() + "!");
                            }
                        } else {
                            System.out.println("That arrangement feels wrong. Maybe try a different order.");
                        }

                    } else {
                        System.out.println("Move what?");
                    }
                    break;

                // Statues puzzle: Set statue direction
                case "SET":
                    if (argument.equalsIgnoreCase("statue direction")) {

                        if (current.getId() != 18) {
                            System.out.println("There are no statues to set here.");
                            break;
                        }

                        if (current.isStatuesPuzzleSolved()) {
                            System.out.println("The statues already seem perfectly aligned.");
                            break;
                        }

                        System.out.println("Set first statues' direction to? (North, East, South, West)");
                        String d1 = normalizeDirection(scanner.nextLine().trim());

                        System.out.println("Set second statues' direction to? (North, East, South, West)");
                        String d2 = normalizeDirection(scanner.nextLine().trim());

                        System.out.println("Set third statues' direction to? (North, East, South, West)");
                        String d3 = normalizeDirection(scanner.nextLine().trim());

                        current.setStatueDirections(d1, d2, d3);

                        // Correct order: North, West, South
                        if ("NORTH".equals(d1) && "WEST".equals(d2) && "SOUTH".equals(d3)) {
                            System.out.println("You hear grinding stone as the statues lock into place.");
                            current.setStatuesPuzzleSolved(true);

                            Item reward4 = itemMap.get("DM4"); // Spirit Essence
                            if (reward4 != null) {
                                player.pickUp(reward4);
                                System.out.println("You obtained: " + reward4.getName() + "!");
                            }
                        } else {
                            System.out.println("Nothing seems to happen.");
                        }

                    } else {
                        System.out.println("Set what?");
                    }
                    break;

                // Altar puzzle: place (item) on altar
                case "PLACE":
                    if (argument.toLowerCase().endsWith("on altar")) {

                        if (current.getId() != 6) {
                            System.out.println("There is no altar here.");
                            break;
                        }
                        if (current.isAltarPuzzleSolved()) {
                            System.out.println("The altar has already been used.");
                            break;
                        }

                        String itemName = argument.substring(0, argument.length() - 9).trim(); // remove "on altar"
                        Item itemToPlace = player.getItemByName(itemName);

                        if (itemToPlace == null) {
                            System.out.println("You don't have that item.");
                            break;
                        }

                        if (itemToPlace.getId().equals("DM1")) {
                            current.setDM1Placed(true);
                            player.getInventory().remove(itemToPlace);
                            System.out.println("You placed the Scale Fragment onto the altar.");
                        } 
                        else if (itemToPlace.getId().equals("DM4")) {
                            current.setDM4Placed(true);
                            player.getInventory().remove(itemToPlace);
                            System.out.println("You placed the Spirit Essence onto the altar.");
                        } 
                        else {
                            System.out.println("This item does not fit into the altar.");
                        }

                    } else {
                        System.out.println("Place what?");
                    }
                    break;

                // Altar puzzle: craft key
                case "CRAFT":
                    if (argument.equalsIgnoreCase("key")) {
                        if (current.getId() == 6) {

                            if (current.isDM1Placed() && current.isDM4Placed()) {

                                Item crafted = itemMap.get("DM6"); // Guardian Key
                                if (crafted != null) {
                                    player.pickUp(crafted);
                                    System.out.println("You crafted the Guardian Key!");
                                }

                                current.setAltarPuzzleSolved(true);

                            } else {
                                System.out.println("You cannot craft the key yet.");
                            }

                        } else {
                            System.out.println("There is no altar here.");
                        }
                    } 
                    else {
                        System.out.println("Craft what?");
                    }
                    break;

                // Scroll / decipher puzzle
                case "READ":
                    if (argument.equalsIgnoreCase("scroll")) {
                        if (current.getId() == 11) {
                            System.out.println("The word to decode is \"blah\" for next room, find which it could be.");
                        } else {
                            System.out.println("There is no scroll to read here.");
                        }
                    } else {
                        System.out.println("Read what?");
                    }
                    break;

                case "DECIPHER":
                    if (argument.equalsIgnoreCase("blah")) {
                        if (current.getId() == 12) {
                            System.out.println("Abyssal Guardian exists somewhere in your path.. I think..");
                        } else {
                            System.out.println("You don't see any deciphering tool here.");
                        }
                    } else {
                        if (current.getId() == 12) {
                            System.out.println("That doesn't seem to produce anything meaningful.");
                        } else {
                            System.out.println("You don't see any deciphering tool here.");
                        }
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
    

    private int parseIntOrDefault(String s, int def) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    // Helper for tile positions: accepts "first", "first slot", etc.
    private String normalizeSlot(String s) {
        String t = s.toLowerCase();
        if (t.startsWith("first")) return "first";
        if (t.startsWith("second")) return "second";
        if (t.startsWith("third")) return "third";
        return t;
    }

    // Helper for statue directions: accepts "north", "n", etc.
    private String normalizeDirection(String s) {
        String t = s.trim().toLowerCase();
        if (t.startsWith("n")) return "NORTH";
        if (t.startsWith("e")) return "EAST";
        if (t.startsWith("s")) return "SOUTH";
        if (t.startsWith("w")) return "WEST";
        return t.toUpperCase();
    }
    private Map<String, Monster> buildMonsterMap() {
        Map<String, Monster> out = new HashMap<>();
        for (Room r : roomMap.values()) {
            Monster m = r.getMonster();
            if (m != null) {
                try {
                    // prefer id field if present via reflection
                    java.lang.reflect.Field idF = Monster.class.getDeclaredField("id");
                    idF.setAccessible(true);
                    Object idVal = idF.get(m);
                    if (idVal != null && !idVal.toString().isEmpty()) {
                        out.put(idVal.toString(), m);
                        continue;
                    }
                } catch (Exception ignored) {}
                // fallback to name
                try {
                    String nm = m.getName();
                    if (nm != null && !nm.isEmpty()) out.put(nm, m);
                } catch (Exception ignored) {}
            }
        }
        return out;
    }
}
