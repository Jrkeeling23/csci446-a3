package wumpasWorld;

import java.util.ArrayList;

public class KnowledgeBase{
	
	boolean player_has_gold;
	boolean player_returning_to_start;
	boolean wall_hit;
	boolean wompus_alive;
	boolean wompus_found;
	int[] wompus_Pos;
	ArrayList<int[]> smellySpaces;
	boolean have_arrow;
	boolean heard_scream;
	Direction current_direction;
	int points;
	
	// tracks the size of the maze when we know how big it is = the max allowed index
	int mazeSize;
	
	// adjacent squares of kbs are added to frontier
	private ArrayList<ModelSet> frontier = new ArrayList<ModelSet>();
			
	Square current_square;
	
	
	// add squares that are have been visited or proven by FOL to kbs,(kbs = knowledge base square)
	// set the array list to smallest known possible maze size
	public ArrayList<ArrayList<Square>> kbs;
	
	
	public KnowledgeBase() {
		player_has_gold = false;
		player_returning_to_start = false;
		wall_hit = false;
		wompus_alive = true;
		wompus_found = false;
		wompus_Pos = new int[2];
		have_arrow = true;
		heard_scream = false;
		points = 0;
		//current maze size. But only do so when wall_hit = false
		mazeSize = 1;
		// Initializes agent's direction to 'south' (facing down from the top left)
		current_direction = Direction.south;
		smellySpaces = new ArrayList<>();
		
		FirstOrderLogic.init(this);
		kbs = initializeKbsSize();
	}
	
	public void setCurrentSquare(Square currentSquare) {
		current_square = currentSquare;
		printMaze();
		updateKbs(currentSquare);
		//updateFrontier(getCurrentSquare());
		printMaze();
		//TODO: needs to update frontier
	}
	
	public Square getCurrentSquare() {
		return current_square;
	}
	
	/**
	 * Rotates the Agent in the given direction
	 * @param Action, RotateCCW or RotateCW
	 */
	public void rotate(Action act) {
		Direction[] dir = Direction.values();
		int i;
		if (act == Action.RotateCCW) {
			i = -1;
		}
		else {
			i = 1;
		}
		int ord = (current_direction.ordinal() + i) % dir.length;
		if (ord < 0) {
			ord = dir.length - 1;
		}
		current_direction = dir[ord];
	}
	
	/**
	 * Tests if a square is dangerous
	 * @param row
	 * @param col
	 * @return true if the square is potentially dangerous
	 */
	public boolean is_dangerous_square(int row, int col) {
		try {
			Square tmp = get_Kbs(row, col);
			// false if square is good
			return !(!tmp.has_obj(EnvType.pit) && (!tmp.has_obj(EnvType.wumpus) || !wompus_alive));
		}
		catch(IndexOutOfBoundsException e) {
			// assume unknown squares are bad
			return true;
		}	
	}
	
	/**
	 * Finds the closest square on the frontier, or {Integer.MAX_VALUE, Integer.MAX_VALUE} if the
	 * frontier is empty
	 * @return {row, col} of closest square in the frontier
	 */
	public int[] closest_frontier_square() {
		int row = current_square.row;
		int col = current_square.col;
		int[] closest_pos = {Integer.MAX_VALUE, Integer.MAX_VALUE};
		
		for (int i=0; i<frontier.size(); i++) {
			// find distances to each location
			double r1 = Math.pow((closest_pos[0] - row), 2) + Math.pow((closest_pos[1] - col), 2);
			double r2 = Math.pow((frontier.get(i).getX() - row), 2) + Math.pow((frontier.get(i).getY() - col), 2);
			if (r2 < r1) {
				// update value
				closest_pos[0] = frontier.get(i).getX();
				closest_pos[1] = frontier.get(i).getY();
			}
		}
		return closest_pos;
	}
	
