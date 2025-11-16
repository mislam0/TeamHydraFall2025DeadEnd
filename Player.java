import java.util.*;

public class Player {
    // Room tracking
    private int currentRoomNumber;
    private Set<Integer> visitedRooms;

    // Player stats
    private int hp;
    private int baseAttackDamage;
    private List<Item> inventory;
    private double defense;
    
    

    // Equipped items
    private Item equippedWeapon;
    private Item equippedArmor;

    // Combat state
    private boolean isInCombat;

    // Constants
    private final int MAX_HP = 100;
    private final int MAX_INVENTORY_SIZE = 25;

   
   
    public Player(){
        this.currentRoomNumber = 1;
        this.visitedRooms = new HashSet<>();
        this.visitedRooms.add(currentRoomNumber);
        this.hp = 100;
        this.baseAttackDamage = 0;
        this.defense = 0.0;
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
            if (equippedWeapon != null) {
                System.out.println(equippedWeapon.getName() + " unequipped.");
            }
            equippedWeapon = item;
            System.out.println(item.getName() + " equipped. Attack damage: " + attackDamage());

        } else if (item.isEquipable() && inventory.contains(item) && item.isArmor()) {
            if (equippedArmor != null) {
                System.out.println(equippedArmor.getName() + " unequipped.");
            }
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
    int starthp = hp;
        if (hp < MAX_HP && item.isConsumable() && inventory.contains(item) ) {
            hp += item.getHealing();

            if (hp > MAX_HP) {
            hp = MAX_HP;
            }

            inventory.remove(item);
            System.out.println("Healed " + (hp - starthp) + " HP. Current HP: " + hp); 
        } else if (hp >= MAX_HP) {
            System.out.println("Your HP is already full.");
            
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
        } if (equippedArmor != null) {
            System.out.println("Equipped Armor: " + equippedArmor.getName() + " (Armor: " + equippedArmor.getArmor() + ")");
        }else {
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
    Dice dice = new Dice();

    if (equippedWeapon != null && equippedWeapon.isWeapon()) {
        int base = equippedWeapon.getDamage();       // e.g., 15 for Guardian Sword
        String diceType = equippedWeapon.getDice();  // e.g., "d12"
        int diceRoll = 0;

        if (diceType != null && !diceType.isEmpty()) {
            switch(diceType.toLowerCase()) {
                case "d2": diceRoll = dice.d2(); break;
                case "d4": diceRoll = dice.d4(); break;
                case "d6": diceRoll = dice.d8(); break;
                case "d12": diceRoll = dice.d12(); break;
            }
        }

        int totalDamage = base + diceRoll;

        if (diceRoll > 0) {
            System.out.println("You swing " + equippedWeapon.getName() + " (rolled " + diceRoll + ") for " + totalDamage + " damage!");
        } else {
            System.out.println("You swing " + equippedWeapon.getName() + " for " + totalDamage + " damage!");
        }

        return totalDamage;
    }

    return baseAttackDamage;
}

public int monsterAttackDamageWithDice(Monster monster) {
    Dice dice = new Dice();
    if (monster != null) {
        int base = monster.getDamage();
        String diceType = monster.monsterTypeDice();
        int diceRoll = 0;

        if (diceType != null && !diceType.isEmpty()) {
            switch(diceType.toLowerCase()) {
                case "d4": diceRoll = dice.d4(); break;
                case "d8": diceRoll = dice.d8(); break;
                case "d12": diceRoll = dice.d12(); break;
            }
        }

        int totalDamage = base + diceRoll;

        if (diceRoll > 0) {
            System.out.println(monster.getName() + " attacks (rolled " + diceRoll + ") for " + totalDamage + " damage!");
        } else {
            System.out.println(monster.getName() + " attacks for " + totalDamage + " damage!");
        }

        return totalDamage;
    }
    return 0;
}

    public double defense() {
        return equippedArmor != null ? (double) equippedArmor.getArmor() : 0;
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

        if (hp <= 25) {
                System.out.println("Hint: Use the ‘Heals’ command to recover, or “Escape” to return to safety in the previous room.");
            }

        System.out.println("\nWhat would you like to do?");
        System.out.println("1. Analyze Monster");
        System.out.println("2. Attack");
        System.out.println("3. Heal");
        System.out.println("4. Escape");
        System.out.print("Enter command: " );
        

        String choice = scanner.nextLine().trim().toUpperCase();

        switch (choice) {

            case "1":
            case "ANALYZE":
                System.out.println("Monster: " + monster.getName());
                System.out.println("Description: " + monster.getDescription());
                System.out.println("Type: " + monster.getType());
                System.out.println("HP: " + monster.getHitPoints());
                System.out.println("Damage: " + monster.getDamage());
               
                break;


            case "2":
            case "ATTACK":
    int playerDamage = attackDamageWithDice();
    if (playerDamage <= 0) {
        System.out.println("You strike but deal no damage.");
    } else {
        monster.takeDamage(playerDamage);
        System.out.println("You deal " + playerDamage + " damage!");
    }

    if (monster.isAlive()) {
        // Monster attacks back
        int monsterDamage = (int) (monsterAttackDamageWithDice(monster) * (1 - defense()));
        takeDamage(monsterDamage);
        System.out.println(monster.getName() + " hits you for " + monsterDamage + " damage!");
        if (!isAlive()) {
            handlePlayerDeath();
            return; // exit game
        }
    } else {

        // Monster is defeated and drops item
        System.out.println("You have defeated " + monster.getName() + "!");
        Item drop = monster.getDropItem();
        if (drop != null && !drop.getName().equals("-") && !drop.getType().equals("Restore")) {
            inventory.add(drop);
            System.out.println(monster.getName() + " dropped " + drop.getName() + "!");
        }

        // Monster drops health restore item that is auto-used
        if (drop != null && drop.getType().equals("Restore")) {
            int starthp = hp;
            hp += drop.getHealing();
            if (hp > MAX_HP) {
            hp = MAX_HP;
            }
            System.out.println("You obtained a health restoration item: " + drop.getName()+"\n" +"You healed " + (hp - starthp) +"HP" +"\n" +"Your HP is now " + hp + "!");

        }
        // If boss defeated, end game
        if (monster.getType().equalsIgnoreCase("Boss")) {
            System.out.println("Congratulations! You have defeated the Boss and completed the game!");
            System.exit(0);
        }
        // Only set combat false here
        setInCombat(false);
    }
    break;
            case "3":
            case "HEAL":
                System.out.print("Enter healing item name: ");
                String itemName = scanner.nextLine().trim();
                Item healItem = getItemByName(itemName);
                if (healItem != null) {
                    heal(healItem);
                    // Monster attacks after healing
                if (monster.isAlive()) {
                    int monsterDamage = (int) (monsterAttackDamageWithDice(monster) * (1 - defense()));
                    takeDamage(monsterDamage);
                    System.out.println(monster.getName() + " hits you for " + monsterDamage + " damage!");
                    if (!isAlive()) {
                        setInCombat(false);
                        handlePlayerDeath();
                        return;
                    }
                }
                } else {
                    System.out.println("Item not in inventory or doesn't exist.");
                    break;
                }

                
                break;

            case "4":
            case "ESCAPE":
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
                    int monsterDamage = (int) (monsterAttackDamageWithDice(monster) * (1 - defense()));
                    takeDamage(monsterDamage);
                    System.out.println(monster.getName() + " hits you for " + monsterDamage + " damage!");
                    if (!isAlive()) {
                        setInCombat(false);
                        handlePlayerDeath();
                        return;
                    }
                }
                break;

            

            default:
                System.out.println("Invalid choice. Please select a valid action.");
               } 
        }
    }
}

