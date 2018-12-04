package wumpasWorld;

import java.util.ArrayList;

public class ModelSet {

	private ArrayList<Square> models;
	private static final int env_size = EnvType.values().length;
	private int x_pos;
	private int y_pos;
	
	public ModelSet(int x, int y) {
		// list for all models for a square
		ArrayList<Square> model_list = new ArrayList<Square>();
		x_pos = x;
		y_pos = y;
		
		// list of all env attributes to ignore
		EnvType[] ignore = {EnvType.glitter, EnvType.breeze, EnvType.stench};
		
		// get all possible models minus the use of the gold since it does not affect inference
		boolean[][] env_models = bin_count(env_size - ignore.length);
		
		// build model list
		for (int row=0; row<env_models.length; row++) {
			// add a new model
			model_list.add(new Square(x, y));
			
			// add a new env list
			model_list.get(row).environment_attributes = new boolean[env_size];
			// index of the binary count
			int n = 0;
			
			// place env values as determined
			for (int i=0; i<env_size; i++) {
				boolean ignored = false;
				for (int j=0; j<ignore.length; j++) {
					if (i == ignore[j].ordinal()) {
						// false for ignored attributes
						model_list.get(row).environment_attributes[i] = false;
						ignored = true;
					}
				}
				if (!ignored) {
					// non-ignored, add whatever is in the count
					model_list.get(row).environment_attributes[i] = env_models[row][n];
					n++;
				}
			}
		}
		models = model_list;
	}
	
	public int getX() {
		return x_pos;
	}
	
	public int getY() {
		return y_pos;
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
	
	public ArrayList<Square> getModels(){
		return models;
	}
	
	public void removeSafe() {
		Square s = new Square(models.get(0).row, models.get(0).col);
		//checks if the first object in models is infact Safe, other wise ignore, as it has already been removed
		if(models.get(0).content_equals(s)){
			models.remove(0);
		}else {
			System.out.println("Attempted to remove safe space that was already removed");
		}
	}
	
	//Remove elements of a certain type
	public void removeModel(EnvType e) {
		ArrayList<Square> removeThis = new ArrayList<>();
		for (Square square : models) {
			if(square.environment_attributes[e.ordinal()]) {
				removeThis.add(square);
			}
		}
		for (Square square : removeThis) {
			models.remove(square);
		}
	}
}
