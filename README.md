# BOOKING SERVICE #

### About

- Spring boot 2
- Maven

### Instructions

- cd into the project root directory
- Run command: ```mvn spring-boot:run```
- The swagger api documentation will be available at ```http://localhost:8000```


### Notes: ###

- The availability endpoint takes the arrival date and departure dates and return nights available.
The departure date will not be part of the result, but will be part of the reservation
- Snapshot dependency of swagger-ui for spring-webflux is still under development.