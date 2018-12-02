package wumpasWorld;

import java.util.ArrayList;

public class KnowledgeBase{
	
	static boolean player_has_gold;
	static boolean player_returning_to_start;
	static boolean wall_hit;
	static boolean wompus_alive;
	static boolean wompus_found;
	static int[] wompus_Pos;
	static ArrayList<int[]> smellySpaces;
	static boolean have_arrow;
	static boolean heard_scream;
	static Direction current_direction;
	static int points;
	
	static int mazeSize;
	
	// adjacent squares of kbs are added to frontier
	private static ArrayList<ModelSet> frontier = new ArrayList<ModelSet>();
			
	static Square current_square;
	
	
	// add squares that are have been visited or proven by FOL to kbs,(kbs = knowledge base square)
	// set the array list to smallest known possible maze size
	public ArrayList<ArrayList<Square>> kbs = initializeKbsSize();
	
	
	public void init() {
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
		
		//Initialzes agent's direction to 'south' (facing down from the top left)
		current_direction = Direction.south;
		
		FirstOrderLogic.init();
	}
	
	public void setCurrentSquare(Square currentSquare) {
		current_square = currentSquare;
		updateKbs(currentSquare);
		
	}
	
	public static Square getCurrentSquare() {
		return current_square;
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
		catch(ArrayIndexOutOfBoundsException e) {
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
	public void foundASmell() {
		//get current square pos x and y 
		int x = current_square.col;
		int y = current_square.row;
		int[] position = {x,y};
		
		smellySpaces.add(position);
		
		if(smellySpaces.size() == 2) {
			
			//checks if they are on opposite ends: (equal on one axis or another)
			if(smellySpaces.get(0)[0] == smellySpaces.get(1)[0]) {
				//horizontal match, the wompus is inbetween
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
				//vertical match, the wompus is inbetween
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
			if(smellySpaces.get(0)[0]<smellySpaces.get(1)[0]) {//checks if the first on in the arraylist is the smallest
				//check left & down from the last in list if one or the other has been visited/is surrounded by any other visited square
				
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
				
			}else {
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
			}
			
		}
		
		//pin points wompus with 3 or more squares
		if(smellySpaces.size() > 2) {
			//goes through the coords of every possible combination of the 3 squares, 
			//to see if there is a hortizontal or vertical match
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
	public Square get_Kbs(int row, int col) throws ArrayIndexOutOfBoundsException {
		// check KBS is big enough
		if (row >= kbs.size() || col >= kbs.get(row).size()) {
			changeArrayListSize(kbs);
		}
		// get the element
		Square element = kbs.get(row).get(col);
		if (element.fake) {
			throw new ArrayIndexOutOfBoundsException();
		}
		else {
			return element;
		}
	}
	
	public void updateKbs(Square square) { // add squares one at a time to updateKbs
		// col and row of square 
		int col_coord = square.col;
		int row_coord = square.row;
		
		try {
			if (!get_Kbs(row_coord, col_coord).equals(square)){
				kbs.get(row_coord).set(col_coord, square);
			}
		}
		catch(ArrayIndexOutOfBoundsException e) {
			changeArrayListSize(kbs);
		}
		
			
	}
	
	public void updateFrontier(Square square) {
		int col_coord = square.col;
		int row_coord = square.row;
		
		//Makes a list of the adjacent squares
		ArrayList<int[]> adjacent_squares = getSurroundingPos(col_coord, row_coord);
		
		// check if adjacent squares are in frontier already or if contained in kbs
		for (int[] pos : adjacent_squares) {
			int pos_X = pos[0]+col_coord;
			int pos_Y = pos[1]+row_coord;
			try {
				Square tempS = get_Kbs(pos_X, pos_Y);
				continue;
			} catch (ArrayIndexOutOfBoundsException e) {
				//This should be reached if it meets the conditions for not being in the kbs
				
				//Checks for out of bounds issues
				if(MazeBuilder.outOfBoundsCheck(pos_X)&&MazeBuilder.outOfBoundsCheck(pos_Y)&&(pos_X>=0)&&(pos_Y>=0)) {
					for (ModelSet ms : frontier) {
						//checks if the adjacent node is already in the frontier
						if((ms.getX() == pos_X)&&(ms.getY() == pos_Y)) {
							break;
						}else {
							//Adds the adjacent square to the frontier
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
					//In a try catch incase we don't actually know about that square
					if(!(MazeBuilder.outOfBoundsCheck(pos[0])||MazeBuilder.outOfBoundsCheck(pos[1])||(pos[0]<0)||(pos[1]<0))) {
						Square temp_square = get_Kbs(pos[0],pos[1]);
							if(temp_square.visited)
								surrounding_squares.add(temp_square);
					}
				} catch (ArrayIndexOutOfBoundsException e) {
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
				if(!wompus_found&&FirstOrderLogic.smells.Smells(square)) {
					//Needs to check now to see if we know about all the surrounding squares (in maze bounds)
					ArrayList<int[]> checkIfKnownPos = getSurroundingPos(square.col, square.row);
					ArrayList<Square> checkIfKnown =  new ArrayList<>();
					int unknown = 0;
					
					for (int[] pos : checkIfKnownPos) {
						//Adds the actual square to surrounding squares
						try {
							//In a try catch in case we don't actually know about that square
							//check for if the position is within the maze bounds, otherwise don't attempt to add it
							if(!(MazeBuilder.outOfBoundsCheck(pos[0])||MazeBuilder.outOfBoundsCheck(pos[1])||(pos[0]<0)||(pos[1]<0))) {
								Square temp_square = get_Kbs(pos[0],pos[1]);
									checkIfKnown.add(temp_square);
							}
						} catch (ArrayIndexOutOfBoundsException e) {
							//We don't know enough about this square, iterate unknown by 1
							unknown++;
						}
					}
					if(unknown > 1) {
						//can't make inference
					}else if(unknown == 1){
						//make inference on unknown square (remove the safe square)
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
					ArrayList<int[]> checkIfKnownPos = getSurroundingPos(square.col, square.row);
					ArrayList<Square> checkIfKnown =  new ArrayList<>();
					int unknown = 0;
					
					for (int[] pos : checkIfKnownPos) {
						//Adds the actual square to surrounding squares
						try {
							//In a try catch in case we don't actually know about that square
							//check for if the position is within the maze bounds, otherwise don't attempt to add it
							if(!(MazeBuilder.outOfBoundsCheck(pos[0])||MazeBuilder.outOfBoundsCheck(pos[1])||(pos[0]<0)||(pos[1]<0))) {
								Square temp_square = get_Kbs(pos[0],pos[1]);
									checkIfKnown.add(temp_square);
							}
						} catch (ArrayIndexOutOfBoundsException e) {
							//We don't know enough about this square, iterate unknown by 1
							unknown++;
						}
					}
					if(unknown > 1) {
						//can't make inference
					}else if(unknown == 1){
						//make inference on unknown square (remove the safe square)
						ms.removeSafe();
						
					}else {
						System.out.println("No unknown squares?");
					}
				}
				
			}
		}
		//Checks all the modelset's on the frontier for if they have a length of 1, and if so, 
		//puts them on the KB & removes from frontier
		for (ModelSet ms : frontier) {
			if(ms.getModels().size()<=1) {
				
				//put in KB
				kbs.get(ms.getX()).set(ms.getY(),ms.getModels().get(0));
				
				//remove from frontier
				frontier.remove(ms);
			}
		}
	}
	
	//Returns surrounding positions as long as they are not out of bounds
	public ArrayList<int[]> getSurroundingPos(int x, int y) {
		
		int[] surrounding_x = {0,0,1,-1};
		int[] surrounding_y = {1,-1,0,0};
		
		ArrayList<int[]> surrounding = new ArrayList<>();
		int[] pos = new int[2];
		
		for(int i = 0; i < 4; i++) {
			//checks if the new position is even valid before adding it to the returning list
			if(((x+surrounding_x[i]>0)&&(MazeBuilder.outOfBoundsCheck(x+surrounding_x[i]))) 
					&& ((y+surrounding_y[i]>0)&&(MazeBuilder.outOfBoundsCheck(y+surrounding_y[i])))) {
				pos[0] = x+surrounding_x[i];
				pos[1] = y+surrounding_y[i];
				
				surrounding.add(pos);
			}
		}
		
		return surrounding;
	}
	
	private void changeArrayListSize(ArrayList<ArrayList <Square>>  list) {
		int length = list.size()*2;
		ArrayList<Square> row;
		ArrayList<ArrayList <Square>> newList = new ArrayList<ArrayList<Square>>(length);
		
		for(int i = 0; i <length; i ++) {
			row = new ArrayList<Square>(length);
			for(int j = 0; j <length; j ++) {
				Square fakeSquare = new Square(i, j);
				fakeSquare.fake = true;
				row.add(fakeSquare);
			}
			newList.add(row);
		}
		
		mergeLists(list, newList, length);
			
	}
	
	private void mergeLists(ArrayList<ArrayList <Square>>  list, ArrayList<ArrayList <Square>>  newList, int length) {
		
		for(int i = 0; i <length/2; i ++) {
			for(int j = 0; j < length/2; j++) {
				if(list.get(i).get(j).fake == false) {
					newList.get(i).set(j, list.get(i).get(j));
				}
			}
		}
	}
	private ArrayList<ArrayList<Square>> initializeKbsSize() {
		ArrayList<Square> row = new ArrayList<Square>(5);
		ArrayList<ArrayList<Square>> kbs_to_be = new ArrayList<ArrayList<Square>>(5);
		for(int i = 0; i< row.size(); i++) {
			kbs_to_be.add(row);
		}
		return kbs_to_be;
	}
	
}