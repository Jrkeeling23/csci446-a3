package wumpasWorld;

import java.util.ArrayList;

public class Agent {
	// agent needs to have reference to its knowledge base
	KnowledgeBase kb;
	
	public Agent() {
		kb = new KnowledgeBase();
	}
	
	public void getPercept(Square currentSquare) {
		// create new square instance for knowledge base using the attributes from the actual square
		Square current = new Square(currentSquare.col, currentSquare.row);
		current.environment_attributes = currentSquare.environment_attributes;
		current.visited = true;
		kb.setCurrentSquare(current);
				
	}
}