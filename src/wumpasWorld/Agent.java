package wumpasWorld;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
	 * @return path List of squares in the optimal path, null if there is no path to the square
	 */
	public ArrayList<Square> get_optimal_path(int target_row, int target_col) {
		try {
			// get current square
			Square current = KnowledgeBase.getCurrentSquare();
			Square target = kb.get_Kbs(target_row, target_col);
			// find optimal path to target
			ArrayList<Square> path = search_BFS(current, target);
			if (path == null) {
				return null;
			}
			// return resulting path
			return path;
		} 
		catch(ArrayIndexOutOfBoundsException e) {
			return null;
		}
	}
	
	private ArrayList<Square> search_BFS(Square start, Square end){
		Node<Square> root = new Node<Square>(start);
		// add start to queue
		LinkedList<Node<Square>> queue = new LinkedList<Node<Square>>();
		queue.add(root);
		// optimal path tree
		Node<Square> current = root;
		boolean[][] in_queue = new boolean[kb.kbs.size()][kb.kbs.size()];
		
		// starting location
		boolean solution = false;
		in_queue[current.data.row][current.data.col] = true;
		
		// expand all frontier nodes
		while (!queue.isEmpty()) {
			// pop from queue
			current = queue.pop();
			
			// expand current
			if (current.data.equals(end)) {
				// start building solution
				solution = true;
				break;
			}
			
			// add current children to queue
			for (int[] d : adj) {
				try {
					// get KBS element at [row + d[0]][col + d[1]]
					Square tmp = kb.get_Kbs(current.data.row + d[0], current.data.col + d[1]);
					// don't add an element that is already in the queue, and don't add a dangerous square
					if (!in_queue[tmp.row][tmp.col] && !kb.is_dangerous_square(tmp.row, tmp.col)){
						// add element to tree
						Node<Square> tmp_node = current.add_child(tmp);
						// add element to queue
						queue.add(tmp_node);
						// don't add this element again
						in_queue[tmp.row][tmp.col] = true;
					}
				}
				catch(ArrayIndexOutOfBoundsException e) {
					// skip
				}
			}
		}
		if (solution) {
			// build solution
			ArrayList<Square> path = new ArrayList<>();
			while(!current.data.equals(root.data)) {
				path.add(0, current.data);
				current = current.get_parent();
			}
			return path;
		}
		// path not possible
		return null;	
	}
}