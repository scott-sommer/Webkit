package com.example.webcommissioningtool.item;

import java.util.Arrays;
import java.util.Collection;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.ObjectProperty;

public class ProjectItem implements Item {
	
	public enum PropertyID {
		PROJECT_NAME;
	}

	private Property<String> name;
	
	public ProjectItem(Project project) {
		this.name = new ObjectProperty<String>(project.getName());
	}

	@Override
	public Property<?> getItemProperty(Object id) {
		
		Property<?> prop = null;
		
		if(id instanceof PropertyID)
		{
			PropertyID propertyId = (PropertyID) id;

			switch(propertyId){
			case PROJECT_NAME:
				prop = name;
				break;
			}
		}
		
		return prop;
	}

	@Override
	public Collection<?> getItemPropertyIds() {
		return Arrays.asList(PropertyID.values());
	}

	@Override
	public boolean addItemProperty(Object id, Property property)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeItemProperty(Object id)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

}
