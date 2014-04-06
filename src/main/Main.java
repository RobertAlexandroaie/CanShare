package main;

import model.Graph;
import model.Permission;

/**
 * @author Robert
 * 
 *         Main class for running the program
 */
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Graph testGraph = new Graph("g6.tgs");
		// Graph testGraph = new Graph("g2.tgs");
		// Graph testGraph = new Graph("g3.tgs");
		// Graph testGraph = new Graph("g4.tgs");
		// Graph testGraph = new Graph("g5.tgs");

		// for (Island island : testGraph.getIslands()) {
		// System.out.println(island);
		// }

		Permission permission = new Permission();
		permission.setRead(true);
		//permission.setWrite(true);

		System.out.println(testGraph.canShare(permission, "s9", "s2"));
	}

}
