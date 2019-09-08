package com.thomas.client;

import java.io.IOException;
import gnu.io.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Observable;
import java.util.Observer;
import java.util.TooManyListenersException;

public class SerialCommNew extends Observable implements SerialPortEventListener, Runnable {
	private String portName = "";
	private int iIndex;
	private CommPortIdentifier commPort;
	private SerialPort serialPort;
	private InputStream inputStream;
	private OutputStream outputStream;
	private Observer parentObject;

	// 串口读取的变量
	private byte[] readBuffer;;
	private int iLength;
	private int iOffSet;

	public SerialCommNew(String portName, int index, Observer o) {
		this.portName = portName;
		this.iIndex = index;
		this.parentObject = o;
		readBuffer = new byte[1024 * 1000];
		iLength = 0;
		iOffSet = 0;
		selectPort(portName);

	}

	/**
	 * @方法名称 :selectPort
	 * @功能描述 :选择一个端口，比如：COM1
	 * @返回值类型 :void
	 * @param portName
	 */
	@SuppressWarnings("rawtypes")
	public void selectPort(String portName) {

		this.commPort = null;
		CommPortIdentifier cpid;
		Enumeration en = CommPortIdentifier.getPortIdentifiers();

		while (en.hasMoreElements()) {
			cpid = (CommPortIdentifier) en.nextElement();
			if (cpid.getPortType() == CommPortIdentifier.PORT_SERIAL && cpid.getName().equals(portName)) {
				this.commPort = cpid;
				break;
			}
		}

		openPort();
	}

	/**
	 * @throws IOException
	 * @throws TooManyListenersException
	 * @throws UnsupportedCommOperationException
	 * @方法名称 :openPort
	 * @功能描述 :打开SerialPort
	 * @返回值类型 :void
	 */
	private void openPort() {
		if (commPort == null) {
			return;
		} else {
			System.out.println("端口选择成功，当前端口：" + commPort.getName() + ",现在实例化 SerialPort:");

			try {
				serialPort = (SerialPort) commPort.open("Majiang", 2000);
				try {
					inputStream = serialPort.getInputStream();
					outputStream = serialPort.getOutputStream();
				} catch (IOException e) {
					e.printStackTrace();
				}

				serialPort.addEventListener(this);
				serialPort.notifyOnDataAvailable(true);

				serialPort.setSerialPortParams(115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

				// 添加tileSetStatus为事件处理类
				addObserver((Observer) parentObject);

				System.out.println("实例 SerialPort 成功！");
			} catch (gnu.io.PortInUseException e) {
				throw new RuntimeException(String.format("端口'%1$s'正在使用中！", commPort.getName()));
			} catch (TooManyListenersException e) {
				throw new RuntimeException(String.format("端口'%1$s'监听太多！", commPort.getName()));
			} catch (UnsupportedCommOperationException e) {
				throw new RuntimeException(String.format("端口'%1$s'不支持该操作！", commPort.getName()));
			}

		}
	}

	/**
	 * 数据接收的监听处理函数
	 */

	public void serialEvent(SerialPortEvent arg0) {
		switch (arg0.getEventType()) {
		case SerialPortEvent.BI:/* Break interrupt,通讯中断 */
		case SerialPortEvent.OE:/* Overrun error，溢位错误 */
		case SerialPortEvent.FE:/* Framing error，传帧错误 */
		case SerialPortEvent.PE:/* Parity error，校验错误 */
		case SerialPortEvent.CD:/* Carrier detect，载波检测 */
		case SerialPortEvent.CTS:/* Clear to send，清除发送 */
		case SerialPortEvent.DSR:/* Data set ready，数据设备就绪 */
		case SerialPortEvent.RI:/* Ring indicator，响铃指示 */
		case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
			/** Output buffer is* empty，输出缓冲区清空 */
			break;
		case SerialPortEvent.DATA_AVAILABLE:/*
											 * Data available at the serial port，端口有可用数据。读到缓冲数组，输出到终端
											 */

			break;
		}
	}

	public void changeMessage(byte[] message, int length) {
		byte[] temp = new byte[length];
		System.arraycopy(message, 0, temp, 0, length);
		String s=FormatToString(temp);
		System.out.println("ReceiveStep1:" +s );
		if (s.trim().endsWith("05 AB")) {
			System.out.println("收到测试结束");
			return;
		}
		setChanged();
		notifyObservers(temp);
	}

	public void run() {
		try {
			while (inputStream != null) {
				if ((iLength = inputStream.available()) > 0) {
					inputStream.read(readBuffer, iOffSet, iLength);
					iOffSet += iLength;
//					System.out.println("receivce:" + iLength + "===iOffSet:" + iOffSet);
					if (iOffSet < 8) {// 接收不全
						continue;
					} else {// 大于等于1个包
						while (iOffSet >= 8) {
							byte[] temp = new byte[8];
							System.arraycopy(readBuffer, 0, temp, 0, 8);
							changeMessage(temp, 8);
							// 长度减少
							iOffSet -= 8;
//							System.out.println("iOffSet-----" + iOffSet);
							if (iOffSet == 0) {
								break;
							}
							// 剩下的数据
							byte[] temp2 = new byte[iOffSet];
							System.arraycopy(readBuffer, 8, temp2, 0, iOffSet);
							readBuffer = new byte[1024 * 1000];
							System.arraycopy(temp2, 0, readBuffer, 0, iOffSet);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * 写数据
	 */
	public final void SendData(byte[] buffer, int offset, int count) {
		if (buffer.length < (offset + count)) {
			System.out.println("Error! Data not written due to length check failure!");
			return;
		}
		try {
			outputStream.write(Arrays.copyOfRange(buffer, offset, offset + count));
			System.out.println("Send :" + FormatToString(buffer));
		} catch (IOException e) {
			throw new RuntimeException("向端口发送信息时出错：" + e.getMessage());
		} finally {
			try {
				outputStream.close();
			} catch (Exception e) {
			}
		}
	}

	public final boolean SendPacket(byte[] packetData) {// not use
		try {
			outputStream.write(packetData);
			return true;
		} catch (IOException e) {
			throw new RuntimeException("向端口发送信息时出错：" + e.getMessage());
		} finally {
			try {
				outputStream.close();
				return false;
			} catch (Exception e) {
			}
		}
	}

//	public final void Stop() {
//		try {
//			serialPort.close();
//			serialPort = null;
//			commPort = null;
//		} catch (RuntimeException e) {
//		}
//	}
	
	public void Stop() {
		if (serialPort != null) {
			serialPort.notifyOnDataAvailable(false);
			serialPort.removeEventListener();
			if (inputStream != null) {
				try {
					inputStream.close();
					inputStream = null;
				}
				catch (IOException e) {}
			}
			if (outputStream != null) {
				try {
					outputStream.close();
					outputStream = null;
				}
				catch (IOException e) {}
			}
			serialPort.close();
			serialPort = null;
		}
	}


	public static String FormatToString(byte[] orig) {
		String result = "";
		for (byte by : orig) {
			result += String.format("%02X", by) + " ";
		}
		return result;
	}
}