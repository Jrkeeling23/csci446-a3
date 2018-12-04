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
		kb.current_square = start;
		follow_path = new ArrayList<>();
		// TODO finish initial set up of the agent
		
	}
	
	public boolean agent_cycle(Square agent_square) {
		// get precepts
		boolean end_game = false;
		
		kb.printMaze();
		System.out.println("currently at " + kb.current_square.row + " " + kb.current_square.col);
		
		
		if (!kb.current_square.visited) {
			end_game = this.getPrecepts(agent_square);
		}
		
		if (end_game) {
			return false;
		}
		System.out.println("follow len:" + follow_path.size());
		
		// don't update frontier if we are following a path
		// paths can only be on visited squares
		if (follow_path.size() <= 1) {
			System.out.println("Frontier size:" + kb.get_frontier_size());
			// update frontier
			kb.updateFrontier(kb.getCurrentSquare());
			System.out.println("Frontier size:" + kb.get_frontier_size());
			// trim frontier
			kb.trimFrontier();
			System.out.println("Frontier size:" + kb.get_frontier_size());
		}
		
		// action query
		Action action = this.actionQuery();
		
		// perform action
		this.performAction(action);
		return true;
	}
	
	public boolean getPrecepts(Square currentSquare) {
		// create new square instance for knowledge base using the attributes from the actual square
		Square current = new Square(currentSquare.col, currentSquare.row);
		
		current.environment_attributes = currentSquare.environment_attributes;
		current.visited = true;
		kb.setCurrentSquare(current);
		
		// stuff to do every time a new square is entered
		// check if the agent died or if the agent can escape
		if (FirstOrderLogic.die.die() || FirstOrderLogic.climb.climb()) {
			// end the game
			return true;
		}
		// look for the gold
		FirstOrderLogic.grab.grab();
		
		// finish this update
		// update number of stinks found
		if(current.environment_attributes[2] && !kb.getWompusFound()) {
			kb.foundASmell();
		}
		return false;			
	}
	
	/**
	 * Finds the Action that should be performed at the moment
	 * @return Action
	 */
	public Action actionQuery() {
		try {
			if (kb.getWompusFound() && kb.getWompusAlive() &&
					!kb.get_Kbs(kb.wompus_Pos[0], kb.getWompusPos()[1]).has_obj(EnvType.pit) &&
					kb.current_square.has_obj(EnvType.stench)) {
				return Action.Shoot;
			}
			else if (follow_path.size() > 0) {
				return Action.Follow;
			}
			else if (kb.has_gold()) {
				this.follow_path = this.get_optimal_path(0, 0);
				return Action.Follow;
			}
			// if there are unvisited empty squares in the KBS, go to the closest one
			else if (this.closest_unvisited()) {
				return Action.Follow;
			}
			// Wumpus is still alive and not on a pit -> navigate to adj square
			else if (kb.getWompusFound() && kb.getWompusAlive() &&
					!kb.get_Kbs(kb.getWompusPos()[0], kb.getWompusPos()[1]).has_obj(EnvType.pit)) {
				// Requires moving to the correct square first
				this.follow_path = this.closest_EnvType(EnvType.stench);
				return Action.Follow;
			}
			else {
				// go to the closest frontier square
				int[] pos = kb.closest_frontier_square();
				
				// find path to closest frontier square
				this.follow_path = this.get_optimal_path(pos[0], pos[1]);
				// update follow_path
				return Action.Follow;
			}
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}
	
	/**
	 * Performs the action given by Action Query and updates points
	 * @param Action
	 */
	public void performAction(Action act) {
		if (act == Action.Follow) {
			Square tmp = follow_path.get(0);
			//tmp.printEnv();
			Action movement = move_to(tmp);
			// move or rotate
			if (movement == Action.Move) {
				if (FirstOrderLogic.forward.check_forward()) {
					// move the agent
					// set next square as current
					kb.current_square = tmp;
					// remove square from follow list
					follow_path.remove(0);
					// update points
					kb.setPoints(kb.getPoints()-1);
					for (Square sq : follow_path) {
						System.out.println("Follow Path:"+sq.col+", "+sq.row);
					}
				}
				else {
					// TODO move not possible
					follow_path.clear();
					kb.points -= 1;
				}
			}
			else if (movement == Action.RotateCW) {
				// rotate
				kb.rotate(movement);
			}
		}
		else if (act == Action.Shoot) {
			// get how to point at the wumpus
			Action tmp = point_at_target_action(kb.get_Kbs(kb.wompus_Pos[0], kb.wompus_Pos[1]));
			if (tmp == Action.Move) {
				// shoot
				FirstOrderLogic.shoot.shoot();
			}
			else {
				// rotate
				kb.rotate(tmp);
			}
		}
	}
	
	/**
	 * Finds the correct action for going to the target square
	 * @param target
	 * @return Action, a rotation or Move
	 */
	private Action move_to(Square target) throws IllegalArgumentException {
		Square current = kb.getCurrentSquare();
		// valid target square
		if ((Math.abs(current.row - target.row) == 1 && Math.abs(current.col - target.col) == 0) ||
				(Math.abs(current.row - target.row) == 0 && Math.abs(current.col - target.col) == 1)) {
			return point_at_target_action(target);
		}
		// invalid target square
		else {
			throw new IllegalArgumentException();
		}
	}
	
	/**
	 * Gets the action required to point at the target square
	 * @param target square to point at, should be adjacent to the current square
	 * @return Action, a rotation or Move
	 * @throws IllegalArgumentException
	 */
	private Action point_at_target_action(Square target) throws IllegalArgumentException {
		Square current = kb.getCurrentSquare();
		// get delta pos
		int dr = target.row - current.row;
		int dc = target.col - current.col;
		
		Direction target_dir;
		if (dr<0) {
			target_dir = Direction.north;
		}
		else if (dr>0) {
			target_dir = Direction.south;
		}
		else if (dc<0) {
			target_dir = Direction.west;
		}
		else {
			target_dir = Direction.east;
		}
		
		// Evaluates a move condition
		if (kb.current_direction == target_dir) {
			return Action.Move;
		}
		
		// Evaluates rotation direction
		switch (kb
				.current_direction) {
		case north:
			//y-- 
			if (target_dir == Direction.west) {
				return Action.RotateCCW;
			}
			else if (target_dir == Direction.east) {
				return Action.RotateCW;
			}
			else {
				return Action.RotateCW;
			}
			
		case east:
			//x++
			if (target_dir == Direction.south) {
				return Action.RotateCW;
			}
			else if (target_dir == Direction.north) {
				return Action.RotateCCW;
			}
			else {
				return Action.RotateCW;
			}
		case south:
			//y++
			if (target_dir == Direction.east) {
				return Action.RotateCCW;
			}
			else if (target_dir == Direction.west) {
				return Action.RotateCW;
			}
			else {
				return Action.RotateCW;
			}
		case west:
			//x--
			if (target_dir == Direction.south) {
				return Action.RotateCCW;
			}
			else if (target_dir == Direction.north) {
				return Action.RotateCW;
			}
			else {
				return Action.RotateCW;
			}
		default:
			System.out.println("Agent has no direction");
			throw new IllegalArgumentException();
		}
	}
	
	/**
	 * Finds the optimal path to the target square, if one exists
	 * @param target_row
	 * @param target_col
	 * @return path List of squares in the optimal path, empty if there is no path to the square
	 */
	private ArrayList<Square> get_optimal_path(int target_row, int target_col) {
		try {
			// get current square
			Square current = kb.getCurrentSquare();
			Square target = kb.get_Kbs(target_row, target_col);
			// find optimal path to target
			ArrayList<Square> path = search_BFS(current, target, null, 0);
			if (path == null) {
				return new ArrayList<Square>();
			}
			// return resulting path
			return path;
		} 
		catch(IndexOutOfBoundsException e) {
			return new ArrayList<Square>();
		}
	}
	
	private boolean closest_unvisited() {
		// get current square
		Square current = kb.getCurrentSquare();
		// get the closest Square that has not been visited
		ArrayList<Square> path = search_BFS(current, null, null, 1);
		// path not found
		if (path == null) {
			return false;
		}
		// update the follow_path
		follow_path = path;
		
		return true;
	}
	
	private ArrayList<Square> closest_EnvType(EnvType env){
		// get current square
		Square current = kb.getCurrentSquare();
		// find optimal path to closest stench
		ArrayList<Square> path = search_BFS(current, null, env, 2);
		if (path == null) {
			return new ArrayList<Square>();
		}
		// return resulting path
		return path;
	}
	
	private ArrayList<Square> search_BFS(Square start, Square end, EnvType env, int search_end_switch){
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
			
			// check end type switch
			if (search_end_switch==0) {
				// check end condition
				if (current.data.equals(end)) {
					// start building solution
					solution = true;
					break;
				}
			}
			// check end type switch
			else if (search_end_switch==1) {
				// check end condition
				if (!current.data.visited) {
					// start building solution
					solution = true;
					break;
				}
			}
			// check end type switch
			else if (search_end_switch==2) {
				// check end condition
				if (!current.data.has_obj(env)) {
					// start building solution
					solution = true;
					break;
				}
			}
			
			
			// add current children to queue
			for (int[] d : adj) {
				try {
					// get KBS element at [row + d[0]][col + d[1]]
					Square tmp = kb.get_Kbs(current.data.row + d[0], current.data.col + d[1]);
					// don't add an element that is already in the queue, and don't add a dangerous square
					//TODO: check if temp.tow/col are less than 0
					if(tmp.row>-1&&tmp.col>-1) {
						if (!in_queue[tmp.row][tmp.col] && !kb.is_dangerous_square(tmp.row, tmp.col)){
							// add element to tree
							Node<Square> tmp_node = current.add_child(tmp);
							// add element to queue
							queue.add(tmp_node);
							// don't add this element again
							in_queue[tmp.row][tmp.col] = true;
						}
					}
				}
				catch(IndexOutOfBoundsException e) {
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
	
	public KnowledgeBase getKB() {
		return kb;
	}
}