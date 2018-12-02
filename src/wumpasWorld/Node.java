package wumpasWorld;

import java.util.List;

public class Node<T> {
	// Node data
    public T data;
    // parent of the Node
    private Node<T> parent;
    // list of Node children
    private List<Node<T>> children;
    
    public Node(T data) {
    	this.data = data;
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
