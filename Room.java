import java.util.HashMap;
import java.util.Map;

public class Room {
    private int id;
    private String name;
    private String description;
    private boolean visited;
    private Map<String, Integer> exits;

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
}
