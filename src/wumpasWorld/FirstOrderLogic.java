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
	
	//Initializes the lambda expressions
	public void init() {
		breezy = () -> {
			if(KnowledgeBase.b == EnvType.breeze.ordinal()) {
				return true;
			}
			return false;
		};
		
		smells = () -> {
			if(KnowledgeBase.s == EnvType.stench.ordinal()) {
				return true;
			}
			return false;
		};
		
		glitter = () -> {
			if(KnowledgeBase.g == EnvType.glitter.ordinal()) {
				return true;
			}
			return false;
		};
		
		pit = () -> {
			if(KnowledgeBase.p == EnvType.pit.ordinal()) {
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
		System.out.println("Breezy: "+(breezy.Breezy() && smells.Smells()));
		
		//The Following test combined peices of prepositional logic evaulation statements
		
		start = () -> {
			//TODO: when implementing, use player's location to evaluate this check
			//Starting location will be initialized into the KB
			//TBI (To Be Implemented)
			return false;
		};
		
		//Checks if the current space has a living wompus in it
		wompus = () -> {
			if((KnowledgeBase.w == EnvType.wumpus.ordinal())&& wompus_is_alive.wompas_is_alive()) {
				return true;
			}
			return false;
		};
		
		//Fires an arrow if the agent has one, and sets the wompus as dead in the KB if it connects. Also -10pts
		shoot = () -> {
			if(KnowledgeBase.have_arrow) {
				//TODO: when implementing, use player's location & wompus location to evaluate this check
				//Kills wompus if hits
				/*if(pos & direction align with wompus) {
					wompus_alive = false;
					//Wompus screams
				}*/
				KnowledgeBase.have_arrow = false;
				KnowledgeBase.points -= 10;
				return true;
			}
			return false;
		};
		
		System.out.println("The Wompus is here: "+ wompus.is_Wompus());
		
		grab = () -> {
			//If the agent sees a glitter, & dosen't have gold, pick up gold and return to start
			if(glitter.Glimmers()&&!KnowledgeBase.player_has_gold) {
				KnowledgeBase.player_has_gold = true;
				KnowledgeBase.player_returning_to_start = true;
			}
		};
		
		//TODO: requires pit & wompus to be determined based off current Space to function
		//checks if the player dies after having moved
		die = () -> {
			if(pit.Pit()||wompus.is_Wompus()) {
				//Possibly trigger end/break of game loop here
				return true;
			}
			return false;
		};
		
		climb = () -> {
			if(start.is_Start()&&KnowledgeBase.player_has_gold) {
				//TODO: Trigger flag here for ending game loop & adding points
			}
		};
		
		forward = () -> {
			//TODO: evaluate by maze bounds & player pos tracker
			if(true) {//if((maze.length<(position_tracker+direction))||((position_tracker+direction)<0))
				//TODO: update position tracker in Knowledge Base(unless wall has been hit)
				return true;
			}
			//You have bumped into a wall/boundry
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
	}
}
