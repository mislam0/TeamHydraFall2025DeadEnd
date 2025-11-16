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

    // Added for door puzzle
    private boolean doorPuzzleSolved = false;

    // Added for lever puzzle
    private boolean leverPuzzleSolved = false;
    private boolean leverRequiresReset = false;
    private int leverSeq1 = 0;
    private int leverSeq2 = 0;
    private int leverSeq3 = 0;
    private int leverResetCount = 0;

    // Added for altar puzzle
    private boolean altarPuzzleSolved = false;
    private boolean altarDM1Placed = false;
    private boolean altarDM4Placed = false;

    // Added for guardian key lock
    private boolean guardianDoorUnlocked = false;

    // Added for tiles puzzle
    private boolean tilesPuzzleSolved = false;

    // Added for statues puzzle
    private boolean statuesPuzzleSolved = false;
    private String statueDir1 = "NORTH";
    private String statueDir2 = "NORTH";
    private String statueDir3 = "NORTH";

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

    // Door puzzle
    public boolean isDoorPuzzleSolved() {
        return doorPuzzleSolved;
    }

    public void setDoorPuzzleSolved(boolean solved) {
        this.doorPuzzleSolved = solved;
    }

    // Lever puzzle (room 5)
    public boolean isLeverPuzzleSolved() {
        return leverPuzzleSolved;
    }

    public void setLeverPuzzleSolved(boolean solved) {
        this.leverPuzzleSolved = solved;
    }

    public boolean isLeverRequiresReset() {
        return leverRequiresReset;
    }

    public void setLeverRequiresReset(boolean requiresReset) {
        this.leverRequiresReset = requiresReset;
    }

    public void setLeverSequence(int s1, int s2, int s3) {
        this.leverSeq1 = s1;
        this.leverSeq2 = s2;
        this.leverSeq3 = s3;
    }

    public int getLeverSeq1() { return leverSeq1; }
    public int getLeverSeq2() { return leverSeq2; }
    public int getLeverSeq3() { return leverSeq3; }

    public int getLeverResetCount() { return leverResetCount; }

    public void resetLeverPanel() {
        leverRequiresReset = false;
        leverSeq1 = 0;
        leverSeq2 = 0;
        leverSeq3 = 0;
        leverResetCount++;
    }

    // Altar puzzle
    public boolean isAltarPuzzleSolved() {
        return altarPuzzleSolved;
    }

    public void setAltarPuzzleSolved(boolean solved) {
        this.altarPuzzleSolved = solved;
    }

    public boolean isDM1Placed() { return altarDM1Placed; }
    public boolean isDM4Placed() { return altarDM4Placed; }

    public void setDM1Placed(boolean b) { altarDM1Placed = b; }
    public void setDM4Placed(boolean b) { altarDM4Placed = b; }

    public void resetAltar() {
        altarDM1Placed = false;
        altarDM4Placed = false;
    }

    // Guardian key door
    public boolean isGuardianDoorUnlocked() {
        return guardianDoorUnlocked;
    }

    public void setGuardianDoorUnlocked(boolean unlocked) {
        this.guardianDoorUnlocked = unlocked;
    }

    // Tiles puzzle
    public boolean isTilesPuzzleSolved() {
        return tilesPuzzleSolved;
    }

    public void setTilesPuzzleSolved(boolean solved) {
        this.tilesPuzzleSolved = solved;
    }

    // Statues puzzle
    public boolean isStatuesPuzzleSolved() {
        return statuesPuzzleSolved;
    }

    public void setStatuesPuzzleSolved(boolean solved) {
        this.statuesPuzzleSolved = solved;
    }

    public String getStatueDir1() { return statueDir1; }
    public String getStatueDir2() { return statueDir2; }
    public String getStatueDir3() { return statueDir3; }

    public void setStatueDirections(String d1, String d2, String d3) {
        this.statueDir1 = d1;
        this.statueDir2 = d2;
        this.statueDir3 = d3;
    }
}
