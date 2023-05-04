package com.opcua.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;


@Service
public class CSVMapping {
	
	public List<HashMap<String, String>> colValMapping(List<String[]> lines, ArrayList<String> headers){
		List<HashMap<String, String>> listOfMapLine = new ArrayList<HashMap<String, String>>();
		
		for(int i = 0;lines.size()>i;i++) {
			HashMap<String, String> pair = new HashMap<String, String>();
			if(lines.get(i).length != 0) {
				for(int j = 0;headers.size()>j;j++) {
					if(j<lines.get(i).length) {
						pair.put(headers.get(j), lines.get(i)[j]);
					}else {
						pair.put(headers.get(j), "");
					}
				}
			}
			listOfMapLine.add(pair);
		}
		return listOfMapLine;
	}
}
