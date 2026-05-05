plugins {
    id("java")
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.21"
}


group = "dev.zenqrt.clownchase"
version = "1.0-SNAPSHOT"

repositories {
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    paperweight.paperDevBundle("26.1.2.build.+")
}

paperweight {
    javaLauncher = javaToolchains.launcherFor {
        // Example scenario:
        // Paper 1.17.1 was originally built with JDK 16 and the bundle
        // has not been updated to work with 21+ (but we want to compile with a 25 toolchain)
        languageVersion = JavaLanguageVersion.of(25)
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}

tasks.test {
    useJUnitPlatform()
}