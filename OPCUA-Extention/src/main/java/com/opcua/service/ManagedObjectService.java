package com.opcua.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.opcua.util.CumulocitySDKCall;
import com.opcua.util.CustomInventoryFilter;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.inventory.ManagedObjectCollection;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class ManagedObjectService {
	
	@Autowired
	private final CumulocitySDKCall cumulocitySDKCall;

	public String updateInterval(String type, int period) {
		List<ManagedObjectRepresentation> managedObjectRepresentationList = new ArrayList<ManagedObjectRepresentation>();
		try {
			managedObjectRepresentationList = getMoOfType(type);

			if (managedObjectRepresentationList != null) {
				managedObjectRepresentationList.forEach(managedObjectRepresentation -> {
					ManagedObjectRepresentation updateMo = new ManagedObjectRepresentation();
					Map<String, Integer> responseInterval = new HashMap<String, Integer>();
					responseInterval.put("responseInterval", period);
					updateMo.setProperty("c8y_RequiredAvailability", responseInterval);
					updateMo.setId(managedObjectRepresentation.getId());
					cumulocitySDKCall.updateInventory(updateMo);
					log.info("Inventory updated");
				});

			} else {
				return "No managed object found for the type: " + type;
			}
			return String.valueOf(managedObjectRepresentationList.size()) + " Mo found and updated";
		} catch (Exception e) {
			log.error("Error while updating the required interval {}", e);
			return null;
		}
		
	}
	
	public List<ManagedObjectRepresentation> getMoOfType(String type) {
		try {
			CustomInventoryFilter filterToRetrieveTask = CustomInventoryFilter.searchInventory()
					.byQuery("type eq '" + type + "'");
			log.debug("Before: Calling inventory to check Mo of type: " + type);
			ManagedObjectCollection taskManagedObjectCollection = cumulocitySDKCall
					.getManagedObjectsByFilter(filterToRetrieveTask);
			List<ManagedObjectRepresentation> taskManagedObjects = taskManagedObjectCollection.get(2000)
					.getManagedObjects();
			log.debug("After: called inventory to check Mo of type: " + type);
			if (taskManagedObjects.isEmpty()) {
				return null;
			} else {
				return taskManagedObjects;
			}
		} catch (Exception ex) {
			log.error("Error while fetching the inventory of type: {}", type);
			log.error("", ex);
			return null;
		}
	}
}
