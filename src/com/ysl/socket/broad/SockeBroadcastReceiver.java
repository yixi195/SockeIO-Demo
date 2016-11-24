package com.ysl.socket.broad;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.ysl.socket.MainActivity;
import com.ysl.socket.interfaces.SocketCallback;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

/**
 * Socket长链接核心类
 * 1.定时与服务器心跳保持(时间可以自定义)
 * 2.断线自动重连
 * 3.连接成功、断开连接、收到消息回调处理
 * 4.消息发送状态获取(成功true or 失败false)
 * 5.注册广播
 * @author 爱学习di年轻人
 *  2016年11月22日 下午4:23:17
 */
public class SockeBroadcastReceiver extends BroadcastReceiver
{
	private static final String TAG = "ysl";
	/** 心跳检测时间 */
	private static final long HEART_BEAT_RATE = 10 * 1000;
	/** 主机IP地址 */
	public static final String HOST = "192.168.1.109";
	/** 端口号 */
	public static final int PORT = 30003;
	/** 超时设置 **/
	public static final int SOCKET_TIME_OUT = 10 * 1000;
	/** 消息广播 */
	public static final String MESSAGE_ACTION = "com.ysl.message_ACTION";
	/** 心跳广播 */
	public static final String HEART_BEAT_ACTION = "com.ysl.heart_beat_ACTION";
	/** 线程池 **/
	private ExecutorService executorService = Executors.newFixedThreadPool(5);
	/** 为了节省开销：如果最后发送时间间隔不超过心跳时间则不发心跳 */
	private long sendTime = 0L;

	private ReadThread mReadThread;
	private MainActivity mMainActivity;
	private Socket socket;
	private boolean isConnected = true; //是否处于连接状态
	private SocketCallback callback; // 回调

	/**
	 * 初始化
	 * @param mMainActivity
	 */
	public SockeBroadcastReceiver(MainActivity mMainActivity)
	{
		this.mMainActivity = mMainActivity;
		initSocket();
	}

	/**
	 * 设置状态回调
	 * @param callback
	 */
	public void setSocketCallback(SocketCallback callback){
		this.callback = callback;
	}

	/**
	 * 初始化Socake
	 */
	public void initSocket()
	{
		executorService.execute(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					socket = new Socket();
					SocketAddress socAddress = new InetSocketAddress(HOST,PORT);
					socket.connect(socAddress, SOCKET_TIME_OUT);
					if (callback != null)  //链接成功
					{  
						isConnected = true;
						callback.connected();
					}
					mReadThread = new ReadThread();
					mReadThread.start();
					mHandler.postDelayed(heartBeatRunnable, HEART_BEAT_RATE);// 初始化成功后，就准备发送心跳包
				} catch (UnknownHostException e)
				{
					if (callback != null)  //链接失败
					{  
						isConnected = false;
						callback.disConnected();
					}
					e.printStackTrace();
				} catch (IOException e)
				{
					if (callback != null)  //链接失败
					{  
						isConnected = false;
						callback.disConnected();
					}
					e.printStackTrace();
				}
			}
		});
		
	}

	@Override
	public void onReceive(Context context, Intent intent)
	{
		String action = intent.getAction();
		if (action.equals(MESSAGE_ACTION)){ // 消息广播
			String stringExtra = intent.getStringExtra("message");
//			Log.i(TAG, "收到服务器消息：" + stringExtra);
			if (callback != null)
			{
				callback.receiveMessage(stringExtra);
			}
		} else if (action.equals(HEART_BEAT_ACTION)){// 心跳广播
			Log.i("ysl", "收到服务器正常心跳。");
		}

	}

	/**
	 * 发送心跳包
	 */
	private Handler mHandler = new Handler();
	private Runnable heartBeatRunnable=new Runnable(){
		@Override 
		public void run(){
			if(System.currentTimeMillis()-sendTime>=HEART_BEAT_RATE){
				boolean isSuccess=sendMsg("xt");// 可以随意与服务器定义好内容。。。
				if(!isSuccess){ // 如果发送不成功重连
					mHandler.removeCallbacks(heartBeatRunnable);
					mReadThread.release();
					releaseLastSocket(socket);
					if (callback != null)
					{
						callback.disConnected();
					}
				}
			}
				mHandler.postDelayed(this,HEART_BEAT_RATE);
			}
		};

	/**
	 * 发送消息
	 * @param msg
	 * @return
	 */
	public boolean sendMsg(String msg)
	{
		if (null == socket){
			return false;
		}
		try
		{
			if (!socket.isClosed() && !socket.isOutputShutdown() && isConnected)
			{
				OutputStream os = socket.getOutputStream();
				String message = msg + "\r\n";
				os.write(message.getBytes());
				os.flush();
				sendTime = System.currentTimeMillis();// 每次发送成功数据，就改一下最后成功发送的时间，节省心跳间隔时间
				Log.i(TAG, "发送成功的时间：" + sendTime);
			} else
			{
				return false;
			}
		} catch (IOException e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 释放socket
	 * @param mSocket
	 */
	private void releaseLastSocket(Socket mSocket)
	{
		try
		{
			if (null != mSocket)
			{
				if (!mSocket.isClosed())
				{
					mSocket.close();
				}
				mSocket = null;
			}
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 接收消息
	 */
	public class ReadThread extends Thread
	{
		private boolean isStart = true;
		public void release()
		{
			isStart = false;
			releaseLastSocket(socket);
		}

		@SuppressLint("NewApi")
		@Override
		public void run()
		{
			if (null != socket && isConnected)
			{
				try
				{
					InputStream is = socket.getInputStream();
					byte[] buffer = new byte[1024 * 4];
					int length = 0;
					while (!socket.isClosed() && !socket.isInputShutdown() && isStart && ((length = is.read(buffer)) != -1))
					{
						isConnected = true;
						if (length > 0)
						{
							String message = new String(Arrays.copyOf(buffer, length)).trim();
							// 收到服务器过来的消息，就通过Broadcast发送出去
							if (message.equals("ok"))
							{// 处理心跳回复
								Intent intent = new Intent(HEART_BEAT_ACTION);
								mMainActivity.sendBroadcast(intent);
							} else
							{ // 其他消息回复
								Intent intent = new Intent(MESSAGE_ACTION);
								intent.putExtra("message", message);
								mMainActivity.sendBroadcast(intent);
							}
						}
					}
					isConnected = false;
					if (callback != null)
					{
						callback.disConnected();
					}
				} catch (IOException e)
				{
					isConnected = false;
					if (callback != null)
					{
						callback.disConnected();
					}
					e.printStackTrace();
					Log.i("ysl", "已经断开IOException...");
				}
			}
		}
	}
}
