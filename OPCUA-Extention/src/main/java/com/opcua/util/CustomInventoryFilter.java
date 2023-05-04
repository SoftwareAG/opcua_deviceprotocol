package com.opcua.util;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cumulocity.sdk.client.ParamSource;
import com.cumulocity.sdk.client.inventory.InventoryFilter;

public class CustomInventoryFilter extends InventoryFilter {
	@ParamSource
	private String query;
//	
//	@ParamSource
//	private String ids;

	public static CustomInventoryFilter searchInventory() {
		return new CustomInventoryFilter();
	}

	public CustomInventoryFilter byQuery(final String query) {
		if (!query.isEmpty()) {
			setOrAddQuery(query);
		}
		return this;
	}

	@Override
	public String getType() {
		return query;
	}

	public CustomInventoryFilter isDevice() {
		final String isDeviceQuery = "has('c8y_IsDevice')";
		setOrAddQuery(isDeviceQuery);
		return this;
	}

	public CustomInventoryFilter hasPosition() {
		final String hasPositionQuery = "has('c8y_Position')";
		setOrAddQuery(hasPositionQuery);
		return this;
	}
	
	public CustomInventoryFilter hasFragment(String fragment) {
		if (!fragment.isEmpty()) {
			final String hasFragment = "has("+fragment+")";
			setOrAddQuery(hasFragment);
		}
		return this;
	}

	public CustomInventoryFilter addDynamicFragment(List<String>fragment) {
		if (fragment.size() > 0) {
			Iterator<String> it = fragment.iterator();
			while (it.hasNext()) {
				String workerId = it.next();
				final String hasFragment = "has(workerList."+workerId+")";
				setOrAddQuery(hasFragment);
			}
		}
		return this;
	}
	
	public CustomInventoryFilter withParents(boolean value) {
		final String hasPositionQuery = "withParents="+value;
		setParam(hasPositionQuery);
		return this;
	}
	
	public CustomInventoryFilter orderBy(String value, String order) {
		final String query = " $orderby="+value+ " "+ (order!=null?order:"");
		this.query = this.query + query;
		return this;
	}
	
	public CustomInventoryFilter withChildren(boolean value) {
		final String hasPositionQuery = "withChildren="+value;
		setParam(hasPositionQuery);
		return this;
	}
	
	private void setParam(String param) {
		if (this.query == null || this.query.isBlank()) {
			this.query = param;
		} else {
			this.query = this.query + "&" + param;
		}
	}
	
	
	private void setOrAddQuery(final String query) {
		if (StringUtils.isBlank(this.query)) {
			this.query = "$filter="+query;
		} else {
			this.query = this.query + " and " + query;
		}
	}
}
