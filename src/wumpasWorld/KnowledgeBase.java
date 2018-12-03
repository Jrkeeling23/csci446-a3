
	public Square get_Kbs(int row, int col)  {
		// try- catch for the index out of bounds exception . 
			// get the element
			
			}
		catch(IndexOutOfBoundsException e){
		return element;
			row = new ArrayList<Square>(length);
			for(int j = 0; j <length; j ++) {
				// new square instance 
				Square fakeSquare = new Square(i, j);
				// set the fake flag in square instance to true, this determines which values need to be written over with the smaller kbs.
				fakeSquare.fake = true;
				// add the square to the row list
				row.add(fakeSquare);
			}
			// add the row to the new list, which will become the larger kbs
			newList.add(row);
		}
		// merges important values from the smaller kbs back to the new larger kbs
		mergeLists(list, newList, length);
	}
	
	
	// method called from changeArrayListSize(). This method takes the current knowledge base and places them into a larger arraylist to represent kbs
	private void mergeLists(ArrayList<ArrayList <Square>>  list, ArrayList<ArrayList <Square>>  newList, int length) {
		// only need to check half the length since the old array list is half the size
		// remember, we are only pulling values from the smaller kbs arraylist to add the the new larger kbs array list
		for(int i = 0; i <length/2; i ++) {
			for(int j = 0; j < length/2; j++) {
				if(list.get(i).get(j).fake == false) {
					newList.get(i).set(j, list.get(i).get(j));
				}
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