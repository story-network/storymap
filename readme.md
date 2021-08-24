# Storymap
StoryNetwork gradle plugin.  

Provide deobfuscated Minecraft server source with offical mappings!

## Example
build.gradle settings after applying  
Ex) 1.16.5
```groovy
repositories {
    mavenCentral()

    maven {
        name = 'StoryNetwork'
        url = 'https://raw.githubusercontent.com/story-network/maven/master/'
    }
}

dependencies {
    // Minecraft server
    storymap name: 'minecraft-server', version: '1.16.5'
}
```
