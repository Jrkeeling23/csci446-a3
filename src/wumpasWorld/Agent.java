package wumpasWorld;

import java.util.ArrayList;

public class Agent {
	// get size of environment
	private static final int env_size = EnvType.values().length;
	
	public Agent() {
		
	}
	
	/**
	 * Creates a list of all possible models for a square setting gold as false in all cases,
	 * This is the same for all squares
	 * @param x row location of the square
	 * @param y col location of the square
	 * @return model_list
	 */
	public static ArrayList<Square> get_all_models(int x, int y){
		// list for all models for a square
		ArrayList<Square> model_list = new ArrayList<Square>();
		
		// get all possible models minus the use of the gold since it does not affect inference
		boolean[][] env_models = bin_count(env_size - 1);
		
		// build model list
		for (int row=0; row<env_models.length; row++) {
			// add a new model
			model_list.add(new Square(x, y));
			// add a new env list
			model_list.get(row).environment_attributes = new boolean[env_size];
			// place env values as determined
			int index_of_gold = EnvType.glitter.ordinal();
			for (int i=0; i<env_size; i++) {
				if (i == index_of_gold) {
					// false for gold
					model_list.get(row).environment_attributes[i] = false;
				}
				else {
					// non-gold, add whatever is in the count
					model_list.get(row).environment_attributes[i] = env_models[row][i];
				}
			}
		}
		return model_list;
	}
	
	/**
	 * Calculates and returns a binary count of size 'i'
	 * @param i number of bits in each term in the count
	 * @return count_list a 2D boolean array 2^i x i
	 */
	private static boolean[][] bin_count(int i){
		// make empty arrays
		ArrayList<ArrayList<Boolean>> count_list = new ArrayList<ArrayList<Boolean>>();
		ArrayList<Boolean> count = new ArrayList<Boolean>();
		// get 2D ArrayList
		count_list = bin_count(count_list, count, i);
		int h = count_list.size();
		int w = count_list.get(0).size();
		
		// copy count_list to 2D boolean array
		boolean[][] models = new boolean[h][w];
		for (int row=0; row<h; row++) {
			for (int col=0; col<w; col++) {
				models[row][col] = count_list.get(row).get(col);
			}
		}
		return models;
		
	}
	
	/**
	 * Recursive binary count, run from method above
	 * @param count_list
	 * @param count
	 * @param i
	 * @return
	 */
	private static ArrayList<ArrayList<Boolean>> bin_count(ArrayList<ArrayList<Boolean>> count_list,
			ArrayList<Boolean> count, int i) {
		if (i <= 1) {
			// first bit
			ArrayList<Boolean> tmp1 = new ArrayList<>(count);
			tmp1.add(false);
			count_list.add(tmp1);
			
			// second bit
			ArrayList<Boolean> tmp2 = new ArrayList<>(count);
			tmp2.add(true);
			count_list.add(tmp2);
			return count_list;
			
		}
		else {
			// first bit
			ArrayList<Boolean> tmp1 = new ArrayList<>(count);
			tmp1.add(false);
			bin_count(count_list, tmp1, i - 1);
			
			// second bit
			ArrayList<Boolean> tmp2 = new ArrayList<>(count);
			tmp2.add(true);
			bin_count(count_list, tmp2, i - 1);
			return count_list;
		}
	}

}