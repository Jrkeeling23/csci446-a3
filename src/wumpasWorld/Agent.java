package wumpasWorld;

import java.util.ArrayList;
import java.util.LinkedList;

public class Agent {
	private int[][] adj = {{-1, 0}, {0, -1}, {1, 0}, {0, 1}};
	// agent needs to have reference to its knowledge base
	KnowledgeBase kb;
	ArrayList<Square> follow_path;
	
	public Agent(Square start) {
		kb = new KnowledgeBase();
		follow_path = new ArrayList<>();
	}
	
	public void agent_cycle() {
		// get precepts
		this.getPrecepts(KnowledgeBase.getCurrentSquare());
		
		// don't update frontier if we are following a path
		// paths can only be on visited squares
		if (follow_path.size() <= 1) {
			// update frontier
			kb.updateFrontier(KnowledgeBase.getCurrentSquare());
			
			// trim frontier
			kb.trimFrontier();
		}
		
		// action query
		Action action = this.actionQuery();
		
		// perform action
		this.performAction(action);
	}
	
	public void getPrecepts(Square currentSquare) {
		// create new square instance for knowledge base using the attributes from the actual square
		Square current = new Square(currentSquare.col, currentSquare.row);
		current.environment_attributes = currentSquare.environment_attributes;
		current.visited = true;
		kb.setCurrentSquare(current);
		
		// stuff to do every time a new square is entered
		// check if the agent died
		if (FirstOrderLogic.die.die()) {
			// TODO end the game
		}
		// look for the gold
		FirstOrderLogic.grab.grab();
		
		// update number of stinks found
		
						
	}
	
	public Action actionQuery() {
		/*
		 * 
			2) else if Agent sees glitter -> get the gold
			
			4) else if Wumpus location is known & not on a pit -> shoot Wumpus
			5) else if Wumpus could be in a set of squares & no pit -> shoot at one of them
			6) else go to the closest frontier square

		 */
		
		if (follow_path.size() > 0) {
			return Action.Follow;
		}
		else if (KnowledgeBase.player_has_gold) {
			this.follow_path = this.get_optimal_path(0, 0);
			return Action.Follow;
		}
		else if (closest_unvisited()) {
			return Action.Follow;
		}
		
		
	}
	
	public void performAction(Action act) {
		
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
			ArrayList<Square> path = search_BFS(current, target, true);
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
	
	public boolean closest_unvisited() {
		// get current square
		Square current = KnowledgeBase.getCurrentSquare();
		// get the closest Square that has not been visited
		ArrayList<Square> path = search_BFS(current, null, true);
		// path not found
		if (path == null) {
			return false;
		}
		// TODO do with 1 search_BFS call?
		// path existed, so set up for following it
		Square tmp = path.get(path.size() - 1);
		// update the follow_path
		follow_path = get_optimal_path(tmp.row, tmp.col);
		
		return true;
	}
	
	private ArrayList<Square> search_BFS(Square start, Square end, boolean first_type){
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
			// expand current
			current = queue.pop();
			
			if (first_type && current.data.equals(end)) {
				// start building solution
				solution = true;
				break;
			}
			else if (!first_type && !current.data.visited) {
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