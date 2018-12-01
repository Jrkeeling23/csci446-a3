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

	Breezy breezy;
	Smells smells;
	Glimmers glitter;
	Pit pit;
	WompusIA wompus_is_alive;
	Start start;
	Wompus wompus;
	Shoot shoot;
	Grab grab;
	Die die;
	Climb climb;
	Forward forward;
	HitWall hitwall;
	GameFinished finished;
	
	//Initializes the lambda expressions
	public void init() {
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
			if(KnowledgeBase.getCurrentSquare().has_obj(EnvType.glitter)) {
				return true;
			}
			return false;
		};
		
		pit = () -> {
			if(KnowledgeBase.current_square.has_obj(EnvType.pit)) {
				return true;
			}
			return false;
		};
		
		wompus_is_alive = () -> {
			if(KnowledgeBase.wompus_alive) {
				return true;
			}
			return false;
		};
		
		//Tests Propositional Logic sentences
		//System.out.println("Breezy: "+(breezy.Breezy() && smells.Smells()));
		
		//The Following test combined peices of prepositional logic evaulation statements
		
		start = (s) -> {
			if(s.col == 0 && s.row == 0) {
				return true;
			}
			return false;
		};
		
		//Checks if the current space has a living wumpus in it
		wompus = () -> {
			if((KnowledgeBase.current_square.has_obj(EnvType.wumpus))&& wompus_is_alive.wompas_is_alive()) {
				return true;
			}
			return false;
		};
		
		//Fires an arrow if the agent has one, and sets the wumpus as dead in the KB if it connects. Also -10pts
		shoot = () -> {
			if(KnowledgeBase.have_arrow) {
				System.out.println("You fire you're arrow.");
				if(MazeBuilder.verifyWumpusHit()) {
					System.out.print("You hear a howling cry in the distance.");
					KnowledgeBase.wompus_alive = false;
				}
				
				KnowledgeBase.have_arrow = false;
				KnowledgeBase.points -= 10;
				return true;
			}
			System.out.println("You've already used up all your arrows!");
			return false;
		};
		
		//System.out.println("The Wompus is here: "+ wompus.is_Wompus());
		
		grab = () -> {
			//If the agent sees a glitter, & dosen't have gold, pick up gold and return to start
			if((glitter.Glimmers() && (!KnowledgeBase.player_has_gold))) {
				KnowledgeBase.player_has_gold = true;
				KnowledgeBase.player_returning_to_start = true;
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
			if(start.is_Start(KnowledgeBase.getCurrentSquare())&&KnowledgeBase.player_has_gold) {
				//Trigger game end
				KnowledgeBase.points += 1000;
				return true;
			}
			return false;
		};
		
		forward = () -> {
			if(MazeBuilder.checkValidForward()) {
				//TODO: MOVE HERE (before the check below)
				
				//Updates the known maze size in KBS
				if(KnowledgeBase.current_square.col+1 > KnowledgeBase.mazeSize) {
					KnowledgeBase.mazeSize = KnowledgeBase.current_square.col+1;
				}
				if(KnowledgeBase.current_square.row+1 > KnowledgeBase.mazeSize) {
					KnowledgeBase.mazeSize = KnowledgeBase.current_square.row+1;
				}
				return true;
			}else {
				System.out.println("You have bumped into a wall");
				if(!KnowledgeBase.wall_hit) {
				KnowledgeBase.wall_hit = true;
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
