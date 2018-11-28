package game;

import java.util.ArrayList;

//Contains Space type or attribute
public class Space{
	
	//Contains the attributes of the space, such as Breeze, Smell, or Glimmer
	private ArrayList<Character> attributes;
	
	public Space() {
		attributes = new ArrayList<Character>();
	}
	
	//Adds attribute to a space
	public void addAttribute(char att) {
		//Checks if the attribute is already contained, if so, donsen't add
		if(!attributes.contains(att)) {
			attributes.add(att);
		}
		
	}
	
	//returns the attributes as a char array
	public char[] getAttributes() {
		char[] temp = new char[attributes.size()];
		
		for(int i = 0; i < attributes.size();i++) {
			temp[i] = attributes.get(i);
		}
		
		return temp;
	}
	
}