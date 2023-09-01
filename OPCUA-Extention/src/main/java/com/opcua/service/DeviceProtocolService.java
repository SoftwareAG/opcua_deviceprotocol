package com.opcua.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.opcua.dto.AlarmCreation;
import com.opcua.dto.ApplyConstraints;
import com.opcua.dto.DeviceProtocol;
import com.opcua.dto.DeviceProtocolInput;
import com.opcua.dto.EventCreation;
import com.opcua.dto.Mapping;
import com.opcua.dto.MeasurementCreation;
import com.opcua.dto.SubscriptionParameters;
import com.opcua.dto.SubscriptionType;
import com.opcua.util.CSVMapping;
import com.opcua.util.HttpService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class DeviceProtocolService {

	@Autowired
	private CSVMapping csvMapping;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private HttpService httpService;

	public String readFile(MultipartFile file, String mode) {
		String delimiter = ",";
		String line;
		List<String[]> lines = new ArrayList<String[]>();

		List<String> c8MandatoryFields = new ArrayList<String>(
				Arrays.asList("deviceprotocolname", "tagname", "datatype", "type", "text", "text2", "unit", "enabled",
						"serverid", "servername", "subscriptiontype", "samplingperiod"));
		try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
			// Clean Non ASCII Character
			ArrayList<String> headers = new ArrayList<String>(
					Arrays.asList(br.readLine().toLowerCase().replaceAll(" ", "").split(delimiter)));

			if ((headers.containsAll(c8MandatoryFields))) {
				line = br.readLine();

				// Validate Header & Column Size Match
				while (line != null && !line.isEmpty()) {
//					List<String> values = Arrays.asList(line.split(delimiter));
					String[] values = line.split(delimiter, -1);
					if (headers.size() != values.length) {
						return "Header and Data size mismatch";
					}
					lines.add(values);
					line = br.readLine();
				}

				// MappingHeader & Value
				List<HashMap<String, String>> csvMap = csvMapping.colValMapping(lines, headers);

				Map<String, DeviceProtocol> deviceProtocolMap = new HashMap<String, DeviceProtocol>();
				Map<String, DeviceProtocol> existingDeviceProtocolMap = new HashMap<String, DeviceProtocol>();
				Map<String, Boolean> deviceProtocolStatusMap = new HashMap<String, Boolean>();
				
				ResponseEntity<List<DeviceProtocol>> deviceProtocolsResponse = httpService.getDeviceProtocol();

				log.info("{}", deviceProtocolsResponse);
				List<DeviceProtocol> existingDeviceProtocols = deviceProtocolsResponse.getBody();

				for (DeviceProtocol deviceProtocol : existingDeviceProtocols) {
					if (!existingDeviceProtocolMap.containsKey(deviceProtocol.getName())) {
						existingDeviceProtocolMap.put(deviceProtocol.getName(), deviceProtocol);
					}
				}

				// Sorting
				for (HashMap<String, String> randomLine : csvMap) {
					DeviceProtocolInput deviceProtocolInput = objectMapper.convertValue(randomLine,
							DeviceProtocolInput.class);

					if (!deviceProtocolMap.containsKey(deviceProtocolInput.getDeviceprotocolname())) {
						DeviceProtocol deviceProtocol = new DeviceProtocol();
						if (existingDeviceProtocolMap.containsKey(deviceProtocolInput.getDeviceprotocolname())) {
							deviceProtocol = existingDeviceProtocolMap.get(deviceProtocolInput.getDeviceprotocolname());
							deviceProtocolStatusMap.put(deviceProtocolInput.getDeviceprotocolname(), true);
						}

						deviceProtocol.setName(deviceProtocolInput.getDeviceprotocolname());
						deviceProtocol.setEnabled(Boolean.parseBoolean(deviceProtocolInput.getEnabled()));
						deviceProtocol.setReferencedServerId(deviceProtocolInput.getServerid());
						deviceProtocol.setReferencedServerName(deviceProtocolInput.getServername());
						List<Mapping> mapList = deviceProtocol.getMappings();
						if (mapList == null) {
							mapList = new ArrayList<Mapping>();
						}
						Mapping map = new Mapping();
						String[] tags = deviceProtocolInput.getTagname().replace(";", ",").split(",");
						String[] protocolName = tags[tags.length - 1].split(":");
						String name = "";
						for (int i = 1; i < protocolName.length; i++) {
							name = name + protocolName[i] + (i < protocolName.length - 1 ? ":" : "");
						}
						map.setName(name);
						if (mode.equalsIgnoreCase("create")) {
							if (!mapList.contains(map)) {
								map.setBrowsePath(Arrays.asList(deviceProtocolInput.getTagname().replace(";", ",").split(",")));
								if (deviceProtocolInput.getDatatype().equalsIgnoreCase("measurement")) {
									MeasurementCreation measurementCreation = new MeasurementCreation();
									measurementCreation.setType(deviceProtocolInput.getType());
									measurementCreation.setFragmentName(deviceProtocolInput.getText());
									measurementCreation.setSeries(deviceProtocolInput.getText2());
									measurementCreation.setUnit(deviceProtocolInput.getUnit());
									map.setMeasurementCreation(measurementCreation);
								} else if (deviceProtocolInput.getDatatype().equalsIgnoreCase("event")) {
									EventCreation eventCreation = new EventCreation();
									eventCreation
											.setText("Event for " + deviceProtocolInput.getDeviceprotocolname().split("_")[1]);
									eventCreation.setType(deviceProtocolInput.getType());
									map.setEventCreation(eventCreation);
								} else if (deviceProtocolInput.getDatatype().equalsIgnoreCase("alarm")) {
									AlarmCreation alarmCreation = new AlarmCreation();
									alarmCreation.setSeverity(deviceProtocolInput.getText());
									alarmCreation
											.setText("Alarm for " + deviceProtocolInput.getDeviceprotocolname().split("_")[1]);
									alarmCreation.setType(deviceProtocolInput.getType());
									alarmCreation.setStatus(deviceProtocolInput.getText2());
									map.setAlarmCreation(alarmCreation);
								}
								mapList.add(map);
							} 
						} else if (mode.equalsIgnoreCase("delete")) {
							mapList.remove(map);
						}
						deviceProtocol.setMappings(mapList);
						SubscriptionType subscriptionType = new SubscriptionType();
						if (deviceProtocolInput.getSubscriptiontype().equalsIgnoreCase("subscription")) {
							subscriptionType.setType("Subscription");
							SubscriptionParameters subscriptionParameters = new SubscriptionParameters();
							subscriptionParameters
									.setSamplingRate(Integer.valueOf(deviceProtocolInput.getSamplingperiod()));
							subscriptionParameters.setDiscardOldest(true);
							subscriptionParameters.setDeadbandType("None");
							subscriptionParameters.setDataChangeTrigger("StatusValueTimestamp");
							subscriptionParameters.setQueueSize(10);
							subscriptionType.setSubscriptionParameters(subscriptionParameters);
						} else if (deviceProtocolInput.getSubscriptiontype().equalsIgnoreCase("cyclicread")) {
							subscriptionType.setType("Cyclicread");
							subscriptionType.setRate(0);
						}
						deviceProtocol.setSubscriptionType(subscriptionType);

						ApplyConstraints applyConstraint = deviceProtocol.getApplyConstraints();
						if (applyConstraint != null) {
							List<String> constraint = applyConstraint.getMatchesServerIds();
							if (constraint.contains(deviceProtocolInput.getServerid())) {
								constraint.add(deviceProtocolInput.getServerid());
								applyConstraint.setMatchesServerIds(constraint);
							}
						} else {
							applyConstraint = new ApplyConstraints();
							List<String> constraint = new ArrayList<String>();
								constraint.add(deviceProtocolInput.getServerid());
								applyConstraint.setBrowsePathMatchesRegex("");
								applyConstraint.setMatchesNodeIds(new ArrayList<String>());
								applyConstraint.setServerObjectHasFragment("");
								applyConstraint.setMatchesServerIds(constraint);
						}
						deviceProtocol.setApplyConstraints(applyConstraint);
						deviceProtocolMap.put(deviceProtocolInput.getDeviceprotocolname(), deviceProtocol);
					} else {
						DeviceProtocol deviceProtocol = deviceProtocolMap
								.get(deviceProtocolInput.getDeviceprotocolname());
						List<Mapping> mapList = deviceProtocol.getMappings();
						if (mapList == null) {
							mapList = new ArrayList<Mapping>();
						}
						Mapping map = new Mapping();
						String[] tags = deviceProtocolInput.getTagname().replace(";", ",").split(",");
						String[] protocolName = tags[tags.length - 1].split(":");
						String name = "";
						for (int i = 1; i < protocolName.length; i++) {
							name = name + protocolName[i] + (i < protocolName.length - 1 ? ":" : "");
						}
						map.setName(name);
						if (mode.equalsIgnoreCase("create")) {
							if (!mapList.contains(map)) {
								map.setBrowsePath(Arrays.asList(deviceProtocolInput.getTagname().replace(";", ",").split(",")));
								if (deviceProtocolInput.getDatatype().equalsIgnoreCase("measurement")) {
									MeasurementCreation measurementCreation = new MeasurementCreation();
									measurementCreation.setType(deviceProtocolInput.getType());
									measurementCreation.setFragmentName(deviceProtocolInput.getText());
									measurementCreation.setSeries(deviceProtocolInput.getText2());
									measurementCreation.setUnit(deviceProtocolInput.getUnit());
									map.setMeasurementCreation(measurementCreation);
								} else if (deviceProtocolInput.getDatatype().equalsIgnoreCase("event")) {
									EventCreation eventCreation = new EventCreation();
									eventCreation
											.setText("Event for " + deviceProtocolInput.getDeviceprotocolname().split("_")[1]);
									eventCreation.setType(deviceProtocolInput.getType());
									map.setEventCreation(eventCreation);
								} else if (deviceProtocolInput.getDatatype().equalsIgnoreCase("alarm")) {
									AlarmCreation alarmCreation = new AlarmCreation();
									alarmCreation.setSeverity(deviceProtocolInput.getText());
									alarmCreation
											.setText("Alarm for " + deviceProtocolInput.getDeviceprotocolname().split("_")[1]);
									alarmCreation.setType(deviceProtocolInput.getType());
									alarmCreation.setStatus(deviceProtocolInput.getText2());
									map.setAlarmCreation(alarmCreation);
								}
								mapList.add(map);
							} 
						} else if (mode.equalsIgnoreCase("delete")) {
							mapList.remove(map);
						}
						deviceProtocol.setMappings(mapList);
//						
//						ApplyConstraints applyConstraint = deviceProtocol.getApplyConstraints();
//						if (applyConstraint != null) {
//							List<String> constraint = applyConstraint.getMatchesServerIds();
//							if (constraint.contains(deviceProtocolInput.getServerid())) {
//								constraint.add(deviceProtocolInput.getServerid());
//								applyConstraint.setMatchesServerIds(constraint);
//							}
//							deviceProtocol.setApplyConstraints(applyConstraint);
//						}
						
						deviceProtocolMap.put(deviceProtocolInput.getDeviceprotocolname(), deviceProtocol);

					}

				}
				Iterator<String> keys = deviceProtocolMap.keySet().iterator();
				while (keys.hasNext()) {
					String key = keys.next();
					DeviceProtocol deviceProtocol = deviceProtocolMap.get(key);
					ResponseEntity<String> response = null;
					
					if (deviceProtocolStatusMap.containsKey(key)) {
						response = httpService.updateDeviceProtocol(deviceProtocol);
					} else {
						response = httpService.createDeviceProtocol(deviceProtocol);
					}
					
					if (response.getStatusCode().is2xxSuccessful()) {
						log.info("Success");
					} else {
						log.info("Error");
					}
				}
				existingDeviceProtocolMap.clear();

			} else {
				return "Mandatory headers missing";
			}

		} catch (Exception e) {
			log.error("{}", e);
			return "Error in creation";
		}
		return "Successfully created";
	}

}