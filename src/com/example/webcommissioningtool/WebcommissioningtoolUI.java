package com.example.webcommissioningtool;

import java.util.Vector;

import com.clipsal.predator.core.ConnectionException;
import com.clipsal.predator.core.cgate.CgateCommand;
import com.clipsal.predator.core.cgate.CgateCommandConnection;
import com.example.webcommissioningtool.controller.MainController;
import com.example.webcommissioningtool.item.Project;
import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.Table;
import com.vaadin.ui.Tree;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
@Theme("webcommissioningtool")
public class WebcommissioningtoolUI extends UI {
	
	public static String UNKNOWN_STR;
	
	private static int PROJECTS_TREE_WIDTH = 200;

	private String cgateName = "";
	private String cgateAddress = "localhost";
	private int cgatePort = 20023;
	
	private Tree projectTree;

	@Override
	protected void init(VaadinRequest request) {
		layoutPrototyping();
//		cgateTest();
	}
	
	protected void layoutPrototyping() {
		final VerticalLayout mainLayout = new VerticalLayout();
		final HorizontalLayout horizontalLayout = new HorizontalLayout();
		final VerticalLayout dataGridsLayout = new VerticalLayout();
		final HorizontalLayout dbTitleLayout = createTitleLayout("Units in Database (X)");
		final HorizontalLayout dbButtonsLayout = new HorizontalLayout();
		final HorizontalLayout nwTitleLayout = createTitleLayout("Units in Network (X)");
		final HorizontalLayout nwButtonsLayout = new HorizontalLayout();
		
		setContent(mainLayout);
		
		mainLayout.setMargin(true);
		mainLayout.addComponent(horizontalLayout);
		
		horizontalLayout.addComponent(createProjectsTree());
		horizontalLayout.addComponent(dataGridsLayout);
		
		dataGridsLayout.addComponent(dbTitleLayout);
		dataGridsLayout.addComponent(dbButtonsLayout);
		dataGridsLayout.addComponent(createDBTable());
		dataGridsLayout.addComponent(nwTitleLayout);
		dataGridsLayout.addComponent(nwButtonsLayout);
		dataGridsLayout.addComponent(createNWTable());
		
		MainController mainController = new MainController();
		mainController.initialize();
		
//		projectTree.setItemCaptionPropertyId(ProjectItem.PropertyID.PROJECT_NAME);
		try {
			mainController.populateProjectsAction();
			for (Project project : mainController.getProjects()) {
				projectTree.addItem(project.getName());
			}
		} catch (ConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private Tree createProjectsTree() {
		projectTree = new Tree();
		
		projectTree.setWidth(PROJECTS_TREE_WIDTH, Unit.PIXELS);
		
		return projectTree;
	}
	
	private Table createDBTable() {
		Table table = new Table();
		
		return table;
	}
	
	private Table createNWTable() {
		Table table = new Table();
		
		return table;
	}
	
	private HorizontalLayout createTitleLayout(String title) {
		final HorizontalLayout layout = new HorizontalLayout();
		
		Label label = new Label(title);
		Button quick = new NativeButton("Quick");
//		quick.setWidth(50, Unit.PIXELS);
		Button output = new NativeButton("Output");
//		output.setWidth(50, Unit.PIXELS);
		Button input = new NativeButton("Input");
//		input.setWidth(50, Unit.PIXELS);
		Button summary = new NativeButton("Summary");
//		summary.setWidth(50, Unit.PIXELS);

		layout.addComponent(label);
		layout.addComponent(quick);
		layout.addComponent(output);
		layout.addComponent(input);
		layout.addComponent(summary);
		
		return layout;
	}
	
	protected void cgateTest() {
		final VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		setContent(layout);
		
		Table table = new Table();
		
		table.setWidth(100, Unit.PERCENTAGE);
		
//		table.setColumnHeaders(new String[]{"Type", "Address"});

//		table.addContainerProperty("Type", String.class, UNKNOWN_STR);
//		table.addContainerProperty("Address", String.class, UNKNOWN_STR);
		table.addContainerProperty("Name", String.class, UNKNOWN_STR);
		table.addContainerProperty("SendString", String.class, UNKNOWN_STR);
		

//		table.addItem(new Object[]{"RELDN8B", "01"}, new Integer(1));
//		table.addItem(new Object[]{"KEYGL5", "02"}, new Integer(2));
//		table.addItem(new Object[]{"KEYM1", "03"}, new Integer(3));
		
		try {
//			CgateSecureCommandConnection connection = new CgateSecureCommandConnection(cgateName, cgateAddress, cgatePort);
			CgateCommandConnection connection = new CgateCommandConnection(cgateName, cgateAddress, cgatePort);
			connection.connect();
			
//			connection.addListener(new CgateListener(){});
			connection.send(new CgateCommand("GETSTATUS"));
			
			Vector<CgateCommand> commands = connection.getCommands();
			for(int i = 0; i < commands.size(); i++) {
				CgateCommand command = commands.get(i);
				
//				UnitItem unitItem = new UnitItem();
//				unitItem.fromUnit(unit);
				table.addItem(command);
//				table.addItem(new Object[]{command.getName(), command.getSendString()}, new Integer(i));
			}
		} catch (ConnectionException e) {
			// TODO Auto-generated catch block
			layout.addComponent(new Label(e.toString()));
			e.printStackTrace();
		}
		
		table.setSelectable(true);
		
		layout.addComponent(table);

		Button button = new Button("Click Me");
		button.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				layout.addComponent(new Label("Thank you for clicking"));
			}
		});
		layout.addComponent(button);
	}

}