package FirstOrderLogic;

//Checks if the Agent can move forward
@FunctionalInterface
public interface Forward {
	//Checks the bounds and players position +1 in the direction they are heading
	//To evaluate if they can move forward, or if they bump into a wall/have other cirumstances occur.
	abstract boolean check_forward();

}
