package com.jiabin.exercise.rpc.impl;

import com.jiabin.exercise.rpc.service.IHelloService;
import com.jiabin.exercise.rpc.service.IServerCenterService;

import java.io.IOException;

/**
 * Created by jiabi on 2018/3/15.
 */
public class Provider {
    public static void main(String[] args) throws IOException{
        try {
            IServerCenterService serverCenterService = new ServerCenterService(8088);
            serverCenterService.register(IHelloService.class, HelloService.class);
            serverCenterService.start();
            System.out.println("serverCenter start");
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
