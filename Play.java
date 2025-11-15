import java.util.Scanner;

public class Play {
    public static void main(String[] args) {
        // Create a scanner for user input
        Scanner scanner = new Scanner(System.in);

        // Create a player with default stats (startingRoom=1, HP=100, baseAttack=5)
        Player player = new Player();

        // Initialize RoomLoader (loads rooms, items, puzzles, monsters)
        RoomLoader loader = new RoomLoader();

        // Start the game
        loader.startGame(player, scanner);

        // Close scanner after game ends
        scanner.close();
    }
}
