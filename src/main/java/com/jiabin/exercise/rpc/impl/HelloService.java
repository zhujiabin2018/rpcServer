package com.jiabin.exercise.rpc.impl;

import com.jiabin.exercise.rpc.service.IHelloService;

/**
 * Created by jiabi on 2018/3/15.
 */
public class HelloService implements IHelloService {

    @Override
    public String sayHi(String name) {
        return "Hi, " + name;
    }
}
