package wumpasWorld;

import java.util.ArrayList;

public class KnowledgeBase{
	
	// get size of environment
	private static final int env_size = EnvType.values().length;
	
	static boolean player_has_gold;
	static boolean player_returning_to_start;
	static boolean wall_hit;
	static boolean wompus_alive;
	static boolean have_arrow;
	static boolean heard_scream;
	static Direction current_direction;
	static int points;
	
	static int mazeSize;
	
	// adjacent squares of kbs are added to frontier
	private ArrayList<ModelSet> frontier = new ArrayList<ModelSet>();
			
	static Square current_square;
	
	// add squares that are have been visited or proven by FOL to kbs,(kbs = knowledge base square)
	ArrayList<ArrayList<Square>> kbs = new ArrayList<ArrayList<Square>>(5);
	
	public void init() {
		player_has_gold = false;
		player_returning_to_start = false;
		wall_hit = false;
		wompus_alive = true;
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
	
	
	public void updateKbs(Square square) { // add squares one at a time to updateKbs
		int col_coord = square.col;
		int row_coord = square.row;
		
		try {
			if (kbs.get(row_coord).get(col_coord) != square && square != current_square){
				// TODO: add the square to the knowledge base
				
				//Square toBeAddedSquare = new Square();
				
				// update frontier for each square. This prohibits using each element in kbs to update kbs
				updateFrontier(square);
			}
			else if(kbs.get(row_coord).get(col_coord) != square && square == current_square) {
				//TODO: add the current square to the knowledge base
				kbs.get(row_coord).set(col_coord, square);
				
			}
		}
		catch(Exception arrayindexoutofboundsexception) {}
			
	}
	
	public void updateFrontier(Square square) {
		int col_coord = square.col;
		int row_coord = square.row;
		// adjacent squares relative to the square's position
		int[][] adj = {{-1, 0}, {0, -1}, {1, 0}, {0, 1}};
		
		// check if adjacent squares are in frontier already or if contained in kbs
		for (int i = 0 ; i <  adj.length; i ++) {
			int row = adj[i][0] + row_coord;
			int col = adj[i][1] + col_coord;
			
			Square possibleSquareToAdd = new Square(row,col);
			try {
				// if its not in frontier or in kb, add to frontier, else nothing
				if (frontier.get(row).get(col) != possibleSquareToAdd || kbs.get(row).get(col) != possibleSquareToAdd ){
					//TODO: add to frontier
					
				}
			}
			catch(Exception arrayindexoutofboundsexception) {}
			
		}
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