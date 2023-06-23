### Overview 
This microservice will be used to automate the OPCUA device protocol creation based on below CSV template. This uses OPCUA microservice end points to create a    device protocol automatically and enable them. Below is the sample device protocol (CSV) file.

[opcua_device_protocol_mapping_template_v1.0.csv](https://github.com/SoftwareAG/opcua_deviceprotocol/files/11844331/opcua_device_protocol_mapping_template_v1.0.csv)

### Template field description


![image](https://github.com/SoftwareAG/opcua_deviceprotocol/assets/20227250/5594f139-b8a8-4cb2-81ad-42b4aa72b2d5)

### Prerequisite

* Java 11
* Maven 3.6 and above

### Installation
Go into main directory and run below command to build and deploy this microservice

Note:- This microservice can be built and run locally pointing to a tenant or can be deployed on a target tenant where this device protocol needs to be created. The build and installation will be same as standard cumulocity IoT microservice build & deploy steps - https://cumulocity.com/guides/microservice-sdk/concept/#configure-the-microservice-utility-tool

### Usage

Call the below API using Sample CSV file created





