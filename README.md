# data-conjurer
A tool to generate data for MySQL Database
## Environment
The tool needs java 17 or higher to run. The code is tested using Correto 17.
## Installation
Before stable release, the binary file can be created through maven command after cloning the code
```shell
mvn clean install
```
It creates a **data-conjurer.jar** file under conjurer-shell/target folder
## Usage
```shell
java -jar data-conjurer.jar schema.yaml hw-plan.yaml 
```
schema.yaml defines data structure
plan.yaml defines data generation plan such as rows
For example, to create data for yamls defined under examples folder
```shell
data-conjurer.jar examples/hw-schema.yaml examples/hw-plan.yaml
```

