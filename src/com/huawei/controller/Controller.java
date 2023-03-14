package com.huawei.controller;

import com.huawei.calculate.Calculater;
import com.huawei.common.Const;
import com.huawei.common.Robot;
import com.huawei.common.WorkStation;
import com.huawei.io.Output;

import java.util.*;


public class Controller {

    //robot Map  :Input.robotMap
    public static PriorityQueue<Plan> plans1 = new PriorityQueue<>();
    public static PriorityQueue<Plan> plans2 = new PriorityQueue<>();
    public static Robot[] robots = new Robot[4];

    //id-坐标
    public static HashMap<Integer,int[]>WorkStations = new HashMap<>();
    public void MakePlan(){

    }

    public void Initialize(){
        //取到每一个工作台位置并编辑plan



    }



    public void MainLoop(){
        int[] idleRobots = new int[4];
        int flag = 0;
        for(int i = 0;i<4;i++){
            if(robots[i].state==Const.ROBOT_IDEL){
                idleRobots[i] = Const.ROBOT_IDEL;
                flag++;
            }else if(robots[i].state==Const.ROBOT_FIRST||robots[i].state==Const.ROBOT_SECOND){
                idleRobots[i] = Const.ROBOT_BUSY;
            }
        }
        List<List<String>> res = new LinkedList<>();
        if(flag!=0){
            //此时有空闲机器人
            //通过两个指针保证操作一致
            PriorityQueue<Plan> plans = !plans1.isEmpty()?plans1:plans2;
            PriorityQueue<Plan> emptyPlans = plans1.isEmpty()?plans1:plans2;
            while (flag>0&&!plans.isEmpty()){
                //两个优先级队列交替使用
                Plan plan = plans.poll();
                emptyPlans.add(plan);
                //记录信息direction表示目标工作台id，robotID代表机器人ID
                double cost = Integer.MAX_VALUE;
                int[] direction = new int[2];
                int robotID = -1;
                //三重循环找到cost最小的高优先级任务和机器人；
                outer:
                for(int first : plan.sourceList){
                    for(int second : plan.destinationList){
                        for(int i = 0;i<idleRobots.length;i++){
                            if(idleRobots[i]==Const.ROBOT_IDEL){
                                double now_cost = Calculater.CalculateCost(robots[i],WorkStations.get(first)[0],
                                        WorkStations.get(first)[1],WorkStations.get(second)[0],
                                        WorkStations.get(second)[1]);
                                if(now_cost<cost){
                                    cost = now_cost;
                                    direction = new int[]{first,second};
                                    robotID = i;
                                    break outer;
                                }
                            }
                        }

                    }
                }
                //更新robot信息
                robots[robotID].SetInfo(Const.ROBOT_FIRST,WorkStations.get(direction[0])[0],
                        WorkStations.get(direction[0])[1],WorkStations.get(direction[1])[0],
                        WorkStations.get(direction[1])[1]);
                List<Double>list = Calculater.Caculate(robots[robotID]);
                List<String> s = new LinkedList<>();
                s.add("forward "+robotID+" "+list.get(0));
                s.add("rotate "+robotID+" "+list.get(0));
                res.add(s);
                flag--;
            }
            //清空当前优先级队列
            while (!plans.isEmpty()) {

                Plan plan = plans.poll();
                emptyPlans.add(plan);
            }
        }
        //无空闲机器人，仅更新下每个机器人的速度和角速度；
        for(int i = 0;i<idleRobots.length;i++){
            if(idleRobots[i] == Const.ROBOT_BUSY){
                Robot robot = robots[i];
                List<String> temp = null;
                int action = Calculater.BuyOrSell(robot);
                temp = addAction(action,robot.id);
                //进行买卖，这帧不再运动
                if(temp==null){
                    temp = new LinkedList<>();
                    Double[] move =Calculater.Caculate(robot).toArray(new Double[2]);
                    temp.add("forward "+robot.id+" "+move[0]);
                    temp.add("rotate "+robot.id+" "+move[1]);
                }
                res.add(temp);
            }
        }
        Output.Print(res);

    }

    List<String> addAction(int action,int id){
        List<String> res = new ArrayList<>();
        switch (action){
            case Const.DO_NOTHING:
                return null;
            case Const.BUY:
                res.add("buy "+id);

                break;
            case Const.SELL:
                res.add("sell "+id);
                break;
                case Const.BUY_AND_SELL:
                    res.add("buy "+id);
                    res.add("sell "+id);

        }
        return res;
    }

}
class Plan implements Comparable{
    int sourceID;
    int destinationID;
    int[]sourceList;
    int[]destinationList;
    int firstPriority;
    int secondPriority;

    @Override
    public int compareTo(Object o) {
        Plan p = (Plan)o;
        if(this.firstPriority==p.firstPriority){
            return -(this.secondPriority-p.secondPriority);
        }else {
            return -(this.firstPriority-p.firstPriority);
        }
    }
}
