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

		//��handler�������̵߳���UI
		myHandler = new MyHandler();
		final Socket socket = new Socket();
		
		ConnectThread connectThread = new ConnectThread(socket);
		connectThread.start();
		
		//���ð�ť��Ӧ����
		send.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//�������ӵĹ���һ��Ҫ���߳������
				ClientThread clientThread = new ClientThread(socket);
				clientThread.start();
			}
		});
	}

	//�ͻ����߳�
	public class ClientThread extends Thread{
		
		Socket socket = null;
		
		ClientThread(Socket socket){
			this.socket = socket;
		}
		
		//��дrun����
		public void run() {
			
//			Socket socket = null;
//			InetSocketAddress ipAddress = null;
//			int timeout = 3000;
			String msgFromServer = null;

			String sentence = edittext.getText().toString()+"\r\n";
			
			try {
				//�����˷�������
				BufferedWriter bw = null;
				bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
				bw.write(sentence);
				bw.flush();

				//�ӷ���˻�ȡ����
				BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				msgFromServer = br.readLine();
				
				//��ȡ�����ݺ����һ����Ϣ�����͸�handler����
				Message msg = Message.obtain();
				msg.obj = msgFromServer;
				myHandler.sendMessage(msg);
				
				//�ر�����
//				bw.close();
//				br.close();
//				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	//дһ��handler���������߳��е���Ϣ
	public class MyHandler extends Handler{
		
		public MyHandler() {  
            super(); 
        }  
		
		//��дhandlerMessage�������������ϵͳ���Զ�����
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			tv.setText("server say:"+msg.obj);
			Toast.makeText(MainActivity.this,"handle ok", 1000).show();
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
			ConnecttoServer(socket, "115.156.249.6", 12345);
		}
	}
	
}



























