import java.util.*;

public class Player {
    private int currentRoomNumber;
    private Set<Integer> visitedRooms;
    private int hp;
    private List<Item> inventory;
    private Item equippedWeapon;
    private Item equippedArmor;
    private int baseAttackDamage;
    private boolean isInCombat;
   
    public Player(){
        this.currentRoomNumber = 1;
        this.visitedRooms = new HashSet<>();
        this.visitedRooms.add(currentRoomNumber);
        this.hp = 100;
        this.baseAttackDamage = 0;
        this.inventory = new ArrayList<>();
        this.equippedWeapon = null;
        this.equippedArmor = null;
        this.isInCombat = false;

    }
     // Movement
    public Room move(String direction, Map<Integer, Room> roomMap) {
        Room currentRoom = roomMap.get(currentRoomNumber);
        Integer nextRoomNumber = currentRoom.getExit(direction.toUpperCase());

        if (nextRoomNumber == null || nextRoomNumber == 0) {
            System.out.println("You can't go this way.");
            return null;
        }

        setCurrentRoomNumber(nextRoomNumber);
        return roomMap.get(nextRoomNumber);
    }

    // Room tracking
    public int getCurrentRoomNumber() {
        return currentRoomNumber;
    }

    private void setCurrentRoomNumber(int roomNumber) {
        this.currentRoomNumber = roomNumber;
        visitedRooms.add(roomNumber);
    }

    public boolean hasVisited(int roomNumber) {
        return visitedRooms.contains(roomNumber);
    }
     // Enter Room 
        public void enterRoom(Room room, Scanner scanner) {
            if (!room.isVisited()) {
                System.out.println("\nYou have arrived at " + room.getName() + "... " + room.getDescription());
                room.visit();
            } else {
                System.out.println("\nYou have returned to " + room.getName() + ".");
            }
    

        }

    // Inventory Management

    public List<Item> getInventory() {
        return inventory;
    }

        public void pickUp(Item item) {
        inventory.add(item);
    }



    public void drop(Item item) {
        inventory.remove(item);
        if (item == equippedWeapon) unequip(item);
        if (item == equippedArmor) unequip(item);
    }



    public Item getItemByName(String name) {
        for (Item i : inventory) if (i.getName().equalsIgnoreCase(name)) return i;
        return null;
    }




    // Equip / Unequip
    public void equip(Item item) {
        if (item.isEquipable() && inventory.contains(item) && item.isWeapon()) {
            equippedWeapon = item;
            System.out.println(item.getName() + " equipped. Attack damage: " + attackDamage());

        } else if (item.isEquipable() && inventory.contains(item) && item.isArmor()) {
            equippedArmor = item;
            System.out.println(item.getName() + " equipped. Defense increased." + defense());





        }
    else {
            System.out.println("Cannot equip this item.");
        }
    }

    public void unequip(Item item) {
        if (equippedWeapon != null) {
            System.out.println(equippedWeapon.getName() + " unequipped.");
            equippedWeapon = null;
        } else if (equippedArmor != null) {
            System.out.println(equippedArmor.getName() + " unequipped.");
            equippedArmor = null;
        } else {
            System.out.println("No item equipped.");
        }
    }

    // Heal
    public void heal(Item item) {
        if (item.isConsumable() && inventory.contains(item)) {
            hp += item.getHealing();
            inventory.remove(item);
            System.out.println("Healed " + item.getHealing() + " HP. Current HP: " + hp);
        } else {
            System.out.println("Cannot use this item to heal.");
        }
    }

    public void help() {
        System.out.println(
        "Available commands:\n"+
        "> MOVE <direction>: moves the player in the specified direction (NORTH, EAST, SOUTH, WEST)\n" +
        "> EXPLORE: lists items in the room \n" + 
        "> PICKUP <item>: picks up an item from the room, \n" + 
        "> DROP <item>: drops an item into the room, \n" + 
        "> INSPECT <item>: shows item description, \n" + 
        "> EQUIP <item>: equips an item, \n" +
        "> UNEQUIP <item>: unequips an item, \n" +
        "> HEAL <item>: uses a healing item, \n" +
        "> INVENTORY: lists items in inventory, \n" +
        "> STATUS: shows player status, \n" +
        "> QUIT: exits the game.");
    }

    public void printStatus() {
        System.out.println("Player HP: " + hp);
        if (equippedWeapon != null) {
            System.out.println("Equipped Item: " + equippedWeapon.getName() + " (Damage: " + equippedWeapon.getDamage() + ")");
        } else {
            System.out.println("No item equipped.");
        }
    }



    // Stats
    public int getHp() {
        return hp;
    }

    public int attackDamage() {
        return equippedWeapon != null ? equippedWeapon.getDamage() : baseAttackDamage;
    }

    public int defense() {
        return equippedArmor != null ? (int) equippedArmor.getArmor() : 0;
    }

    public boolean isAlive() {
        return hp > 0;
    }

    // Take Damage
     public void takeDamage(int damage) {
        hp -= damage ;
        if (hp < 0) hp = 0;
    }
    public boolean isInCombat() {
        return isInCombat;
    }
    public void setInCombat(boolean inCombat) {
        isInCombat = inCombat;
    }

