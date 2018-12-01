package wumpasWorld;

public class KnowledgeBase{
	
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
			
	Square current_square;
	
	public void init() {
		player_has_gold = false;
		player_returning_to_start = false;
		wall_hit = false;
		wompus_alive = true;
		have_arrow = true;
		points = 0;
		
		//TODO: init current_square in KB so we can make references to it in FOL
	}
	
}