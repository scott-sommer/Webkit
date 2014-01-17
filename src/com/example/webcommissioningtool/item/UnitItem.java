package com.example.webcommissioningtool.item;

import java.util.Arrays;
import java.util.Collection;

import com.vaadin.data.Item;
import com.vaadin.data.Property;

public class UnitItem implements Item {
	
	public enum PropertyID {
		UNIT_TYPE, SERIAL_NUMBER, ADDRESS;
	}

	private Property<String> unitType;
	private Property<String> serialNumber;
	private Property<Integer> address;

	@Override
	public Property<?> getItemProperty(Object id) {
		
		Property<?> prop = null;
		
		if(id instanceof PropertyID)
		{
			PropertyID propertyId = (PropertyID) id;

			switch(propertyId){
			case UNIT_TYPE:
				prop = unitType;
				break;
			case SERIAL_NUMBER:
				prop = serialNumber;
				break;
			case ADDRESS:
				prop = address;
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
