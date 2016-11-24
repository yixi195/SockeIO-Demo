package com.ysl.socket.interfaces;

public interface SocketCallback
{
	void connected();
	void disConnected();
	void receiveMessage(String message);
}
