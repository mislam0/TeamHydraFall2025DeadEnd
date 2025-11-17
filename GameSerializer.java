/*
 * Authors: Rahsun and Mohammed
 */

import java.util.*;
import java.lang.reflect.*;

public class GameSerializer {
    private static final String PLAYER_PREFIX = "player.";
    private static final String ROOM_PREFIX = "room.";
    private static final String MONSTER_PREFIX = "monster.";

    private static final String INV_DELIM = ";;";
    private static final String ITEM_LIST_DELIM = ";;";

    // Serialize full game state
    public static Map<String, String> serialize(Player player, Map<Integer, Room> roomMap, Map<String, Item> itemByName, Map<String, Monster> monstersById) {
        Map<String, String> out = new LinkedHashMap<>();

        // Player basic
        out.put(PLAYER_PREFIX + "currentRoomNumber", Integer.toString(player.getCurrentRoomNumber()));
        out.put(PLAYER_PREFIX + "hp", Integer.toString(player.getHp()));
        out.put(PLAYER_PREFIX + "isInCombat", Boolean.toString(player.isInCombat()));

        // Player inventory
        List<String> invNames = new ArrayList<>();
        for (Item it : player.getInventory()) invNames.add(it.getName());
        out.put(PLAYER_PREFIX + "inventory", String.join(INV_DELIM, invNames));

        // Equipped names (try reflection to read private fields)
        String equippedWeaponName = "";
        String equippedArmorName = "";
        try {
            Field wF = Player.class.getDeclaredField("equippedWeapon");
            Field aF = Player.class.getDeclaredField("equippedArmor");
            wF.setAccessible(true);
            aF.setAccessible(true);
            Item w = (Item) wF.get(player);
            Item a = (Item) aF.get(player);
            equippedWeaponName = (w != null) ? w.getName() : "";
            equippedArmorName = (a != null) ? a.getName() : "";
        } catch (Exception e) {
            // ignore; fall back to empty names
        }
        out.put(PLAYER_PREFIX + "equippedWeapon", equippedWeaponName);
        out.put(PLAYER_PREFIX + "equippedArmor", equippedArmorName);

        // Rooms: visited, items, monster id, and puzzle flags
        for (Map.Entry<Integer, Room> re : roomMap.entrySet()) {
            Room r = re.getValue();
            String base = ROOM_PREFIX + r.getId() + ".";

            out.put(base + "visited", Boolean.toString(r.isVisited()));

            // items present in room
            List<String> roomItemNames = new ArrayList<>();
            for (Item it : r.getItems()) roomItemNames.add(it.getName());
            out.put(base + "items", String.join(ITEM_LIST_DELIM, roomItemNames));

            // monster in room (if any)
            Monster m = r.getMonster();
            if (m != null) {
                String mid = monsterIdSafe(m);
                out.put(base + "monsterKey", mid);
                out.put(base + "monsterHp", Integer.toString(m.getHitPoints()));
            } else {
                out.put(base + "monsterKey", "");
                out.put(base + "monsterHp", "0");
            }

            // Puzzle flags and fields (explicit from Room.java)
            out.put(base + "doorPuzzleSolved", Boolean.toString(r.isDoorPuzzleSolved()));
            out.put(base + "leverPuzzleSolved", Boolean.toString(r.isLeverPuzzleSolved()));
            out.put(base + "leverRequiresReset", Boolean.toString(r.isLeverRequiresReset()));
            out.put(base + "leverSeq1", Integer.toString(r.getLeverSeq1()));
            out.put(base + "leverSeq2", Integer.toString(r.getLeverSeq2()));
            out.put(base + "leverSeq3", Integer.toString(r.getLeverSeq3()));
            out.put(base + "leverResetCount", Integer.toString(r.getLeverResetCount()));

            out.put(base + "altarPuzzleSolved", Boolean.toString(r.isAltarPuzzleSolved()));
            out.put(base + "altarDM1Placed", Boolean.toString(r.isDM1Placed()));
            out.put(base + "altarDM4Placed", Boolean.toString(r.isDM4Placed()));

            out.put(base + "guardianDoorUnlocked", Boolean.toString(r.isGuardianDoorUnlocked()));

            out.put(base + "tilesPuzzleSolved", Boolean.toString(r.isTilesPuzzleSolved()));

            out.put(base + "statuesPuzzleSolved", Boolean.toString(r.isStatuesPuzzleSolved()));
            out.put(base + "statueDir1", safeString(r.getStatueDir1()));
            out.put(base + "statueDir2", safeString(r.getStatueDir2()));
            out.put(base + "statueDir3", safeString(r.getStatueDir3()));
        }

        // Monsters registry (save hitPoints and dropped item name)
        if (monstersById != null) {
            for (Map.Entry<String, Monster> me : monstersById.entrySet()) {
                Monster m = me.getValue();
                String base = MONSTER_PREFIX + me.getKey() + ".";
                out.put(base + "hitPoints", Integer.toString(m.getHitPoints()));
                out.put(base + "isAlive", Boolean.toString(m.isAlive()));
                // droppedItem name (if any)
                try {
                    Field dropF = Monster.class.getDeclaredField("droppedItem");
                    dropF.setAccessible(true);
                    Item drop = (Item) dropF.get(m);
                    out.put(base + "droppedItem", (drop != null) ? drop.getName() : "");
                } catch (Exception e) {
                    out.put(base + "droppedItem", "");
                }
            }
        }

        return out;
    }

