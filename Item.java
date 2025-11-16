import java.util.ArrayList;

public class Item {
    private String id;
    private String name;
    private String description;
    private final String type; // "consumable" or "weapon"
    
    private final int healing;
    private final int damage;
    private final double armor;
    private final String dice;
    private final String rarity;


    public Item(String id, String name, String description, String type, int hp, int damage, double armor, String dice, String rarity) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.healing = hp;
        this.damage = damage;
        this.armor = armor;
        this.dice = dice;
        this.rarity = rarity;
        
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }

    public int getHealing() {
        return healing;
    }

    public int getDamage() {
        return damage;
    }

    public double getArmor() {
        return armor;
    }

    public String getDice() {
        return dice;
    }

    public String getRarity() {
        return rarity;
    }



     public boolean isConsumable() { return "health".equalsIgnoreCase(type) && healing > 0; }
     public boolean isRestore() { return "Restore".equalsIgnoreCase(type) && healing > 0; }
    public boolean isWeapon() { return "weapon".equalsIgnoreCase(type); }
    public boolean isArmor() { return "defense".equalsIgnoreCase(type); }
    public boolean isEquipable() { return isWeapon() || isArmor(); }
}
