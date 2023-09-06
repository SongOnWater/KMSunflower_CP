pluginManagement {

    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }


//    resolutionStrategy {
//        eachPlugin {
//            println("plugin requested: "+this.requested.id.id+","+this.requested.version+","+this.requested.module)
//            println("plugin target: "+this.target.id.id+","+this.target.version+","+this.target.module)
//
//        }
//    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}



rootProject.name = "KMSunflower"
include(":androidApp")
include(":shared")



