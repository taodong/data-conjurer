# data-conjurer
[![Build](https://github.com/taodong/data-conjurer/actions/workflows/maven.yml/badge.svg)](https://github.com/taodong/data-conjurer/actions/workflows/maven.yml/badge.svg)
[![codecov](https://codecov.io/gh/taodong/data-conjurer/graph/badge.svg?token=O4AYAUHEI3)](https://codecov.io/gh/taodong/data-conjurer)
[![Maven Central](https://img.shields.io/badge/Maven_Central-v1.0.0-blue)](https://central.sonatype.com/artifact/io.github.taodong/data-conjurer)

A tool to generate insert statements for MySQL Database. The main purpose of this tool is enable data generation for a full relational schema in real time scenarios.
## Environment
The tool requires java 21. The code is developed using Temurin 21 JDK.
## Installation
To build the executable jar file from the source, the binary file can be created through maven command after cloning the repository
```shell
mvn clean install
```
It creates a **data-conjurer-<version>.jar** file under conjurer-shell/target folder. The current version is 1.0.0.
## Usage
```shell
java -jar data-conjurer-<version>.jar schema.yaml plan.yaml 
```
- **schema.yaml**: defines data structure
- **plan.yaml**: defines data generation plan such as rows
For example, to create data using yaml files defined under examples/helloworld folder
```shell
java -jar data-conjurer-1.0.0.jar examples/helloworld/schema.yaml examples/helloworld/plan.yaml
```
The output files are named using format ${applyOrder}_${entityName}.sql.
The above command will output two files
- 0_country.sql: sql insert statements for country table which should be applied first 
- 1_city.sql: sql insert statements for city table which should be applied after
### Other Configurations
The following configurations can be used to control behaviors of this tool

| Short Format | Long Format    | Description                                                                        | Default    |
|--------------|----------------|------------------------------------------------------------------------------------|------------|
| c            | max-collision  | Max occurrence of generated records which violate index constraints                | 100        |
| e            | entity-timeout | Single entity generation timeout in minutes                                        | 5 minutes  |
| i            | wait-interval  | Wait interval of data generation service to check entity status updates in seconds | 10 seconds |
| p            | partial-result | Allow partial results of entity generation                                         | false      |
| t            | timeout        | Program execution timeout in minutes                                               | 15 minutes |

The following is the output of `java -jar conjurer-shell/target/data-conjurer.jar -h`
```shell
Usage: conjure [-hpV] [-c=<maxCollision>] [-e=<timeOutInMinutes>]
               [-i=<generationInterval>] [-t=<maxTimeout>] <schema> <plan>
Command to generate data
      <schema>           Data schema file
      <plan>             Data generation plan
  -c, --max-collision=<maxCollision>
                         Max occurrence of generated records which violate
                           index constraints for each entity
  -e, --entity-timeout=<timeOutInMinutes>
                         Single entity generation timeout in minutes
  -h, --help             Show this help message and exit.
  -i, --wait-interval=<generationInterval>
                         Wait interval of data generation service to check
                           entity status updates in seconds
  -p, --partial-result   Allow partial results of entity generation
  -t, --timeout=<maxTimeout>
                         Program execution timeout in minutes
  -V, --version          Print version information and exit.
```
When generating large rows of the data, you may want to increase max-collision, entity-timeout, timeout values accordingly.

*partial-result* allows the tool to generate data for next entity after max-collision of current entity is reached. It's experimental, use it with caution.

## Reference Document
https://github.com/taodong/data-conjurer/wiki/Data-Conjurer-Reference

