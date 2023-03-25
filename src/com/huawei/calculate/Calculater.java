package com.huawei.calculate;

import com.huawei.common.Const;
import com.huawei.common.Robot;
import com.huawei.io.Input;

import java.util.LinkedList;
import java.util.List;

import static com.huawei.common.Const.AVOID_WALL;
import static com.huawei.io.Input.robotMap;

public class Calculater {

    public static List<Double> Caculate(Robot robot){
        List<Double>list = new LinkedList<>();
        if (robot.state == Const.ROBOT_IDEL){//不应该出现的情况，即调用此函数时robot处于idle状态，此时就让它停下
            list.add(0.0);
            list.add(0.0);
            return list;
        }
        //根据状态来判断目的地
        double Destination_x = robot.state==1?robot.buyDestination_x:robot.sellDestination_x;
        double Destination_y = robot.state==1?robot.buyDestination_y:robot.sellDestination_y;
        //目的地到机器人所在地的向量
        double vector_x = Destination_x - robot.x;
        double vector_y = Destination_y - robot.y;
        //向量的模
        double modulus = Math.sqrt(vector_x*vector_x+vector_y*vector_y);
        //航向角从[-pi, pi]转换为[0, 2pi]
        double alpha = robot.direction>=0?robot.direction:(2*Math.PI+robot.direction);
        //向量角
        double beta = Math.acos(vector_x/modulus);//目前取值为[0, pi]
        //根据y的正负映射到[0, 2pi]
        beta = vector_y>0?beta:(2*Math.PI-beta);

        /**
         * m(kg)    12.724     17.65
         * a(m/s^2) 19.6479    14.1643
         * vt/2     0.9m       1.2708m
         */

        IsAwoidWall(robot);
        //线速度计算
        if(modulus <= 0.4){//到达目标地点则减到0
            list.add(0.0);
        } else if (modulus <= 1.5) {//考虑惯性，提前开始减速，为了避免停在0.4的范围之外卡死，所以给了一个最低限度的速度
            list.add(1.0);
        } else if (robot.avoid_wall==1) {
            list.add(0.0);
        } else if (robot.collisionFlag) {
            list.add(3.0);
        } else{
            list.add(6.0);//保持6m/s最大速度
        }

//        boolean[] location = IsAwoidRobot(robot,0,0);
//        if(robot.avoid_robot==1){
//            list.add(list.remove(0)-1);
//        }
//
//        if(location[1]&&location[2]){
//            list.add(Math.PI);
//        }else if(!location[1]&&location[2]){
//            list.add(-Math.PI);
//        } else if (location[1]) {
//            list.add(Math.PI);
//        }else{
        if (robot.collisionFlag){
            list.add(Math.PI);
        }
        else {
            if (beta > alpha) {
                if (beta - alpha < Math.PI) {//应该逆时针旋转
//                    list.add(Math.PI);
                    list.add((beta - alpha) / 0.02);
                } else {//顺时针旋转
//                    list.add(-Math.PI);
                    list.add(-(2 * Math.PI - beta + alpha) / 0.02);
                }
            } else if (beta < alpha) {
                if (alpha - beta < Math.PI) {//顺时针旋转
//                    list.add(-Math.PI);
                    list.add(-(alpha - beta) / 0.02);
                } else {//应该逆时针旋转
//                    list.add(Math.PI);
                    list.add((2 * Math.PI - alpha + beta) / 0.02);
                }
            } else {//不旋转
                list.add(0.0);
            }
        }
//        }
        //角速度计算
        return list;
    }

    //0不做任何事，1 buy，
    public static int BuyOrSell(Robot robot){
        if(robot.state == Const.ROBOT_IDEL){
            return  Const.DO_NOTHING;
        } else if (robot.state == Const.ROBOT_FIRST) {
            if (robot.realWorkstationID == robot.buyDestinationID){//状态1且到达买的工作站
                return Const.BUY;
            }
        } else if (robot.state == Const.ROBOT_SECOND) {
            if (robot.realWorkstationID == robot.sellDestinationID){//状态2且到达卖的工作站
                return Const.SELL;
            }
        }
        return Const.DO_NOTHING;
    }

    public static double BetweenWorkDistanceCost(int source, int target){
        return Math.pow((Input.workStationMap.get(source).x - Input.workStationMap.get(target).x), 2) + Math.pow((Input.workStationMap.get(source).y - Input.workStationMap.get(target).y), 2);
    }

