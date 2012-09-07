import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class USBClient
{
	public static void main(String[] args) throws IOException
	{
		Socket echoSocket = null;
		ObjectOutputStream out = null;
		ObjectInputStream in = null;
		String message = "";

		// Create socket connection with host address as localhost and port number with 38300 
		try
		{
			echoSocket = new Socket("localhost", 38300);
			out = new ObjectOutputStream(echoSocket.getOutputStream());
			out.flush();
			in = new ObjectInputStream(echoSocket.getInputStream());

			// Communicating with the server
			try
			{
				message = (String) in.readObject();
				System.out.println("server>" + message);
			}
			catch (ClassNotFoundException classNot)
			{
				System.err.println("data received in unknown format");
			}
		}
		catch (UnknownHostException e)
		{
			System.err.println("Don't know about host: LocalHost.");
			System.exit(1);
		}
		catch (IOException e)
		{
			System.err.println("Couldn't get I/O for " + "the connection to: LocalHost:");
			System.exit(1);
		}
		finally
		{
			// Closing connection
			try
			{
				in.close();
				out.close();
				if (echoSocket != null)
				{
					echoSocket.close();
				}
			}
			catch (IOException ioException)
			{
				ioException.printStackTrace();
			}
		}
	}
}
