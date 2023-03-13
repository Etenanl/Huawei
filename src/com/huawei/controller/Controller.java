package com.huawei.controller;

import com.huawei.io.Input;

import java.util.PriorityQueue;

public class Controller {

    //robot Map  :Input.robotMap
    public static PriorityQueue<plan> plans1 = new PriorityQueue<>();
    public static PriorityQueue<plan> plans2 = new PriorityQueue<>();
    public void makePlan(){

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
