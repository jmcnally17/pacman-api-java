plugins {
    id("java")
    id("org.springframework.boot") version "3.1.3"
    id("io.spring.dependency-management") version "1.1.3"
    jacoco
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("redis.clients:jedis:5.0.0")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

jacoco {
    toolVersion = "0.8.10"
    reportsDirectory.set(layout.buildDirectory.dir("coverage"))
}


tasks.test {
    testLogging {
        showStandardStreams = true
        events("passed", "skipped", "failed")
    }
    finalizedBy(tasks.jacocoTestReport)
    useJUnitPlatform()
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
}
