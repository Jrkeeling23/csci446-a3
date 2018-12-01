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
		
		pit = (s) -> {
			if(s.has_obj(EnvType.pit)) {
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
			//Uses 0,0 as start & compairs the agent's current pos
			//TODO: when implementing, use player's location to evaluate this check
			//Starting location will be initialized into the KB
			//TBI (To Be Implemented)
			return false;
		};
		
		//Checks if the current space has a living wumpus in it
		wompus = (s) -> {
			if((s.has_obj(EnvType.wumpus))&& wompus_is_alive.wompas_is_alive()) {
				return true;
			}
			return false;
		};
		
		//Fires an arrow if the agent has one, and sets the wumpus as dead in the KB if it connects. Also -10pts
		shoot = () -> {
			if(KnowledgeBase.have_arrow) {
				//TODO: when implementing, use player's location & wumpus location to evaluate this check
				//Kills wumpus if hits
				/*if(pos & direction align with wumpus) {
					wompus_alive = false;
					//Wompus screams
				}*/
				KnowledgeBase.have_arrow = false;
				KnowledgeBase.points -= 10;
				return true;
			}
			return false;
		};
		
		//System.out.println("The Wompus is here: "+ wompus.is_Wompus());
		
		grab = () -> {
			//If the agent sees a glitter, & dosen't have gold, pick up gold and return to start
			if(glitter.Glimmers()&&!KnowledgeBase.player_has_gold) {
				KnowledgeBase.player_has_gold = true;
				KnowledgeBase.player_returning_to_start = true;
			}
		};
		
		//TODO: requires pit & wumpus to be determined based off current Space to function
		//checks if the player dies after having moved
		die = () -> {
			if(pit.Pit(KnowledgeBase.getCurrentSquare())||wompus.is_Wompus(KnowledgeBase.getCurrentSquare())) {
				//Possibly trigger end/break of game loop here
				return true;
			}
			return false;
		};
		
		climb = () -> {
			//Checks if the player has the Gold and is at the start position
			if(start.is_Start(KnowledgeBase.getCurrentSquare())&&KnowledgeBase.player_has_gold) {
				//trigger game end
				KnowledgeBase.points += 1000;
				return true;
			}
			return false;
		};
		
		forward = (s) -> {
			//TODO: evaluate by maze bounds & player pos tracker
			if(true) {//if((maze.length<(position_tracker+direction))||((position_tracker+direction)<0))
				//TODO: update position tracker in Knowledge Base(unless wall has been hit)
				return true;
			}
			//You have bumped into a wall/boundary
			return false;
		};
		
		hitwall = () -> {
			//Sets maze size in knowledge base if hit a wall not known about from the start 
			if(true) {//what forward checks, but only if it has gone over
				//Set wall_hit to true if not already set
				if(!KnowledgeBase.wall_hit) {
					KnowledgeBase.wall_hit = true;
				}
				return true;
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
}
