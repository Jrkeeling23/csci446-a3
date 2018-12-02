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
			// make the agent
			agent = new Agent(maze[0][0]);
			running = true;
			//While not dead or finished, continue running the agent cycle
			while(running) {
				running = agent.agent_cycle(maze[KnowledgeBase.getCurrentSquare().col][KnowledgeBase.getCurrentSquare().row]);
			}
			points = KnowledgeBase.points;
		}
		
		private static void testing_maze_printout(Square[][] maze) {
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
