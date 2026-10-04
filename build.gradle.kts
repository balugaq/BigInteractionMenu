import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.language.jvm.tasks.ProcessResources

plugins {
    java
    alias(libs.plugins.shadow)
    id("xyz.jpenilla.run-paper") version "3.0.2"
    id("maven-publish")
    id("signing")
    id("io.github.sgtsilvio.gradle.maven-central-publishing") version "0.5.0"
}

group = "io.github.balugaq"
version = "0.0.4"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks.compileJava {
    options.encoding = "UTF-8"
    options.release.set(21)
}

tasks.withType<Javadoc>().configureEach {
    // 出错（含 doclint 之外的警告）也不让 javadoc 任务失败，避免阻断构建/发布
    isFailOnError = false
    (options as StandardJavadocDocletOptions).apply {
        encoding = "UTF-8"
        charSet = "UTF-8"
        addStringOption("Xdoclint", "none")
    }
}

tasks.withType<JavaExec>().configureEach {
    systemProperty("file.encoding", "UTF-8")
    systemProperty("sun.stdout.encoding", "UTF-8")
    systemProperty("sun.stderr.encoding", "UTF-8")
}

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://jitpack.io")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.alessiodp.com/releases/")
}

dependencies {
    implementation(libs.libby.bukkit)
    compileOnly(libs.paper.api)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    // System-scoped local JARs
    // compileOnly(fileTree(mapOf("dir" to "lib", "include" to listOf("*.jar"))))

    testImplementation(libs.junit.jupiter)
    testImplementation(libs.mockito.core)
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    enabled = false
}

tasks.named<ProcessResources>("processResources") {
    filesMatching("**/*.yml") {
        expand(
            mapOf(
                "version" to project.version,
                "name" to project.name
            )
        )
    }
}

tasks.named<ShadowJar>("shadowJar") {
    archiveVersion.set(project.version.toString())
    archiveClassifier.set("")
    relocate("net.byteflux.libby", "io.github.balugaq.libraries.libby")
}

val sourcesJar = tasks.register<Jar>("sourcesJar") {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

val javadocJar = tasks.register<Jar>("javadocJar") {
    archiveClassifier.set("javadoc")
    from(tasks.named<Javadoc>("javadoc"))
}

tasks.build {
    dependsOn(tasks.named("shadowJar"))
}

tasks.runServer {
    dependsOn(tasks.named("shadowJar"))
    val run = file(providers.gradleProperty("server.run.dir").orElse("run"))
    runDirectory.set(run)

    doFirst {
        run.resolve("eula.txt").writeText("eula=true")

        val pl = run.resolve("plugins")
        pl.mkdirs()
        copy {
            from(projectDir.resolve("build/libs")) {
                include("${name}-${version}.jar")
            }
            into(pl)
        }
    }

    jvmArgs(
        "-Dfile.encoding=UTF-8",
        "-Dsun.jnu.encoding=UTF-8",
        "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5001",
        "-Dnet.kyori.adventure.text.warn_when_legacy_formatting_detected=false"
    )
    maxHeapSize = "4G"
    minecraftVersion("1.21.11")
}

publishing {
    repositories {
        maven {
            name = "Central"
            url = uri("https://central.sonatype.com/api/v1/publisher")
        }
    }
    publications {
        create<MavenPublication>("mavenJava") {
            artifact(tasks.named("shadowJar"))
            // Maven Central 发布硬性要求：附带 sources / javadoc 构件
            artifact(sourcesJar)
            artifact(javadocJar)

            pom {
                name = "${name}"
                description = "Grid-based instant interaction menu library for Minecraft Paper, powered by display entities"
                url = "https://github.com/balugaq/${name}"
                licenses {
                    license {
                        name = "MIT License"
                        url  = "https://opensource.org/licenses/MIT"
                    }
                }
                developers {
                    developer {
                        id = "balugaq"
                        name = "balugaq"
                        email = "balugaq@qq.com"
                    }
                }
                scm {
                    connection = "scm:git:https://github.com/balugaq/${name}.git"
                    developerConnection = "scm:git:ssh://github.com/balugaq/${name}.git"
                    url = "https://github.com/balugaq/${name}"
                }
            }
        }
    }
}

// 签名配置
signing {
    // 从环境变量或 gradle.properties 读取敏感信息；
    // 仅在提供了签名密钥时才启用签名，避免本地 build/无密钥时配置失败
    val signingKey = providers.gradleProperty("signingKey")
        .orElse(providers.systemProperty("signingKey"))
        .orElse(providers.environmentVariable("SIGNING_KEY"))
        .orNull

    val signingPassword = providers.gradleProperty("signingPassword")
        .orElse(providers.systemProperty("signingPassword"))
        .orElse(providers.environmentVariable("SIGNING_PASSWORD"))
        .orNull
    if (signingKey != null && signingPassword != null) {
        useInMemoryPgpKeys(signingKey, signingPassword)
        sign(publishing.publications["mavenJava"])
    } else {
        // 未提供签名密钥（例如本地开发构建），跳过签名
    }
}