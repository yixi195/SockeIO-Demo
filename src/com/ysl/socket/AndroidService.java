package com.ysl.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 模拟服务器  可直接运行
 * @author 爱学习di年轻人
 * 2016年11月24日 下午3:34:47
 */
public class AndroidService
{
	
	public static void main(String[] args) throws IOException
	{
		ServerSocket serivce = new ServerSocket(30003);  
		System.out.println("服务器已运行...");
        while (true) {  
            //等待客户端连接  
            Socket socket = serivce.accept();  
            new Thread(new AndroidRunable(socket)).start();  
        } 
	}
}