    // Deserialize full game state from previously saved map.
    // itemByName resolves item names to Item objects.
    // monstersById resolves monster id to Monster instances (keys should match identifiers used when serializing).
    public static void deserialize(Map<String, String> data, Player player, Map<Integer, Room> roomMap, Map<String, Item> itemByName, Map<String, Monster> monstersById) {
        // Player
        if (data.containsKey(PLAYER_PREFIX + "currentRoomNumber")) {
            try {
                int roomId = Integer.parseInt(data.get(PLAYER_PREFIX + "currentRoomNumber"));
                player.setCurrentRoomNumber(roomId, roomMap);
            } catch (Exception ignored) {}
        }

        if (data.containsKey(PLAYER_PREFIX + "hp")) {
            try {
                int hp = Integer.parseInt(data.get(PLAYER_PREFIX + "hp"));
                setPlayerHp(player, hp);
            } catch (NumberFormatException ignored) {}
        }

        if (data.containsKey(PLAYER_PREFIX + "isInCombat")) {
            try {
                boolean inCombat = Boolean.parseBoolean(data.get(PLAYER_PREFIX + "isInCombat"));
                player.setInCombat(inCombat);
            } catch (Exception ignored) {}
        }

        // Inventory
        if (data.containsKey(PLAYER_PREFIX + "inventory")) {
            String inv = data.get(PLAYER_PREFIX + "inventory");
            player.getInventory().clear();
            if (inv != null && !inv.isEmpty()) {
                String[] names = inv.split(INV_DELIM, -1);
                for (String n : names) {
                    Item it = itemByName.get(n);
                    if (it != null) player.getInventory().add(it);
                    // If item missing, skip gracefully.
                }
            }
        }

        // Equipped items
        if (data.containsKey(PLAYER_PREFIX + "equippedWeapon")) {
            String wn = data.get(PLAYER_PREFIX + "equippedWeapon");
            if (wn != null && !wn.isEmpty()) {
                Item it = itemByName.get(wn);
                if (it != null) player.equip(it);
            }
        }
        if (data.containsKey(PLAYER_PREFIX + "equippedArmor")) {
            String an = data.get(PLAYER_PREFIX + "equippedArmor");
            if (an != null && !an.isEmpty()) {
                Item it = itemByName.get(an);
                if (it != null) player.equip(it);
            }
        }

        // Rooms & puzzles & items in rooms & monsters in rooms
        for (Map.Entry<Integer, Room> re : roomMap.entrySet()) {
            Room r = re.getValue();
            String base = ROOM_PREFIX + r.getId() + ".";

            if (data.containsKey(base + "visited")) r.setVisited(Boolean.parseBoolean(data.get(base + "visited")));

            if (data.containsKey(base + "items")) {
                r.getItems().clear();
                String itemsStr = data.get(base + "items");
                if (itemsStr != null && !itemsStr.isEmpty()) {
                    String[] names = itemsStr.split(ITEM_LIST_DELIM, -1);
                    for (String nm : names) {
                        Item it = itemByName.get(nm);
                        if (it != null) r.addItem(it);
                    }
                }
            }

            if (data.containsKey(base + "monsterKey")) {
                String mkey = data.get(base + "monsterKey");
                if (mkey != null && !mkey.isEmpty()) {
                    Monster m = resolveMonsterByKey(mkey, monstersById);
                    if (m != null) {
                        r.setMonster(m);
                        // set monster.room if available
                        try {
                            Method setRoom = Monster.class.getMethod("setRoom", Room.class);
                            setRoom.invoke(m, r);
                        } catch (Exception e) {
                            try {
                                Field roomF = Monster.class.getDeclaredField("room");
                                roomF.setAccessible(true);
                                roomF.set(m, r);
                            } catch (Exception ignored) {}
                        }
                        // Restore monster HP if present
                        if (data.containsKey(base + "monsterHp")) {
                            try {
                                int mhp = Integer.parseInt(data.get(base + "monsterHp"));
                                setMonsterHp(m, mhp);
                            } catch (NumberFormatException ignored) {}
                        }
                    } else {
                        // If monster not found in registry, skip (room will be left without monster)
                        r.removeMonster();
                    }
                } else {
                    r.removeMonster();
                }
            }

            // Puzzle flags
            if (data.containsKey(base + "doorPuzzleSolved")) r.setDoorPuzzleSolved(Boolean.parseBoolean(data.get(base + "doorPuzzleSolved")));
            if (data.containsKey(base + "leverPuzzleSolved")) r.setLeverPuzzleSolved(Boolean.parseBoolean(data.get(base + "leverPuzzleSolved")));
            if (data.containsKey(base + "leverRequiresReset")) r.setLeverRequiresReset(Boolean.parseBoolean(data.get(base + "leverRequiresReset")));
            try {
                if (data.containsKey(base + "leverSeq1") && data.containsKey(base + "leverSeq2") && data.containsKey(base + "leverSeq3")) {
                    int s1 = Integer.parseInt(data.get(base + "leverSeq1"));
                    int s2 = Integer.parseInt(data.get(base + "leverSeq2"));
                    int s3 = Integer.parseInt(data.get(base + "leverSeq3"));
                    r.setLeverSequence(s1, s2, s3);
                }
            } catch (NumberFormatException ignored) {}
            // leverResetCount has no setter in Room; skipping explicit restore unless setter added.

            if (data.containsKey(base + "altarPuzzleSolved")) r.setAltarPuzzleSolved(Boolean.parseBoolean(data.get(base + "altarPuzzleSolved")));
            if (data.containsKey(base + "altarDM1Placed")) r.setDM1Placed(Boolean.parseBoolean(data.get(base + "altarDM1Placed")));
            if (data.containsKey(base + "altarDM4Placed")) r.setDM4Placed(Boolean.parseBoolean(data.get(base + "altarDM4Placed")));

            if (data.containsKey(base + "guardianDoorUnlocked")) r.setGuardianDoorUnlocked(Boolean.parseBoolean(data.get(base + "guardianDoorUnlocked")));

            if (data.containsKey(base + "tilesPuzzleSolved")) r.setTilesPuzzleSolved(Boolean.parseBoolean(data.get(base + "tilesPuzzleSolved")));

            if (data.containsKey(base + "statuesPuzzleSolved")) r.setStatuesPuzzleSolved(Boolean.parseBoolean(data.get(base + "statuesPuzzleSolved")));
            if (data.containsKey(base + "statueDir1") && data.containsKey(base + "statueDir2") && data.containsKey(base + "statueDir3")) {
                r.setStatueDirections(data.get(base + "statueDir1"), data.get(base + "statueDir2"), data.get(base + "statueDir3"));
            }
        }

        // Monsters registry: restore global monster data (if provided)
        if (monstersById != null) {
            for (Map.Entry<String, Monster> me : monstersById.entrySet()) {
                String key = me.getKey();
                Monster m = me.getValue();
                String base = MONSTER_PREFIX + key + ".";
                if (data.containsKey(base + "hitPoints")) {
                    try {
                        int mhp = Integer.parseInt(data.get(base + "hitPoints"));
                        setMonsterHp(m, mhp);
                    } catch (NumberFormatException ignored) {}
                }
                if (data.containsKey(base + "droppedItem")) {
                    String di = data.get(base + "droppedItem");
                    if (di != null && !di.isEmpty()) {
                        Item it = itemByName.get(di);
                        if (it != null) {
                            try {
                                Field dropF = Monster.class.getDeclaredField("droppedItem");
                                dropF.setAccessible(true);
                                dropF.set(m, it);
                            } catch (Exception ignored) {}
                        }
                    }
                }
            }
        }
    }

