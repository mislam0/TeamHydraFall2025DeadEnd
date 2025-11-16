import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Room {
    private int id;
    private String name;
    private String description;
    private boolean visited;
    private Map<String, Integer> exits;
    private Monster monster;

    private ArrayList<Item> items = new ArrayList<>();


    // Constructor
    public Room(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;

        this.exits = new HashMap<>();
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public boolean isVisited() { return visited; }
    public void setVisited(boolean visited) { this.visited = visited; }
    public void visit() { this.visited = true; }
    public Monster getMonster() { return monster; }
    public void setMonster(Monster monster) { this.monster = monster; }

    // Exits
    public void addExit(String direction, int roomNumber) { exits.put(direction.toUpperCase(), roomNumber); }
    public Map<String, Integer> getExits() { return exits; }
    public Integer getExit(String direction) { return exits.get(direction.toUpperCase()); }
    public ArrayList<Item> getItems() { return items; }

    public void addItem(Item item) {
        items.add(item);
    }

    public Item removeItem(String name) {
        for (Item item : items) {
            if (item.getName().equalsIgnoreCase(name)) {
                items.remove(item);
                return item;
            }
        }
        return null;
    }
}