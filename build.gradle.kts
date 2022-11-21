@file:Suppress("UnstableApiUsage")

plugins {
	id("org.jetbrains.kotlin.jvm").version(libs.versions.kotlin)
	id("org.quiltmc.quilt-mappings-on-loom") version "4.2.1"
	alias(libs.plugins.quilt.loom)
	`maven-publish`
}

val modVersion: String by project
val mavenGroup: String by project
val modId: String by project

base.archivesBaseName = modId
version = modVersion
group = mavenGroup

repositories {
	// Add repositories to retrieve artifacts from in here.
	// You should only use this when depending on other mods because
	// Loom adds the essential maven repositories to download Minecraft and libraries from automatically.
	// See https://docs.gradle.org/current/userguide/declaring_repositories.html
	// for more information about repositories.
	maven {
		name = "TerraformersMC"
		url = uri("https://maven.terraformersmc.com/")
	}
	
	maven {
		name = "Modrionth"
		url = uri("https://api.modrinth.com/maven")
		content {
			includeGroup("maven.modrinth")
		}
	}
	
	maven {
		name = "auoeke Maven"
		url = uri("https://maven.auoeke.net")
	}
	
	maven {
		url = uri("https://maven.bai.lol")
	}
	
	maven {
		name = "Cursed Maven"
		url = uri("https://cursemaven.com")
		content {
			includeGroup("curse.maven")
		}
	}
	maven {
		name = "TerraformersMC"
		url = uri("https://maven.terraformersmc.com/")
	}
	maven {
		url = uri("https://repo.minelittlepony-mod.com/maven/release")
	}
	
	maven {
		name = "Gegy"
		url = uri("https://maven.gegy.dev")
	}
	
	maven {
		url = uri("https://nexus.velocitypowered.com/repository/maven-public/")
	}
	
	maven {
		name = "QuiltMC Snapshot"
		url = uri("https://maven.quiltmc.org/repository/snapshot")
	}
	// jfrog ladysnake
	maven {
		name = "Ladysnake Mods"
		url = uri("https://ladysnake.jfrog.io/artifactory/mods")
	}
	maven {
		name = "Geggolib"
		url = uri("https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/")
	}
}


val modImplementationInclude by configurations.register("modImplementationInclude")

dependencies {
	minecraft(libs.minecraft)
	mappings(variantOf(libs.quilt.mappings) { classifier("intermediary-v2") })

	modImplementation(libs.quilt.loader)
	modImplementation(libs.quilted.fabric.api)
	//modImplementation("org.quiltmc:qsl:3.0.0-beta.18+1.19.2")
	modImplementation(libs.quilt.lang.kotlin)
	include(libs.quilt.lang.kotlin)
	
	modRuntimeOnly("maven.modrinth", "lazydfu", "0.1.3")
 
	modRuntimeOnly("maven.modrinth", "fastload", "1.1.5.fabric.1.19")
	modRuntimeOnly("maven.modrinth", "just-load", "1.0.3")
	modRuntimeOnly("curse.maven", "skip-transitions-592071", "3735539")

	modApi("dev.onyxstudios.cardinal-components-api:cardinal-components-base:5.0.2")
	modImplementation("dev.onyxstudios.cardinal-components-api:cardinal-components-entity:5.0.2")
	modImplementation("software.bernie.geckolib:geckolib-quilt-1.19:3.1.30")
	modImplementation("dev.emi:trinkets:3.4.0") {
		isTransitive = false
	}
	modImplementation(libs.satin)
}

tasks.processResources {
	inputs.property("version", version)
	
	filesMatching("quilt.mod.json") {
		expand("group" to mavenGroup, "id" to modId, "version" to version)
	}
	
	filesMatching("**/lang/*.json") {
		expand("id" to modId)
	}
}

tasks.withType<JavaCompile> {
	options.encoding = "UTF-8"
	// Minecraft 1.18 (1.18-pre2) upwards uses Java 17.
	options.release.set(17)
}

java {
	// Still required by IDEs such as Eclipse and Visual Studio Code
	sourceCompatibility = JavaVersion.VERSION_17
	targetCompatibility = JavaVersion.VERSION_17
	
	// Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task if it is present.
	// If you remove this line, sources will not be generated.
	withSourcesJar()
	
	// If this mod is going to be a library, then it should also generate Javadocs in order to aid with developement.
	// Uncomment this line to generate them.
	// withJavadocJar()
}

// If you plan to use a different file for the license, don't forget to change the file name here!
tasks.withType<AbstractArchiveTask> {
	from("LICENSE") {
		rename { "${it}_${modId}" }
	}
}

// Configure the maven publication
publishing {
	publications {}
	
	// See https://docs.gradle.org/current/userguide/publishing_maven.html for information on how to set up publishing.
	repositories {
		// Add repositories to publish to here.
		// Notice: This block does NOT have the same function as the block in the top level.
		// The repositories here will be used for publishing your artifact, not for
		// retrieving dependencies.
	}
}

// compiler args for context recievers
// TODO: check
tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class).all {
	kotlinOptions.freeCompilerArgs = listOf("-Xcontext-receivers", "-Xuse-ir")
}

