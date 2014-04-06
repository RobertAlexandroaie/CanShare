/**
 * 
 */
package model;

import java.util.HashMap;
import java.util.TreeSet;

/**
 * @author Robert
 * 
 *         Class that represents and entity (subject or anything else).
 */
public class Entity implements Comparable<Entity> {
	private String name = "";
	private HashMap<Entity, Permission> accessTo_HM_EntityPerm;
	private HashMap<Entity, Permission> accessFrom_HM_EntityPerm;
	private boolean subject;
	private static int count;

	public Entity() {
		accessTo_HM_EntityPerm = new HashMap<>();
		accessFrom_HM_EntityPerm = new HashMap<>();
		count++;
	}

	/**
	 * 
	 * @param name
	 *            to set
	 */
	public Entity(String label) {
		this();
		if (label.startsWith("*")) {
			name = label.substring(1);
			subject = true;
		} else {
			name = label;
		}
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the accessTo_HM_EntityPerm
	 */
	public HashMap<Entity, Permission> getAccessTo_HM_EntityPerm() {
		return accessTo_HM_EntityPerm;
	}

	/**
	 * @param accessTo_HM_EntityPerm
	 *            the accessTo_HM_EntityPerm to set
	 */
	public void setAccessTo_HM_EntityPerm(
			HashMap<Entity, Permission> accessTo_HM_EntityPerm) {
		this.accessTo_HM_EntityPerm = accessTo_HM_EntityPerm;
	}

	/**
	 * @return the accessFrom_HM_EntityPerm
	 */
	public HashMap<Entity, Permission> getAccessFrom_HM_EntityPerm() {
		return accessFrom_HM_EntityPerm;
	}

	/**
	 * @param accessFrom_HM_EntityPerm
	 *            the accessFrom_HM_EntityPerm to set
	 */
	public void setAccessFrom_HM_EntityPerm(
			HashMap<Entity, Permission> accessFrom_HM_EntityPerm) {
		this.accessFrom_HM_EntityPerm = accessFrom_HM_EntityPerm;
	}

	/**
	 * @return the subject
	 */
	public boolean isSubject() {
		return subject;
	}

	/**
	 * @param subject
	 *            the subject to set
	 */
	public void setSubject(boolean subject) {
		this.subject = subject;
	}

	/**
	 * @return the count
	 */
	public static int getCount() {
		return count;
	}

	/**
	 * @param count
	 *            the count to set
	 */
	public static void setCount(int count) {
		Entity.count = count;
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
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + (subject ? 1231 : 1237);
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
		Entity other = (Entity) obj;

		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;

		if (subject != other.subject)
			return false;
		return true;
	}

	@Override
	public int compareTo(Entity o) {
		return name.compareTo(o.getName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return name;
	}

	/**
	 * 
	 * @param accessedNode
	 * @param permission
	 */
	void addConnectionToNodeWithPermission(Entity accessedNode,
			Permission permission) {
		accessTo_HM_EntityPerm.put(accessedNode, permission);
		accessedNode.getAccessFrom_HM_EntityPerm().put(this, permission);
	}

	/**
	 * 
	 * @param entity
	 * @param permission
	 * @return
	 */
	public boolean hasDirectAccess(Entity entity, Permission permission,
			PathWay pathWay) {

		if (pathWay.equals(PathWay.FROM)) {
			if (accessFrom_HM_EntityPerm.size() > 0) {
				Permission perm = accessTo_HM_EntityPerm.get(entity);
				if (perm != null) {
					return perm.equals(permission);
				} else {
					return false;
				}
			} else {
				return false;
			}
		} else if (accessTo_HM_EntityPerm.size() > 0) {
			Permission perm = accessTo_HM_EntityPerm.get(entity);
			if (perm != null) {
				return perm.equals(permission);
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * 
	 * @param entity
	 * @return
	 */
	public boolean hasDirectTGAccess(Entity entity, PathWay pathWay) {
		Permission permission = null;

		if (pathWay.equals(PathWay.FROM)) {
			permission = accessFrom_HM_EntityPerm.get(entity);
		} else {
			permission = accessTo_HM_EntityPerm.get(entity);
		}

		if (permission == null) {
			return false;
		} else {
			return permission.isTake() || permission.isGrant();
		}
	}

	public boolean isIsolated() {
		if (accessFrom_HM_EntityPerm.size() == 0
				&& accessTo_HM_EntityPerm.size() == 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 
	 * @param entity
	 * @return
	 */
	public boolean isDirectTGConnected(Entity entity) {
		return hasDirectTGAccess(entity, PathWay.TO)
				|| hasDirectTGAccess(entity, PathWay.FROM);
	}

	public TreeSet<Entity> getAccessableNodesWithPermission(PathWay pathWay,
			Permission permission) {
		TreeSet<Entity> neighbours = new TreeSet<Entity>();
		HashMap<Entity, Permission> list = null;

		if (pathWay.equals(PathWay.FROM)) {
			list = accessFrom_HM_EntityPerm;
		} else {
			list = accessTo_HM_EntityPerm;
		}

		if (list.size() > 0) {
			for (Entity entity : list.keySet()) {
				if (list.get(entity).equals(permission)) {
					neighbours.add(entity);
				}
			}
		}

		return neighbours;
	}
}