    private void handlePlayerDeath(){
        System.out.println("You have been defeated. Game Over.");
        setInCombat(false);
    }

    //combat methods
    public void handleCombat(Monster monster,Room room, Scanner scanner){
        Room currentRoom = roomMap.get(currentRoomNumber);
        if (monster == null || !monster.isAlive()) {
            System.out.println("No monster to fight here.");
            return;
        }

        System.out.println("You are in combat with "+ monster.getName() + "!");
        setInCombat(true);
        while(isInCombat && monster.isAlive() && isAlive()){
            System.out.println("\n" + monster.getName() + " HP: " + monster.getHitPoints());
            System.out.println("Your HP: " + getHp());

            if(!isAlive()){
                setInCombat(false);
                handlePlayerDeath();
                break;
            }

            System.out.println("\nWhat would you like to do?");
            System.out.println("1. Attack");
            System.out.println("2. Heal");
            System.out.println("3. Run");
            System.out.println("4. Analyze Monster");

            String choice = scanner.nextLine().trim();

            switch(choice.toUpperCase()) {
                case "1":
                case "ATTACK":
                int playerDamage = attackDamage();
                if (playerDamage <= 0) {
                    System.out.println("You strike but deal no damage.");
                } else {
                    monster.takeDamage(playerDamage);
                    System.out.println("You deal " + playerDamage + " damage!");
                }

                if(monster.isAlive()){
                      
                    // Monster attacks back
                    int monsterDamage = monster.getDamage() - defense();
                    int net = Math.max(0, monsterDamage - defense());
                    takeDamage(net);
                    System.out.println(monster.getName() + "hits you for " + net + " damage!");
                      if(!isAlive()){
                        setInCombat(false);
                        handlePlayerDeath();
                        return;
                      }
                        } else {
                    System.out.println("You have defeated " + monster.getName() + "!");
                    setInCombat(false);
                    Item drop = monster.getDropItem();
                    if(drop !=null){
                        currentRoom.addItem(drop);
                        System.out.println(monster.getName() + " dropped " + drop.getName() + "!");
                    }
                    if(monster.getType().equalsIgnoreCase("Boss")){
                        System.out.println("Congratulations! You have defeated the Boss and completed the game!");
                        currentRoom.removeMonster();
                    }
                } break;
                case "2":
                case "HEAL":
                    System.out.print("Enter healing item name: ");
                    String itemName = scanner.nextLine().trim();
                    Item healItem = getItemByName(itemName);
                    if (healItem == null) {
                        System.out.println("Item not in inventory or doesn't exist.");
                    } else {
                        heal(healItem);
                    }if (monster.isAlive()) {
                        int monsterDamage2 = monster.attack();
                        int net2 = Math.max(0, monsterDamage2 - defense());
                        takeDamage(net2);
                        System.out.println(monster.getName() + " hits you for " + net2 + " damage!");
                        if (!isAlive()) {
                            setInCombat(false);
                            handlePlayerDeath();
                            return;
                        }
                    }
                    break;
                    case "3":
                    case "RUN":
                        boolean escaped = Math.random() < 0.5;
                        if (escaped) {
                            System.out.println("You managed to escape!");
                            setInCombat(false);
    
                            // Move player to a random valid exit (if any)
                            Map<String, Integer> exits = currentRoom.getExits();
                            List<String> validDirs = new ArrayList<>();
                            for (Map.Entry<String, Integer> e : exits.entrySet()) {
                                if (e.getValue() != null && e.getValue() > 0) validDirs.add(e.getKey());
                            }
                            if (!validDirs.isEmpty()) {
                                String chosenDir = validDirs.get(new Random().nextInt(validDirs.size()));
                                Room next = move(chosenDir, roomMap);
                                if (next != null) {
                                    System.out.println("You run " + chosenDir + " to " + next.getName() + ".");
                                    // Optionally, if the next room has a monster, the caller (RoomLoader) can trigger combat there.
                                } else {
                                    System.out.println("You couldn't find a way out and remain in the same room.");
                                }
                            } else {
                                System.out.println("No exits to run through — you remain in place.");
                            }
                        } else {
                            System.out.println("Failed to escape!");
                            int monsterDamage3 = monster.attack();
                            int net3 = Math.max(0, monsterDamage3 - defense());
                            takeDamage(net3);
                            System.out.println(monster.getName() + " hits you for " + net3 + " damage!");
                            if (!isAlive()) {
                                setInCombat(false);
                                handlePlayerDeath();
                                return;
                            }
                        }
                        break;
                        case "4":
                        case "ANALYZE":
                            System.out.println("Monster: " + monster.getName());
                            System.out.println("Description: " + monster.getDescription());
                            System.out.println("Type: " + monster.getType());
                            System.out.println("HP: " + monster.getHitPoints());
                            System.out.println("Damage: " + monster.getDamage());
                            break;
                default:
                    System.out.println("Invalid choice. Please select a valid action.");
                    break;


            }

        }
        
    }











}