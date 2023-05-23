### Overview 
This microservice will be used to automate the OPCUA device protocol creation based on below CSV template. This uses OPCUA microservice end points to create a    device protocol automatically and enable them. Below is the sample device protocol (CSV) file.
![image](https://github.com/SoftwareAG/opcua_deviceprotocol/assets/20227250/1c0950ce-9ea4-4d4f-91b2-4cb4181055ec)

### Prerequisite

* Java 11
* Maven 3.6 and above

### Installation
Go into main directory and run below command to build and deploy this microservice

Note:- This microservice can be built and run locally pointing to a tenant or can be deployed on a target tenant where this device protocol needs to be created. The build and installation will be same as standard cumulocity IoT microservice build & deploy steps - https://cumulocity.com/guides/microservice-sdk/concept/#configure-the-microservice-utility-tool

### Usage

Call the below API using Sample CSV file created





