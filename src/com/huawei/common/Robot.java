package com.huawei.common;

public class Robot {
    public int id;
    public double x;
    public double y;

    /**
     * state    0               1               2
     * 含义      idle(空闲无工作)  去工作站买东西     去工作站卖东西
     *
     * 例如我们分配机器人从工作站1买产品1，再到工作站4卖出产品1，过程如下:
     * 首先找到一个state=0的机器人，初始化它的buyDestination=工作站1的坐标，sellDestination=工作站4的坐标，设置state=1
     * 此时判断机器人是否到达buyDestination，若到达，判断realProduction是否==planProduction,
     * 若相等，说明已经买到plan中的产品，此时状态1的目标完成，转换状态state=2，调用Caculator向sellDestination移动
     * 若不相等，则下达buy的指令，同时调用Caculator指令计算速度、角速度使机器人维持在buyDestination周围
     * 若未到达，则只调用Caculator向buyDestination移动
     * if(机器人state ==1){ %%
     *     if(机器人到达buyDestination){
     *         if(realProduction==planProduction){//买到plan产品
     *             state = 2;
     *             Caculator;//输出一条向sellDestination移动的指令
     *         }
     *         else{//未买则下达买的指令
     *             Buy;//输出一条买planProduction的指令
     *             Caculator;//输出一条保持在buyDestination附近的移动指令
     *         }
     *     }
     *     else{//未到达buyDestination则向其移动
     *         Caculator;//输出一条向buyDestination移动的指令
     *     }
     * }
     * else if(机器人state ==2){
     *     if(机器人到达sellDestination){
     *         if(realProduction==0){//0表示未携带物品，即已成功卖出
     *             state = 0;//恢复idle状态 **此时又可以通过controller分配新的任务
     *         }
     *         else{//未卖则下达卖的指令
     *             Sell;
     *             Caculator;//输出一条保持在sellDestination附近的移动指令
     *         }
     *     }
     *     else{
     *         Caculator;//输出一条向sellDestination移动的指令
     *     }
     * }%%
     * List idleRobot = new List();//存放所有idle的机器人，用于进行controller分配任务
     * for(所有机器人){
     *     if(机器人state == 0){//包括两部分，即上面刚刚通过状态2成功卖出产品转递到状态0的机器人，和本来就是状态0的机器人
     *         idleRobot.add(机器人)
     *     }
     * }
     * Controller.makeplan(idleRobot);//给idle机器人分配任务
     * 对于idleRobot利用%%括起来的代码进行判断，其他的robot因为已经输出了指令所以这里不要重复判断
     */
    public int state;

    //计划中分配给机器人要运送的产品
    public int planProduction;
    //每帧输入中机器人真实携带的物品，用于判断是否成功买入或卖出计划运送的产品
    public int realProduction;
    //朝向
    public double direction;
    public double speed;

    //机器人处于状态1时买东西要去的目的地工作站
    public double buyDestination_x;
    public double buyDestination_y;

    //机器人处于状态2时卖东西要去的目的地工作站
    public double sellDestination_x;
    public double sellDestination_y;
}
