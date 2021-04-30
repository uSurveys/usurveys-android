# HOLY SHIT THIS IS COMPLEX

Followed a very nice guide here: https://getstream.io/blog/publishing-libraries-to-mavencentral-2021/
Some useful official documentation: https://central.sonatype.org/publish/publish-manual/

Setup:
1. install gpg
2. get the gpg file here: https://keys.openpgp.org/search?q=calderwoodra1113%40gmail.com
3. get the local.properties file from allen
4. mkdir musurveys-android-sdk/build/bundle/

Instructions to upload a release:
0.  Replace the version numbers in each step to match musurveys-android-sdk/build.gradle PUBLISH_VERSION
1.  $ ./gradlew musurveys-android-sdk:publishReleasePublicationToMavenLocal
2.  $ cd musurveys-android-sdk/build/
3.  $ mkdir bundle/
4.  $ cp libs/musurveys-android-sdk-0.3.0-sources.jar* bundle/
5.  $ cp outputs/aar/musurveys-android-sdk-release.aar bundle/musurveys-android-sdk-0.3.0.aar 
6.  $ cp publications/release/pom-default.xml bundle/pom.xml
7.  $ cd bundle/
8.  $ gpg -ab pom.xml
10. $ gpg -ab musurveys-android-sdk-0.3.0.aar
11. $ jar -cvf bundle.jar .
12. $ cp bundle.jar ~/Desktop/
13. $ open https://s01.oss.sonatype.org/#staging-upload
14. Select: Artifact bundle
15. Upload ~/Desktop/bundle.jar
16. Click the release button (automatically drop checkbox)

# DO THIS: shift + shift > "replace in file" > 0.3.0 > 0.3.0 > replace all

massive:
./gradlew musurveys-android-sdk:publishReleasePublicationToMavenLocal && \
    cd musurveys-android-sdk/build/ && \
    rm bundle/* && \
    cp libs/musurveys-android-sdk-0.3.0-sources.jar* bundle/ && \
    cp outputs/aar/musurveys-android-sdk-release.aar bundle/musurveys-android-sdk-0.3.0.aar && \
    cp publications/release/pom-default.xml bundle/pom.xml && \
    cd bundle/ && \
    gpg -ab pom.xml && \
    gpg -ab musurveys-android-sdk-0.3.0.aar && \
    jar -cvf bundle.jar . && \
    cp bundle.jar ~/Desktop/ && \
    open https://s01.oss.sonatype.org/#staging-upload