/**
 * 
 */
package model;

import java.util.ArrayList;
import java.util.TreeSet;

/**
 * @author Robert
 * 
 * Class that represents an Island in tg model.
 */
public class Island {
	private TreeSet<Entity> subjects;
	private ArrayList<Island> bridgedIslands;

	public Island() {
		subjects = new TreeSet<Entity>();
		bridgedIslands = new ArrayList<Island>();
	}

	/**
	 * @return the subjects
	 */
	public TreeSet<Entity> getSubjects() {
		return subjects;
	}

	/**
	 * @param subjects
	 *            the subjects to set
	 */
	public void setSubjects(TreeSet<Entity> subjects) {
		this.subjects = subjects;
	}

	
	
	/**
	 * @return the bridgedIslands
	 */
	public ArrayList<Island> getBridgedIslands() {
		return bridgedIslands;
	}

	/**
	 * @param bridgedIslands the bridgedIslands to set
	 */
	public void setBridgedIslands(ArrayList<Island> bridgedIslands) {
		this.bridgedIslands = bridgedIslands;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((subjects == null) ? 0 : subjects.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Island other = (Island) obj;
		if (subjects == null) {
			if (other.subjects != null)
				return false;
		} else if (!subjects.containsAll(other.subjects))
			return false;
		return true;
	}

	/**
	 * 
	 * @param entity
	 * @return
	 */
	public boolean contains(Entity entity) {
		if (entity.isSubject()) {
			return subjects.contains(entity);
		} else {
			return false;
		}
	}

	public boolean isTGConnectedTo(Entity entity) {
		if (entity.isIsolated()) {
			return false;
		} else if (entity.isSubject()) {
			for (Entity entityOfIsland : subjects) {
				if (entityOfIsland.isDirectTGConnected(entity)) {
					return true;
				}
			}
		} else {
			return false;
		}
		return false;
	}

	/**
	 * 
	 * @param entity
	 * @return
	 */
	public boolean add(Entity entity) {
		if (entity.isSubject()) {
			return subjects.add(entity);
		} else {
			return false;
		}
	}

	/**
	 * 
	 * @param island
	 * @return
	 */
	public boolean addAll(Island island) {
		return subjects.addAll(island.getSubjects());
	}

	@Override
	public String toString() {
		return subjects.toString();
	}
}
