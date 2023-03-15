package com.huawei.io;

import com.huawei.common.Const;
import com.huawei.common.Robot;
import com.huawei.common.WorkStation;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Input {
    public static int frame;

    public static int money;

    public static int workstationNum;
    public static Map<Integer, WorkStation> workStationMap = new HashMap<>();
    public static Map<Integer, Robot>robotMap = new HashMap<>();

    private static final Scanner inStream = new Scanner(System.in);
    //读取地图，第一次更新workStationMap
    //这里读map信息时没有工作台id，读frame才有工作台id，可以想办法进行对应，或者干脆不读取map信息，读第一帧才进行全部初始化
    public static void ReadMap(){
        int robotID = 0;
        int workstationID = 0;
        for (int i = 0; i < 100; i++) {
            String line = inStream.nextLine();
            for (int j = 0; j < 100; j++) {
                if(line.charAt(j) == 'A'){
                    Robot initialRobot = new Robot();
                    initialRobot.id = robotID;
                    initialRobot.x = j * 0.5 + 0.25;
                    initialRobot.y = (99 - i) * 0.5 + 0.25;
                    robotMap.put(robotID, initialRobot);
                    robotID++;
                } else if (line.charAt(j) >= '1' && line.charAt(j) <= '9') {
                    WorkStation initialWorkstation = new WorkStation();
                    initialWorkstation.id = workstationID;
                    initialWorkstation.type = line.charAt(j) - '0';
                    initialWorkstation.x = j * 0.5 + 0.25;
                    initialWorkstation.y = (99 - i) * 0.5 + 0.25;
                    workStationMap.put(workstationID, initialWorkstation);
                    workstationID++;
                }
            }
        }
        //读取OK
        inStream.nextLine();
        Output.OKPrint();
    }
    //读取每一帧内容
    public static void ReadFrame(){
        String[] parts = inStream.nextLine().split(" ");
        frame = Integer.parseInt(parts[0]);
        money = Integer.parseInt(parts[1]);
        workstationNum = Integer.parseInt(inStream.nextLine());
        for (int i = 0; i < workstationNum; i++) {
            parts = inStream.nextLine().split(" ");
            WorkStation station = workStationMap.get(i);
            station.time = Integer.parseInt(parts[3]);
            station.material = Integer.parseInt(parts[4]);
            station.production = Integer.parseInt(parts[5]);
        }
        for (int i = 0; i < 4; i++) {
            parts = inStream.nextLine().split(" ");
            Robot robot = robotMap.get(i);
            robot.realWorkstationID = Integer.parseInt(parts[0]);
            robot.realProduction = Integer.parseInt(parts[1]);
            robot.direction = Double.parseDouble(parts[7]);
            robot.x = Double.parseDouble(parts[8]);
            robot.y = Double.parseDouble(parts[9]);
            if (robot.state == Const.ROBOT_FIRST){//状态机转换
                if (robot.realProduction == robot.planProduction){//拿到计划产品
                    robot.state = Const.ROBOT_SECOND;
                }
            } else if (robot.state == Const.ROBOT_SECOND) {
                if (robot.realProduction == 0){//现在是空手，已经卖出产品
                    robot.state = Const.ROBOT_IDEL;
                }
            }
        }
        //读取OK
        inStream.nextLine();
    }
    //根据readmap独到的工作台坐标与readFrame读到的工作台坐标，对每个工作台更新id
    static void findID(){

    }
    static void updateWorkStation(){

    }
    static void updataRobot(){

    }

}

