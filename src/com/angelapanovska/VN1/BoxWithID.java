package com.angelapanovska.VN1;

import java.awt.*;

public class BoxWithID {
    public int id;
    public int minX, minY, maxX, maxY;

    public BoxWithID(int id, int minX, int minY, int maxX, int maxY) {
        this.id = id;
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
    }

    public Point getCenter() {
        return new Point((minX + maxX) / 2, (minY + maxY) / 2);
    }
}