    public static double Robot2WorkDistanceCost(int robootID, int workstationID){
        return Math.pow((robotMap.get(robootID).x - Input.workStationMap.get(workstationID).x), 2) + Math.pow((robotMap.get(robootID).y - Input.workStationMap.get(workstationID).y), 2);
    }

    public static double CalculateCost(Robot robot,double x1,double y1,double x2,double y2,int firstID,int secondID){
        double costFirst = Math.sqrt(Math.pow(Math.abs(robot.x-x1),2)+Math.pow(Math.abs(robot.y-y1),2));
        double costSecond = Math.sqrt(Math.pow(Math.abs(x1-x2),2)+Math.pow(Math.abs(y1-y2),2));

        if(costFirst/5.0< Input.workStationMap.get(firstID).time){
            return Integer.MAX_VALUE;
        } else if (((costFirst+costSecond)/5.0< Input.workStationMap.get(secondID).time)) {
            return Integer.MAX_VALUE;
        }
        return costFirst+costSecond;
    }
    public static void IsAwoidWall(Robot robot){
        double prediction = 1.5;

        double prediction_x = robot.x+prediction*Math.cos(robot.direction);
        double prediction_y = robot.y+prediction*Math.sin(robot.direction);

        if(prediction_x+0.5>=50||prediction_y+0.53>=50||prediction_x-0.53<=0||prediction_y-0.53<=0){
            robot.avoid_wall = 1;
        }else{
            robot.avoid_wall = 0;
        }

    }

    //返回boolean[]，1-true表示左边有机器人并且朝向自己，2-true表示右边
    public static boolean[] IsAwoidRobot(Robot robot,double dx,double dy){

        int  DetectionRange = 4;
        //判断是否正朝向taget
//        boolean IsTowardTarget = JudgePointInFun(robot,
//                robot.state==1?robot.buyDestination_x:robot.sellDestination_x,
//                robot.state==1?robot.buyDestination_x:robot.sellDestination_x,
//                0.2,0.2);
//
        boolean[] location = new boolean[3];
        for(int i = 0;i<4;i++){
            //排除自己
            Robot tempRobot = robotMap.get(i);
            if(tempRobot.id==robot.id){
                continue;
            }
            //排除距离超过DetectionRange的机器人
            double distance = Math.sqrt(Math.pow(robot.x-tempRobot.x,2)+Math.pow(robot.y-tempRobot.y,2));
            if(distance>=DetectionRange){
                continue;
            }
            if(tempRobot.avoid_robot == 1)continue;
            int IsTowardRobot = JudgePointInFun(robot,tempRobot.x,tempRobot.y,0.5,-0.5);
            int across = IsEncounter(robot,tempRobot,IsTowardRobot);
            if(IsTowardRobot==2&&across == 1){
                //此时对方在自己右边且相对
                location[1] = true;
            }else if(IsTowardRobot==1&&across == 1){
                //此时对方在自己左边且相对
                location[2] = true;
            }


        }
        if(location[1] || location[2]){
            robot.avoid_robot = 1;
        }else{
            robot.avoid_robot = 0;
        }
        return location;
    }
//返回点xy在robot前方的扇形吗，1左2右
    static int JudgePointInFun(Robot robot,double x,double y,double alpha1,double alpha2)
    {
        double theta;
        theta=Math.atan2(y-robot.y,x-robot.x);
        if(theta >= robot.direction+alpha2&&theta <= robot.direction){
            return 2;
        } else if (theta <= robot.direction+alpha1&&theta >= robot.direction) {
            return 1;
        }else{
            return -0;
        }
    }
    //r1为主机器人，location表示r2在他的左右，1左2右
    //返回1表示相对，2表示不相对
    static int IsEncounter(Robot r1,Robot r2,int location){
        //认定的面对的弧度范围的一半
        double alpha = Math.PI/4;

        double alphaDiff = r2.direction-r1.direction;
        if(location == 1){
            if(alphaDiff<=alpha-Math.PI/2&&alphaDiff>=-alpha-Math.PI/2){
                return 1;
            }
        }else{
            if(alphaDiff<=alpha+Math.PI/2&&alphaDiff>=-alpha+Math.PI/2){
                return 1;
            }
        }
        return 2;
    }




}