	//Tried to deduce where the wompus is early via 2nd order logic
	// TODO debug this method
	public void foundASmell() {
		//get current square pos x and y 
		int x = current_square.row;
		int y = current_square.col;
		int[] position = {x,y};
		
		smellySpaces.add(position);
		
		if(smellySpaces.size() == 2) {
			
			//checks if they are on opposite ends: (equal on one axis or another)
			if(smellySpaces.get(0)[0] == smellySpaces.get(1)[0]) {
				//horizontal match, the wompus is in between
				wompus_found = true;
				//for finding mid point
				int y1 = smellySpaces.get(0)[1];
				int y2 = smellySpaces.get(1)[1];
				int y_val;
				if(y2>y1) {
					y_val = y2-1;
				}else {
					y_val = y2+1;
				}
				
				wompus_Pos[0] = smellySpaces.get(0)[0];
				wompus_Pos[1] = y_val;
			}else if(smellySpaces.get(0)[1] == smellySpaces.get(1)[1]) {
				//vertical match, the wompus is in between
				wompus_found = true;
				
				//for finding mid point
				int x1 = smellySpaces.get(0)[1];
				int x2 = smellySpaces.get(1)[1];
				int x_val;
				if(x2>x1) {
					x_val = x2-1;
				}else {
					x_val = x2+1;
				}
				
				wompus_Pos[0] = x_val;
				wompus_Pos[1] = smellySpaces.get(0)[0];
			}
			
			//checks if they are in corners: (plus one in each axis) from the smallest
			//X
			// X
			if(smellySpaces.get(0)[0]<smellySpaces.get(1)[0]) {//checks if the first on in the arraylist is the smallest
				//check left & down from the last in list if one or the other has been visited/is surrounded by any other visited square
				try {
					if(get_Kbs(smellySpaces.get(1)[0]-1, smellySpaces.get(1)[1]).visited) {
						if(!get_Kbs(smellySpaces.get(1)[0], smellySpaces.get(1)[1]-1).visited) {
							wompus_found = true;
							System.out.println("You think there is a wumpus near by.");
							wompus_Pos[0] = smellySpaces.get(1)[0];
							wompus_Pos[1] = smellySpaces.get(1)[1]-1;
						}
							
					}else if(get_Kbs(smellySpaces.get(1)[0], smellySpaces.get(1)[1]-1).visited){
						if(!get_Kbs(smellySpaces.get(1)[0]-1, smellySpaces.get(1)[1]).visited){
							wompus_found = true;
							System.out.println("You think there is a wumpus near by.");
							wompus_Pos[0] = smellySpaces.get(1)[0]-1;
							wompus_Pos[1] = smellySpaces.get(1)[1];
						}
					}else {
						//We don't know which one the wompus is in yet, but could do more checks.
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
				
			}else {
				
				try {
					if(get_Kbs(smellySpaces.get(0)[0]-1, smellySpaces.get(0)[1]).visited) {
						if(!get_Kbs(smellySpaces.get(0)[0], smellySpaces.get(0)[1]-1).visited) {
							wompus_found = true;
							System.out.println("You think there is a wumpus near by.");
							wompus_Pos[0] = smellySpaces.get(0)[0];
							wompus_Pos[1] = smellySpaces.get(0)[1]-1;
						}
							
					}else if(get_Kbs(smellySpaces.get(0)[0], smellySpaces.get(0)[1]-1).visited){
						if(!get_Kbs(smellySpaces.get(0)[0]-1, smellySpaces.get(0)[1]).visited){
							wompus_found = true;
							System.out.println("You think there is a wumpus near by.");
							wompus_Pos[0] = smellySpaces.get(0)[0]-1;
							wompus_Pos[1] = smellySpaces.get(0)[1];
						}
					}else {
						//We don't know which one the wompus is in yet, but could do more checks.
						System.out.println("You think there is a wumpus near by, but you are unsure...");
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
				// TODO needs to handle a IndexOutOfBoundsException
			}
			
			//Checks if they are in corners (flipped)
			// X
			//X
			//checks if they are in corners: (plus one in each axis) from the smallest
			if(smellySpaces.get(0)[0]<smellySpaces.get(1)[0]) {//checks if the first on in the arraylist is the smallest
				//check left & down from the last in list if one or the other has been visited/is surrounded by any other visited square
				try {
					if(get_Kbs(smellySpaces.get(0)[0]+1, smellySpaces.get(0)[1]).visited) {
						if(!get_Kbs(smellySpaces.get(0)[0], smellySpaces.get(0)[1]+1).visited) {
							wompus_found = true;
							System.out.println("You think there is a wumpus near by.");
							wompus_Pos[0] = smellySpaces.get(0)[0];
							wompus_Pos[1] = smellySpaces.get(0)[1]+1;
						}
							
					}else if(get_Kbs(smellySpaces.get(0)[0], smellySpaces.get(0)[1]+1).visited){
						if(!get_Kbs(smellySpaces.get(0)[0]+1, smellySpaces.get(0)[1]).visited){
							wompus_found = true;
							System.out.println("You think there is a wumpus near by.");
							wompus_Pos[0] = smellySpaces.get(0)[0]+1;
							wompus_Pos[1] = smellySpaces.get(0)[1];
						}
					}else {
						//We don't know which one the wompus is in yet, but could do more checks.
					}
				} catch (Exception e) {
				}
				
			}else {
				
				try {
					if(get_Kbs(smellySpaces.get(1)[0]+1, smellySpaces.get(1)[1]).visited) {
						if(!get_Kbs(smellySpaces.get(1)[0], smellySpaces.get(1)[1]+1).visited) {
							wompus_found = true;
							System.out.println("You think there is a wumpus near by.");
							wompus_Pos[0] = smellySpaces.get(1)[0];
							wompus_Pos[1] = smellySpaces.get(1)[1]+1;
						}
							
					}else if(get_Kbs(smellySpaces.get(1)[0], smellySpaces.get(1)[1]+1).visited){
						if(!get_Kbs(smellySpaces.get(1)[0]+1, smellySpaces.get(1)[1]).visited){
							wompus_found = true;
							System.out.println("You think there is a wumpus near by.");
							wompus_Pos[0] = smellySpaces.get(1)[0]+1;
							wompus_Pos[1] = smellySpaces.get(1)[1];
						}
					}else {
						//We don't know which one the wompus is in yet, but could do more checks.
						System.out.println("You think there is a wumpus near by, but you are unsure...");
					}
				} catch (Exception e) {
				}
			}
			
		}
		
		//pin points wompus with 3 or more squares
		if(smellySpaces.size() > 2) {
			//goes through the coords of every possible combination of the 3 squares, 
			//to see if there is a horizontal or vertical match
			boolean breakflag = false;
			for(int smellX = 0; smellX < smellySpaces.size();smellX++) {
				for(int smellY = 0; smellY < smellySpaces.size();smellY++) {
					if(smellX != smellY) {
						//horizontal match found
						if(smellySpaces.get(smellX)[0] == smellySpaces.get(smellX)[0]) {
							wompus_found = true;
							//for finding mid point
							int x1 = smellySpaces.get(smellX)[0];
							int x2 = smellySpaces.get(smellY)[0];
							int x_val;
							if(x2>x1) {
								x_val = x2-1;
							}else {
								x_val = x2+1;
							}
							wompus_Pos[0] = x_val;
							wompus_Pos[1] = smellySpaces.get(smellX)[1];
							breakflag = true;
							break;
						//vertical match found
						}else if(smellySpaces.get(smellY)[0] == smellySpaces.get(smellY)[0]) {
							wompus_found = true;
							
							//for finding mid point
							int y1 = smellySpaces.get(smellX)[1];
							int y2 = smellySpaces.get(smellY)[1];
							int y_val;
							if(y2>y1) {
								y_val = y2-1;
							}else {
								y_val = y2+1;
							}
							wompus_Pos[0] = smellySpaces.get(smellX)[0];
							wompus_Pos[1] = y_val;
							breakflag = true;
							break;
						}
					}
				}
				if(breakflag) {
					break;
				}
			}
		}
	}
	
	/**
	 * gets the square located at the coordinates in KBS if it exists
	 * @param row
	 * @param col
	 * @return the square in KBS at row, col
	 */
	public Square get_Kbs(int row, int col) throws IndexOutOfBoundsException {
		// check if KBS is actually big enough
		if (row >= kbs.size() || col >= kbs.get(row).size()) {
			if(!wall_hit) {
				changeArrayListSize(kbs);
			}
		}
		// get the element
		Square element = kbs.get(row).get(col);
		if (element.fake) {
			// element is not in the KBS
			throw new IndexOutOfBoundsException();
		}
		else {
			return element;
		}
	}
	
	public int get_frontier_size() {
		return this.frontier.size();
	}
	
	//TODO: edit to work for kbs for easy debugging
	public void printMaze() {
		for (int row=0; row<kbs.size(); row++) {
			for (int col=0; col<kbs.size(); col++) {
				String tmp = "NK";
				if(kbs.get(row).get(col).visited) {
					tmp = "VK";
				}
				//If the square is fake, mark it with F
				if(kbs.get(row).get(col).fake) {
					tmp = "NF";
					if(kbs.get(row).get(col).visited) {
						tmp = "VF";
					}
				}
				//If this position is on the frontier, temp = K
				for (ModelSet modelSet : frontier) {
					if(modelSet.getX() == row && modelSet.getY() == col) {
						tmp = "NM";
						if(kbs.get(row).get(col).visited) {
							tmp = "VM";
						}
					}
				}
				for (int i=0; i<EnvType.values().length; i++) {
					if(kbs.get(row).get(col).fake) {
						tmp += "_";
					}
					else {
						tmp += kbs.get(row).get(col).environment_attributes[i] ? "1" : "0";
					}
				}
				System.out.print(tmp + ", ");
				}
				System.out.println();
			}
	}
	
	public void updateKbs(Square square) { // add squares one at a time to updateKbs
		// col and row of square 
		int col_coord = square.col;
		int row_coord = square.row;
		System.out.println("Upating square:"+row_coord+", "+col_coord);
		try {
			if (!get_Kbs(row_coord, col_coord).content_equals(square)){
				// in KBS but not equal
				kbs.get(row_coord).get(col_coord).environment_attributes = square.environment_attributes;
				kbs.get(row_coord).get(col_coord).visited = true;
				kbs.get(row_coord).get(col_coord).fake = false;
			}
		}
		// get_Kbs handles changeArrayListSize, so here we want to just add a square
		catch(IndexOutOfBoundsException e) {
			// not in the KBS
			kbs.get(row_coord).get(col_coord).environment_attributes = square.environment_attributes;
			kbs.get(row_coord).get(col_coord).visited = true;
			kbs.get(row_coord).get(col_coord).fake = false;
		}
		// get ride of this square from the frontier if it is on it
		int remove = -1;
		for (int i=0; i<frontier.size(); i++) {
			if (frontier.get(i).getX() == row_coord && frontier.get(i).getY() == col_coord) {
				// remove from frontier
				remove = i;
			}
		}
		if (remove >= 0) {
			frontier.remove(remove);
		}
	}
	
	public void updateFrontier(Square square) {
		int col_coord = square.col;
		int row_coord = square.row;
		
		//Makes a list of the adjacent squares
		ArrayList<int[]> adjacent_squares = getSurroundingPos(row_coord, col_coord);
		
		// check if adjacent squares are in frontier already or if contained in kbs
		for (int[] pos : adjacent_squares) {
			int pos_X = pos[0];
			int pos_Y = pos[1];
			try {
				get_Kbs(pos_X, pos_Y);
			} catch (IndexOutOfBoundsException e) {
				//This should be reached if it meets the conditions for not being in the kbs
				
				//Checks for out of bounds issues
				if(!(is_out_of_bounds(pos_X) || is_out_of_bounds(pos_Y))) {
			        // add to frontier if it is not already there
					if (frontier.size() == 0) {
						//Adds the adjacent square to the frontier
						frontier.add(new ModelSet(pos_X, pos_Y));
					}
					else {
						// can't modify frontier while in loop (Concurrent Modification Exception)
						ArrayList<ModelSet> tmp = new ArrayList<>();
						boolean on_frontier = false;
						for (ModelSet ms : frontier) {
							//checks if the adjacent node is already in the frontier
							if((ms.getX() == pos_X)&&(ms.getY() == pos_Y)) {
								on_frontier = true;
								break;
							}
						}
						//Adds the adjacent square to the frontier
						if (!on_frontier) {
							// add new squares to frontier
							frontier.add(new ModelSet(pos_X, pos_Y));
						}
					}
				}
			}
		}
	}
	
	//Trims the frontier models and moves frontier models that have a length of 1 to the KBS
	public void trimFrontier() {
		//check over all the ModelSets on the frontier
		for (ModelSet ms : frontier) {
			//Trimming checks go here
			
			//Check all Known Squares around the frontier space for if they are breezy or smelly
			ArrayList<int[]> surrounding_positions = getSurroundingPos(ms.getX(), ms.getY());
			
			//Initializes the list of squares surrounding the ModelSet's position so we can infer things
			ArrayList<Square> surrounding_squares =  new ArrayList<>();
			
			for (int[] pos : surrounding_positions) {
				//Adds the actual square to surrounding squares
				try {
					//In a try catch in case we don't actually know about that square
					if(!(is_out_of_bounds(pos[0]) || is_out_of_bounds(pos[1]))) {
						Square temp_square = get_Kbs(pos[0],pos[1]);
							if(temp_square.visited)
								surrounding_squares.add(temp_square);
					}
				} catch (IndexOutOfBoundsException e) {
					//Ignore
				}
			}
			
			//Check if any of the surrounding squares that we know about are not breezy/smelly (First order checks)
			for (Square square : surrounding_squares) {
				//checks if the square is breezy
				if(!FirstOrderLogic.breezy.Breezy(square)) {
					//remove the possibility of it being a pit
					ms.removeModel(EnvType.pit);
				}
				if(!FirstOrderLogic.smells.Smells(square)||wompus_found) {
					//remove the possibility of this square being a wumpus
					ms.removeModel(EnvType.wumpus);
				}
			
				//attempts 2nd order logic to deduce the wompus's location if it hasn't been found already
				if(!wompus_found && FirstOrderLogic.smells.Smells(square)) {
					//Needs to check now to see if we know about all the surrounding squares (in maze bounds)
					ArrayList<int[]> checkIfKnownPos = getSurroundingPos(square.row, square.col);
					ArrayList<Square> checkIfKnown =  new ArrayList<>();
					int unknown = 0;
					
					for (int[] pos : checkIfKnownPos) {
						//Adds the actual square to surrounding squares
						try {
							//In a try catch in case we don't actually know about that square
							//check for if the position is within the maze bounds, otherwise don't attempt to add it
							if(!(is_out_of_bounds(pos[0]) || is_out_of_bounds(pos[1]))) {
								Square temp_square = get_Kbs(pos[0],pos[1]);
									checkIfKnown.add(temp_square);
							}
						} catch (IndexOutOfBoundsException e) {
							//We don't know enough about this square, iterate unknown by 1
							unknown++;
						}
					}
					if(unknown > 1) {
						//can't make inference
					}else if(unknown == 1){
						//make inference on unknown square (remove the safe square)
						//TODO: Handle out of bounds exception
						ms.removeSafe();
						
					}else {
						System.out.println("No unknown squares?");
					}
					//if there is more than one unknown space (inside maze bounds) then we know nothing.
					//If there is only one unknown space, that is the wompus
				}
				
				//Attempt second order logic for finding pits
				if(FirstOrderLogic.breezy.Breezy(square)) {
					//Needs to check now to see if we know about all the surrounding squares (in maze bounds)
					ArrayList<int[]> checkIfKnownPos = getSurroundingPos(square.row, square.col);
					ArrayList<Square> checkIfKnown =  new ArrayList<>();
					int unknown = 0;
					
					for (int[] pos : checkIfKnownPos) {
						//Adds the actual square to surrounding squares
						try {
							//In a try catch in case we don't actually know about that square
							//check for if the position is within the maze bounds, otherwise don't attempt to add it
							if(!(is_out_of_bounds(pos[0]) || is_out_of_bounds(pos[1]))) {
								Square temp_square = get_Kbs(pos[0],pos[1]);
									checkIfKnown.add(temp_square);
									
									if(temp_square.has_obj(EnvType.pit)){
										unknown++;
									}
							}
						} catch (IndexOutOfBoundsException e) {
							//We don't know enough about this square, iterate unknown by 1
							unknown++;
						}
					}
					if(unknown > 1) {
						//can't make inference
					}else if(unknown == 1){
						//make inference on unknown square (remove the safe square)
						//check if it is in a corner, if so, require all checkIfKnown squares are visited
						if(!MazeBuilder.cornerCheck(ms.getX(), ms.getY())) {
							ms.removeSafe();	
							System.out.println("removed safe");
						}
						
					}else {
						System.out.println("No unknown squares?");
					}
				}
				
			}
		}
		move_single_models();
	}
	
	private void move_single_models() {
		// can't modify frontier while in loop (Concurrent Modification Exception)
		ArrayList<ModelSet> tmp = new ArrayList<>(frontier);
		
		//Checks all the modelset's on the frontier for if they have a length of 1, and if so, 
		//puts them on the KB & removes from frontier
		for (ModelSet ms : tmp) {
			if(ms.getModels().size()==1) {
				//put in KB
				kbs.get(ms.getX()).set(ms.getY(), ms.getModels().get(0));
				if (ms.getModels().get(0).has_obj(EnvType.wumpus)) {
					this.wompus_found = true;
				}
				//remove from frontier
				frontier.remove(ms);
			}
			else if (ms.getModels().size()==0) {
				System.out.println("!!!!!!!!!!!! Model obj with no models !!!!!!!!!!!!");
			}
		}
	}
	
	/**
	 * Finds if the given coordinate is out of bounds given the current state of the KB
	 * @param distance
	 * @return true if it is out of bounds
	 */
	public boolean is_out_of_bounds(int distance) {
		if (distance < 0) {
			// Negative values are always out of bounds
			return true;
		}
		if (wall_hit) {
			// we do know how big the maze is
			return distance > mazeSize;
		}
		else {
			// we don't know how big the maze is
			return false;
		}
	}
	
	//Returns surrounding positions as long as they are not out of bounds
	public ArrayList<int[]> getSurroundingPos(int x, int y) {
		
		int[] surrounding_x = {0,0,1,-1};
		int[] surrounding_y = {1,-1,0,0};
		
		ArrayList<int[]> surrounding = new ArrayList<>();
		
		for(int i = 0; i < 4; i++) {
			//checks if the new position is even valid before adding it to the returning list
			if(!is_out_of_bounds(x + surrounding_x[i]) && !is_out_of_bounds(y + surrounding_y[i])) {
				int[] pos = {x + surrounding_x[i], y + surrounding_y[i]};
				surrounding.add(pos);
			}
		}
		
		return surrounding;
	}
	/**
	 * Changes the kbs array list size in case the maze is larger than the minimum size the maze can be
	 */
	private void changeArrayListSize(ArrayList<ArrayList <Square>>  list) {
		
		// double the size of the array list
		int length = list.size()+1;
		System.out.println("length " + length);
		ArrayList<Square> row;
		
		// iterate through to instantiate a new square instance for the row, which will be appended to the new list
		row = new ArrayList<Square>(length);

		for(int j = 0; j <length; j ++) {
			// new square instance 
			Square fakeSquare = new Square(length-1, j);
			// set the fake flag in square instance to true, this determines which values need to be written over with the smaller kbs.
			fakeSquare.fake = true;
			// add the square to the row list
			row.add(fakeSquare);
		}
		// add the row to the new list, which will become the larger kbs
		list.add(row);
		
		for (ArrayList<Square> al : list) {
			Square fakeSquare = new Square(al.get(al.size()-1).row, al.get(al.size()-1).col+1);
			fakeSquare.fake = true;
			al.add(fakeSquare);
		}
		// merges important values from the smaller kbs back to the new larger kbs
	}
	
	//Once we hit a wall & know maze size, resize kbs and disable the changeArrayListSize method
	public void resizeMaze() {
		
		for(int i = 0; i < kbs.size(); i++) {
			for(int j = 0; j < kbs.get(i).size(); j++) {
				Square square = kbs.get(i).get(j);
				if(square.col > mazeSize || square.row > mazeSize) {
					kbs.get(i).remove(kbs.get(i).size()-1);
					j--;
				}
			}
			if(kbs.size()-1 > mazeSize) {
				kbs.remove(kbs.size()-1);
				i--;
				System.out.println("Removing row");
			}
		}
		System.out.println("X:"+kbs.size()+",Y:"+kbs.get(0).size());
		//Removes Squares fron the frontier that are now invalid
		for(int i = 0; i<frontier.size();i++) {
			if(frontier.get(i).getX()>mazeSize||frontier.get(i).getY()>mazeSize) {
				frontier.remove(frontier.get(i));
			}
		}
	}
	
	// This function initializes the size of the initial kbs to the size of the smallest maze to beigin with
	private ArrayList<ArrayList<Square>> initializeKbsSize() {
		ArrayList<Square> row;
		// kbs array list values are set to "fake squares" using the fake flag (boolean variable)
		ArrayList<ArrayList<Square>> kbs_to_be = new ArrayList<ArrayList<Square>>();
		for(int i = 0; i< 5; i++) {
			row = new ArrayList<Square>();
			for(int j = 0; j < 5; j++) {
				Square temp = new Square(i,j);
				temp.fake = true;
				row.add(temp);
			}
			kbs_to_be.add(row);
		}
		// return this initial fake set of values to the class variable kbs
		return kbs_to_be;
	}
	
	//Getters
	public boolean has_gold() {
		return player_has_gold;
	}
	public boolean getIfReturningStart() {
		return player_returning_to_start;
	}
	public boolean getWallHit() {
		return wall_hit;
	}
	public boolean getWompusAlive() {
		return wompus_alive;
	}
	public boolean getWompusFound() {
		return wompus_found;
	}
	public boolean getHaveArrow() {
		return have_arrow;
	}
	public boolean getHeardScream() {
		return heard_scream;
	}
	public Direction getCurrentDirection() {
		return current_direction;
	}
	public int[] getWompusPos() {
		return wompus_Pos;
	}
	public ArrayList<int[]> getSmellySpaces(){
		return smellySpaces;
	}
	public int getPoints() {
		return points;
	}
	//Setters
	public void setPoints(int p) {
		points = p;
	}
	
	// just a temporary print function to see what was happening. 
	public void printKbs(int row, int col) {
//		for(int i = 0; i < kbs.size(); i ++) {
//			for(int j = 0; j < kbs.size(); j ++) {
//				Square square = kbs.get(i).get(j);
//				square.printEnv();
//			}
//		}
		System.out.println(row + col);
		kbs.get(row).get(col).printEnv();
	}
}