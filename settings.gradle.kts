pluginManagement {
    plugins {
        fun String.v() = extra["$this.version"].toString()
        fun idv(id: String, key: String = id) = id(id) version key.v()
        idv("com.github.vlsi.crlf", "com.github.vlsi.vlsi-release-plugins")
        idv("com.github.vlsi.gradle-extensions", "com.github.vlsi.vlsi-release-plugins")
        idv("com.github.vlsi.license-gather", "com.github.vlsi.vlsi-release-plugins")
        idv("com.github.vlsi.stage-vote-release", "com.github.vlsi.vlsi-release-plugins")
    }
}
rootProject.name = "java-native-foundation"
