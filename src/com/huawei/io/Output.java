package com.huawei.io;

import java.io.BufferedOutputStream;
import java.io.PrintStream;
import java.util.List;

public class Output {

    private static final PrintStream outStream = new PrintStream(new BufferedOutputStream(System.out));
    public static void Print (List<List<String>> list){

    }

    public static void OKPrint(){
        outStream.println("OK");
        outStream.flush();
    }
}
