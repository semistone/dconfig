package org.siraya.dconfig;
/**
 * root is master and  level 0
 * level 1 is rootBranch 
 * 
 * 
 * 
 * @author angus_chen
 *
 */
public class Branch implements Comparable<Branch>{
	public static Branch MASTER;
	
	private Branch levelOneBranch;
	private String id;
	private Branch parentBranch;
	private int branchLevel;



	static{
		MASTER = new Branch();	

	}
	
	/**
	 * constructor for master branch.
	 */
	public Branch(){
		this.levelOneBranch = null;
		this.id = "master";
		this.branchLevel = 0;
		this.parentBranch = this;
	}
	
	/**
	 * add child branch
	 * @param child
	 */
	public void addChildBranch(Branch child) {
		child.setBranchLevel(this.branchLevel + 1 );

		child.setParentBranch(this);
		
		if (this.levelOneBranch == null) { // means current node is master.
			child.setLevelOneBranch(child); 
		} else {
			child.setLevelOneBranch(levelOneBranch);			
		}
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public Branch getParentBranch() {
		return parentBranch;
	}
	
	public void setParentBranch(Branch parentBranch) {
		this.parentBranch = parentBranch;
	}
	public int getBranchLevel() {
		return branchLevel;
	}
	
	public void setBranchLevel(int branchLevel) {
		this.branchLevel = branchLevel;
	}
	
	public Branch getLevelOneBranch() {
		return levelOneBranch;
	}
	
	public void setLevelOneBranch(Branch rootBranch) {
		this.levelOneBranch = rootBranch;
	}
	
	/**
	 * compare id and root branch.
	 */
	public boolean equals(Object object){
		if (object instanceof Branch) {
			
			Branch tmp = (Branch)object;
			if (this.levelOneBranch == null && 
					this.levelOneBranch == tmp.getLevelOneBranch() &&
					this.id.equals(tmp.getId())) {
				return true;
			} else if (this.id.equals(tmp.getId())
					&& this.levelOneBranch.getId().equals(tmp.getLevelOneBranch().getId())) {
				return true;
			}else{
				return false;
			}
		} else{
			return false;
		}
	}
	
	
	/**
	 * if match the compare criteria
	 * return if not in the same root branch -3
	 *        if in the same root branch but highter return -2
	 *        if just not match return -1
	 *        if match return match level
	 */
	public int matchLevel(Branch obj){
		//
		// not in the same root branch
		//
		if (!isSameLevelOneBranch(obj)) {
			return -3;
		}
		
		//
		// obj branch is highter than me.
		//
		if (this.branchLevel < obj.getBranchLevel()) {
			return -2;
		}
		
		//
		// back to the same level.
		//
		Branch compareBranch = this;
		for(int i = this.branchLevel - obj.getBranchLevel() ; i > 0 ; i--) {			
			compareBranch = compareBranch.getParentBranch();
		}

		if (obj.equals(compareBranch)) {
			return compareBranch.getBranchLevel();
		} else {
			//
			// not match branch.
			//
			return -1;
		}
	}
	
	/**
	 * compare is two branch are in direct ancestry relation
	 * @param branch
	 * @return
	 */
	public boolean isSameFamily(Branch branch) {
		//
		// master is the same family with everyone.
		//
		if (branch.getBranchLevel() == 0 || this.getBranchLevel() == 0) {
			return true;
		}
		
		if (!isSameLevelOneBranch(branch)) {
			return false;
		}
		
		int diff = branch.getBranchLevel() - this.getBranchLevel();
		if (diff > 0) {
			Branch compareBranch = branch;
			for(int i = diff ; i > 0 ; i--) {	
				compareBranch = compareBranch.getParentBranch();
			}
			return this.equals(compareBranch);
		} else if (diff < 0) {
			Branch compareBranch = this;
			for(int i = -diff ; i > 0 ; i--) {			
				compareBranch = compareBranch.getParentBranch();
			}
			return branch.equals(compareBranch);	
		} else {
			return this.equals(branch);
		}
	}
	
	public boolean isSameLevelOneBranch(Branch obj){
		// master compare to master.
		if (this.getLevelOneBranch() == null && obj.getLevelOneBranch() == null) {
			return true;
		} else {
			return this.getLevelOneBranch().equals(obj.getLevelOneBranch());			
		}
	}
	
	/**
	 * compare branch level only.
	 * @param o
	 * @return
	 */
	public int compareTo(Branch o){
		return new Integer(this.getBranchLevel()).compareTo(o.getBranchLevel());
	}
	
}
