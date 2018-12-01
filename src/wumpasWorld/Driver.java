package wumpasWorld;

import java.util.ArrayList;

public class Driver {

	public static int points = 0;
	
	//TODO: Have the game return points gained/lost as an int at the end of the game
	public static void main(String[] args) {
		// build a 4x4 maze
		MazeBuilder maze4x4_obj = new MazeBuilder(4);
		Square[][] maze4x4 = maze4x4_obj.get_maze();

	}

}
