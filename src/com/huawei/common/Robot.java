package com.huawei.common;

public class Robot {
    public int id;
    public double x;
    public double y;

    public int state;

    //计划中分配给机器人要运送的产品
    public int planProduction;
    //每帧输入中机器人真实携带的物品，用于判断是否成功买入或卖出计划运送的产品
    public int realProduction;
    //每帧输入中机器人所处工作台ID
    public int realWorkstationID;
    //朝向
    public double direction;
    public double speed_x;
    public double speed_y;
    public int planID;

    public int avoid_wall = 100;
    public int avoid_robot = 100;


    //机器人处于状态1时买东西要去的目的地工作站
    public int buyDestinationID;
    public double buyDestination_x;
    public double buyDestination_y;

    //机器人处于状态2时卖东西要去的目的地工作站
    public int sellDestinationID;
    public double sellDestination_x;
    public double sellDestination_y;

    public  void SetInfo(int state,double buyDestination_x,double buyDestination_y,double sellDestination_x,double sellDestination_y,int planProduction, int buyDestinationID, int sellDestinationID){
        this.state = state;
        this.buyDestination_x = buyDestination_x;
        this.buyDestination_y = buyDestination_y;
        this.sellDestination_x = sellDestination_x;
        this.sellDestination_y = sellDestination_y;
        this.planProduction = planProduction;
        this.buyDestinationID = buyDestinationID;
        this.sellDestinationID = sellDestinationID;


    }
    public  void SetPlanID(int id){
        this.planID = id;
    }
    public  void SetState(int state){
        this.state = state;
    }
}
