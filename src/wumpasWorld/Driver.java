package wumpasWorld;

import java.util.ArrayList;

public class Driver {
	// get size of environment
	private static final int env_size = EnvType.values().length;

	public static int points = 0;
	
	//TODO: Have the game return points gained/lost as an int at the end of the game
	public static void main(String[] args) {
		// build a 4x4 maze
		MazeBuilder maze4x4_obj = new MazeBuilder(4);
		Square[][] maze4x4 = maze4x4_obj.get_maze();
		testing_maze_printout(maze4x4);
		
	}
	
	private static void testing_maze_printout(Square[][] maze) {
		for (int row=0; row<4; row++) {
			for (int col=0; col<4; col++) {
				String tmp = "";
				for (int i=0; i<env_size; i++) {
					tmp += maze[row][col].environment_attributes[i] ? "1" : "0";
				}
				System.out.print(tmp + ", ");
			}
			System.out.println();
		}
	}

}
