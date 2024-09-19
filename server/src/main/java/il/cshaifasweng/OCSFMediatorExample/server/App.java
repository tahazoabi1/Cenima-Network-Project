package il.cshaifasweng.OCSFMediatorExample.server;

import java.io.IOException;

public class App 
{
	
	private static SimpleServer server;
    public static void main( String[] args ) throws IOException
    {
        ConnectToDataBase.initializeDatabase();
        System.out.println("Server started");
        server = new SimpleServer(3001);
        server.listen();

    }
}
