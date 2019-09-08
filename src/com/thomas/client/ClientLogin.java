package com.thomas.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetSocketAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.UIManager;

import org.omg.CORBA.PUBLIC_MEMBER;

import com.sun.corba.se.impl.orbutil.closure.Constant;
import com.thomas.data.ConstantValue;
import com.thomas.data.LogHelper;

public class ClientLogin extends JFrame {
	private static final long serialVersionUID = 1L;


	//
	private JLabel jUserLabel;
	private JTextField jUsernameField;
	private JLabel jPassLabel;
	private JPasswordField jPasswordField;
	
	private JButton jLoginButton;
	private JPanel jPanel1;

	//
//	public static final String host = "192.168.1.106";
	public static final String host = "127.0.0.1";
	private final int port = 8001;
	//
	public static int connState = 0;
	public static ChannelFuture cf;
	//
	private ScheduledExecutorService scheduler;

	public ClientLogin() {
//		try {  
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());  
//        } catch (Exception e) {  
//            e.printStackTrace();  
//        }  
		init();

		this.setSize(300, 120);
		this.setTitle("客户端程序");
		this.setLocationRelativeTo(null);
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		try {
			enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		scheduler = Executors.newScheduledThreadPool(1);
		scheduler.scheduleWithFixedDelay(new commThread(), 0, 5, TimeUnit.SECONDS);

/*		//重启线程阻塞，否则界面不响应
		new Thread() {
            public void run() {
            	startlogin();
            }
        }.start();
*/
		
		
	}

	public void init() {
		jUserLabel = new JLabel("用户名：");
		jUsernameField = new JTextField();
		jUsernameField.setColumns(10);
		jPassLabel = new JLabel("密码：");
		jPasswordField = new JPasswordField();
		jPasswordField.setColumns(10);
		jLoginButton = new JButton("登录");

		jPanel1 = new JPanel();
		jPanel1.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 5));

//		if(ConstantValue.USERTYPE == ConstantValue.GROUPER || ConstantValue.USERTYPE == ConstantValue.PLAYER){
//			jPanel1.add(jUserLabel);
//			jPanel1.add(jUsernameField);
//		}
		jPanel1.add(jPassLabel);
		jPanel1.add(jPasswordField);
		jPanel1.add(jLoginButton);

		this.add(jPanel1, BorderLayout.CENTER);

		jLoginButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					String userString = jUsernameField.getText();
					String passString = new String(jPasswordField.getPassword());
					userLogin(passString,userString);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	public void userLogin(String password ,String username) {

		
		if (connState == 0) {
			JOptionPane.showMessageDialog(null, "无法和Server建立网络连接！", "信息",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}

			if ( password.length() != 10) {
				JOptionPane.showMessageDialog(null, "请输入10个字符的用户密码", "信息",
						JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			
		
		// 发送密码去server鉴权
		byte[] bcmdbuf = password.getBytes();
		//保存一下
		ConstantValue.strUserName = password;

		//构造发送报文，登录命令只有用户授权码
		ByteBuf cmdBuf = cf.channel().alloc().buffer();
		cmdBuf.writeShort(ConstantValue.USERTYPE);
		cmdBuf.writeShort(ConstantValue.LOGINCMD);
		cmdBuf.writeShort(ConstantValue.NULLREC);
		cmdBuf.writeBytes(bcmdbuf);//10 bytes pass
		cmdBuf.writeInt(0xFBFEBFEF);

		cf.channel().writeAndFlush(cmdBuf);

	}

	public void startlogin() {
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			// 指定 EventLoopGroup 来处理客户端事件。由于我们使用 NIO 传输，所以用到了 NioEventLoopGroup
			// 的实现
			b.group(group).channel(NioSocketChannel.class)
			// 设置服务器的地址和端口
					.remoteAddress(new InetSocketAddress(host, port))
					// 当建立一个连接和一个新的通道时，创建添加到 EchoClientHandler 实例 到 channel
					// pipeline
					.handler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel ch) {
							ByteBuf deliBuf = Unpooled.buffer();
							deliBuf.writeInt(0xFBFEBFEF);
							ch.pipeline().addLast(
									new DelimiterBasedFrameDecoder(1024,true,
											deliBuf));
							ch.pipeline().addLast(new ClientHandler());
						}
					});

			// 连接到远程;等待连接完成 也可以在这里设置服务器地址和端口
			cf = b.connect().sync();
			connState = 1;
			// 阻塞直到 Channel 关闭
			cf.channel().closeFuture().sync();
		} catch (Exception e) {
//			JOptionPane.showMessageDialog(null, "无法和Server建立连接！", "信息",
//					JOptionPane.INFORMATION_MESSAGE);
			LogHelper.log.info("客户端启动异常", e);
			connState = 0;
		} finally {
			// 调用 shutdownGracefully() 来关闭线程池和释放所有资源
			try {
				group.shutdownGracefully().sync();
				connState = 0;
			} catch (InterruptedException e) {
				e.printStackTrace();
				LogHelper.log.info("客户端退出异常", e);
			}
		}
	}

	public void hideWindow(){
		setVisible(false);
	}

	public ChannelFuture getChannelFuture(){
		return cf;
	}
}

class commThread implements Runnable{
	public void run() {
		while(true){
			if(ClientLogin.connState == 1){
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				EventLoopGroup group = new NioEventLoopGroup();
				try {
					Bootstrap b = new Bootstrap();
					// 指定 EventLoopGroup 来处理客户端事件。由于我们使用 NIO 传输，所以用到了 NioEventLoopGroup
					// 的实现
					b.group(group).channel(NioSocketChannel.class)
					// 设置服务器的地址和端口
							.remoteAddress(new InetSocketAddress(ClientLogin.host, 8001))
							// 当建立一个连接和一个新的通道时，创建添加到 EchoClientHandler 实例 到 channel
							// pipeline
							.handler(new ChannelInitializer<SocketChannel>() {
								@Override
								public void initChannel(SocketChannel ch) {
									ByteBuf deliBuf = Unpooled.buffer();
									deliBuf.writeInt(0xFBFEBFEF);
									ch.pipeline().addLast(
											new DelimiterBasedFrameDecoder(1024,true,
													deliBuf));
									ch.pipeline().addLast(new ClientHandler());
								}
							});

					// 连接到远程;等待连接完成 也可以在这里设置服务器地址和端口
					ClientLogin.cf = b.connect().sync();
					ClientLogin.connState = 1;
					// 阻塞直到 Channel 关闭
					ClientLogin.cf.channel().closeFuture().sync();
				} catch (Exception e) {
//					JOptionPane.showMessageDialog(null, "无法和Server建立连接！", "信息",
//							JOptionPane.INFORMATION_MESSAGE);
					LogHelper.log.info("客户端启动异常", e);
					ClientLogin.connState = 0;
				} finally {
					// 调用 shutdownGracefully() 来关闭线程池和释放所有资源
					try {
						group.shutdownGracefully().sync();
						ClientLogin.connState = 0;
					} catch (InterruptedException e) {
						e.printStackTrace();
						LogHelper.log.info("客户端退出异常", e);
					}
				}
			}
		}
	}
}
