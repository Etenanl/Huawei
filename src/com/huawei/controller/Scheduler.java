package com.huawei.controller;

import com.huawei.calculate.Calculater;
import com.huawei.common.Const;
import com.huawei.common.Robot;
import com.huawei.common.WorkStation;
import com.huawei.io.Input;
import com.huawei.io.Output;

public class Scheduler {
    public static void schedule(){
        int flag = 0;//idle的机器人的数量
        for (int i = 0; i < 4; i++) {
            if (Input.robotMap.get(i).state == Const.ROBOT_IDEL){
                flag++;
            }
        }

        Outer:
        while (flag > 0) {
            boolean notEmpty = false;
            int finalSourceID = -1;
            int finalTagetId = -1;

            //第一优先级：7->8,7->9
            if (Input.legalSourceWorkstation.get(7).size() != 0) {
                double cost = Double.MAX_VALUE;
                for (int i = 8; i < 10; i++) {
                    for (int target : Input.DestWorkstation89.get(i)) {
                        for (int source : Input.legalSourceWorkstation.get(7)) {
                            if (!Input.workStationMap.get(source).RobotProductionLock) {
                                double distance = Calculater.BetweenWorkDistanceCost(source, target);
                                if (distance < cost) {
                                    cost = distance;
                                    finalSourceID = source;
                                    finalTagetId = target;
                                    notEmpty = true;
                                }
                            }
                        }
                    }
                }
                if (notEmpty) {
                    Input.workStationMap.get(finalSourceID).RobotProductionLock = true;
                    findOneIdleRobot(finalSourceID, finalTagetId);
                    flag--;
                    continue Outer;//如果找到一个满足的plan，设置锁和机器人的状态，floag--，并跳过这一轮的循环
                }
            }

            /**
             * 第二优先级：4,5,6->仅缺少一个材料的7
             * 第三优先级：4,5,6->仅缺少两个个材料的7
             * 第四优先级：4,5,6->缺少三个个材料的7
             */
            if (Input.legalSourceWorkstation.get(4).size() != 0 || Input.legalSourceWorkstation.get(5).size() != 0 || Input.legalSourceWorkstation.get(6).size() != 0) {
                for (int i = 1; i < 4; i++) {//缺少材料1种,2种,3种
                    double cost = Double.MAX_VALUE;
                    for (int target : Input.legalDestWorkstation.get(7).get(i)) {
                        for (int j = 0; j < i; j++) {//遍历缺少的这些材料
                            int materialType = Input.workStationMap.get(target).emptyMaterial[j];
                            if (!Input.workStationMap.get(target).RobotMaterialLock[materialType]) {
                                for (int source : Input.legalSourceWorkstation.get(materialType)) {
                                    if (!Input.workStationMap.get(source).RobotProductionLock) {
                                        double distance = Calculater.BetweenWorkDistanceCost(source, target);
                                        if (distance < cost) {
                                            cost = distance;
                                            finalSourceID = source;
                                            finalTagetId = target;
                                            notEmpty = true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (notEmpty) {
                        Input.workStationMap.get(finalSourceID).RobotProductionLock = true;
                        Input.workStationMap.get(finalTagetId).RobotMaterialLock[Input.workStationMap.get(finalSourceID).type] = true;
                        findOneIdleRobot(finalSourceID, finalTagetId);
                        flag--;
                        continue Outer;
                    }
                }
            }

            /**
             * 根据Type456Priority来确定4,5,6的生产优先级
             */
            if (Input.legalSourceWorkstation.get(1).size() != 0 || Input.legalSourceWorkstation.get(2).size() != 0 || Input.legalSourceWorkstation.get(3).size() != 0) {
                for (int i = 0; i < 2; i++) {//根据4,5,6的生产优先级来决定谁先遍历
                    for (int j = 1; j < 3; j++) {//缺少材料1种,2种
                        double cost = Double.MAX_VALUE;
                        for (int target: Input.legalDestWorkstation.get(Input.Type456Priority[i]).get(j)){
                            for (int k = 0; k < j; k++) {//遍历缺少的材料
                                int materialType = Input.workStationMap.get(target).emptyMaterial[k];
                                if (!Input.workStationMap.get(target).RobotMaterialLock[materialType]){
                                    for (int source : Input.legalSourceWorkstation.get(materialType)){
                                        if (!Input.workStationMap.get(source).RobotProductionLock) {
                                            double distance = Calculater.BetweenWorkDistanceCost(source, target);
                                            if (distance < cost) {
                                                cost = distance;
                                                finalSourceID = source;
                                                finalTagetId = target;
                                                notEmpty = true;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (notEmpty) {
                            Input.workStationMap.get(finalSourceID).RobotProductionLock = true;
                            Input.workStationMap.get(finalTagetId).RobotMaterialLock[Input.workStationMap.get(finalSourceID).type] = true;
                            findOneIdleRobot(finalSourceID, finalTagetId);
                            flag--;
                            continue Outer;
                        }
                    }
                }
            }

            /**
             * 最低优先级 1,2,3,4,5,6->9
             */
            for (int i = 6; i > 0; i--) {
                if (Input.legalSourceWorkstation.get(i).size() != 0){
                    double cost = Double.MAX_VALUE;
                    for (int source: Input.legalSourceWorkstation.get(i)){
                        if (!Input.workStationMap.get(source).RobotProductionLock){
                            for (int target: Input.DestWorkstation89.get(9)){
                                double distance = Calculater.BetweenWorkDistanceCost(source, target);
                                if (distance < cost) {
                                    cost = distance;
                                    finalSourceID = source;
                                    finalTagetId = target;
                                    notEmpty = true;
                                }
                            }
                        }
                    }
                    if (notEmpty) {
                        Input.workStationMap.get(finalSourceID).RobotProductionLock = true;
                        findOneIdleRobot(finalSourceID, finalTagetId);
                        flag--;
                        continue Outer;
                    }
                }
            }
            if (!notEmpty){
                break Outer;
            }
        }
        CommandGenerator();
    }

    public static void CommandGenerator(){
        StringBuilder builder = new StringBuilder();
        builder.append(Input.frame).append('\n');
        for (int i = 0; i < 4; i++) {
            Robot robot = Input.robotMap.get(i);
            if (robot.state == Const.ROBOT_FIRST){
                if (robot.realWorkstationID == robot.buyDestinationID){
                    builder.append("buy").append(' ').append(i).append('\n');
                }
                else {
                    Double[] move =Calculater.Caculate(robot).toArray(new Double[2]);
                    builder.append("forward ").append(' ').append(i).append(" ").append(move[0]).append('\n');
                    builder.append("rotate ").append(' ').append(i).append(" ").append(move[1]).append('\n');
                }
            } else if (robot.state == Const.ROBOT_SECOND) {
                if (robot.realWorkstationID == robot.sellDestinationID){
                    builder.append("sell").append(' ').append(i).append('\n');
                }
                else {
                    Double[] move =Calculater.Caculate(robot).toArray(new Double[2]);
                    builder.append("forward ").append(' ').append(i).append(" ").append(move[0]).append('\n');
                    builder.append("rotate ").append(' ').append(i).append(" ").append(move[1]).append('\n');
                }
            }
        }
        builder.append("OK").append('\n');
        Output.Print(builder);
    }

    public static void findOneIdleRobot(int finalSourceId, int finalTragetID){
        double cost = Double.MAX_VALUE;
        int finalRobotID = -1;
        for (int i = 0; i < 4; i++) {
            if (Input.robotMap.get(i).state == Const.ROBOT_IDEL){
                double distance = Calculater.Robot2WorkDistanceCost(i, finalSourceId);
                if (distance < cost){
                    finalRobotID = i;
                    cost = distance;
                }
            }
        }
        if (finalRobotID != -1) {
            Input.robotMap.get(finalRobotID).SetInfo(Const.ROBOT_FIRST,
                    Input.workStationMap.get(finalSourceId).x, Input.workStationMap.get(finalSourceId).y,
                    Input.workStationMap.get(finalTragetID).x, Input.workStationMap.get(finalTragetID).y,
                    Input.workStationMap.get(finalSourceId).type, finalSourceId, finalTragetID);
        }
    }
}
