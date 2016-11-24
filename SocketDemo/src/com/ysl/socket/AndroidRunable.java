package com.ysl.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;

/**
 * 测试socket用的线程
 * @author 爱学习di年轻人
 * 2016年11月18日 下午2:51:56
 */
public class AndroidRunable implements Runnable
{

	Socket socket = null;
	String str = "ok";

	public AndroidRunable(Socket socket)
	{
		this.socket = socket;
	}

	@Override
	public void run()
	{
		// 向android客户端输出hello worild
		String line = null;
		InputStream input;
		OutputStream output;
		try
		{
			// 向客户端发送信息
			output = socket.getOutputStream();
			input = socket.getInputStream();
			byte[] buffer = new byte[1024 * 4];
			int length = 0;
//			output.write(str.getBytes("UTF-8"));
//			output.flush();
			// 半关闭socket
//			socket.shutdownOutput();
			//获取客户端发来的信息
			while (!socket.isClosed() && ((length = input.read(buffer)) != -1))
			{
				String message = new String(Arrays.copyOf(buffer, length)).trim();
				System.out.print("服务器接收到的消息：\n" + message + "\n");
				if ("xt".equals(message))  //收到心跳
				{
					output.write("收到心跳".getBytes("UTF-8"));
					output.flush();
				}
			}
			// 关闭输入输出流
			output.close();
			input.close();
			socket.close();

		} catch (IOException e)
		{
			e.printStackTrace();
		}

	}
	/**
	 * input-->string
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public static String inputStream2String(InputStream in) throws IOException
	{
		StringBuffer out = new StringBuffer();
		byte[] b = new byte[4096];
		int n;
		while ((n = in.read(b)) != -1)
		{
			out.append(new String(b, 0, n));
		}
		return out.toString();
	}
}