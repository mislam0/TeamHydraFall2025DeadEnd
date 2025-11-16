public class Monster {
    private String id;
    private String name;
    private String description;
    private int hitPoints;
    private int damage;
    private String type; // Weak, Regular, Boss
    private Item droppedItem;
    private Room room; // The room where the monster is located

    // Constructor
    public Monster(String id, String name, String description, int hitPoints, int damage, String type, Item droppedItem) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.hitPoints = hitPoints;
        this.damage = damage;
        this.type = type;
        this.droppedItem = droppedItem;
       
    }

    // Getters
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getHitPoints() { return hitPoints; }
    public int getDamage() { return damage; }
    public String getType() { return type; }
    public void setRoom(Room room) { this.room = room; }
    public Room getRoom() { return room; }

    // Check if alive
    public boolean isAlive() { return hitPoints > 0; }

    // Receive damage
    public void takeDamage(int amount) {
        hitPoints -= amount;
        if (hitPoints < 0) hitPoints = 0;
    }

    // Attack method
    public int attack() {
        return damage;
    }

    // Return the dropped item when defeated
    public Item getDropItem() {
        if (droppedItem != null) {
            System.out.println(name + " dropped " + droppedItem.getName() + "!");
        }
        return droppedItem;
    }
}
