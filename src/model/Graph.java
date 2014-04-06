/**
 * 
 */
package model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.TreeSet;

import model.util.GraphUtils;

/**
 * @author Robert
 * 
 * 
 *         Class that represents a state in TG model.
 */
public class Graph {
	private ArrayList<Entity> nodes;
	private ArrayList<Island> islands;

	public Graph() {
		nodes = new ArrayList<>();
		islands = new ArrayList<>();
	}

	public Graph(String filename) {
		this();

		GraphUtils.buildGraphFromFilename(this, filename);

		GraphUtils.bridgeIslands(this);
	}

	/**
	 * @return the nodes
	 */
	public ArrayList<Entity> getNodes() {
		return nodes;
	}

	/**
	 * @param nodes
	 *            the nodes to set
	 */
	public void setNodes(ArrayList<Entity> nodes) {
		this.nodes = nodes;
	}

	/**
	 * @return the islands
	 */
	public ArrayList<Island> getIslands() {
		return islands;
	}

	/**
	 * @param islands
	 *            the islands to set
	 */
	public void setIslands(ArrayList<Island> islands) {
		this.islands = islands;
	}

	/**
	 * 
	 * @param newNode
	 */
	public void addNodeToIsland(Entity newNode) {
		if (newNode.isSubject()) {
			if (islands.isEmpty()) {
				Island newIsland = new Island();
				newIsland.add(newNode);
				islands.add(newIsland);
			} else {
				ArrayList<Island> islandsBackup = new ArrayList<>();
				islandsBackup.addAll(islands);

				boolean entityAddedToIsland = false;

				for (Island island : islandsBackup) {
					if (island.isTGConnectedTo(newNode)) {
						islands.get(islandsBackup.indexOf(island)).add(newNode);
						entityAddedToIsland = true;
					}
				}

				if (entityAddedToIsland == false) {
					Island newIsland = new Island();
					newIsland.add(newNode);
					islands.add(newIsland);
				}
			}
		}
	}

	/**
	 * 
	 * @param label
	 */
	public void addNode(String label) {
		Entity newNode = new Entity(label);
		nodes.add(newNode);
		addNodeToIsland(newNode);
	}

	/**
	 * 
	 * @param node1
	 * @param node2
	 * @param permission
	 */
	public void addConnection(Entity node1, Entity node2, Permission permission) {
		node1.addConnectionToNodeWithPermission(node2, permission);
		if (node1.isSubject() && node2.isSubject()) {
			if (permission.isGrant() || permission.isTake()) {
				ArrayList<Island> islandsBackup = new ArrayList<Island>();
				islandsBackup.addAll(islands);

				for (Island islandOfNode1 : islandsBackup) {
					if (islandOfNode1.contains(node1)) {
						for (Island islandOfNode2 : islandsBackup) {
							if (islandOfNode2.contains(node2)) {
								if (!islandOfNode1.equals(islandOfNode2)) {
									islands.get(
											islandsBackup
													.indexOf(islandOfNode1))
											.addAll(islandOfNode2);
									islands.remove(islandOfNode2);
									break;
								}
							}
						}
						break;
					}

				}
			}
		}
	}

	/**
	 * 
	 * @param permission
	 * @param subject1
	 * @param subject2
	 * @return
	 */
	public boolean canShare(Permission permissionSet, String targetLabel,
			String invaderLabel) {
		Entity target = GraphUtils.getNodeByName(this, targetLabel);
		Entity invader = GraphUtils.getNodeByName(this, invaderLabel);

		ArrayList<Permission> permissionList = getPermissionList(permissionSet);
		boolean canShare = true;
		boolean masterCanShare = true;
		for (Permission permission : permissionList) {
			if (invader.hasDirectAccess(target, permission, PathWay.TO)) {
				canShare = true;
			} else {
				TreeSet<Entity> friends = target
						.getAccessableNodesWithPermission(PathWay.FROM,
								permission);
				if (friends.size() == 0) {
					canShare = false;
				} else {
					LinkedList<Entity> pPrimeNodes = new LinkedList<Entity>();
					pPrimeNodes.add(invader);
					if (!invader.isSubject()) {
						pPrimeNodes.addAll(GraphUtils.getReversedISEndpoints(
								this, invader, PathWay.FROM));
					}
					for (Entity friend : friends) {
						LinkedList<Entity> sPrimeNodes = GraphUtils
								.getTerminallySpanEndpoints(this, friend,
										PathWay.FROM);
						for (Entity sPrime : sPrimeNodes) {
							System.out
							.println("S' = " + sPrime);
							for (Entity pPrime : pPrimeNodes) {

								System.out
										.println("P' = " + pPrime);
								
								Island islandOfSPrime = null;
								Island islandOfPPrime = null;
								for (Island island : islands) {
									if (island.contains(pPrime)) {
										islandOfPPrime = island;
									} else if (island.contains(sPrime)) {
										islandOfSPrime = island;
										if (islandOfPPrime != null) {
											break;
										}
									}
								}

								if (GraphUtils.hasBridge(this, islandOfPPrime,
										islandOfSPrime)) {
									canShare = true;
								} else {
									int[] visited = new int[islands.size()];
									LinkedList<Island> queue = new LinkedList<Island>();

									for (int i = 0; i < visited.length; i++) {
										visited[i] = 0;
									}
									int index = islands.indexOf(islandOfPPrime);

									queue.add(islandOfPPrime);
									visited[index] = 1;

									while (!queue.isEmpty()) {
										Island poll = queue.pollFirst();
										ArrayList<Island> pollsIslands = poll
												.getBridgedIslands();
										if (pollsIslands != null) {
											for (Island island : pollsIslands) {
												index = islands.indexOf(island);
												if (island
														.equals(islandOfSPrime)) {
													canShare = canShare && true;
												} else {
													if (visited[index] == 0) {
														queue.add(island);
														visited[index] = 1;
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
			masterCanShare = masterCanShare && canShare;
		}
		return masterCanShare;
	}

	private ArrayList<Permission> getPermissionList(Permission permission) {
		ArrayList<Permission> permissionList = new ArrayList<Permission>();
		Permission newPermission = null;

		if (permission.isRead() == true) {
			newPermission = new Permission();
			newPermission.setRead(true);
			permissionList.add(newPermission);
		}

		if (permission.isWrite() == true) {
			newPermission = new Permission();
			newPermission.setWrite(true);
			permissionList.add(newPermission);
		}

		return permissionList;
	}
}
