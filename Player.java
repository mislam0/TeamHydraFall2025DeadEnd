import java.util.*;

public class Player {
    private int currentRoomNumber;
    private Set<Integer> visitedRooms;
    private int hp;
    private List<Item> inventory;
    private Item equippedWeapon;
    private Item equippedArmor;
    private int baseAttackDamage;
   
    public Player(){
        this.currentRoomNumber = 1;
        this.visitedRooms = new HashSet<>();
        this.visitedRooms.add(currentRoomNumber);
        this.hp = 100;
        this.baseAttackDamage = 0;
        this.inventory = new ArrayList<>();
        this.equippedWeapon = null;
        this.equippedArmor = null;
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












}