package com.example.webcommissioningtool.controller;

import java.util.ArrayList;
import java.util.List;

import com.clipsal.predator.core.ConnectionException;
import com.clipsal.predator.core.Responses;
import com.clipsal.predator.core.cgate.CgateCommand;
import com.clipsal.predator.core.cgate.CgateSecureCommandConnection;
import com.example.webcommissioningtool.MainService;
import com.example.webcommissioningtool.item.Project;

public class MainController {
	
	List<Project> projects = new ArrayList<Project>();
	
	MainService mainService;

	public void initialize() {
		mainService = new MainService();
		mainService.initialize();
	}
	
	public void populateProjectsAction() throws ConnectionException {
		projects = mainService.getProjectDir();
	}

	public List<Project> getProjects() {
		return projects;
	}

	public void setProjects(List<Project> projects) {
		this.projects = projects;
	}
	
//	public List<Network> getNetworks(Project project) {
//		
//	}
	
	private void loadProjectIfRequired(Project project) {
		if(!project.isLoaded()) {
			mainService.loadProject(project);
		}
	}
}
