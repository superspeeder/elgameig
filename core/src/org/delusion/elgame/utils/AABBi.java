package org.delusion.elgame.utils;

public class AABBi {

    public int left, right, bottom, top;

    public AABBi(int left, int right, int bottom, int top) {
        this.left = left;
        this.right = right;
        this.bottom = bottom;
        this.top = top;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AABBi aabBi = (AABBi) o;

        if (left != aabBi.left) return false;
        if (right != aabBi.right) return false;
        if (bottom != aabBi.bottom) return false;
        return top == aabBi.top;
    }

    @Override
    public int hashCode() {
        int result = left;
        result = 31 * result + right;
        result = 31 * result + bottom;
        result = 31 * result + top;
        return result;
    }

    @Override
    public String toString() {
        return "AABBi{" +
                "left=" + left +
                ", right=" + right +
                ", bottom=" + bottom +
                ", top=" + top +
                '}';
    }
}
