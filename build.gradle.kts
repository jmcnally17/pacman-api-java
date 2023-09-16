plugins {
    id("java")
    jacoco
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.testng:testng:7.8.0")
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
    useTestNG()
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
}
