package com.jiabin.exercise.rpc.service;

import java.io.IOException;

/**
 * Created by jiabi on 2018/3/15.
 */
public interface IServerCenterService {
    public void stop();

    public void start() throws IOException;

    public void register(Class serviceInterface, Class serviceImpl);

    public boolean isRunning();

    public int getPort();
}
