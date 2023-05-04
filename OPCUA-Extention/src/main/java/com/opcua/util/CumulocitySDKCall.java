package com.opcua.util;

import org.springframework.stereotype.Service;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.inventory.InventoryApi;
import com.cumulocity.sdk.client.inventory.InventoryFilter;
import com.cumulocity.sdk.client.inventory.ManagedObject;
import com.cumulocity.sdk.client.inventory.ManagedObjectCollection;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CumulocitySDKCall {

	private final InventoryApi inventoryApi;
	
	public ManagedObjectRepresentation createInventory(ManagedObjectRepresentation mo) {
		return inventoryApi.create(mo);
	}
	
	public ManagedObjectRepresentation updateInventory(ManagedObjectRepresentation mo) {
		return inventoryApi.update(mo);
	}
	
	public ManagedObjectCollection getManagedObjectsByFilter(InventoryFilter filter) {
		return inventoryApi.getManagedObjectsByFilter(filter);
	}
	
	public ManagedObject getManagedObjectApi(GId id) {
		return inventoryApi.getManagedObjectApi(id);
	}

}
