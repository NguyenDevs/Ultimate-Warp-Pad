plugins {
    id("java-library")
    id("xyz.jpenilla.run-paper") version "3.0.2"
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://maven.enginehub.org/repo/")
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21-R0.1-SNAPSHOT")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.13")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    implementation("org.xerial:sqlite-jdbc:3.45.3.0")
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(25)
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(21)
}

tasks {
    jar {
        from({
            configurations.runtimeClasspath.get().filter { it.exists() }.map {
                if (it.isDirectory) it else zipTree(it)
            }
        }) {
            exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")
        exclude("org/sqlite/native/FreeBSD/**")
        exclude("org/sqlite/native/Linux-Android/**")
        exclude("org/sqlite/native/Linux-Musl/**")
        exclude("org/sqlite/native/Mac/**")
        }
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }

    runServer {
        minecraftVersion("1.21")
        jvmArgs("-Xms2G", "-Xmx2G")
    }

    processResources {
        val props = mapOf("version" to version)
        filesMatching("plugin.yml") {
            expand(props)
        }
    }
}
