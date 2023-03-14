package com.huawei.calculate;

import com.huawei.common.Const;
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
        //线速度计算
        if(modulus <= 0.4){//到达目标地点则减到0
            list.add(0.0);
        } else if (modulus <= 1) {//考虑惯性，提前开始减速，为了避免停在0.4的范围之外卡死，所以给了一个最低限度的速度
            list.add(1.0);
        } else{
            list.add(6.0);//保持6m/s最大速度
        }
        //角速度计算
        if(beta > alpha){
            if(beta - alpha < Math.PI){//应该逆时针旋转
                list.add(Math.PI);
            }
            else{//顺时针旋转
                list.add(-Math.PI);
            }
        }
        else if(beta < alpha){
            if(alpha - beta < Math.PI){//顺时针旋转
                list.add(-Math.PI);
            }
            else {//应该逆时针旋转
                list.add(Math.PI);
            }
        }
        else{//不旋转
            list.add(0.0);
        }
        return list;
    }

    //0不做任何事，1 buy，
    public static int BuyOrSell(Robot robot){
        return Const.DO_NOTHING;
    }

    public static double CalculateCost(Robot robot,int x1,int y1,int x2,int y2){
        return 0.0;
    }




}
