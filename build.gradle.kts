import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile

plugins {
    base
    id("com.moowork.node").version("1.2.0")
    kotlin("jvm").version("1.2.0")
//    id("org.jetbrains.kotlin.jvm").version("1.2.0")
}

fun getProperty(name: String, default: String = "") = findProperty(name)?.toString() ?: default

val dryRun = getProperty("dryRun", "false")
val authToken = getProperty("authToken", "")
val deployTag = getProperty("deployTag", "dev")

configure(subprojects.filter { it.parent != rootProject }) {

    apply {
        plugin("kotlin2js")
        plugin("com.moowork.node")
    }

    node {
        download = true
        workDir = file("${rootProject.buildDir}/nodejs")
    }

    repositories {
        jcenter()
        mavenCentral()
    }

    dependencies {
        "compile"(kotlin("stdlib-js"))
    }

    the<JavaPluginConvention>().sourceSets["main"].withConvention(KotlinSourceSet::class) {
        kotlin.srcDirs += projectDir
    }

    val libraryPath = projectDir.parent
    val packageJsonPath = "$libraryPath/package.json"
    val readmePath = "$libraryPath/README.md"
    val deployDir = "$buildDir/deploy_to_npm"
    val konfigPath = "$projectDir/konfig.json"

    tasks {
        "compileKotlin2Js"(Kotlin2JsCompile::class) {
            kotlinOptions {
                moduleKind = "umd"
                sourceMap = true
            }
        }
    }

//    task validate {
//        doFirst {
//            if (!file(packageJsonPath).exists()) throw new GradleException("${packageJsonPath} doesn't exist")
//            if (!file(readmePath).exists()) throw new GradleException("Readme file ${readmePath} doesn't exist")
//            if (!file(konfigPath).exists()) throw new GradleException("Configuration file ${konfigPath} doesn't exist")
//
//            try {
//                validate.ext.deployVersion = new JsonSlurper().parseText(file(konfigPath).text).version
//            } catch (JsonException e) {
//                throw new GradleException("Error while processing ${konfigPath}", e)
//            }
//        }
//    }
//
//    check.dependsOn(validate)
//
//    task cleanDeployDir(type: Delete) {
//    delete = deployDir
//}
//
//    task copyTemplate(type: Copy, dependsOn: [validate, cleanDeployDir]) {
//    from packageJsonPath
//            from readmePath
//            into deployDir
//
//            doFirst {
//                expand(version: validate.deployVersion)
//            }
//}
//
//    task copySources(type: Copy, dependsOn: [compileKotlin2Js, cleanDeployDir]) {
//    from compileKotlin2Js.destinationDir
//            into deployDir
//}
//
//    task publishToNpm(type: NpmTask, dependsOn: [copyTemplate, copySources]) {
//    workingDir = file(deployDir)
//
//    def deployArgs = ['publish',
//            '--access=public',
//            "--//registry.npmjs.org/:_authToken=${authToken}",
//            "--tag=${deployTag}"]
//
//    if (dryRun == "true") {
//        deployArgs += ['--reg=http://localhost:3676',
//            '--//localhost:3676/:_password=mock!!password',
//            '--//localhost:3676/:username=mock!!user',
//            '--//localhost:3676/:email=mock@email.com',
//            '--//localhost:3676/:always-auth=false']
//
//        doFirst {
//            // Run during execution, not configuration
//            println("${deployDir} \$ npm arguments: ${deployArgs.join(" ")}")
//            def executor = Executors.newSingleThreadExecutor()
//
//            def launchNpm = Executors.callable {
//
//                def serverSocket = new ServerSocket(3676)
//
//                def s = serverSocket.accept()
//
//                def webServerAddress = s.getInetAddress().toString()
//
//                println("New Connection:" + webServerAddress);
//                def sin = new BufferedReader(new InputStreamReader(s.getInputStream()));
//
//                def request = sin.readLine()
//                println("--- Client request: " + request)
//                def url = request.substring(4, request.substring(4).indexOf(' '))
//
//                println "URL: " + url
//
//                def contentLength = 0
//
//                while (request != "") {
//                    request = sin.readLine()
//                    println("--- Client request: " + request)
//                    def coLeS = "content-length: "
//                    if (request.toLowerCase().startsWith(coLeS)) {
//                        contentLength = Integer.parseInt(request.substring(coLeS.length()).trim())
//                    }
//                }
//
//                def jsonBuffer = new char[contentLength]
//
//                println "About to read ${contentLength} bytes"
//
//                sin.read(jsonBuffer)
//
//                def json = new String(jsonBuffer)
//
//                println "JSON: " + json
//
//                def data = new JsonSlurper().parseText(json)
//
//                def name = "${data.name}-${validate.deployVersion}.tgz"
//
//                def tgz = data._attachments[name].data as String
//
//                println "TGZ " + tgz
//
//                def decodedTgz = Base64.decoder.decode(tgz)
//
//                println "Len = " + decodedTgz.length
//
//                Files.write(Paths.get("${deployDir}/name.tgz"), decodedTgz)
//
//                def sout = new PrintWriter(s.getOutputStream(), true)
//
//                sout.println("HTTP/1.0 201");
//                sout.println("Content-type: application/json");
//                sout.println("Server-name: myserver");
//                String response = "{ \"message\": \"Cached data for: $url\" }";
//                sout.println("Content-length: " + response.length())
//                sout.println("")
//                sout.println(response)
//                sout.flush()
//                sout.close()
//                s.close()
//                serverSocket.close()
//
//            }
//
//            executor.submit(launchNpm)
//        }
//        args = deployArgs//['pack']
//    } else {
//        args = deployArgs
//    }
//}


}

