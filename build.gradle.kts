import com.google.protobuf.gradle.id
import java.net.URL

plugins {
	java
	id("org.springframework.boot") version "3.5.6"
	id("io.spring.dependency-management") version "1.1.7"
	id("gg.jte.gradle") version "3.1.16"
	id("com.google.protobuf") version "0.9.4"
	id("org.flywaydb.flyway") version "11.12.0"
	id("com.github.davidmc24.gradle.plugin.avro") version "1.9.1"
}

val avroDir = layout.projectDirectory.dir("src/main/avro")

tasks.register("fetchAvroSchemas") {
	group = "avro"
	description = "Fetch Avro schemas from GitHub repository"
	doLast {
		val repoUrl = "https://raw.githubusercontent.com/issakhrorov/demo-infra-schemas/main/"
		val schemas = listOf("NewUserMessage.avsc")

		avroDir.asFile.mkdirs()

		schemas.forEach { schema ->
			val file = avroDir.file(schema).asFile
			println("Downloading $schema to ${file.absolutePath}")
			URL("$repoUrl$schema").openStream().use { input ->
				file.outputStream().use { output -> input.copyTo(output) }
			}
		}
	}
}

tasks.named("generateAvroJava") {
	dependsOn("fetchAvroSchemas")
}


avro {
	isCreateSetters.set(true)
	fieldVisibility.set("PRIVATE")
	stringType.set("String")
}

sourceSets {
	main {
		java {
			srcDir("build/generated-main-avro-java")
		}
	}
}

group = "demo.infra"
version = "0.0.1-SNAPSHOT"
description = "Demo Infra User Service"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
	maven("https://packages.confluent.io/maven/")
}

extra["springCloudVersion"] = "2025.0.0"
extra["springGrpcVersion"] = "0.11.0"

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
	implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
	implementation("org.springframework.boot:spring-boot-starter-graphql")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.cloud:spring-cloud-starter-circuitbreaker-reactor-resilience4j")
	implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
	implementation("org.springframework.grpc:spring-grpc-spring-boot-starter")
	implementation("org.springframework.kafka:spring-kafka")

	implementation("org.postgresql:postgresql")
	implementation("org.flywaydb:flyway-core:11.12.0")
	implementation("org.apache.avro:avro:1.12.0")

	implementation("io.confluent:kafka-avro-serializer:7.9.1")
	implementation("io.zipkin.reporter2:zipkin-reporter-brave")
	implementation("io.grpc:grpc-services")
	implementation("io.micrometer:micrometer-tracing-bridge-brave")
	implementation("io.jsonwebtoken:jjwt-api:0.12.5")
	implementation("io.jsonwebtoken:jjwt-gson:0.12.6")
	implementation("gg.jte:jte-spring-boot-starter-3:3.1.16")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

	compileOnly("org.projectlombok:lombok")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	annotationProcessor("org.projectlombok:lombok")

	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
	runtimeOnly("io.micrometer:micrometer-registry-otlp")
	runtimeOnly("io.micrometer:micrometer-registry-prometheus")
	runtimeOnly("org.flywaydb:flyway-database-postgresql:11.12.0")
	runtimeOnly("org.postgresql:postgresql")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.projectreactor:reactor-test")
	testImplementation("org.springframework.graphql:spring-graphql-test")
	testImplementation("org.springframework.grpc:spring-grpc-test")
	testImplementation("org.springframework.kafka:spring-kafka-test")
	testImplementation("org.springframework.security:spring-security-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.grpc:spring-grpc-dependencies:${property("springGrpcVersion")}")
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
	}
}

jte {
	generate()
	binaryStaticContent = true
}

protobuf {
	protoc {
		artifact = "com.google.protobuf:protoc"
	}
	plugins {
		id("grpc") {
			artifact = "io.grpc:protoc-gen-grpc-java"
		}
	}
	generateProtoTasks {
		all().forEach {
			it.plugins {
				id("grpc") {
					option("@generated=omit")
				}
			}
		}
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
