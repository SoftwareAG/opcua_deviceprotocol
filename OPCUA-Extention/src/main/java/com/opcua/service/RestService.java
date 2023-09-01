package com.opcua.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.opcua.dto.CustomError;
import com.opcua.dto.IntervalData;
import com.opcua.util.Util;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(value = "api/")
@Slf4j
public class RestService {
	
	@Autowired
	private DeviceProtocolService deviceProtocolService;
	
	@Autowired
	private OPCUA_IDAdditionService opcuaIDAdditionService;
	
	@Autowired
	private ManagedObjectService managedObjectService;
	
	@Autowired
	private Util util;
	
	@RequestMapping(value = "uploadDP", method = RequestMethod.POST, consumes = { MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> deviceProtocolFileUpload(
			@RequestPart("file") MultipartFile file,
			@RequestPart("mode") String mode
			) {
			
		if (!file.getContentType().contains("csv")) {
			return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON)
					.body(new CustomError(HttpStatus.NOT_ACCEPTABLE, "File format should be csv"));
		}

		if (file.isEmpty()) {
			return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON)
					.body(new CustomError(HttpStatus.NO_CONTENT, "Empty File not allowed"));
		}		

		String message = deviceProtocolService.readFile(file, mode);
		
		return util.checkMessageAndProcessResponse(message);
	}
	
	@RequestMapping(value = "uploadOPCUAID", method = RequestMethod.POST, consumes = { MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> opcuaFileUpload(
			@RequestPart("file") MultipartFile file
			) {
			
		if (!file.getContentType().contains("csv")) {
			return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON)
					.body(new CustomError(HttpStatus.NOT_ACCEPTABLE, "File format should be csv"));
		}

		if (file.isEmpty()) {
			return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON)
					.body(new CustomError(HttpStatus.NO_CONTENT, "Empty File not allowed"));
		}		

		String message = opcuaIDAdditionService.readFile(file);
		
		
		return util.checkMessageAndProcessResponse(message);
	}
	
	@PostMapping("intervalchange")
	public ResponseEntity<?> updateRequiredInterval(@RequestBody IntervalData intervalData) {
		log.info("----------------------------------------");
		log.info("Changing the required internval of the devices of type: {}", intervalData.getType());
		log.info("----------------------------------------");
		
		try {
			String response = managedObjectService.updateInterval(intervalData.getType(), intervalData.getPeriod());
			return ResponseEntity.status(HttpStatus.OK).body(response);
		} catch (Exception ex) {
			log.error("Error while changing the interval: {}", ex);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error");
		}
		
	}
}
