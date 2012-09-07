package com.demo.usb;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class USBServerActivity extends Activity implements OnClickListener
{

	public static final String TAG = "USBCommActivity";
	public static final int TIMEOUT = 10;
	private Button btnStartServer;
	private String connectionStatus = null;
	private final String sendMsg = "Hello From Server";
	private Handler mHandler = null;
	private ServerSocket server = null;
	private Socket client = null;
	private ObjectOutputStream out;
	public static InputStream nis = null;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		btnStartServer = (Button) findViewById(R.id.btnView);
		btnStartServer.setOnClickListener(this);
		mHandler = new Handler();
	}

	/**
	 * Thread to initialize Socket connection
	 */
	private final Runnable initializeConnection = new Thread()
	{
		@Override
		public void run()
		{
			// initialize server socket
			try
			{
				server = new ServerSocket(38300);
				server.setSoTimeout(USBServerActivity.TIMEOUT * 1000);

				//attempt to accept a connection
				client = server.accept();

				out = new ObjectOutputStream(client.getOutputStream());
				USBServerActivity.nis = client.getInputStream();
				try
				{
					out.writeObject(sendMsg);
					System.out.println("client >" + sendMsg);

					byte[] bytes = new byte[1024];
					int numRead = 0;
					while ((numRead = USBServerActivity.nis.read(bytes)) >= 0)
					{
						connectionStatus = new String(bytes, 0, numRead);
						mHandler.post(showConnectionStatus);
					}
				}
				catch (IOException ioException)
				{
					Log.e(USBServerActivity.TAG, "" + ioException);
				}
			}
			catch (SocketTimeoutException e)
			{
				connectionStatus = "Connection has timed out! Please try again";
				mHandler.post(showConnectionStatus);
			}
			catch (IOException e)
			{
				Log.e(USBServerActivity.TAG, "" + e);
			}

			if (client != null)
			{
				connectionStatus = "Connection was succesful!";
				mHandler.post(showConnectionStatus);
			}
		}
	};

	/**
	 * Runnable to show pop-up for connection status
	 */
	private final Runnable showConnectionStatus = new Runnable()
	{
		//----------------------------------------
		/**
		 * @see java.lang.Runnable#run()
		 */
		//----------------------------------------
		@Override
		public void run()
		{
			Toast.makeText(USBServerActivity.this, connectionStatus, Toast.LENGTH_SHORT).show();
		}
	};

	//----------------------------------------
	/**
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	//----------------------------------------
	@Override
	public void onClick(View v)
	{
		//initialize server socket in a new separate thread
		new Thread(initializeConnection).start();
		String msg = "Attempting to connectâ€¦";
		Toast.makeText(USBServerActivity.this, msg, msg.length()).show();
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		try
		{
			// Close the opened resources on activity destroy
			USBServerActivity.nis.close();
			out.close();
			if (server != null)
			{
				server.close();
			}
		}
		catch (IOException ec)
		{
			Log.e(USBServerActivity.TAG, "Cannot close server socket" + ec);
		}
	}
}