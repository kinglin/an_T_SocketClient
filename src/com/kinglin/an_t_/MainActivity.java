package com.kinglin.an_t_;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint({ "ShowToast", "HandlerLeak" })
public class MainActivity extends ActionBarActivity {

	public EditText edittext = null;
	public Button send = null;
	public TextView tv = null;
	public MyHandler myHandler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		edittext = (EditText) findViewById(R.id.editText1);
		send = (Button) findViewById(R.id.button1);
		tv = (TextView) findViewById(R.id.textView1);

		//用handler管理处理线程调用UI
		myHandler = new MyHandler();
		
		//设置按钮响应函数
		send.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//网络连接的工作一定要在线程中完成
				ClientThread clientThread = new ClientThread();
				clientThread.start();
			}
		});
	}

	//客户端线程
	public class ClientThread extends Thread{
		
		//重写run函数
		public void run() {
			
			Socket socket = null;
			InetSocketAddress ipAddress = null;
			int timeout = 3000;
			String msgFromServer = null;

			String sentence = edittext.getText().toString()+"\r\n";
			
			try {
				//设置网络连接
				socket = new Socket();
				ipAddress = new InetSocketAddress("192.168.8.100", 12345);
				socket.connect(ipAddress, timeout);
				
				//向服务端发送数据
				BufferedWriter bw = null;
				bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
				bw.write(sentence);
				bw.flush();

				//从服务端获取数据
				BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				msgFromServer = br.readLine();
				
				//获取到数据后产生一个消息，传送给handler处理
				Message msg = Message.obtain();
				msg.obj = msgFromServer;
				myHandler.sendMessage(msg);
				
				//关闭连接
				bw.close();
				br.close();
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	//写一个handler类来处理线程中的消息
	public class MyHandler extends Handler{
		
		public MyHandler() {  
            super(); 
        }  
		
		//重写handlerMessage函数，这个函数系统会自动调用
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			tv.setText("server say:"+msg.obj);
			Toast.makeText(MainActivity.this,"handle ok", 1000).show();
		}
	}
	
}



























