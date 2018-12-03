package wumpasWorld;

import FirstOrderLogic.Breezy;
import FirstOrderLogic.Climb;
import FirstOrderLogic.Die;
import FirstOrderLogic.Forward;
import FirstOrderLogic.Glimmers;
import FirstOrderLogic.Grab;
import FirstOrderLogic.HitWall;
import FirstOrderLogic.Pit;
import FirstOrderLogic.Shoot;
import FirstOrderLogic.Smells;
import FirstOrderLogic.Start;
import FirstOrderLogic.Wompus;
import FirstOrderLogic.WompusIA;
import FirstOrderLogic.*;

public class FirstOrderLogic {

	static Breezy breezy;
	static Smells smells;
	static Glimmers glitter;
	static Pit pit;
	static WompusIA wompus_is_alive;
	static Start start;
	static Wompus wompus;
	static Shoot shoot;
	static Grab grab;
	static Die die;
	static Climb climb;
	static Forward forward;
	static HitWall hitwall;
	static GameFinished finished;
	static KnowledgeBase kb;
	
	//Initializes the lambda expressions
	public static void init(KnowledgeBase kbb) {
		kb = kbb;
		breezy = (s) -> {
			//Pass in a space and evaluate if it is breezy
			if(s.has_obj(EnvType.breeze)) {
				return true;
			}
			return false;
		};
		
		smells = (s) -> {
			if(s.has_obj(EnvType.stench)) {
				return true;
			}
			return false;
		};
		
		glitter = () -> {
			if(kb.getCurrentSquare().has_obj(EnvType.glitter)) {
				return true;
			}
			return false;
		};
		
		pit = () -> {
			if(kb.current_square.has_obj(EnvType.pit)) {
				System.out.println("The agent has fallen into a pit and starved to death as a result. RIP.");
				return true;
			}
			return false;
		};
		
		wompus_is_alive = () -> {
			if(kb.wompus_alive) {
				return true;
			}
			return false;
		};
		
		//The Following test combined peices of prepositional logic evaulation statements
		
		start = (s) -> {
			if(s.col == 0 && s.row == 0) {
				return true;
			}
			return false;
		};
		
		//Checks if the current space has a living wumpus in it
		wompus = () -> {
			if((kb.current_square.has_obj(EnvType.wumpus))&& wompus_is_alive.wompas_is_alive()) {
				System.out.println("The agent stumbled into a Wompus in the dark, and was made into a nice thanksgiving meal.");
				return true;
			}
			return false;
		};
		
		//Fires an arrow if the agent has one, and sets the wumpus as dead in the KB if it connects. Also -10pts
		shoot = () -> {
			if(kb.have_arrow) {
				System.out.print("You fire you're arrow,");
				if(MazeBuilder.verifyWumpusHit()) {
					System.out.print(" and hear the sound of it piercing flesh, followed by a distant howling cry");
					kb.wompus_alive = false;
				}else {
					System.out.print(" and hear nothing other than it wistle through the darkness followed by a sharp 'tik'! And silence.");
				}
				System.out.print(".\n");
				
				kb.have_arrow = false;
				kb.points -= 10;
				return true;
			}
			System.out.println("You've already used up all your arrows!");
			return false;
		};
		
		grab = () -> {
			//If the agent sees a glitter, & dosen't have gold, pick up gold and return to start
			if((glitter.Glimmers() && (!kb.player_has_gold))) {
				kb.player_has_gold = true;
				kb.player_returning_to_start = true;
			}
		};
		
		//checks if the player dies after having moved
		die = () -> {
			if(pit.Pit()||wompus.is_Wompus()) {
				//Triggers game's end
				return true;
			}
			return false;
		};
		
		climb = () -> {
			//Checks if the player has the Gold and is at the start position
			if(start.is_Start(kb.getCurrentSquare())&&kb.player_has_gold) {
				//Trigger game end
				kb.points += 1000;
				return true;
			}
			return false;
		};
		
		forward = () -> {
			//evaluate x and y for next move based on direction
			int temp_x = kb.current_square.col;
			int temp_y = kb.current_square.row;
			
			switch (kb.current_direction) {
				case north:
					//y--
					temp_y--;
					break;
				case east:
					//x++
					temp_x++;
					break;
				case south:
					//y++
					temp_y++;
					break;
				case west:
					//x--
					temp_x--;
					break;
				default:
					System.out.println("Agent has no direction");
			}
			
			if(MazeBuilder.checkValidForward(temp_x, temp_y)) {
				//Updates the known maze size in KBS
				if(temp_x+1 > kb.mazeSize) {
					kb.mazeSize = temp_x;
				}
				if(temp_y+1 > kb.mazeSize) {
					kb.mazeSize = temp_y;
				}
				return true;
			}else {
				System.out.println("You have bumped into a wall");
				if(!kb.wall_hit) {
					kb.wall_hit = true;
				}
			}
			return false;
		};
		
		//Returns true if the game is finished
		finished = () -> {
			if(die.die()||climb.climb()) {
				return true;
			}
			return false;
		};
	}
	
	//returns True if the wompus is dead
	public boolean smell_Ignore() {
		return !wompus_is_alive.wompas_is_alive();
	}
	
	
}
