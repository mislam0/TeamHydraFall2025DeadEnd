import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Room {
    private int id;
    private String name;
    private String description;
    private boolean visited;
    private Map<String, Integer> exits;

    private ArrayList<Item> items = new ArrayList<>();
    private Monster monster;

    // Added for puzzle
    private boolean doorPuzzleSolved = false;

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

    // Exits
    public void addExit(String direction, int roomNumber) { exits.put(direction.toUpperCase(), roomNumber); }
    public Map<String, Integer> getExits() { return exits; }
    public Integer getExit(String direction) { return exits.get(direction.toUpperCase()); }
    public ArrayList<Item> getItems() { return items; }

    public void addItem(Item item) {
        items.add(item);
    }

    public void removeItem(Item item) { items.remove(item); }

    public Item getItemByName(String name) {
        for (Item i : items) {
            if (i.getName().equalsIgnoreCase(name)) return i;
        }
        return null;
    }

    public boolean hasMonster() { return monster != null && monster.isAlive(); }
    public Monster getMonster() { return monster; }
    public void setMonster(Monster m) { monster = m; }
    public void removeMonster() { monster = null; }

    // Added this for puzzle
    public boolean isDoorPuzzleSolved() {
        return doorPuzzleSolved;
    }

    public void setDoorPuzzleSolved(boolean solved) {
        this.doorPuzzleSolved = solved;
    }
}
