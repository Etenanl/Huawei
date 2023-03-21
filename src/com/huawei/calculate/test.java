package com.huawei.calculate;

public class test {
    public static void main(String[] args) {
        System.out.println(1);
        Config c = new Config(6,-2,Math.PI,14.16,10,
                0.1,0.1,0.02,0.02,
                1,1,1,new Rect(-0.51,-0.51,0.51,0.51));

        Dwa dwa = new Dwa();
        Velocity v = dwa.planning(new Pose(new Point(0,0),0),
                new Velocity(0,0),
                new Point(10,0),new PointCloud(1,new Point[]{new Point(200,100)}),c);
        System.out.println(v.linearVelocity);
        System.out.println(v.angularVelocity);
    }
}
