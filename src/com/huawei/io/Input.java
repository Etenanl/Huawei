package com.huawei.io;

import com.huawei.common.Robot;
import com.huawei.common.WorkStation;

import java.util.HashMap;
import java.util.Map;

public class Input {
    public static int frame;
    public static Map<Integer, WorkStation> workStationMap = new HashMap<>();
    public static Map<Integer, Robot>robotMap = new HashMap<>();
    //读取地图，第一次更新workStationMap
    //这里读map信息时没有工作台id，读frame才有工作台id，可以想办法进行对应，或者干脆不读取map信息，读第一帧才进行全部初始化
    public static void ReadMap(){

    }
    //读取每一帧内容
    public static void ReadFrame(){

    }
    //根据readmap独到的工作台坐标与readFrame读到的工作台坐标，对每个工作台更新id
    static void findID(){

    }
}

