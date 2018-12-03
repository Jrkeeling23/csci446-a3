package wumpasWorld;

/**
 * A Square instance is created for each square in the game. It holds environmental variables that an agent will use to
 * detect the state of that particular square.
 */
public class Square {

    // variable to hold x,y coordinates of square
    public int row, col;
    public boolean visited;
    public boolean fake = false;

    private static final int env_size = EnvType.values().length;

    public boolean[] environment_attributes = new boolean[env_size];


    public Square(int row, int col) {
        this.row = row;
        this.col = col;
        for(int i = 0; i<environment_attributes.length; i++){
            environment_attributes[i] = false;
        }

    }
    
    public boolean has_obj(EnvType env) {
    	return environment_attributes[env.ordinal()];
    }

    public void add_obj(EnvType env) {
        int index = env.ordinal();
        environment_attributes[index] = true;
    }

    public void remove_obj(EnvType env) {
        int index = env.ordinal();
        environment_attributes[index] = false;
    }
    
    public boolean equals(Square squ) {
    	return this.row == squ.row && this.col == squ.col;
    }
    
    /**
     * Stronger equals function, requires all contents of the Squares to be equal
     * @param squ to compare to this one
     * @return true if they are the same
     */
    public boolean content_equals(Square squ) {
    	boolean same_pos = (this.row == squ.row && this.col == squ.col);
    	
    	for (int i=0; i<Square.env_size; i++) {
    		// all elements must be the same
    		if (this.environment_attributes[i] != squ.environment_attributes[i]) {
    			return false;
    		}
    	}
    	return same_pos && this.fake == squ.fake && this.visited == squ.visited;
    	
    }
    // temporary print function just to determine what squares were being added the kbs.
    public void printEnv() {
    	for (int i = 0; i < EnvType.values().length; i ++) {
    		System.out.print(environment_attributes[i]);
    	}
    	System.out.println();
    }
}