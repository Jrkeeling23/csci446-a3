package wumpasWorld;

import java.util.ArrayList;
import java.util.LinkedList;

public class Agent {
	private int[][] adj = {{-1, 0}, {0, -1}, {1, 0}, {0, 1}};
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
	
	public void actionQuery() {
		
	}
	
	/**
	 * Finds the optimal path to the target square, if one exists
	 * @param target_row
	 * @param target_col
	 * @return path List of squares in the optimal path, empty if there is no path to the square
	 */
	public ArrayList<Square> get_optimal_path(int target_row, int target_col) {
		// get current square
		Square current = KnowledgeBase.getCurrentSquare();
		
		// find optimal path to target
		ArrayList<Square> path = search_BFS(current);
		
		// return resulting path
		return path;
	}
	
	private ArrayList<Square> search_BFS(Square start){
		
		// add start to queue
		LinkedList<Square> queue = new LinkedList<Square>();
		queue.add(start);
		int row = start.row;
		int col = start.col;
		
		// expand all frontier nodes
		while (!queue.isEmpty()) {
			for (int[] d : adj) {
				// get KBS element at [row + d[0]][col + d[1]]
				Square tmp = kb.kbs.get(row + d[0]).get(col + d[1]);
				// add element to queue
				queue.add(tmp);
			}
			// pop from queue
			
		}
			
		
	}
}