package wumpasWorld;

/**
 * A Square instance is created for each square in the game. It holds environmental variables that an agent will use to
 * detect the state of that particular square.
 */
public class Square{

    // variable to hold x,y cordinates of square
    public int coord[];

    /*
     * array index : environment attriubte
     * 0 : breeze
     * 1 : stench
     * 2 : glitter
     * 3 : wumpus
     * 4 : pit
     */
    private boolean environment_attributes[];

    // Square's constructor
    public Square(int coord[], boolean environment_attributes[]){
        this.coord = coord;
        this.environment_attributes = environment_attributes;

    }

	public Square(int row, int col) {
		// TODO Auto-generated constructor stub
	}

	public boolean has_pit() {
		// TODO Auto-generated method stub
		return false;
	}

	public void add_obj(EnvType env) {
		// TODO Auto-generated method stub
		
	}

	public void remove_obj(EnvType pit) {
		// TODO Auto-generated method stub
		
	}
}