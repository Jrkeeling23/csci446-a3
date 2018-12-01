package FirstOrderLogic;

public class TestMain {

	//TODO: set equal to false in an initialization method
	static boolean player_has_gold = false;
	static boolean player_returning_to_start = false;
	static boolean wall_hit = false;
	static boolean wompus_alive = true;
	static boolean have_arrow = true;
	static int points = 0;
	
	//TODO: Move int logic to pull from world/KB
	//TODO: Don't put it all in main, it can be public info
	//TODO: Make Proper Knowledge base when implementing with knowledge of:
	//	-current space
	//  -player's x and y diff from start
	//	-maze size based on x/y diff/if they hit a wall
	//
	public static void main(String[] args) {
		//Set up the wompus lambda expression to check the list of a Space's current 'attributes' to see if there is a wompus
		//OR: Set it up to recognize the pre made space cases in which there is a wompus
		
		//TEMP KNOWLEDGE BASE
		int b = 0;
		int s = 1;
		int g = 2;
		int w = 3;
		int p = 4;
		
		
		//The 5 lambda expressions below define the propositional logic for environmental variables

		Breezy breezy = () -> {
			if(b == 0) {
				return true;
			}
			return false;
		};
		
		Smells smells = () -> {
			if(s == 1) {
				return true;
			}
			return false;
		};
		
		Glimmers glitter = () -> {
			if(g == 2) {
				return true;
			}
			return false;
		};
		
		Pit pit = () -> {
			if(p == 4) {
				return true;
			}
			return false;
		};
		
		WompusIA wompus_is_alive = () -> {
			if(wompus_alive) {
				return true;
			}
			return false;
		};
		
		//Tests Propositional Logic sentences
		System.out.println("Breezy: "+(breezy.Breezy() && smells.Smells()));
		
		//The Following test combined peices of prepositional logic evaulation statements
		
		Start start = () -> {
			//TODO: when implementing, use player's location to evaluate this check
			//Starting location will be initialized into the KB
			//TBI (To Be Implemented)
			return false;
		};
		
		//Checks if the current space has a living wompus in it
		Wompus wompus = () -> {
			if((w == 3)&& wompus_is_alive.wompas_is_alive()) {
				return true;
			}
			return false;
		};
		
		//Fires an arrow if the agent has one, and sets the wompus as dead in the KB if it connects. Also -10pts
		Shoot shoot = () -> {
			if(have_arrow) {
				//TODO: when implementing, use player's location & wompus location to evaluate this check
				//Kills wompus if hits
				/*if(pos & direction align with wompus) {
					wompus_alive = false;
					//Wompus screams
				}*/
				have_arrow = false;
				points -= 10;
				return true;
			}
			return false;
		};
		
		System.out.println("The Wompus is here: "+ wompus.is_Wompus());
		
		Grab grab = () -> {
			//If the agent sees a glitter, & dosen't have gold, pick up gold and return to start
			if(glitter.Glimmers()&&!player_has_gold) {
				player_has_gold = true;
				player_returning_to_start = true;
			}
		};
		
		//TODO: requires pit & wompus to be determined based off current Space to function
		//checks if the player dies after having moved
		Die die = () -> {
			if(pit.Pit()||wompus.is_Wompus()) {
				//Possibly trigger end/break of game loop here
				return true;
			}
			return false;
		};
		
		Climb climb = () -> {
			if(start.is_Start()&&player_has_gold) {
				//TODO: Trigger flag here for ending game loop & adding points
			}
		};
		
		Forward forward = () -> {
			//TODO: evaluate by maze bounds & player pos tracker
			if(true) {//if((maze.length<(position_tracker+direction))||((position_tracker+direction)<0))
				//TODO: update position tracker in Knowledge Base(unless wall has been hit)
				return true;
			}
			//You have bumped into a wall/boundry
			return false;
		};
		
		HitWall hitwall = () -> {
			//Sets maze size in knowledge base if hit a wall not known about from the start 
			if(true) {//what forward checks, but only if it has gone over
				//Set wall_hit to true if not already set
				if(!wall_hit) {
					wall_hit = true;
				}
				return true;
			}
			return false;
		};
	}
}
