Dead End  
A text-based adventure game by Team Hydra (Fall 2025)

------------------------------------------------------------
Overview
------------------------------------------------------------

Dead End is a single-player text-based dungeon crawler.
You wake up in a cell deep underground and must explore rooms, fight monsters,
collect items, and solve environmental puzzles to either escape to freedom…
or meet a very permanent dead end.

The game is played entirely through text commands in the terminal.


------------------------------------------------------------
How to Run
------------------------------------------------------------

1. Make sure all .java and data files are in the same project folder:

   - Dice.java
   - GameSerializer.java
   - Item.java
   - Itemreader.java
   - Monster.java
   - MonsterReader.java
   - Player.java
   - Room.java
   - RoomLoader.java
   - Roomreader.java
   - SaveManager.java
   - Play.java
   - Items.txt
   - Map.txt
   - Monsters.txt

2. Compile everything:

   javac *.java

3. Run the game (entry point is Play):

   java Play

Save files will be created in a "saves" folder automatically when you use
the in-game SAVE command.


------------------------------------------------------------
Core Commands
------------------------------------------------------------

These are the general commands shown by the in-game HELP command.
(Type them at the prompt during the game.)

--------------------
Movement & Exploration
--------------------

<direction>
   Move to an adjacent room in the given direction.
   Valid directions: NORTH, EAST, SOUTH, WEST
   Example: MOVE NORTH

EXPLORE
   Lists any items currently visible in the room.


--------------------
Item & Inventory Management
--------------------

PICKUP <item>
   Pick up an item from the room.

DROP <item>
   Drop an item from your inventory.

INSPECT <item>
   Show the description of an item in your inventory.

EQUIP <item>
   Equip a weapon or armor.

UNEQUIP <item>
   Unequip a currently equipped weapon or armor.

HEAL <item>
   Use a consumable healing item.

INVENTORY
   List items you are carrying.


--------------------
Player Info
--------------------

STATUS
   Show your current HP, equipped weapon/armor, and inventory usage.


--------------------
Saving & Loading
--------------------

SAVE <name>
   Save the current game state to a file with the given name.
   Example: SAVE run1

LOAD <name>
   Load a previously saved game state.
   Example: LOAD run1


--------------------
Exiting the Game
--------------------

QUIT
   Exit the game.


------------------------------------------------------------
Puzzles & Special Interactions
------------------------------------------------------------

Throughout the dungeon, some rooms contain puzzles (doors, levers, altars,
tiles, statues, scrolls, etc.). These use extra context-specific commands
that do NOT appear in HELP.

When you enter a puzzle room, the game will tell you the available actions.

Examples printed in the game:
   "Say 'Examine Door' to read it"
   "Type 'Check components'..."
   "Say 'Move Tiles' to start moving them"

Just follow the hints printed when you enter those rooms.


------------------------------------------------------------
Good luck…
------------------------------------------------------------

Explore the crypts, experiment with items and dice-based combat, and see
whether you can reach the Freedom ending instead of the Dead End…