    // Helper: produce a stable key for a Monster: prefer "id" field if present, otherwise monster name.
    private static String monsterIdSafe(Monster m) {
        try {
            Field idF = Monster.class.getDeclaredField("id");
            idF.setAccessible(true);
            Object val = idF.get(m);
            if (val != null) return val.toString();
        } catch (Exception ignored) {}
        // fallback to name
        try {
            Method getName = Monster.class.getMethod("getName");
            return (String) getName.invoke(m);
        } catch (Exception ignored) {}
        // last resort: identity hash
        return "monster@" + System.identityHashCode(m);
    }

    // Resolve monster by key: check exact key lookup in registry, otherwise try to find by name among registry values.
    private static Monster resolveMonsterByKey(String key, Map<String, Monster> monstersById) {
        if (monstersById == null) return null;
        Monster m = monstersById.get(key);
        if (m != null) return m;
        // try by name
        for (Monster cand : monstersById.values()) {
            try {
                Method getName = Monster.class.getMethod("getName");
                String nm = (String) getName.invoke(cand);
                if (key.equals(nm)) return cand;
            } catch (Exception ignored) {}
        }
        return null;
    }

    // Try to set Monster HP using setter if available, else reflection
    private static void setMonsterHp(Monster m, int hp) {
        try {
            Method setHpM = Monster.class.getMethod("setHitPoints", int.class);
            setHpM.invoke(m, hp);
            return;
        } catch (NoSuchMethodException nsme) {
            // reflection fallback:
        } catch (Exception ignored) {
            // other reflection problems ignore and continue
        }
        try {
            Field hpF = Monster.class.getDeclaredField("hitPoints");
            hpF.setAccessible(true);
            hpF.setInt(m, hp);
        } catch (Exception ignored) {}
    }

