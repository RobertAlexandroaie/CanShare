/**
 * 
 */
package model.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.StringTokenizer;

import model.Entity;
import model.Graph;
import model.Island;
import model.PathWay;
import model.Permission;

/**
 * @author Robert
 * 
 *         Class with utilities (methods) that helps in finding paths between
 *         entities/islands
 */
public class GraphUtils {

	/**
	 * The method initializes the array with 0;
	 * 
	 * @param visited
	 *            array for marking visited elements in BFS
	 */
	private static void initVisited(int[] visited) {
		for (int i = 0; i < visited.length; i++) {
			visited[i] = 0;
		}
	}

	/**
	 * The method finds all subjects in "graph" that can get to "entity" through
	 * IS.
	 * 
	 * @param graph
	 *            Graph that contains the relations
	 * @param entity
	 *            Entity found at the end of InitialSpan
	 * @param pathWay
	 *            The direction of the search
	 * @return List of start subjects that can, through IS, get to "entity"
	 */
	public static LinkedList<Entity> getReversedISEndpoints(Graph graph,
			Entity entity, PathWay pathWay) {
		LinkedList<Entity> reversedISQueue = new LinkedList<>();
		LinkedList<Entity> queue = new LinkedList<>();
		HashMap<Entity, Permission> list = null;
		int[] visited = new int[graph.getNodes().size()];
		int indexOfVisitedElement = 0;

		// initializing visited array for bfs
		initVisited(visited);

		// finding the list of elements in which we will search
		if (pathWay.equals(PathWay.FROM)) {
			list = entity.getAccessFrom_HM_EntityPerm();
		} else {
			list = entity.getAccessTo_HM_EntityPerm();
		}

		// for every entity in the list above
		for (Entity entityInList : list.keySet()) {
			// we get the index in order to visit it
			indexOfVisitedElement = graph.getNodes().indexOf(entityInList);

			// if this entity is connected by the one in the parameter list
			// in the "pathway" given and the connection is grant
			// then this entity is ok to consider
			if (list.get(entityInList).isGrant()
					&& visited[indexOfVisitedElement] == 0) {
				// we add the entity in the queue
				queue.add(entityInList);
				visited[indexOfVisitedElement] = 1;
				if (entityInList.isSubject()) {
					// if it is also a subject, than this can be considered to
					// initially span
					// to the entity in parameter list
					reversedISQueue.add(entityInList);
				}
			}
		}

		// we init visited again
		initVisited(visited);
		while (!queue.isEmpty()) {
			Entity pop = queue.pollFirst();
			if (pathWay.equals(PathWay.FROM)) {
				list = pop.getAccessFrom_HM_EntityPerm();
			} else {
				list = pop.getAccessTo_HM_EntityPerm();
			}
			// for every node connected to the node in queue
			// if the permission is take (a viable IS connection node), we add
			// it to queue
			// if it also is a subject (a viable IS start point), we add it to
			// the main queue(returned queue)
			for (Entity neighbour : list.keySet()) {
				indexOfVisitedElement = graph.getNodes().indexOf(neighbour);
				Permission permission = list.get(neighbour);
				if (permission.isTake()) {
					if (visited[indexOfVisitedElement] == 0) {
						queue.add(neighbour);
						visited[indexOfVisitedElement] = 1;
						if (neighbour.isSubject()) {
							reversedISQueue.add(neighbour);
						}
					}
				}
			}
		}
		return reversedISQueue;
	}

