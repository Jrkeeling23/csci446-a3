package wumpasWorld;

import java.util.ArrayList;

public class Node<T> {
	// Node data
    public T data;
    // parent of the Node
    private Node<T> parent;
    // list of Node children
    private ArrayList<Node<T>> children;
    
    public Node(T data) {
    	this.data = data;
    	children = new ArrayList<>();
    }
    
    /**
     * add a child add update its parent
     * @param Child
     */
    public Node<T> add_child(T Child) {
    	Node<T> c = new Node<T>(Child);
    	this.children.add(c);
    	c.parent = this;
    	return c;
    }
    
    public Node<T> get_parent() {
    	return this.parent;
    }
}
