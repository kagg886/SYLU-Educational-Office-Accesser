plugins {
    id("java")
}

group = "com.kagg886"
version = "unspecified"

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation("org.jsoup:jsoup:1.15.3")
    implementation("com.alibaba:fastjson:1.2.76")
}

tasks.test {
    useJUnitPlatform()
}