	/**
	 * The method finds all subjects that can be endpoints in a Terminal Span
	 * from entity given as param
	 * 
	 * @param graph
	 * @param entity
	 * @param pathWay
	 * @return
	 */
	public static LinkedList<Entity> getTerminallySpanEndpoints(Graph graph,
			Entity entity, PathWay pathWay) {
		LinkedList<Entity> endpointsQueue = new LinkedList<>();
		int[] visited = new int[graph.getNodes().size()];
		int indexOfVisitedElement = 0;

		initVisited(visited);

		if (entity.isSubject()) {
			endpointsQueue.add(entity);
		}

		LinkedList<Entity> queue = new LinkedList<>();
		queue.add(entity);
		indexOfVisitedElement = graph.getNodes().indexOf(entity);
		visited[indexOfVisitedElement] = 1;

		HashMap<Entity, Permission> list = null;

		while (!queue.isEmpty()) {
			Entity pollFirst = queue.pollFirst();

			if (pathWay.equals(PathWay.FROM)) {
				list = pollFirst.getAccessFrom_HM_EntityPerm();
			} else {
				list = pollFirst.getAccessTo_HM_EntityPerm();
			}

			for (Entity neighbour : list.keySet()) {
				indexOfVisitedElement = graph.getNodes().indexOf(neighbour);
				Permission permission = list.get(neighbour);
				if (permission.isTake() && visited[indexOfVisitedElement] == 0) {
					queue.add(neighbour);
					visited[indexOfVisitedElement] = 1;
					if (neighbour.isSubject()) {
						endpointsQueue.add(neighbour);
					}
				}
			}
		}
		return endpointsQueue;
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	public static Entity getNodeByName(Graph graph, String name) {
		for (Entity iterator : graph.getNodes()) {
			if (iterator.getName().compareTo(name) == 0) {
				return iterator;
			}
		}
		return null;
	}

	public static void buildGraphFromFilename(Graph graph, String filename) {
		BufferedReader fileReader = null;
		try {
			fileReader = new BufferedReader(new FileReader(filename));
			int nodesC;
			nodesC = Integer.parseInt(fileReader.readLine());

			String readLine = "";
			String label = "";

			for (; nodesC > 0; nodesC--) {

				readLine = fileReader.readLine();

				StringTokenizer tokenizer = new StringTokenizer(readLine, " ");

				if (tokenizer.hasMoreTokens()) {
					tokenizer.nextToken();
				}

				if (tokenizer.hasMoreTokens()) {
					label = tokenizer.nextToken().toString();
				}
				graph.addNode(label);
			}

			int connectionsCount;

			connectionsCount = Integer.parseInt(fileReader.readLine());

			for (int i = 0; i < connectionsCount; i++) {
				int index1 = 0;
				int index2 = 0;

				readLine = new String(fileReader.readLine());
				StringTokenizer tokenizer = new StringTokenizer(readLine, " ");

				if (tokenizer.hasMoreTokens()) {
					index1 = Integer.parseInt(tokenizer.nextToken());
				}
				if (tokenizer.hasMoreTokens()) {
					index2 = Integer.parseInt(tokenizer.nextToken());
				}

				if (tokenizer.hasMoreTokens()) {
					label = tokenizer.nextElement().toString();
				}

				Permission newPermission = new Permission();
				if (label.contains("r"))
					newPermission.setRead(true);
				if (label.contains("w"))
					newPermission.setWrite(true);
				if (label.contains("e"))
					newPermission.setExec(true);
				if (label.contains("t"))
					newPermission.setTake(true);
				if (label.contains("g"))
					newPermission.setGrant(true);

				graph.addConnection(graph.getNodes().get(index1 - 1), graph
						.getNodes().get(index2 - 1), newPermission);
			}

		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param island1
	 * @param island2
	 * @return
	 */
	private static boolean hasTSPath(Graph graph, Island island1,
			Island island2, PathWay pathWay) {
		LinkedList<Entity> terminallySpansQueue = null;
		if (island1 == null || island2 == null || pathWay == null) {
			return false;
		}
		for (Entity entityInFirstIsland : island1.getSubjects()) {
			terminallySpansQueue = getTerminallySpanEndpoints(graph,
					entityInFirstIsland, pathWay);

			if (terminallySpansQueue != null) {
				for (Entity tsEndPoint : terminallySpansQueue) {
					if (island2.contains(tsEndPoint)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * 
	 * @param queue
	 * @return
	 */
	private static LinkedList<Entity> getGrantQueue(
			LinkedList<Entity> terminallySpanEndpoints) {
		HashMap<Entity, Permission> grantEndpoints = null;
		LinkedList<Entity> grantQueue = new LinkedList<Entity>();
		Entity poll = null;

		while (!terminallySpanEndpoints.isEmpty()) {
			poll = terminallySpanEndpoints.pollFirst();
			grantEndpoints = poll.getAccessTo_HM_EntityPerm();

			for (Entity entity : grantEndpoints.keySet()) {
				if (grantEndpoints.get(entity).isGrant()) {
					grantQueue.add(entity);
				}
			}
		}

		return grantQueue;
	}

	/**
	 * 
	 * @param island2
	 * @param grantQueue
	 * @return
	 */
	private static boolean hasISReversedTSEndpoints(Graph graph,
			Island island2, LinkedList<Entity> grantQueue) {
		if (island2 != null) {
			Entity poll = null;
			while (!grantQueue.isEmpty()) {
				poll = grantQueue.pollFirst();
				if (poll.isSubject()) {
					if (island2.contains(poll)) {
						return true;
					} else {
						for (Entity entity : getTerminallySpanEndpoints(graph,
								poll, PathWay.FROM)) {
							if (entity.isSubject() && island2.contains(entity)) {
								return true;
							}
						}
					}
				}
			}
			return false;
		} else {
			return false;
		}
	}

	/**
	 * 
	 * @param island2
	 * @return
	 */
	private static boolean hasISReversedTSPath(Graph graph, Island island1,
			Island island2) {
		LinkedList<Entity> terminallySpanQueue = null;
		LinkedList<Entity> grantQueue = null;
		for (Entity entityInFirstIsland : island1.getSubjects()) {
			terminallySpanQueue = getTakePath(graph, entityInFirstIsland,
					PathWay.TO);
			grantQueue = getGrantQueue(terminallySpanQueue);

			if (hasISReversedTSEndpoints(graph, island2, grantQueue)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @param terminallySpanQueue
	 * @return
	 */
	private static LinkedList<Entity> getReversedISGrantQueue(
			LinkedList<Entity> terminallySpanQueue) {
		LinkedList<Entity> reversedISGrantQueue = new LinkedList<Entity>();
		HashMap<Entity, Permission> grantList = null;

		Entity poll = null;

		while (!terminallySpanQueue.isEmpty()) {
			poll = terminallySpanQueue.pollFirst();
			grantList = poll.getAccessFrom_HM_EntityPerm();
			for (Entity entity : grantList.keySet()) {
				if (grantList.get(entity).isGrant()) {
					reversedISGrantQueue.add(entity);
				}
			}
		}

		return reversedISGrantQueue;
	}

	/**
	 * 
	 * @param island2
	 * @return
	 */
	private static boolean hasTSReversedISEndpoints(Graph graph,
			Island island2, LinkedList<Entity> reversedISGrantQueue) {
		if (island2 != null) {
			Entity poll = null;

			while (!reversedISGrantQueue.isEmpty()) {
				poll = reversedISGrantQueue.pollFirst();
				if (island2.contains(poll) && poll.isSubject()) {
					return true;
				} else {
					for (Entity entity : getTerminallySpanEndpoints(graph,
							poll, PathWay.FROM)) {
						if (entity.isSubject() && island2.contains(entity)) {
							return true;
						}
					}
				}
			}
			return false;
		} else {
			return false;
		}
	}

	private static LinkedList<Entity> getTakePath(Graph graph, Entity entity,
			PathWay pathWay) {
		LinkedList<Entity> takePathQueue = new LinkedList<>();
		int[] visited = new int[graph.getNodes().size()];
		int indexOfVisitedElement = 0;

		initVisited(visited);

		LinkedList<Entity> queue = new LinkedList<>();
		queue.add(entity);
		takePathQueue.add(entity);
		indexOfVisitedElement = graph.getNodes().indexOf(entity);
		visited[indexOfVisitedElement] = 1;

		HashMap<Entity, Permission> list = null;

		while (!queue.isEmpty()) {
			Entity pollFirst = queue.pollFirst();

			if (pathWay.equals(PathWay.FROM)) {
				list = pollFirst.getAccessFrom_HM_EntityPerm();
			} else {
				list = pollFirst.getAccessTo_HM_EntityPerm();
			}

			for (Entity neighbour : list.keySet()) {
				indexOfVisitedElement = graph.getNodes().indexOf(neighbour);
				Permission permission = list.get(neighbour);
				if (permission.isTake() && visited[indexOfVisitedElement] == 0) {
					queue.add(neighbour);
					takePathQueue.add(neighbour);
					visited[indexOfVisitedElement] = 1;
				}
			}
		}
		return takePathQueue;
	}

	/**
	 * 
	 * @param island2
	 * @return
	 */
	private static boolean hasTSReversedISPath(Graph graph, Island island1,
			Island island2) {
		LinkedList<Entity> terminallySpanQueue = null;
		LinkedList<Entity> reversedISQueue = null;

		for (Entity entityOfIsland1 : island1.getSubjects()) {
			terminallySpanQueue = getTakePath(graph, entityOfIsland1,
					PathWay.TO);
			reversedISQueue = getReversedISGrantQueue(terminallySpanQueue);

			if (hasTSReversedISEndpoints(graph, island2, reversedISQueue)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @param island2
	 * @return
	 */
	public static boolean hasBridge(Graph graph, Island island1, Island island2) {
		if (island1 == island2) {
			return true;
		} else {
			if (hasTSPath(graph, island1, island2, PathWay.TO)) {
				return true;
			} else if (hasTSPath(graph, island1, island2, PathWay.FROM)) {
				return true;
			} else if (hasISReversedTSPath(graph, island1, island2)) {
				return true;
			} else if (hasTSReversedISPath(graph, island1, island2)) {
				return true;
			} else {
				return false;
			}
		}
	}

	public static void bridgeIslands(Graph graph) {
		ArrayList<Island> islands = graph.getIslands();
		for (int i = 0; i < islands.size(); i++) {
			for (int j = 0; j < islands.size(); j++) {
				if (i != j) {
					Island island1 = islands.get(i);
					Island island2 = islands.get(j);
					if (hasBridge(graph, island1, island2)
							&& !island1.equals(island2)) {
						island1.getBridgedIslands().add(island2);
					}
				}
			}
		}
	}
}
