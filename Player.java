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
     // Enter a room
public void enterRoom(Room room, Scanner scanner, Map<Integer, Room> roomMap) {
    if (room == null) {
        System.out.println("Error: Room does not exist.");
        return;
    }

    // Print description if first visit
    if (!room.isVisited()) {
        System.out.println("\nYou have arrived at " + room.getName() + "... " + room.getDescription());
        room.visit();
    } else {
        System.out.println("\nYou have returned to " + room.getName() + ".");
    }

    // Set current room number
    setCurrentRoomNumber(room.getId());

    // Trigger combat if monster is present
    if (room.hasMonster() && room.getMonster().isAlive()) {
        handleCombat(room.getMonster(), scanner, roomMap);
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

    public int attackDamageWithDice() {
        if (equippedWeapon != null && equippedWeapon.getDice() != null) {
            Dice dice = new Dice();
            return dice.rollDice(equippedWeapon.getDice());
        }
        return baseAttackDamage ;
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
        
        System.exit(0);
    }

    //combat methods
    // Combat method
public void handleCombat(Monster monster, Scanner scanner, Map<Integer, Room> roomMap) {
    if (monster == null || !monster.isAlive()) {
        System.out.println("No monster to fight here.");
        return;
    }

    System.out.println("You are in combat with " + monster.getName() + "!");
    setInCombat(true);

    Dice dice = new Dice();

    while (isInCombat && monster.isAlive() && isAlive()) {
        Room currentRoom = roomMap.get(currentRoomNumber);
        if (currentRoom == null) {
            System.out.println("Error: Current room not found.");
            setInCombat(false);
            return;
        }

        System.out.println("\n" + monster.getName() + " HP: " + monster.getHitPoints());
        System.out.println("Your HP: " + getHp());

        System.out.println("\nWhat would you like to do?");
        System.out.println("1. Attack");
        System.out.println("2. Heal");
        System.out.println("3. Escape");
        System.out.println("4. Analyze Monster");

        String choice = scanner.nextLine().trim().toUpperCase();

        switch (choice) {
            case "1":
            case "ATTACK":
                int playerDamage = attackDamageWithDice();
                if (playerDamage <= 0) {
                    System.out.println("You strike but deal no damage.");
                } else {
                    monster.takeDamage(playerDamage);
                    System.out.println("You deal " + playerDamage + " damage!");
                }

                if (monster.isAlive()) {
                    int monsterDamage = Math.max(0, monster.getDamage() - defense());
                    takeDamage(monsterDamage);
                    System.out.println(monster.getName() + " hits you for " + monsterDamage + " damage!");
                    if (!isAlive()) {
                        setInCombat(false);
                        handlePlayerDeath();
                        return;
                    }
                } else {
                    System.out.println("You have defeated " + monster.getName() + "!");
                    setInCombat(false);
                    Item drop = monster.getDropItem();
                    if (drop != null) {
                        inventory.add(drop);
                        System.out.println(monster.getName() + " dropped " + drop.getName() + "!");
                    }
                    if (monster.getType().equalsIgnoreCase("Boss")) {
                        System.out.println("Congratulations! You have defeated the Boss and completed the game!");
                        System.exit(0);
                    }
                }
                break;

            case "2":
            case "HEAL":
                System.out.print("Enter healing item name: ");
                String itemName = scanner.nextLine().trim();
                Item healItem = getItemByName(itemName);
                if (healItem != null) {
                    heal(healItem);
                } else {
                    System.out.println("Item not in inventory or doesn't exist.");
                }

                // Monster attacks after healing
                if (monster.isAlive()) {
                    int monsterDamage = Math.max(0, monster.getDamage() - defense());
                    takeDamage(monsterDamage);
                    System.out.println(monster.getName() + " hits you for " + monsterDamage + " damage!");
                    if (!isAlive()) {
                        setInCombat(false);
                        handlePlayerDeath();
                        return;
                    }
                }
                break;

            case "3":
            case "escape":
                boolean escaped = (dice.dX(2) == 1); // 50% chance
                if (escaped) {
                    System.out.println("You managed to escape!");
                    setInCombat(false);

                    Map<String, Integer> exits = currentRoom.getExits();
                    List<String> validDirs = new ArrayList<>();
                    for (Map.Entry<String, Integer> entry : exits.entrySet()) {
                        if (entry.getValue() != null && entry.getValue() > 0) {
                            validDirs.add(entry.getKey());
                        }
                    }

                    if (!validDirs.isEmpty()) {
                        int roll = dice.dX(validDirs.size());
                        String chosenDir = validDirs.get(roll - 1);
                        Room next = move(chosenDir, roomMap);
                        if (next != null) {
                            System.out.println("You run " + chosenDir + " to " + next.getName() + ".");
                        } else {
                            System.out.println("You couldn't find a way out and remain in the same room.");
                        }
                    } else {
                        System.out.println("No exits to run through — you remain in place.");
                    }

                } else {
                    System.out.println("Failed to escape!");
                    int monsterDamage = Math.max(0, monster.getDamage() - defense());
                    takeDamage(monsterDamage);
                    System.out.println(monster.getName() + " hits you for " + monsterDamage + " damage!");
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
               }   break;
        }
    }
}

