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
	ArrayList<ArrayList<Square>> kbs = new ArrayList<ArrayList<Square>>();
	
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
	
	public void updateKbs(Square square) {
		//TODO: call FOL method to obtain actual "model" of square
		
		if (!kbs.contains(square)){
		}
	}
	
	public void updateFrontier() {
		
	}
	
}