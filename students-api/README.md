## Students API for Spring Batch Course from INFYBUZZ

1. Build Docker image
    - `mvn package -Dpackaging=docker`
    - `docker tag students-api artarkatesoft/art-infybuzz-students-api`
2. Build Native Docker Image (with GraalVM)
    - `mvn package -Dpackaging=docker-native -Pgraalvm`
    - `docker tag students-api artarkatesoft/art-infybuzz-students-api:native`