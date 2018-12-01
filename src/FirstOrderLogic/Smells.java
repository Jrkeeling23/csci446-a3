package FirstOrderLogic;

import wumpasWorld.Square;

@FunctionalInterface
public interface Smells {
	abstract boolean Smells(Square s);
}
