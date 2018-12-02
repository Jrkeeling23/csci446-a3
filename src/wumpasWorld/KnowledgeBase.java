package wumpasWorld;

import java.util.ArrayList;

public class KnowledgeBase{
	
	static boolean player_has_gold;
	static boolean player_returning_to_start;
	static boolean wall_hit;
	static boolean wompus_alive;
	static boolean wompus_found;
	static int[] wompus_Pos;
	static int smellySpaces;
	static boolean have_arrow;
	static boolean heard_scream;
	static Direction current_direction;
	static int points;
	
	static int mazeSize;
	
	// adjacent squares of kbs are added to frontier
	private static ArrayList<ModelSet> frontier = new ArrayList<ModelSet>();
			
	static Square current_square;
	
	// add squares that are have been visited or proven by FOL to kbs,(kbs = knowledge base square)
	public ArrayList<ArrayList<Square>> kbs = new ArrayList<ArrayList<Square>>();
	
	public void init() {
		player_has_gold = false;
		player_returning_to_start = false;
		wall_hit = false;
		wompus_alive = true;
		wompus_found = false;
		smellySpaces = 0;
		have_arrow = true;
		heard_scream = false;
		points = 0;
		//TODO: Update mazeSize whenever you net gain 1 in either x or y direction (and it is larger than the 
		//current maze size. But only do so when wall_hit = false
		mazeSize = 1;
		
		//Initialzes agent's direction to 'south' (facing down from the top left)
		current_direction = Direction.south;
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
		int col_coord = square.col;
		int row_coord = square.row;
		
		try {
			if (get_Kbs(row_coord, col_coord) != square && square != current_square){
				// TODO: add the square to the knowledge base
				
				//Square toBeAddedSquare = new Square();
				
				// update frontier for each square. This prohibits using each element in kbs to update kbs
				updateFrontier(square);
			}
			else if(get_Kbs(row_coord, col_coord) != square && square == current_square) {
				//TODO: add the current square to the knowledge base
				kbs.get(row_coord).set(col_coord, square);
				
			}
		}
		catch(ArrayIndexOutOfBoundsException e) {
			
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
			} catch (Exception e) {
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
					Square temp_square = get_Kbs(pos[0],pos[1]);
							if(temp_square.visited)
								surrounding_squares.add(temp_square);
				} catch (Exception e) {
					//Ignore
				}
			}
			
			//Check if any of the surrounding squares that we know about are not breezy/smelly (First order checks)
			for (Square square : surrounding_squares) {
				//checks if the square is breezy
				if(!FirstOrderLogic.breezy.Breezy(square)) {
					//remove the breezy element
					ms.removeModel(EnvType.breeze);
				}
				if(!FirstOrderLogic.smells.Smells(square)||wompus_found) {
					//remove the smells element
					ms.removeModel(EnvType.stench);
				}
			}
			
			//Second Order Checks 
			//Wumpus check(if we have 2 or more known smelly locations, infer the location of the wompus & ignore other wompus checks
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
	
}