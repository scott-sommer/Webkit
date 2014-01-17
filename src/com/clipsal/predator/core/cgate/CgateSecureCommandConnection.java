package com.clipsal.predator.core.cgate;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.security.KeyStore;
import javax.net.SocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import com.clipsal.predator.core.*;


/**
 * Establish a connection to the secure C-Gate SSL command port
 * @author carrd
 */
public class CgateSecureCommandConnection extends CgateCommandConnection {

    private static final String PASSWORD = "amazing";
	private static final String ALGORITHM = "sunx509";
	
	public CgateSecureCommandConnection(String name,String address, int port) throws ConnectionException {
		super(name,address,port);
	}

	@Override
	public void connect() throws ConnectionException {
        String keyStoreFile = "/lib/cis.ks";
        try {
        	FileInputStream inputstream = new FileInputStream("C:/Clipsal/C-Gate2/key/cis.ks");

        	//InputStream inputstream = this.getClass().getResourceAsStream(keyStoreFile);
        	
        	if ( inputstream == null ) {
        		throw new FileNotFoundException( keyStoreFile + " not found.");
        	}
        	KeyStore ks = KeyStore.getInstance("JKS");
			ks.load(inputstream, PASSWORD.toCharArray());
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(ALGORITHM);
			kmf.init(ks, PASSWORD.toCharArray());
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(ALGORITHM);
			tmf.init(ks);
            SSLContext sslc = SSLContext.getInstance("TLS");
			sslc.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
			SocketFactory sf = sslc.getSocketFactory();
			// Then we get the socket from the factory and treat it
			// as if it were a standard (plain) socket.
			socket = sf.createSocket(address, port);
	        out = new PrintWriter(socket.getOutputStream(), true);
	        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }
		catch (Exception e) {
			// will get here if c-gate isn't running
			throw new ConnectionException("Cannot connect to address: " + address + " port: " + Integer.toString(port) + "\n" + 
				e.getMessage() );
		}
        
        // capture the initial C-Gate output and move it to another array
		processInitialConnection();

        // all done
    	connected = true;
    }

	
	/*
	 * For some reason, InputStream.ready() does not work on SSL connections.
	 * So we have to override this and pretend the stream is always ready.
	 */
	@Override
	protected boolean isInputStreamReady() throws IOException {
		return true;
	}
	
}
