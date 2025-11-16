public class Monster {
    private String id;
    private String name;
    private String description;
    private int hitPoints;
    private int damage;
    private String type; // Weak, Regular, Boss
    private Item droppedItem;
    private Room room;
    private double spawnChance;
     // The room where the monster is located

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
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getHitPoints() { return hitPoints; }
    public int getDamage() { return damage; }
    public String getType() { return type; }
    public void setRoom(Room room) { this.room = room; }
    public Room getRoom() { return room; }
    public double getSpawnChance() { return spawnChance; }

    // Check if alive
    public boolean isAlive() { return hitPoints > 0; }
    //
    
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
    public double spawnChance() {
        switch (type.toLowerCase()) {
            case "weak":
                return 0.95; // 95% chance to spawn
            case "regular":
                return 0.80; // 80% chance to spawn
            case "boss":
                return 1.0; // 100% chance to spawn
            default:
                return 0.0; // Unknown type, no spawn
        }
    }
    public String monsterTypeDice() {
        switch (type.toLowerCase()) {
            case "weak":
                return "d4"; // Weak monsters use a 4-sided die
            case "regular":
                return "d8"; // Regular monsters use an 8-sided die
            case "boss":
                return "d12"; // Boss monsters use two 12-sided dice
            default:
                return "d4"; // Default to weak if unknown
        }
    }

}
