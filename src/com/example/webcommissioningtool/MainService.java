package com.example.webcommissioningtool;

import java.util.ArrayList;
import java.util.List;

import com.clipsal.predator.core.ConnectionException;
import com.clipsal.predator.core.Response;
import com.clipsal.predator.core.Responses;
import com.clipsal.predator.core.cgate.CgateCommand;
import com.clipsal.predator.core.cgate.CgateSecureCommandConnection;
import com.example.webcommissioningtool.item.Project;

public class MainService {

	private CgateSecureCommandConnection connection;
	private Thread connectionThread;

	private String cgateName = "";
	private String cgateAddress = "localhost";
	private int cgatePort = 20123;

	public void initialize() {
		try {
			connection = new CgateSecureCommandConnection(cgateName,
					cgateAddress, cgatePort);
			connection.connect();
			connectionThread = new Thread(connection);
			connectionThread.start();
		} catch (ConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public CgateSecureCommandConnection getConnection() {
		return connection;
	}

	private Responses executeCommand(String cmd) {
		return executeCommand(cmd, 1000);
	}

	private Responses executeCommand(String cmdStr, int sleepTime) {
		Responses rsps = null;

		try {
			final CgateCommand cmd = new CgateCommand(cmdStr + "\n");
			cmd.addDefaultResponseCriteria();
			connection.send(cmd);

			Thread.sleep(sleepTime);
			rsps = cmd.getResponses();
		} catch (ConnectionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return rsps;
	}

	public List<Project> getProjectDir() {
		final List<Project> projects = new ArrayList<Project>();
		final Responses rsps = executeCommand("PROJECT DIR");

		for (final Response rsp : rsps.getResponses()) {
			final String rspTxt = rsp.getText();
			final String[] tokens = rspTxt.split("=");
			if (tokens[0].contains("project")) {
				projects.add(new Project(tokens[1]));
			}
		}

		return projects;
	}

	public void loadProject(Project project) {
		executeCommand("project load " + project.getName());
	}
}
