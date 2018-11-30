package wumpasWorld;

/**
 * A Square instance is created for each square in the game. It holds environmental variables that an agent will use to
 * detect the state of that particular square.
 */
public class Square {

    // variable to hold x,y cordinates of square
    public int row, col;

    private static final int env_size = EnvType.values().length;

    public boolean[] environment_attributes = new boolean[env_size];


    public Square(int row, int col) {
        this.row = row;
        this.col = col;
        for(int i = 0; i <= environment_attributes.length; i++){
            environment_attributes[i] = false;
        }

    }

    public boolean has_pit() {
        if (environment_attributes[EnvType.pit.ordinal()] == true) {
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