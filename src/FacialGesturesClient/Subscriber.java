package FacialGesturesClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Observable;

/**
 * ThreadSubscriber. A generic subscriber; get data from a network connection;
 * data is served to local consumers
 *
 * @author javiergs
 * @version 20140915
 *
 */
public class Subscriber extends Observable implements Runnable {

  private boolean stop;
  private String Ip;
  public String getIp() {
	return Ip;
}

public void setIp(String ip) {
	Ip = ip;
}

public int getPort() {
	return port;
}

public void setPort(int port) {
	this.port = port;
}

private int port;
  private String data;

  public Subscriber(String Ip, int port) {
    this.stop = false;
    this.Ip = Ip;
    this.port = port;
  }

  protected synchronized void setData(String data) {
    this.data = data;
  }

  public synchronized Object getObject() {
    return this.data;
  }

  public void stop() {
    stop = true;
  }

  @Override
  public void run() {

	boolean serverCheck = false;
	boolean serverRunning = false;
    Socket client = null;
    ObjectInputStream ois = null;
    BufferedReader input = null;
    stop = false;
    String measureLocal=null;
    
    try {
      client = new Socket(InetAddress.getByName(Ip.trim()), port); 
      input = new BufferedReader(new InputStreamReader(client.getInputStream()));      
      client.setSoTimeout(1000);
      serverCheck = true;
  	  serverRunning = true;
    } catch (IOException ex) {
      stop = true;
    }
    while (!stop) {
      System.out.println("in hello");
      try {
    	  
        measureLocal= input.readLine();
      System.out.println("in hello read");
      } catch (IOException sce) {
        measureLocal= null;
      }
      if (measureLocal == null) {
        stop = true;
        serverRunning = false;
      } else {
              System.out.println("in hello read and =" + stop + " " + measureLocal);
        setData(measureLocal);
        setChanged();
        notifyObservers();
      }
      try {
        Thread.sleep(100);
      } catch (InterruptedException ex) {
      }
  System.out.println("in hello end while" + stop);
    }
                  System.out.println("in hello end loop");
    try {
      if (ois != null) {
        ois.close();
      }
      if (input != null) {
        input.close();
      }
      if (client != null) {
        client.close();
      }
    } catch (IOException e) {
    }
    if (!serverCheck)
		setData("FAIL");
	else if (!serverRunning)
		setData("FIN");
	else
		setData("FIN");
    setChanged();
    notifyObservers();
  }

}
