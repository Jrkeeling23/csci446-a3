package wumpasWorld;

public class MazeBuilder {
	private static Square[][] maze;
	private int[][] adj = {{-1, 0}, {0, -1}, {1, 0}, {0, 1}};
	private static int[] wumpus;
	
	/**
	 * Makes a new Maze of width 'size' and height 'size'
	 * @param size of the maze
	 */
	public MazeBuilder(int size) {
		maze = new Square[size][size];
		
		// Initialize all squares
		for (int row=0; row<size; row++) {
			for (int col=0; col<size; col++) {
				// set the row and col of each Square
				maze[row][col] = new Square(row, col);
			}
		}
		// since add_object_to_adjs can add beyond the current square, adding objects must be in its own loop
		for (int row=0; row<size; row++) {
			for (int col=0; col<size; col++) {
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
		// place wumpus
		int[] pos = add_1_object(EnvType.wumpus, false);
		// place all 4 stenches
		add_object_to_adjs(pos[0], pos[1], EnvType.stench);
		wumpus = new int[2];
		wumpus[0] = pos[0];
		wumpus[1] = pos[1];
		// place gold
		add_1_object(EnvType.glitter, true);
		
	}
	
	/**
	 * Gets the instantiated Maze
	 * @return maze
	 */
	public Square[][] get_maze() {
		return maze;
	}
	
	/**
	 * replaces maze with a test maze for being boxed in at the start with stinks
	 */
	public void test_maze_01(){
		int[] w = {1, 1};
		int[] g = {1, 1};
		int[][] p = {};
		make_test_maze(w, g, p);
	}
	
	/**
	 * replaces maze with a test maze for being boxed in at the start with breezes
	 */
	public void test_maze_02(){
		int[] w = {3, 3};
		int[] g = {1, 1};
		int[][] p = {{1, 1}};
		make_test_maze(w, g, p);
	}
	
	/**
	 * replaces maze with a test maze for open maze
	 */
	public void test_maze_03(){
		int[] w = {3, 3};
		int[] g = {1, 2};
		int[][] p = {{2, 3}};
		make_test_maze(w, g, p);
	}
	
	private void make_test_maze(int[] w_pos, int[] gold_pos, int[][] pits_pos) {
		maze = new Square[4][4];
		// Initialize all squares
		for (int row=0; row<4; row++) {
			for (int col=0; col<4; col++) {
				// set the row and col of each Square
				maze[row][col] = new Square(row, col);
			}
		}
		// place wumpus
		maze[w_pos[0]][w_pos[1]].add_obj(EnvType.wumpus);
		// place all 4 stenches
		add_object_to_adjs(w_pos[0], w_pos[1], EnvType.stench);
		wumpus = new int[2];
		wumpus[0] = w_pos[0];
		wumpus[1] = w_pos[1];
		// place the pits
		for (int[] pos : pits_pos) {
			maze[pos[0]][pos[1]].add_obj(EnvType.pit);
			// place all 4 breezes
			add_object_to_adjs(pos[0], pos[1], EnvType.breeze);
		}
		// place gold
		maze[gold_pos[0]][gold_pos[1]].add_obj(EnvType.glitter);
	}
	
	//Uses the agent's position & direction to verify if the shot being taken hits or not without knowledge of
	//the wumpus's actual position being reveiled to the agent.
	public static boolean verifyWumpusHit() {
			int x = FirstOrderLogic.kb.getCurrentSquare().col;
			int y = FirstOrderLogic.kb.getCurrentSquare().row;
			
			//Evaluates the direction
			switch (FirstOrderLogic.kb.current_direction) {
			case north:
				//y-- checks if the wumpas is aligned with the agent's x coordinate, and that they are firing
				//in the proper direction.
				if(x == wumpus[0] && y > wumpus[1]) {
					return true;
				}
			case east:
				//x++
				//checks if the wumpus is aligned with agent's y value, & the x pos of agent is less than that of the wumpus's
				if(y == wumpus[1] && x < wumpus[0]) {
					return true;
				}
			case south:
				//y++
				if(x == wumpus[0] && y < wumpus[1]) {
					return true;
				}
			case west:
				//x--
				if(y == wumpus[1] && x > wumpus[0]) {
					return true;
				}
			default:
				System.out.println("Agent has no direction");
			return false;
			}
			
	}
	
	//Checks if the next movement forward the agent is trying to make is valid
	public static boolean checkValidForward(int x, int y) {
		
		//checks if the agent is going out of bounds when moving in a specific direction
		switch (FirstOrderLogic.kb.current_direction) {
		case north:
			//y--
			if((y-1) >= 0) {
				return true;
			}
		case east:
			//x++
			if((x+1) <= maze.length) {
				return true;
			}
		case south:
			//y++
			if((y+1) <= maze.length) {
				return true;
			}
		case west:
			//x--
			if((x-1) >= 0) {
				return true;
			}
		default:
			System.out.println("Agent has no direction");

		return false;
		}
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
	 * @param no_pit if true will not place the object on a pit
	 * @return array of {row, col} of the placement
	 */
	private int[] add_1_object(EnvType env, boolean no_pit) {
		int size = maze.length;
		// tracks invalid squares that have already been tested
		boolean tested[][] = new boolean[size][size];
		// Initialize array
		for (int row=0; row<size; row++) {
			for (int col=0; col<size; col++) {
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
				if (!no_pit || (no_pit && !maze[rand_row][rand_col].has_obj(EnvType.pit))) {
					// square did not contain a pit or we don't care, add environment object
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
				for (int col=0; col<size; col++) {
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
		if (no_pit) {
			// remove pit
			maze[rand_row][rand_col].remove_obj(EnvType.pit);
			remove_object_from_adjs(rand_row, rand_col, EnvType.breeze);
		}
		// return location
		int[] coords = {rand_row, rand_col};
		return coords;
	}
}
