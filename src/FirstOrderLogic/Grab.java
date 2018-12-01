package FirstOrderLogic;

import wumpasWorld.Square;

@FunctionalInterface
public interface Grab {
	//Grabs the gold and triggers the trip home(enables climb)
	abstract void grab();
}
