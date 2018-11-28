package wumpasWorld;

public class MazeBuilder {
	private Square[][] maze;
	private int[][] adj = {{-1, 0}, {0, -1}, {1, 0}, {0, 1}};
	
	/**
	 * Makes a new Maze of width 'size' and height 'size'
	 * @param size of the maze
	 */
	public MazeBuilder(int size) {
		maze = new Square[size][size];
		
		// Initialize all squares, and place pits
		for (int row=0; row<size; row++) {
			for (int col=0; col<size; row++) {
				// set the row and col of each Square
				maze[row][col] = new Square(row, col);
				
				// add pit w/ 20% probability
				double prob = (Math.random() * 100);
				if (prob <= 20.0 && row != 0 && col != 0) {
					// place pit
					maze[row][col].add_obj(EnvType.pit);
					// place all 4 breezes
					add_object_to_adjs(row, col, EnvType.breeze);
				}
			}
		}
		// place wampus
		int[] pos = add_1_object_no_pit(EnvType.wumpus);
		// place all 4 stenches
		add_object_to_adjs(pos[0], pos[1], EnvType.stench);
		
		// place gold
		add_1_object_no_pit(EnvType.glitter);
		
	}
	
	/**
	 * Gets the instantiated Maze
	 * @return maze
	 */
	public Square[][] get_maze() {
		return maze;
	}
	
	/**
	 * adds the given environment object to all square adjacent to the given coordinates
	 * will ignore out of bounds squares
	 * @param row
	 * @param col
	 * @param env the object to place in each adj square
	 */
	private void add_object_to_adjs(int row, int col, EnvType env) {
		// Perform add to all adjacent squares to the given row, col
		for (int[] coords : adj) {
			try {
				// place object if we can
				maze[row + coords[0]][col + coords[1]].add_obj(env);
			} 
			catch(ArrayIndexOutOfBoundsException exception) {
			    // skip
			}
		}
	}
	
	/**
	 * adds the given environment object to all square adjacent to the given coordinates
	 * will ignore out of bounds squares
	 * @param row
	 * @param col
	 * @param env the object to place in each adj square
	 */
	private void remove_object_from_adjs(int row, int col, EnvType env) {
		// Perform a remove on all adjacent squares to the given row, col
		for (int[] coords : adj) {
			try {
				// place object if we can
				maze[row + coords[0]][col + coords[1]].remove_obj(env);
			} 
			catch(ArrayIndexOutOfBoundsException exception) {
			    // skip
			}
		}
	}
	
	/**
	 * adds the given EnvType to the maze at a random location that is not the starting square or a pit
	 * 
	 * @param env the object to place in a random square
	 * @return array of {row, col} of the placement
	 */
	private int[] add_1_object_no_pit(EnvType env) {
		int size = maze.length;
		// tracks invalid squares that have already been tested
		boolean tested[][] = new boolean[size][size];
		// Initialize array
		for (int row=0; row<size; row++) {
			for (int col=0; col<size; row++) {
				tested[row][col] = false;
			}
		}
		// start square is always invalid
		tested[0][0] = true;
		
		// test a random location
		boolean no_remaining_squares = false;
		while (!no_remaining_squares){
			// pick a random location
			int rand_row = (int)(Math.random() * size);
			int rand_col = (int)(Math.random() * size);
			
			// test location
			if (!tested[rand_row][rand_col]) {
				if (!maze[rand_row][rand_col].has_pit()) {
					// square did not contain a pit, add environment object
					maze[rand_row][rand_col].add_obj(env);
					int[] coords = {rand_row, rand_col};
					return coords;
				}
				else {
					// square contained a pit, mark as tested
					tested[rand_row][rand_col] = true;
				}
			}
			
			// repeat if failed and tested array still contains a false
			no_remaining_squares = true;
			for (int row=0; row<size; row++) {
				for (int col=0; col<size; row++) {
					if (tested[row][col] == false) {
						no_remaining_squares = false;
						break;
					}
				}
				if (!no_remaining_squares) {
					break;
				}
			}
		}
		// instead of failing, pick a random location, remove the pit, and place there anyway
		int rand_row = (int)(Math.random() * size);
		int rand_col = (int)(Math.random() * size);
		if (rand_row + rand_col == 0) {
			// start square is not allowed
			int delta1 = (int)(Math.random() * (size - 1) + 1);
			int delta2 = (int)(Math.random() * (size - 1) + 1);
			rand_row += delta1;
			rand_col += delta2;
		}
		
		// place object
		maze[rand_row][rand_col].add_obj(env);
		// remove pit
		maze[rand_row][rand_col].remove_obj(EnvType.pit);
		remove_object_from_adjs(rand_row, rand_col, EnvType.breeze);
		// return location
		int[] coords = {rand_row, rand_col};
		return coords;
	}
}
