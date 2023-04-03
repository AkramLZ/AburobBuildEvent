plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "dev.akraml"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven { url = uri("https://repo.codemc.org/repository/nms") }
    maven { url = uri("https://repo.papermc.io/repository/maven-public/") }
    maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") }
}

dependencies {
    implementation("fr.mrmicky:fastboard:1.2.1")
    implementation("com.intellectualsites.bom:bom-1.18.x:1.25")
    implementation("com.google.code.gson:gson:2.10.1")
    compileOnly("com.intellectualsites.informative-annotations:informative-annotations:1.3")
    compileOnly("com.intellectualsites.paster:Paster:1.1.5")
    compileOnly("net.kyori:adventure-api:4.13.0")
    compileOnly("org.spigotmc:spigot:1.19.2-R0.1-SNAPSHOT")
    compileOnly("com.plotsquared:PlotSquared-Core:6.11.1")
    compileOnly("com.plotsquared:PlotSquared-Bukkit:6.11.1")
    compileOnly("org.projectlombok:lombok:1.18.26")
    annotationProcessor("org.projectlombok:lombok:1.18.26")
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }
}