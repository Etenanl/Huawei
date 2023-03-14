package com.huawei.controller;

import com.huawei.calculate.Calculater;
import com.huawei.common.Const;
import com.huawei.common.Robot;
import com.huawei.common.WorkStation;
import com.huawei.io.Input;
import com.huawei.io.Output;

import java.util.*;


public class Controller {

    //优先级队列用来分配任务
    public static PriorityQueue<Plan> plans1 = new PriorityQueue<>();
    public static PriorityQueue<Plan> plans2 = new PriorityQueue<>();
    //robot Map  :Input.robotMap
    public static Robot[] robots = new Robot[4];
    //plan数组用来存放plan，inde为plan的id
    public static Plan[] planArray = new Plan[20];

    //id-工作站坐标
    public static HashMap<Integer,double[]>WorkStations = new HashMap<>();

    //工作站type-计划id
    public static HashMap<Integer,int[]>SourceStationToPlan = new HashMap<>();
    public static HashMap<Integer,int[]>DstinationStationToPlan = new HashMap<>();
    public void MakePlan(){
        //1,2,3->4,5,6
        planArray[1] = new Plan(1,1,4,3,5);
        planArray[2] = new Plan(2,2,4,3,5);

        planArray[3] = new Plan(3,1,5,3,5);
        planArray[4] = new Plan(4,3,5,4,5);

        planArray[5] = new Plan(5,2,6,4,5);
        planArray[6] = new Plan(6,3,6,4,5);
        //4,5,6->7
        planArray[7] = new Plan(7,4,7,5,5);
        planArray[8] = new Plan(8,5,7,5,5);
        planArray[9] = new Plan(9,6,7,5,5);
        //7_8
        planArray[10] = new Plan(10,7,8,6,5);

        //1~7->9
        planArray[11] = new Plan(11,1,9,3,5);
        planArray[12] = new Plan(12,2,9,3,5);
        planArray[13] = new Plan(13,3,9,3,5);
        planArray[14] = new Plan(14,4,9,4,5);
        planArray[15] = new Plan(15,5,9,4,5);
        planArray[16] = new Plan(16,6,9,4,5);
        planArray[17] = new Plan(17,7,9,5,5);
        SourceStationToPlan.put(1,new int[]{1,3,11});
        SourceStationToPlan.put(2,new int[]{2,5,12});
        SourceStationToPlan.put(3,new int[]{4,6,13});
        SourceStationToPlan.put(4,new int[]{7,14});
        SourceStationToPlan.put(5,new int[]{8,15});
        SourceStationToPlan.put(6,new int[]{9,16});
        SourceStationToPlan.put(7,new int[]{7,17});


        DstinationStationToPlan.put(4,new int[]{1,2});
        DstinationStationToPlan.put(5,new int[]{3,4});
        DstinationStationToPlan.put(6,new int[]{5,6});
        DstinationStationToPlan.put(7,new int[]{7,8,9});
        DstinationStationToPlan.put(8,new int[]{10});
        DstinationStationToPlan.put(9,new int[]{11, 12 ,13 ,14 ,15 ,16 ,17});

    }

    public void Initialize(){
        //取到每一个工作台位置并编辑plan
        MakePlan();


        //初始化WorkStations
        Set<Integer> stationsID = Input.workStationMap.keySet();
        Set<Map.Entry<Integer,WorkStation>> entrys = Input.workStationMap.entrySet();
        for(Map.Entry<Integer,WorkStation> entry : entrys){
            //初始化WorkStations
            WorkStations.put(entry.getKey(),new double[]{entry.getValue().x,entry.getValue().y});
            //初始化planArray，为每个plan的source和Dstination附上id
            int id = entry.getValue().id;
            int[] tempPlan = SourceStationToPlan.getOrDefault(entry.getValue().type,null);
            if(tempPlan!=null){
                for(int i : tempPlan){
                    planArray[i].AddSource(id);
                }
            }
            tempPlan = DstinationStationToPlan.getOrDefault(entry.getValue().type,null);
            if(tempPlan!=null){
                for(int i : tempPlan){
                    planArray[i].AddSource(id);
                }
            }
        }
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

                                    //修改plan的第二优先级
                                    //
                                    //
                                    //
                                    plan.secondPriority--;


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
                robots[robotID].SetPlanID(plan.planID);
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
                //不需要买卖，修正运动轨迹
                if(temp==null){
                    temp = new LinkedList<>();
                    Double[] move =Calculater.Caculate(robot).toArray(new Double[2]);
                    temp.add("forward "+robot.id+" "+move[0]);
                    temp.add("rotate "+robot.id+" "+move[1]);
                }else{
                    //进行买卖，这帧不再运动
                    //修改plan，如果只包含卖，应当把id变为0
                    if(action == Const.BUY_AND_SELL){
                        robot.SetState(Const.ROBOT_IDEL);
                        planArray[robot.planID].secondPriority++;
                        robot.SetPlanID(-1);

                    }
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
    List<Integer>sourceList = new LinkedList<>();
    List<Integer>destinationList=new LinkedList<>();
    int firstPriority;
    int secondPriority;

    int planID;

    public void AddSource(int id){
        this.sourceList.add(id);
    }
    public void AdddDstination(int id){
        this.destinationList.add(id);
    }

    public Plan(int planID,int sourceID,int destinationID,int firstPriority,int secondPriority){
        this.planID = planID;
        this.sourceID = sourceID;
        this.destinationID = destinationID;
        this.firstPriority = firstPriority;
        this.secondPriority = secondPriority;

    }

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
