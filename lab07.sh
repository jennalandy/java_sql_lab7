#!/bin/bash

export APP_JDBC_URL=jdbc:mysql://csc365spring2018.webredirect.org/iguzmanl?useSSL=false
export APP_JDBC_USER=iguzmanl
export APP_JDBC_PW=365Spring18_011177864

javac InnReservations.java
java -cp mysql-connector-java-8.0.11.jar:. InnReservations
