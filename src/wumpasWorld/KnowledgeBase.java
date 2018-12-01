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
	static int points;
	
	//TEMP KNOWLEDGE BASE stuff
	static int b = 0;
	static int s = 1;
	static int g = 2;
	static int w = 3;
	static int p = 4;
	
	// adjacent squares of kbs are added to frontier
	private ArrayList<ArrayList<Square>> frontier = new ArrayList<ArrayList<Square>>(5);
			
	Square current_square;
	
	// add squares that are have been visited or proven by FOL to kbs,(kbs = knowledge base square)
	ArrayList<ArrayList<Square>> kbs = new ArrayList<ArrayList<Square>>(5);
	
	public void init() {
		player_has_gold = false;
		player_returning_to_start = false;
		wall_hit = false;
		wompus_alive = true;
		have_arrow = true;
		points = 0;
		
		//TODO: init current_square in KB so we can make references to it in FOL
	}
	
	public void setCurrentSquare(Square currentSquare) {
		current_square = currentSquare;
		updateKbs(currentSquare);
		
	}
	
	public void updateKbs(Square square) {
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
	

	
	/**
	 * Creates a list of all possible models for a square setting gold as false in all cases,
	 * This is the same for all squares
	 * @param x row location of the square
	 * @param y col location of the square
	 * @return model_list
	 */
	public static ArrayList<Square> get_all_models(int x, int y){
		// list for all models for a square
		ArrayList<Square> model_list = new ArrayList<Square>();
		
		// list of all env attributes to ignore
		EnvType[] ignore = {EnvType.glitter, EnvType.breeze, EnvType.stench};
		
		// get all possible models minus the use of the gold since it does not affect inference
		boolean[][] env_models = bin_count(env_size - ignore.length);
		
		// build model list
		for (int row=0; row<env_models.length; row++) {
			// add a new model
			model_list.add(new Square(x, y));
			
			// add a new env list
			model_list.get(row).environment_attributes = new boolean[env_size];
			// index of the binary count
			int n = 0;
			
			// place env values as determined
			for (int i=0; i<env_size; i++) {
				boolean ignored = false;
				for (int j=0; j<ignore.length; j++) {
					if (i == ignore[j].ordinal()) {
						// false for ignored attributes
						model_list.get(row).environment_attributes[i] = false;
						ignored = true;
					}
				}
				if (!ignored) {
					// non-ignored, add whatever is in the count
					model_list.get(row).environment_attributes[i] = env_models[row][n];
					n++;
				}
			}
		}
		return model_list;
	}
	
	/**
	 * Calculates and returns a binary count of size 'i'
	 * @param i number of bits in each term in the count
	 * @return count_list a 2D boolean array 2^i x i
	 */
	private static boolean[][] bin_count(int i){
		// make empty arrays
		ArrayList<ArrayList<Boolean>> count_list = new ArrayList<ArrayList<Boolean>>();
		ArrayList<Boolean> count = new ArrayList<Boolean>();
		// get 2D ArrayList
		count_list = bin_count(count_list, count, i);
		int h = count_list.size();
		int w = count_list.get(0).size();
		
		// copy count_list to 2D boolean array
		boolean[][] models = new boolean[h][w];
		for (int row=0; row<h; row++) {
			for (int col=0; col<w; col++) {
				models[row][col] = count_list.get(row).get(col);
			}
		}
		return models;
		
	}
	
	/**
	 * Recursive binary count, run from method above
	 * @param count_list
	 * @param count
	 * @param i
	 * @return
	 */
	private static ArrayList<ArrayList<Boolean>> bin_count(ArrayList<ArrayList<Boolean>> count_list,
			ArrayList<Boolean> count, int i) {
		if (i <= 1) {
			// first bit
			ArrayList<Boolean> tmp1 = new ArrayList<>(count);
			tmp1.add(false);
			count_list.add(tmp1);
			
			// second bit
			ArrayList<Boolean> tmp2 = new ArrayList<>(count);
			tmp2.add(true);
			count_list.add(tmp2);
			return count_list;
			
		}
		else {
			// first bit
			ArrayList<Boolean> tmp1 = new ArrayList<>(count);
			tmp1.add(false);
			bin_count(count_list, tmp1, i - 1);
			
			// second bit
			ArrayList<Boolean> tmp2 = new ArrayList<>(count);
			tmp2.add(true);
			bin_count(count_list, tmp2, i - 1);
			return count_list;
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