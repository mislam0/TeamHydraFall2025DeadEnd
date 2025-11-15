import java.util.*;

public class Player {
    private int currentRoomNumber;
    private Set<Integer> visitedRooms;
    private int hp;
    private List<Item> inventory;
    private Item equippedItem;
    private int baseAttackDamage;
   
    public Player(){
        this.currentRoomNumber = 1;
        this.visitedRooms = new HashSet<>();
        this.visitedRooms.add(currentRoomNumber);
        this.hp = 100;
        this.baseAttackDamage = 5;
        this.inventory = new ArrayList<>();
        this.equippedItem = null;
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
        if (item == equippedItem) unequip();
    }



    public Item getItemByName(String name) {
        for (Item i : inventory) if (i.getName().equalsIgnoreCase(name)) return i;
        return null;
    }




    // Equip / Unequip
    public void equip(Item item) {
        if (item.isEquipable() && inventory.contains(item) && equippedItem.getType().equalsIgnoreCase("weapon")) {
            equippedItem = item;
            System.out.println(item.getName() + " equipped. Attack damage: " + attackDamage());
        } else if (item.isEquipable() && inventory.contains(item) && equippedItem.getType().equalsIgnoreCase("defense")) {





        }
    else {
            System.out.println("Cannot equip this item.");
        }
    }

    public void unequip() {
        if (equippedItem != null) {
            System.out.println(equippedItem.getName() + " unequipped.");
            equippedItem = null;
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



    // Stats
    public int getHp() {
        return hp;
    }

    public int attackDamage() {
        return equippedItem != null ? equippedItem.getDamage() : baseAttackDamage;
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
