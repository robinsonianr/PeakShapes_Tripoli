import Common_build_gradle.Versions.junitVersion

plugins {
    id("common-build")
}


dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:${junitVersion}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${junitVersion}")

    // https://mvnrepository.com/artifact/gov.nist.math/jama
    //implementation group: 'gov.nist.math', name: 'jama', version: '1.0.3'
    // modernized update: https://github.com/topobyte/jama
    implementation("com.github.topobyte:jama:master-SNAPSHOT")

    testImplementation("com.github.cirdles:commons:bc38781605")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}