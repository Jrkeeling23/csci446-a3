package wumpasWorld;

import java.util.Scanner;

public class Driver {
	
	//TODO: Have the game return points gained/lost as an int at the end of the game
	public static void main(String[] args) {
		boolean running = true;
		int point_total = 0;
		Scanner scan = new Scanner(System.in);
		
		while(running) {
			System.out.println("Welcome to Wumpus World! Enter the size of maze you would like to run: ");
			int maze_size = scan.nextInt();
			World world = new World(maze_size);
			point_total += world.points;
			System.out.println("The agent finished this game with a score of "+world.points+" points, totaling up to a net of "+point_total+" points");
			System.out.println("\nEnter 'Q' to quit or C to contine: ");
			String continuePlaying = scan.nextLine();
			if(continuePlaying.equalsIgnoreCase("Q")) {
				break;
			}
		}
		System.out.println("Have a nice day!");
	}

}