    // Robust helper to set Player HP: try several method name variants and signatures before falling back to direct field access.
    private static void setPlayerHp(Player player, int hp) {
        // 1) Try common public setter names with int parameter
        String[] candidateNames = new String[] { "setHp", "setHP", "setHitPoints", "setHitpoints", "setHealth", "setCurrentHp", "setCurrentHP", "setPlayerHp" };
        for (String name : candidateNames) {
            try {
                Method m = Player.class.getMethod(name, int.class);
                m.invoke(player, hp);
                return;
            } catch (NoSuchMethodException nsme) {
                // try next
            } catch (IllegalAccessException | InvocationTargetException e) {
                // invocation failed; continue trying other options
            }
            // also try Integer parameter
            try {
                Method m2 = Player.class.getMethod(name, Integer.class);
                m2.invoke(player, Integer.valueOf(hp));
                return;
            } catch (NoSuchMethodException nsme) {
                // next
            } catch (IllegalAccessException | InvocationTargetException e) {
                // continue
            }
        }

        // 2) Try to find any public method whose name contains "set" and ("hp" or "health" or "hit")
        for (Method m : Player.class.getMethods()) {
            String n = m.getName().toLowerCase(Locale.ROOT);
            if (!n.startsWith("set")) continue;
            if (!(n.contains("hp") || n.contains("health") || n.contains("hit"))) continue;
            Class<?>[] params = m.getParameterTypes();
            if (params.length != 1) continue;
            try {
                if (params[0] == int.class) {
                    m.invoke(player, hp);
                    return;
                } else if (params[0] == Integer.class) {
                    m.invoke(player, Integer.valueOf(hp));
                    return;
                } else if (params[0] == long.class) {
                    m.invoke(player, (long) hp);
                    return;
                } else if (params[0] == short.class) {
                    m.invoke(player, (short) hp);
                    return;
                }
            } catch (IllegalAccessException | InvocationTargetException ignored) {}
        }

        // 3) Try declared (non-public) methods as last resort (search declared methods)
        for (Method m : Player.class.getDeclaredMethods()) {
            String n = m.getName().toLowerCase(Locale.ROOT);
            if (!n.startsWith("set")) continue;
            if (!(n.contains("hp") || n.contains("health") || n.contains("hit"))) continue;
            Class<?>[] params = m.getParameterTypes();
            if (params.length != 1) continue;
            try {
                m.setAccessible(true);
                if (params[0] == int.class) {
                    m.invoke(player, hp);
                    return;
                } else if (params[0] == Integer.class) {
                    m.invoke(player, Integer.valueOf(hp));
                    return;
                } else if (params[0] == long.class) {
                    m.invoke(player, (long) hp);
                    return;
                } else if (params[0] == short.class) {
                    m.invoke(player, (short) hp);
                    return;
                }
            } catch (IllegalAccessException | InvocationTargetException ignored) {}
        }

        // 4) Reflection fallback: set field "hp" directly if it exists
        try {
            Field f = Player.class.getDeclaredField("hp");
            f.setAccessible(true);
            f.setInt(player, hp);
            return;
        } catch (Exception e) {
            // Could not set via field; give up silently to avoid crashing the game.
        }
    }

    private static String safeString(String s) {
        return s == null ? "" : s;
    }
}