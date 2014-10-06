package org.siraya.dconfig;

/**
 * root is master and level 0 level 1 is rootBranch
 * 
 * 
 * 
 * @author angus_chen
 * 
 */
public class Branch implements Comparable<Branch> {
    public static Branch MASTER;

    private Branch levelOneBranch;
    private String id;
    private Branch parentBranch;
    private int branchLevel;

    static {
        Branch.MASTER = new Branch();

    }

    /**
     * constructor for master branch.
     */
    public Branch() {
        this.levelOneBranch = null;
        this.id = "master";
        this.branchLevel = 0;
        this.parentBranch = this;
    }

    /**
     * add child branch
     * 
     * @param child
     */
    public void addChildBranch(final Branch child) {
        child.setBranchLevel(this.branchLevel + 1);

        child.setParentBranch(this);

        if (this.levelOneBranch == null) { // means current node is master.
            child.setLevelOneBranch(child);
        } else {
            child.setLevelOneBranch(this.levelOneBranch);
        }
    }

    public String getId() {
        return this.id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public Branch getParentBranch() {
        return this.parentBranch;
    }

    public void setParentBranch(final Branch parentBranch) {
        this.parentBranch = parentBranch;
    }

    public int getBranchLevel() {
        return this.branchLevel;
    }

    public void setBranchLevel(final int branchLevel) {
        this.branchLevel = branchLevel;
    }

    public Branch getLevelOneBranch() {
        return this.levelOneBranch;
    }

    public void setLevelOneBranch(final Branch rootBranch) {
        this.levelOneBranch = rootBranch;
    }

    /**
     * compare id and root branch.
     */
    @Override
    public boolean equals(final Object object) {
        if (object instanceof Branch) {

            final Branch tmp = (Branch) object;
            if ((this.levelOneBranch == null)
                    && (this.levelOneBranch == tmp.getLevelOneBranch())
                    && this.id.equals(tmp.getId())) {
                return true;
            } else if (this.id.equals(tmp.getId())
                    && this.levelOneBranch.getId().equals(
                            tmp.getLevelOneBranch().getId())) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * if match the compare criteria return if not in the same root branch -3 if
     * in the same root branch but highter return -2 if just not match return -1
     * if match return match level
     */
    public int matchLevel(final Branch obj) {
        //
        // not in the same root branch
        //
        if (!this.isSameLevelOneBranch(obj)) {
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
        for (int i = this.branchLevel - obj.getBranchLevel(); i > 0; i--) {
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
     * 
     * @param branch
     * @return
     */
    public boolean isSameFamily(final Branch branch) {
        //
        // master is the same family with everyone.
        //
        if ((branch.getBranchLevel() == 0) || (this.getBranchLevel() == 0)) {
            return true;
        }

        if (!this.isSameLevelOneBranch(branch)) {
            return false;
        }

        final int diff = branch.getBranchLevel() - this.getBranchLevel();
        if (diff > 0) {
            Branch compareBranch = branch;
            for (int i = diff; i > 0; i--) {
                compareBranch = compareBranch.getParentBranch();
            }
            return this.equals(compareBranch);
        } else if (diff < 0) {
            Branch compareBranch = this;
            for (int i = -diff; i > 0; i--) {
                compareBranch = compareBranch.getParentBranch();
            }
            return branch.equals(compareBranch);
        } else {
            return this.equals(branch);
        }
    }

    public boolean isSameLevelOneBranch(final Branch obj) {
        // master compare to master.
        if ((this.getLevelOneBranch() == null)
                && (obj.getLevelOneBranch() == null)) {
            return true;
        } else {
            return this.getLevelOneBranch().equals(obj.getLevelOneBranch());
        }
    }

    /**
     * compare branch level only.
     * 
     * @param o
     * @return
     */
    public int compareTo(final Branch o) {
        return new Integer(this.getBranchLevel()).compareTo(o.getBranchLevel());
    }

}
