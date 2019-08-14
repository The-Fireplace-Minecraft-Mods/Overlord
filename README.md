[Overlord](https://minecraft.curseforge.com/projects/overlord)
============================================================================

[![Curse Forge](http://cf.way2muchnoise.eu/short_251732_downloads.svg)](https://minecraft.curseforge.com/projects/overlord)

[Issue Reporting](https://github.com/The-Fireplace/Overlord/issues)
------------------------------------------------------------------

If you found a bug or even are experiencing a crash please report it so we can fix it. Please check at first if a bug report for the issue already
[exists](https://github.com/The-Fireplace/Overlord/issues). If not just create a [new issue](https://github.com/The-Fireplace/Overlord/issues/new) and fill out the
form.

Please include the following:

* Minecraft version
* Mod version
* Forge version/build
* Versions of any mods potentially related to the issue 
* Any relevant screenshots/videos are greatly appreciated.
* For crashes:
  * Steps to reproduce
  * fml-client-latest.log or fml-server-latest.log from the logs folder.
 
*(When creating a new issue please follow the template)*

[Feature Requests](https://github.com/The-Fireplace/Overlord/issues)
-------------------------------------------------------------------

If you want a new feature added, go ahead an open a [new issue](https://github.com/The-Fireplace/Overlord/issues/new), remove the existing form and describe your
feature the best you can. The more details you provide the easier it will be to implement it.
You can also talk to me on [Discord](https://discord.gg/29aj3Ah)

Developing with My Mod
----------------------

If you want to use items or blocks from my mod, add support for, or even develop an addon for my mod, you can easily add it to your development environment! ~~Most
releases get uploaded to my maven repository.  
So all you have to do to include the mod is add these lines *(in the appropriate places)* to your build.gradle~~
Most builds on CurseForge have deobf and src builds in the "Additional Files" for each file. Those can be used until I get a new maven set up.

    repositories {
        maven { // The_Fireplace's Mods, and BrainStoneMod
            url "[removed, no longer working]"
        }
        // Other repos...
    }
    
    dependencies {
        deobfCompile "the_fireplace.overlord:Overlord-<MC-Version>:<version>"
        // Other dependencies
    }

Setting up a Workspace/Compiling from Source
--------------------------------------------

* Setup: Run [gradle] in the repository root: `gradlew[.bat] setupDecompWorkspace [eclipse|idea]`
* Build: Run [gradle] in the repository root: `gradlew[.bat] build`
* If obscure Gradle issues are found try running `gradlew clean` and `gradlew cleanCache`(This one is a last resort)
