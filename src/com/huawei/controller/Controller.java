package com.huawei.controller;

import com.huawei.calculate.Calculater;
import com.huawei.common.Const;
import com.huawei.common.Robot;
import com.huawei.io.Output;

import java.util.*;


public class Controller {

    //robot Map  :Input.robotMap
    public static PriorityQueue<plan> plans1 = new PriorityQueue<>();
    public static PriorityQueue<plan> plans2 = new PriorityQueue<>();
    public static Robot[] robots = new Robot[4];
    public void MakePlan(){

    }

    public void MainLoop(){
        int[] idleRobots = new int[4];
        boolean flag = false;
        for(int i = 0;i<4;i++){
            if(robots[i].state==0){
                idleRobots[i] = 1;
                flag = true;
            }
        }

        if(flag){
            //此时有空闲机器人
        }else{
            //无空闲机器人，仅更新下每个机器人的速度和角速度；
            List<List<String>> res = new LinkedList<>();
            for(Robot robot : robots){
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

            Output.Print(res);

        }

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
class plan implements Comparable{
    int source;
    int destination;
    int[]sourceList;
    int[]destinationList;
    int firstPriority;
    int secondPriority;

    @Override
    public int compareTo(Object o) {
        plan p = (plan)o;
        if(this.firstPriority==p.firstPriority){
            return -(this.secondPriority-p.secondPriority);
        }else {
            return -(this.firstPriority-p.firstPriority);
        }
    }
}
