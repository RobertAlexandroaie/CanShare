/**
 * 
 */
package model;

/**
 * @author Robert
 * 
 * Class used to represent the righst an entity can have on another
 */
public class Permission {
	private boolean read = false;
	private boolean write = false;
	private boolean exec = false;
	private boolean take = false;
	private boolean grant = false;

	/**
	 * @return the read
	 */
	public boolean isRead() {
		return read;
	}

	/**
	 * @param read
	 *            the read to set
	 */
	public void setRead(boolean read) {
		this.read = read;
	}

	/**
	 * @return the write
	 */
	public boolean isWrite() {
		return write;
	}

	/**
	 * @param write
	 *            the write to set
	 */
	public void setWrite(boolean write) {
		this.write = write;
	}

	/**
	 * @return the exec
	 */
	public boolean isExec() {
		return exec;
	}

	/**
	 * @param exec
	 *            the exec to set
	 */
	public void setExec(boolean exec) {
		this.exec = exec;
	}

	/**
	 * @return the take
	 */
	public boolean isTake() {
		return take;
	}

	/**
	 * @param take
	 *            the take to set
	 */
	public void setTake(boolean take) {
		this.take = take;
	}

	/**
	 * @return the grant
	 */
	public boolean isGrant() {
		return grant;
	}

	/**
	 * @param grant
	 *            the grant to set
	 */
	public void setGrant(boolean grant) {
		this.grant = grant;
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
		result = prime * result + (exec ? 1231 : 1237);
		result = prime * result + (grant ? 1231 : 1237);
		result = prime * result + (read ? 1231 : 1237);
		result = prime * result + (take ? 1231 : 1237);
		result = prime * result + (write ? 1231 : 1237);
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
		Permission other = (Permission) obj;

		return outfits(other);
	}

	public boolean outfits(Permission other) {
		if (other.exec == true && exec != other.exec)
			return false;
		if (other.grant == true && grant != other.grant)
			return false;
		if (other.read == true && read != other.read)
			return false;
		if (other.take == true && take != other.take)
			return false;
		if (other.write == true && write != other.write)
			return false;
		return true;
	}
}
