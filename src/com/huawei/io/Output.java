package com.huawei.io;

import java.io.BufferedOutputStream;
import java.io.PrintStream;
import java.util.List;

public class Output {

    private static final PrintStream outStream = new PrintStream(new BufferedOutputStream(System.out), true);
    public static void Print (StringBuilder builder){
        outStream.print(builder);
    }

    public static void OKPrint(){
        outStream.print("OK\n");
    }
}
