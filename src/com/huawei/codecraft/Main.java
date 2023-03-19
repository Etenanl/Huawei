package com.huawei.codecraft;

import com.huawei.calculate.Calculater;
import com.huawei.common.Robot;
import com.huawei.controller.Scheduler;
import com.huawei.io.Input;


import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;

public class Main {


    public static void main(String[] args) throws IOException {
        Input.ReadMap();
        String line;
        while ((line = Input.inStream.readLine()) != null){
            Input.ReadFrame(line);
            Scheduler.schedule();
        }
//        readUtilOK();
//        outStream.println("OK");
//        outStream.flush();
//        Robot testRobot = new Robot();
//        testRobot.state = 1;
//        testRobot.buyDestination_x = 25;
//        testRobot.buyDestination_y = 10;
//        int frameID;
//        while (inStream.hasNextLine()){
//            String line = inStream.nextLine();
//            String[] parts = line.split(" ");
//            frameID = Integer.parseInt(parts[0]);
//            int workNum = inStream.nextInt();
//            for (int i = 0; i < workNum + 1; i++){
//                String s = inStream.nextLine();
//            }
//            String robotLine = inStream.nextLine();
//            String[] robotParts = robotLine.split(" ");
//            testRobot.x = Double.parseDouble(robotParts[8]);
//            testRobot.y = Double.parseDouble(robotParts[9]);
//            testRobot.direction = Double.parseDouble(robotParts[7]);
//            List a = Calculater.Caculate(testRobot);
//            readUtilOK();
//
//            outStream.printf("%d\n", frameID);
//            outStream.printf("forward %d %f\n", 0, a.get(0));
//            outStream.printf("rotate %d %f\n", 0, a.get(1));
//            outStream.print("OK\n");
//            outStream.flush();
//
//        }
//        PriorityQueue<Integer> plans =new PriorityQueue<>();
//        Robot testRobot = new Robot();
//        testRobot.x = 0;
//        testRobot.y = 0;
//        testRobot.state = 1;
//        testRobot.buyDestination_x = -1;
//        testRobot.buyDestination_y = -1;
//        testRobot.direction = -Math.PI/4;
//        List a = Calculater.Caculate(testRobot);
    }

}
