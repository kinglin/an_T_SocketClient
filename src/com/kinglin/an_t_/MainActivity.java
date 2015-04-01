package com.kinglin.an_t_;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

		//设置handler方便线程控制UI
		myHandler = new MyHandler();
		final Socket socket = new Socket();
		
		ConnectThread connectThread = new ConnectThread(socket);
		connectThread.start();
		
		//发送按钮，新开线程
		send.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//网络相关工作要在线程中进行
				ClientThread clientThread = new ClientThread(socket);
				clientThread.start();
			}
		});
	}

	//发送线程
	public class ClientThread extends Thread{
		
		Socket socket = null;
		
		ClientThread(Socket socket){
			this.socket = socket;
		}
		
		//重写run函数
		public void run() {
			String msgFromServer = null;

			
			String sentence = edittext.getText().toString();
			
			Person person = new Person();
			person.id = 1;
			person.name = sentence;
			
			try {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("id", person.id);
				jsonObject.put("name", person.name);
				
				//向服务器发送数据
				BufferedWriter bw = null;
				bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
				bw.write(jsonObject.toString()+"\n");
//				bw.write(sentence);
				bw.flush();

				//从服务器接收数据
				BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				msgFromServer = br.readLine();
				JSONTokener jsonTokener = new JSONTokener(msgFromServer);
				JSONObject jsonObject2 = (JSONObject) jsonTokener.nextValue();
				
				//向handler发送消息
				Message msg = Message.obtain();
				msg.obj = jsonObject2;
				myHandler.sendMessage(msg);
				
				//关闭输入输出流
//				bw.close();
//				br.close();
//				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			} 
		}
	}

	//用handler处理消息请求 
	public class MyHandler extends Handler{
		
		public MyHandler() {  
            super(); 
        }  
		
		//重写handlerMessage函数，控制UI更新
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			JSONObject jsonObject = (JSONObject) msg.obj;
			
			try {
				tv.setText("server say: id is "+jsonObject.getString("id")+",name is "+jsonObject.getString("name"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void ConnecttoServer(Socket socket, String ip, int port){
		
		InetSocketAddress ipAddress = null;
		int timeout = 3000;
		
		ipAddress = new InetSocketAddress(ip, port);
		try {
			socket.connect(ipAddress, timeout);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public class ConnectThread extends Thread {
		
		Socket socket = null;
		ConnectThread(Socket socket){
			this.socket = socket;
		}
		
		public void run() {
			ConnecttoServer(socket, "115.156.249.73", 12345);
		}
	}
	
	public class Person{
		int id;
		String name;
	}
	
}



























