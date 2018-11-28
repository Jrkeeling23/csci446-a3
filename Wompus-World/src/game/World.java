package game;

// Responsible for containing the 'world' (All the spaces and their contents/logic)
public class World {

	private Space world[][];
	
	public World(int size) {
		generate_world(size);
	}
	/* Generates a n x n square world
	 * every square has a 20% chance of containing a pit
	 * there is one wompus randomly placed on the map once it is generated
	 * there is one gold bar that is randomly placed on the map once it is generated
	 * the gold cannot be spawned in a pit
	 * the wompus/pit cannot spawn on the start state (0,0)
	 * */
	private void generate_world(int size) {
		//Initializes empty size x size array
		world = new Space[size][size];
	}
	
	//Generates a random position
	private int[] randomPos(int max) {
		
		int[] pos = new int[2];
		
		pos[0] = (int)(Math.random()*max);
		pos[1] = (int)(Math.random()*max);
		
		return pos;
	}
	
}
