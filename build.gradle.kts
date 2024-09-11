plugins {
    kotlin("jvm") version "1.9.24"
    kotlin("plugin.serialization") version "1.9.24"
    id("io.gitlab.arturbosch.detekt").version("1.23.7")
    jacoco
    application
}

sourceSets {
    main {
        kotlin.srcDir("src/main/kotlin")
        resources.srcDir("src/main/resources")
    }
    test {
        kotlin.srcDir("src/test/kotlin")
        resources.srcDir("src/test/resources")
        compileClasspath += sourceSets.main.get().output
        runtimeClasspath += sourceSets.main.get().output
    }
    create("systemtest") {
        kotlin.srcDir("src/systemtest/kotlin")
        resources.srcDir("src/systemtest/resources")
        compileClasspath += sourceSets.main.get().output
        runtimeClasspath += sourceSets.main.get().output
    }
}

tasks.wrapper {
    distributionType = Wrapper.DistributionType.ALL
}

repositories {
    mavenCentral()
    exclusiveContent {
        forRepository {
            maven {
                setUrl("https://sopra.se.cs.uni-saarland.de:51623/resources/jars")
                credentials {
                    username = "resources"
                    password = "[O0zRfLh9x?6}X)]Ww[LaC-{g2Sobs+A"
                }
                metadataSources {
                    artifact()
                }
            }
        }
        filter {
            includeGroup("selab.systemtest")
        }
    }
}

dependencies {
	// Logging
    implementation("io.github.oshai:kotlin-logging-jvm:7.0.0")
    implementation("org.slf4j:slf4j-simple:2.0.7")
    implementation("org.slf4j:slf4j-api:2.0.7")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    // Detekt
    val version = "1.23.7"
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:$version")

    // CLI
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.6")

    // JSON
    implementation("org.json:json:20240303")
    implementation("com.github.erosb:json-sKema:0.16.0")

    // Testing
    testImplementation(kotlin("test"))
    testImplementation("org.mockito:mockito-core:5.13.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0")

    // System Tests
    "systemtestImplementation"("selab.systemtest:systemtest-api:0.5.6") { isChanging = true }
    "systemtestImplementation"("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    "systemtestImplementation"("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
}

tasks.register("detectSuppressions") {
    sourceSets.main.get().kotlin.forEach {
        val regex = Regex("@.*suppress(.*)")
        File(it.path).forEachLine { line ->
            if (regex.containsMatchIn(line.lowercase())) {
                throw GradleException("Suppressions of analysis tools detected in the following file: " + it.path)
            }
        }
    }
    sourceSets.test.get().kotlin.forEach {
        val regex = Regex("@.*suppress(.*)")
        File(it.path).forEachLine { line ->
            if (regex.containsMatchIn(line.lowercase())) {
                throw GradleException("Suppressions of analysis tools detected in the following file: " + it.path)
            }
        }
    }
    sourceSets["systemtest"].kotlin.forEach {
        val regex = Regex("@.*suppress(.*)")
        File(it.path).forEachLine { line ->
            if (regex.containsMatchIn(line.lowercase())) {
                throw GradleException("Suppressions of analysis tools detected in the following file: " + it.path)
            }
        }
    }
}

tasks.withType<Copy> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
}

kotlin {
    jvmToolchain(17)
}

detekt {
    toolVersion = "1.23.7"
    config.setFrom("config/detekt/detekt.yml")
    buildUponDefaultConfig = true
}

tasks.build {
    dependsOn(tasks["detectSuppressions"])
    dependsOn(tasks.javadoc)
    dependsOn(tasks.detektMain)
    dependsOn(tasks.detektTest)
    dependsOn(tasks["detektSystemtest"])
}

tasks.jar {
    archiveFileName.set("selab.jar")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    manifest {
        attributes(
            "Main-Class" to "de.unisaarland.cs.se.selab.MainKt",
            "Entry-Point" to "de.unisaarland.cs.se.selab.MainKt",
            "Implementation-Title" to "SeLab",
            "Application-Name" to "SeLab",
            "Implementation-Version" to project.version,
        )
    }

    from(sourceSets.main.get().output)
    from(sourceSets.named("systemtest").get().output)

    dependsOn(configurations.compileClasspath)
    from({
        configurations.compileClasspath.get()
            .filter { it.name.endsWith("jar") }
            .map { zipTree(it) }
    })
    dependsOn(configurations.named("systemtestCompileClasspath"))
    from({
        configurations.named("systemtestCompileClasspath").get()
            .filter { it.name.endsWith("jar") }
            .map { zipTree(it) }
    })

    doLast {
        copy {
            from(this@jar.archiveFile)
            into("$projectDir/libs/")
        }
    }
}

val serverExec = task<JavaExec>("serverExec") {
    dependsOn(tasks.jar)
    classpath(files("libs/selab.jar"))
    mainClass.set("de.unisaarland.cs.se.selab.MainKt")
    args = listOf("--map", properties["MAP"].toString(),
        "--corporations", properties["CORPORATIONS"].toString(),
        "--scenario", properties["SCENARIO"].toString(),
        "--max_ticks", properties["MAX_TICKS"].toString(),
        "--out", properties["OUT"].toString()
    )
}

val systemtestExec = task<JavaExec>("systemtestExec") {
    dependsOn(tasks.jar)
    classpath(files("libs/selab.jar"))
    mainClass.set("de.unisaarland.cs.se.selab.systemtest.MainKt")
    args = listOf(
        "--jar", "libs/selab.jar",
        "--run", "system",
        "--timeout", "10",
//        "--debug", "1337"
    )
}
