package com.huawei.codecraft;

import com.huawei.calculate.Calculater;
import com.huawei.common.Robot;


import java.io.BufferedOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;

public class Main {

    private static final Scanner inStream = new Scanner(System.in);

    private static final PrintStream outStream = new PrintStream(new BufferedOutputStream(System.out));
    public static void main(String[] args) {
        readUtilOK();
        outStream.println("OK");
        outStream.flush();
        Robot testRobot = new Robot();
        testRobot.state = 1;
        testRobot.buyDestination_x = 25;
        testRobot.buyDestination_y = 10;
        int frameID;
        while (inStream.hasNextLine()){
            String line = inStream.nextLine();
            String[] parts = line.split(" ");
            frameID = Integer.parseInt(parts[0]);
            int workNum = inStream.nextInt();
            for (int i = 0; i < workNum + 1; i++){
                String s = inStream.nextLine();
            }
            String robotLine = inStream.nextLine();
            String[] robotParts = robotLine.split(" ");
            testRobot.x = Double.parseDouble(robotParts[8]);
            testRobot.y = Double.parseDouble(robotParts[9]);
            testRobot.direction = Double.parseDouble(robotParts[7]);
            List a = Calculater.Caculate(testRobot);
            readUtilOK();

            outStream.printf("%d\n", frameID);
            outStream.printf("forward %d %f\n", 0, a.get(0));
            outStream.printf("rotate %d %f\n", 0, a.get(1));
            outStream.print("OK\n");
            outStream.flush();

        }
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

    private static boolean readUtilOK() {
        String line;
        while (inStream.hasNextLine()) {
            line = inStream.nextLine();
            if ("OK".equals(line)) {
                return true;
            }
            // do something;
        }
        return false;
    }
}
