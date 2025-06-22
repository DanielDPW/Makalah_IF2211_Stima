package stimadpw.stima.state;

import net.minecraft.util.math.BlockPos;

public class Node {
    private BlockPos pos;
    private Node parent;
    private double f;
    private double g;
    private double h;
    private boolean jump;
    private double jumpStrength;

    // Constructor
    public Node(BlockPos pos, Node parent, double f, double g, double h, boolean jump, double jumpStrength) {
        this.pos = pos;
        this.parent = parent;
        this.f = f;
        this.g = g;
        this.h = h;
        this.jump = jump;
        this.jumpStrength = jumpStrength;
    }

    public Node(BlockPos pos, Node parent, double g, double h, boolean jump, double jumpStrength) {
        this.pos = pos;
        this.parent = parent;
        this.g = g;
        this.h = h;
        this.f = g + h;
        this.jump = jump;
        this.jumpStrength = jumpStrength;
    }

    // Getters
    public BlockPos getPos() {
        return pos;
    }

    public Node getParent() {
        return parent;
    }

    public double getF() {
        return f;
    }

    public double getG() {
        return g;
    }

    public double getH() {
        return h;
    }

    public boolean isJump() {
        return jump;
    }

    public double getJumpStrength() {
        return jumpStrength;
    }

    // Setters
    public void setPos(BlockPos pos) {
        this.pos = pos;
    }

    public void setF(double f) {
        this.f = f;
    }

    public void setG(double g) {
        this.g = g;
    }

    public void setH(double h) {
        this.h = h;
    }

    public void setJump(boolean jump) {
        this.jump = jump;
    }

    public void setJumpStrength(double jumpStrength) {
        this.jumpStrength = jumpStrength;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return pos.equals(node.pos);
    }

    @Override
    public int hashCode() {
        return pos.hashCode();
    }

    @Override
    public String toString() {
        return "Node{" +
                "pos=" + pos +
                ", f=" + f +
                ", g=" + g +
                ", h=" + h +
                ", jump=" + jump +
                ", jumpStrength=" + jumpStrength +
                '}';
    }
}