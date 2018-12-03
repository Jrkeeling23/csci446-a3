package testCases;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import wumpasWorld.MazeBuilder;
import wumpasWorld.Square;



class TestAgent {
	static MazeBuilder mb;
	static int size;
	static Square maze[][];
	
	
	@BeforeAll
	public void init() {
		// size of 4 for maze
		assertEquals(4, size);
		
		assertEquals(new MazeBuilder(size), mb);
		
		// get maze from Maze Builder class
		assertEquals(mb.get_maze(), maze);
	}
	
	@Test
	public void useMakeTestMethodForDifferentMazes() {
		Square testMaze[][] = null;
		assertEquals(maze, testMaze);
		mb.test_maze_01();
		if(!maze.equals(mb)) {
			System.out.println("Changes Maze!!!");
		}
		else {
			System.out.println("Maze Never Changes...");
		}
	}

	
}
