package com.opcua.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.opcua.util.CSVMapping;
import com.opcua.util.CustomInventoryFilter;
import com.cumulocity.model.ID;
import com.cumulocity.rest.representation.identity.ExternalIDRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.identity.IdentityApi;
import com.cumulocity.sdk.client.inventory.InventoryApi;
import com.cumulocity.sdk.client.inventory.ManagedObjectCollection;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class OPCUA_IDAdditionService {

	private final InventoryApi inventory;
	private final IdentityApi id;
	
	@Autowired
	private CSVMapping csvMapping;
	
	public String readFile(MultipartFile file) {
 		String delimiter = ",";
		String line;
		String type = "c8y_OpcuaDevice";
		List<String[]> lines = new ArrayList<String[]>();
		
		
		//Enhanced By Rahmat 16/11/2022
		ArrayList<String> c8MandatoryFields = new ArrayList<String>(Arrays.asList(
				 "deviceid", "idtype"));
		log.info("File read start");
		try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
			//Clean Non ASCII Character
			log.info("Reading the header...");
			ArrayList<String> headers = new ArrayList<String>(
					Arrays.asList(br.readLine().toLowerCase().replaceAll("[^\\x00-\\x7F]", "" ).split(delimiter)));
			log.info("Reading the header completed");
			if ((headers.containsAll(c8MandatoryFields))) {
				line = br.readLine();
				
				log.info("Validating the data...");
				// Validate Header & Column Size Match
				while (line != null && !line.isEmpty()) {
					String[] values = line.split(delimiter, -1);
					if (headers.size() != values.length) {
						return "Header and Data size mismatch";
					}
					lines.add(values);
					line = br.readLine();
				}
				log.info("Validating the data completed");
			
				log.info("Mapping the header with data...");
				//MappingHeader & Value
				List<HashMap<String, String>> csvMap = csvMapping.colValMapping(lines, headers);
				
				log.info("Mapping the header with data completed");
				
				log.info("Checking the existence of the devices");
				for (HashMap<String, String> map: csvMap) {
					String deviceId = map.get("deviceid");
					String idType = map.get("idtype");
					
					List<ManagedObjectRepresentation> moList = queryMo(deviceId, type);
					ManagedObjectRepresentation foundMo = null;
					if (moList.size() > 0) {
						log.info("{} Managed object with name: {} and type: {} found", moList.size(), deviceId, idType);
						foundMo = moList.get(0);
						checkExternalId(deviceId, idType, foundMo);
					} else {
						log.info("Managed object with name: {} and type: {} not present", deviceId, idType);
					}
				}
				log.info("File read complete");
			} else {
				return "Mandatory headers missing";
			}
			
		} catch (Exception e) {
			log.error("{}", e);
			return "Error in creation";
		}
		return "Successfully created";
	}
	
	private List<ManagedObjectRepresentation> queryMo(String name, String type) {
		CustomInventoryFilter ckParent = CustomInventoryFilter.searchInventory()
				.byQuery("name eq '*" + name + "*'").byQuery("type eq '" + type + "'");
		ManagedObjectCollection groupMo = inventory.getManagedObjectsByFilter(ckParent);
		List<ManagedObjectRepresentation> mo = groupMo.get(2000)
				.getManagedObjects();
		return mo;
	}
	
	private void generateExternalId(ManagedObjectRepresentation managedObject,String deviceId,String idType) {
		ExternalIDRepresentation eir = new ExternalIDRepresentation();
		eir.setExternalId(deviceId);
		eir.setType(idType);
		eir.setManagedObject(managedObject);
		log.info("Creating external id: {} and type: {}...", deviceId, idType);
		id.create(eir);
		log.info("Created external id: {} and type: {}", deviceId, idType);
	}
	
	private ManagedObjectRepresentation checkExternalId(String deviceID, String idType, ManagedObjectRepresentation managedObject) {
		ID newId = new ID();
		newId.setType(idType);
		newId.setValue(deviceID);
		
		try {
			log.info("Checking the presence of external id: {} and type: {}", deviceID, idType);
			ExternalIDRepresentation devId = id.getExternalId(newId);
			log.info("External id: {} and type: {} found", deviceID, idType);
			return devId.getManagedObject();
		} catch (Exception ex) {
			log.error("No identity with type: {} and ID: {}",idType, deviceID);
			log.error("{}",ex);
			if (ex.getMessage().contains("404")) {

				if (managedObject != null) {
					generateExternalId(managedObject, deviceID, idType);
				}
				return managedObject;
			}
			return null;
		}
	}
}