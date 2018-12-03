package wumpasWorld;

public class World {

	// get size of environment
	private static final int env_size = EnvType.values().length;
	private static Agent agent;
	public int points = 0;
	boolean running;
	
	public World(int maze_size) {
		// build a maze
		MazeBuilder maze_obj = new MazeBuilder(maze_size);
		Square[][] maze = maze_obj.get_maze();
		
		// TODO get rid of this printout
		testing_maze_printout(maze);
		
		// make the agent
		agent = new Agent(maze[0][0]);
		running = true;
		
		//Prints world TODO: TEMP
		printMaze(maze);
		//While not dead or finished, continue running the agent cycle
		while(running) {
			running = agent.agent_cycle(maze[agent.getKB().getCurrentSquare().col][agent.getKB().getCurrentSquare().row]);
		}
		points = agent.getKB().points;
	}
	
	private static void testing_maze_printout(Square[][] maze) {
		System.out.println("glitter, breeze, stench, wumpus, pit");
		for (int row=0; row<4; row++) {
			for (int col=0; col<4; col++) {
				String tmp = "";
				for (int i=0; i<env_size; i++) {
					tmp += maze[row][col].environment_attributes[i] ? "1" : "0";
				}
				System.out.print(tmp + ", ");
				}
				System.out.println();
			}
		}
	private void printMaze(Square[][] maze) {
		for (int row=0; row<4; row++) {
			for (int col=0; col<4; col++) {
				String tmp = "";
				for (int i=0; i<env_size; i++) {
					tmp += maze[row][col].environment_attributes[i] ? "1" : "0";
				}
				System.out.print(tmp + ", ");
				}
				System.out.println();
			}
		
		
	}
}
