import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val transitiveInclude: Configuration by configurations.creating

plugins {
	id("fabric-loom") version "0.12-SNAPSHOT"
	id("org.jetbrains.kotlin.jvm") version "1.7.10"
	id("org.jetbrains.kotlin.plugin.serialization") version "1.7.10"
	idea
}

base {
	archivesName.set(properties["archives_name"].toString())
	group = property("maven_group")!!
	version = property("mod_version")!!
}

repositories {
	mavenCentral()
	mavenLocal()
	maven("https://maven.kyrptonaught.dev")
}

dependencies {
	minecraft("com.mojang:minecraft:${properties["minecraft_version"]}")
	mappings("net.fabricmc:yarn:${properties["yarn_mappings"]}:v2")

	modImplementation("net.fabricmc:fabric-loader:${properties["loader_version"]}")
	modImplementation("net.fabricmc.fabric-api:fabric-api:${properties["fabric_version"]}")
	modImplementation("net.fabricmc:fabric-language-kotlin:${properties["fabric_kotlin_version"]}")

	modImplementation("net.kyrptonaught:quickshulker:${property("quickshulker_version")}"){
		exclude(group = "net.fabricmc.fabric-api")
		exclude(module = "modmenu")
	}

	implementation("org.apache.commons:commons-lang3:3.12.0")

	testImplementation("org.jetbrains.kotlin:kotlin-test:1.7.10")
}

tasks {

	val javaVersion = JavaVersion.VERSION_17

	processResources {
		inputs.property("version", project.version)
		filteringCharset = "UTF-8"
		filesMatching("fabric.mod.json") {
			expand(mutableMapOf("version" to project.version))
		}
	}

	java {
		withSourcesJar()
	}

	named<Wrapper>("wrapper") {
		gradleVersion = "7.5.1"
		distributionType = Wrapper.DistributionType.ALL
	}

	named<KotlinCompile>("compileKotlin") {
		kotlinOptions.jvmTarget = javaVersion.toString()
	}

	named<JavaCompile>("compileJava") {
		options.encoding = "UTF-8"
		options.release.set(javaVersion.toString().toInt())
	}

	named<Jar>("jar") {
		from("LICENSE") {
			rename { "${it}_${base.archivesName}" }
		}
	}

	named<Test>("test") { // https://stackoverflow.com/questions/40954017/gradle-how-to-get-output-from-test-stderr-stdout-into-console
		useJUnitPlatform()

		testLogging {
			outputs.upToDateWhen { false } // When the build task is executed, stderr-stdout of test classes will be show
			showStandardStreams = true
		}
	}


	val copyJarToServer = register("copyJarToServer") {
		println("copying mod to server")
		copyFile("build/libs/enderbackpack-1.5.1_1.19.2.jar", project.property("ServerModsFolder") as String)
	}

	val copyJarToClient = register("copyJarToClient") {
		println("copying mod to server")
		copyFile("build/libs/enderbackpack-1.5.1_1.19.2.jar", project.property("ClientModsFolder") as String)
	}

	build {
		doLast {
			copyJarToServer.get()
			copyJarToClient.get()
		}
	}

}

fun copyFile(src: String, dest: String) {
	copy {
		from(src)
		into(dest)
	}
}