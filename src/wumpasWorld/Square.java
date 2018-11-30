package wumpasWorld;

/**
 * A Square instance is created for each square in the game. It holds environmental variables that an agent will use to
 * detect the state of that particular square.
 */
public class Square {

    // variable to hold x,y cordinates of square
    public int row, col;

    /*
     * array index : environment attriubte
     * 0 : breeze
     * 1 : stench
     * 2 : glitter
     * 3 : wumpus
     * 4 : pit
     */
    private boolean []environment_attributes = new boolean[5];

    public Square(int row, int col) {
        this.row = row;
        this.col = col;
        for(int i = 0; i < environment_attributes.length; i++){
            environment_attributes[i] = false;
        }

    }

//    public Square(int row, int col, boolean[] environment_attributes) {
//        this.row = row;
//        this.col = col;
//        this.environment_attributes = environment_attributes;
//    }

    public boolean has_pit() {
        if (environment_attributes[4] == true) {
            return true;
        }
        return false;
    }

    public void add_obj(EnvType env) {
        int index = env.ordinal();
        environment_attributes[index] = true;


    }

    public void remove_obj(EnvType env) {
        int index = env.ordinal();
        environment_attributes[index] = false;

    }
}