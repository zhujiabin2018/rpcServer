package com.jiabin.exercise.rpc.impl;

import com.jiabin.exercise.rpc.service.IServerCenterService;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by jiabi on 2018/3/15.
 */
public class ServerCenterService implements IServerCenterService {
    private static ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private static HashMap<String, Class> serviceRegistry = new HashMap<String, Class>();
    private static boolean isRunning = false;
    private static int port;

    public ServerCenterService(int port) {
        this.port = port;
    }

    @Override
    public void stop() {
        isRunning = false;
        executorService.shutdown();
    }

    @Override
    public void start() throws IOException {
        ServerSocket server = new ServerSocket();
        server.bind(new InetSocketAddress("localhost", port));
        System.out.println("start server");
        try {
            while (true){
                //1.�����ͻ��˵�TCP���ӣ��ӵ�TCP���Ӻ����װ��task�����̳߳�ִ��
                executorService.execute(new ServiceTask(server.accept()));
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            server.close();
        }
        System.out.println("start server3");
    }

    @Override
    public void register(Class serviceInterface, Class serviceImpl) {
        serviceRegistry.put(serviceInterface.getName(), serviceImpl);
    }

    @Override
    public boolean isRunning() {
        return false;
    }

    @Override
    public int getPort() {
        return 0;
    }

    private static class ServiceTask implements Runnable{
        Socket client = null;

        public ServiceTask(Socket client) {
            this.client = client;
        }

        public void run(){
            ObjectInputStream input = null;
            ObjectOutputStream output = null;
            try {
                // 2.���ͻ��˷��͵����������л��ɶ��󣬷�����÷���ʵ���ߣ���ȡִ�н��
                input = new ObjectInputStream(client.getInputStream());
                String serviceName = input.readUTF();
                String methodName = input.readUTF();
                Class<?>[] parameterTypes = (Class<?>[])input.readObject();
                Object[] arguments = (Object[]) input.readObject();
                Class serviceClass = serviceRegistry.get(serviceName);
                if(serviceClass == null)
                    throw new ClassNotFoundException(serviceName + " not found");
                Method method = serviceClass.getMethod(methodName, parameterTypes);
                Object result = method.invoke(serviceClass.newInstance(), arguments);

                // 3.��ִ�н�������л���ͨ��socket���͸��ͻ���
                output = new ObjectOutputStream(client.getOutputStream());
                output.writeObject(result);
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                if(input != null){
                    try {
                        input.close();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
                if(output != null){
                    try {
                        output.close();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
                if(client != null){
                    try {
                        client.close();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
