package com.huawei.calculate;

public class Pose {
    Point point;
    double yaw;

    public Pose(Point point, double yaw) {
        this.point = point;
        this.yaw = yaw;
    }

    public Pose() {
        this.point = new Point();
    }
}
