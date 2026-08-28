plugins {
    `java-library`
    `maven-publish`
}

group = "com.github.xAv3nGerr"
version = "1.0.0"
repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.panda-lang.org/releases")
    maven("https://repo.okaeri.cloud/releases")
    maven("https://projectlombok.org/edge-releases")
    maven("https://repo.eternalcode.pl/releases")
    maven(url = "https://central.sonatype.com/repository/maven-snapshots/") { name = "central-snapshots" }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:26.2.build.+")
    implementation("dev.rollczi:litecommands-bukkit:3.11.0")
    implementation("eu.okaeri:okaeri-configs-yaml-bukkit:5.0.13")
    implementation("eu.okaeri:okaeri-configs-serdes-bukkit:5.0.13")
    implementation("eu.okaeri:okaeri-configs-serdes-commons:5.0.13")
    implementation("eu.okaeri:okaeri-configs-yaml-snakeyaml:5.0.13")
    implementation("org.mongodb:mongodb-driver-sync:5.9.1")
    implementation("io.nats:jnats:2.26.2")
    implementation("dev.morphia.morphia:morphia-core:2.5.3")
    compileOnly("net.kyori:adventure-api:5.2.0")
    implementation("net.kyori:adventure-text-minimessage:5.2.0")

    implementation("me.devnatan:inventory-framework-platform-bukkit:3.8.0-beta")
    runtimeOnly("me.devnatan:inventory-framework-platform-paper:3.8.0-beta")
    implementation("com.eternalcode:multification-paper:1.2.4") {
        exclude(group = "net.kyori")
    }
    implementation("com.eternalcode:multification-okaeri:1.2.4") {
        exclude(group = "net.kyori")
    }

    compileOnly("org.apache.logging.log4j:log4j-core:2.23.1")
    compileOnly("org.projectlombok:lombok:1.18.46")
    annotationProcessor("org.projectlombok:lombok:1.18.46")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}

tasks.test {
    useJUnitPlatform()
}