package com.huawei.common;

public class WorkStation{
    public double x;
    public double y;
    //工作台类型
    public int type;
    //工作台id
    public int id;
    //原材料格状态
    public int material;
    //产品格状态
    public int production;
    //剩余生产时间
    public int time;

    public boolean RobotProductionLock;//True代表已经有一个机器人来取production，不能同时再被别的机器人来取

    public boolean[] RobotMaterialLock = new boolean[8];//True代表已经有一个机器人来送material，不能同时再被别的机器人来送

    public int[] emptyMaterial = new int[3];

}