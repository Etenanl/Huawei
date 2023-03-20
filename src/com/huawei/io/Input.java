package com.huawei.io;

import com.huawei.common.Const;
import com.huawei.common.Robot;
import com.huawei.common.WorkStation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Input {
    public static int frame;

    public static int money;

    public static int workstationNum;
    public static Map<Integer, WorkStation> workStationMap = new HashMap<>();
    public static Map<Integer, Robot>robotMap = new HashMap<>();

    //从type到一个含有workstation id的list的映射，其中每个workstation要么产品格有生产出来的产品，要么正在生产中,且没有别的robot来取，意味着可以作为源
    public static Map<Integer, List<Integer>> legalSourceWorkstation = new HashMap<>();

    /**
     * 两层映射：type -> 缺少的原材料数量 -> workstation id 的list
     * type = 7        缺失原材料范围[1,3]
     * type = 4,5,6    确实原材料范围[1,2]
     * 没有别的robot来送相同的材料，意味着可以作为目的地
     */
    public static Map<Integer, Map<Integer, List<Integer>>> legalDestWorkstation = new HashMap<>();

    //类型89的workstation没有优先级，且卖出极快
    public static Map<Integer, List<Integer>> DestWorkstation89 = new HashMap<>();

    public static int[] Type456Num = new int[3];

    public static int[] Type456Priority = new int[3];

    public static final BufferedReader inStream = new BufferedReader(new InputStreamReader(System.in));
    //读取地图，第一次更新workStationMap
    //这里读map信息时没有工作台id，读frame才有工作台id，可以想办法进行对应，或者干脆不读取map信息，读第一帧才进行全部初始化
    public static void ReadMap() throws IOException {
        for (int i = 1; i < 8; i++) {//初始化legalSourceWorkstation
            legalSourceWorkstation.put(i, new ArrayList<Integer>());
        }
        for (int i = 4; i < 7; i++) {//初始化type4,5,6的legalDestWorkstation
            legalDestWorkstation.put(i, new HashMap<>());
            for (int j = 1; j < 3; j++) {
                legalDestWorkstation.get(i).put(j, new ArrayList<>());
            }
        }
        legalDestWorkstation.put(7, new HashMap<>());//初始化type7的legalDestWorkstation
        for (int i = 1; i < 4; i++) {
            legalDestWorkstation.get(7).put(i, new ArrayList<>());
        }
        for (int i = 8; i < 10; i++) {//初始化type8,9的DestWorkstation89
            DestWorkstation89.put(i, new ArrayList<>());
        }

        int robotID = 0;
        int workstationID = 0;
        for (int i = 0; i < 100; i++) {
            String line = inStream.readLine();
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
                    int workstationType = line.charAt(j) - '0';
                    initialWorkstation.id = workstationID;
                    initialWorkstation.type = workstationType;
                    initialWorkstation.x = j * 0.5 + 0.25;
                    initialWorkstation.y = (99 - i) * 0.5 + 0.25;
                    workStationMap.put(workstationID, initialWorkstation);
                    if (workstationType == 8 || workstationType == 9){
                        DestWorkstation89.get(workstationType).add(workstationID);
                    }
                    workstationID++;
                }
            }
        }
        //读取OK
        inStream.readLine();
        Output.OKPrint();
    }
    //读取每一帧内容
    public static void ReadFrame(String line) throws IOException{
        String[] parts = line.split(" ");
        frame = Integer.parseInt(parts[0]);
        money = Integer.parseInt(parts[1]);
        workstationNum = Integer.parseInt(inStream.readLine());

        for (int i = 1; i < 8; i++) {//初始化legalSourceWorkstation
            legalSourceWorkstation.get(i).clear();
        }
        for (int i = 4; i < 7; i++) {//初始化type4,5,6的legalDestWorkstation
            for (int j = 1; j < 3; j++) {
                legalDestWorkstation.get(i).get(j).clear();
            }
        }
        for (int i = 1; i < 4; i++) {
            legalDestWorkstation.get(7).get(i).clear();
        }
        for (int j = 0; j < 3; j++) {
            Type456Num[j] = 0;
        }

        for (int i = 0; i < workstationNum; i++) {
            parts = inStream.readLine().split(" ");
            WorkStation station = workStationMap.get(i);
            station.time = Integer.parseInt(parts[3]);
            station.material = Integer.parseInt(parts[4]);
            station.production = Integer.parseInt(parts[5]);
            int workstationType = station.type;

            //如果工作台类型是1~3，且没有别的机器人在取这个工作台的产品的路上，那么可以作为source
            if (workstationType < 4 && workstationType > 0 && !station.RobotProductionLock){
                legalSourceWorkstation.get(workstationType).add(i);
            }
            //如果工作台类型是4~7，且产品已经生产出来，且没有别的机器人在取这个工作台的产品的路上，那么可以作为source
            if (workstationType < 8 && workstationType > 3 && !station.RobotProductionLock && station.production == 1){
                legalSourceWorkstation.get(workstationType).add(i);
            }

            //如果工作台类型是4567，则根据缺少的材料的数量放进不同的list作为target
            if (workstationType == 7){
                int EmptyNum = 0;
                for (int j = 4; j < 7; j++) {
                    if (getBit(station.material, j) && !station.RobotMaterialLock[j]){
                        station.emptyMaterial[EmptyNum] = j;
                        EmptyNum++;
                    }
                    if (!getBit(station.material, j)){//原材料格有456，则增加计数
                        Type456Num[j - 4]++;
                    }
                }
                if (EmptyNum != 0){
                    legalDestWorkstation.get(7).get(EmptyNum).add(i);
                }
            }
            if (workstationType == 6){
                int EmptyNum = 0;
                if (getBit(station.material, 2) && !station.RobotMaterialLock[2]){
                    station.emptyMaterial[EmptyNum] = 2;
                    EmptyNum++;
                }
                if (getBit(station.material, 3) && !station.RobotMaterialLock[3]){
                    station.emptyMaterial[EmptyNum] = 3;
                    EmptyNum++;
                }
                if (station.production == 1){//有一个生产出来的6，计数
                    Type456Num[2]++;
                }
                if (station.time != -1){//有一个正在生产中的6，计数
                    Type456Num[2]++;
                }
                if (EmptyNum != 0){
                    legalDestWorkstation.get(6).get(EmptyNum).add(i);
                }
            }
            if (workstationType == 5){
                int EmptyNum = 0;
                if (getBit(station.material, 1) && !station.RobotMaterialLock[1]){
                    station.emptyMaterial[EmptyNum] = 1;
                    EmptyNum++;
                }
                if (getBit(station.material, 3) && !station.RobotMaterialLock[3]){
                    station.emptyMaterial[EmptyNum] = 3;
                    EmptyNum++;
                }
                if (station.production == 1){//有一个生产出来的5，计数
                    Type456Num[1]++;
                }
                if (station.time != -1){//有一个正在生产中的5，计数
                    Type456Num[1]++;
                }
                if (EmptyNum != 0){
                    legalDestWorkstation.get(5).get(EmptyNum).add(i);
                }
            }
            if (workstationType == 4){
                int EmptyNum = 0;
                if (getBit(station.material, 1) && !station.RobotMaterialLock[1]){
                    station.emptyMaterial[EmptyNum] = 1;
                    EmptyNum++;
                }
                if (getBit(station.material, 2) && !station.RobotMaterialLock[2]){
                    station.emptyMaterial[EmptyNum] = 2;
                    EmptyNum++;
                }
                if (station.production == 1){//有一个生产出来的4，计数
                    Type456Num[0]++;
                }
                if (station.time != -1){//有一个正在生产中的4，计数
                    Type456Num[0]++;
                }
                if (EmptyNum != 0){
                    legalDestWorkstation.get(4).get(EmptyNum).add(i);
                }
            }
        }

        for (int i = 0; i < 4; i++) {
            parts = inStream.readLine().split(" ");
            Robot robot = robotMap.get(i);
            robot.realWorkstationID = Integer.parseInt(parts[0]);
            robot.realProduction = Integer.parseInt(parts[1]);
            robot.speed_x = Double.parseDouble(parts[5]);
            robot.speed_y = Double.parseDouble(parts[6]);
            robot.direction = Double.parseDouble(parts[7]);
            robot.x = Double.parseDouble(parts[8]);
            robot.y = Double.parseDouble(parts[9]);
            if (robot.realProduction > 3 && robot.realProduction < 7){//机器人持有456，计数
                Type456Num[robot.realProduction - 4]++;
            }
            if (robot.state == Const.ROBOT_FIRST){//状态机转换
                if (robot.realProduction == robot.planProduction){//拿到计划产品
                    robot.state = Const.ROBOT_SECOND;
                    workStationMap.get(robot.buyDestinationID).RobotProductionLock = false;//释放取产品锁
                }
            } else if (robot.state == Const.ROBOT_SECOND) {
                if (robot.realProduction == 0){//现在是空手，已经卖出产品
                    robot.state = Const.ROBOT_IDEL;
                    workStationMap.get(robot.sellDestinationID).RobotMaterialLock[robot.planProduction] = false;//释放材料锁
                }
            }
        }

        //计算4,5,6三种材料的生产优先级
        for (int i = 0; i < 3; i++) {
            Type456Priority[i] = i + 4;
        }
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2 - i; j++) {
                if (Type456Num[j] > Type456Num[j + 1]){
                    int temp = Type456Num[j];
                    Type456Num[j] = Type456Num[j + 1];
                    Type456Num[j + 1] = temp;
                    temp = Type456Priority[j];
                    Type456Priority[j] = Type456Priority[j + 1];
                    Type456Priority[j + 1] = temp;
                }
            }
        }
        //读取OK
        inStream.readLine();
    }

    private static boolean getBit(int num, int i) {
        return ((num & (1 << i)) == 0);//true 表示第i位为0,否则为1
    }
    //根据readmap独到的工作台坐标与readFrame读到的工作台坐标，对每个工作台更新id
    static void findID(){

    }
    static void updateWorkStation(){

    }
    static void updataRobot(){

    }

}

