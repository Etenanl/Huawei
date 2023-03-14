package com.huawei.calculate;

import com.huawei.common.Robot;

import java.util.LinkedList;
import java.util.List;

public class Calculater {

    public static List<Double> Caculate(Robot robot){


        List<Double>list = new LinkedList<>();
        //根据状态来判断目的地
        double Destination_x = robot.state==1?robot.buyDestination_x:robot.sellDestination_x;
        double Destination_y = robot.state==1?robot.buyDestination_y:robot.sellDestination_y;
        //目的地到机器人所在地的向量
        double vector_x = Destination_x - robot.x;
        double vector_y = Destination_y - robot.y;
        //向量的模
        double modulus = Math.sqrt(vector_x*vector_x+vector_y*vector_y);
        //航向角从[-pi, pi]转换为[0, 2pi]
        double alpha = robot.direction>=0?robot.direction:(Math.PI-robot.direction);
        //向量角
        double beta = Math.acos(vector_x/modulus);//目前取值为[0, pi]
        //根据y的正负映射到[0, 2pi]
        beta = vector_y>0?beta:(2*Math.PI-beta);

        //线速度计算
        if(modulus <= 0.12){//0.12m为6m/s经过1帧（即20ms）移动的距离
            list.add(modulus/0.02);//一帧内能移动到目的地则减速
        }
        else{
            list.add(6.0);//保持6m/s最大速度
        }

        //角速度计算
        if(beta > alpha){//应该逆时针旋转
            list.add(Math.PI);
        }
        else if(beta < alpha){//顺时针旋转
            list.add(-Math.PI);
        }
        else{//不旋转
            list.add(0.0);
        }

        return list;
    }
}
