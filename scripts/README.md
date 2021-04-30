# HOLY SHIT THIS IS COMPLEX

Followed a very nice guide here: https://getstream.io/blog/publishing-libraries-to-mavencentral-2021/
Some useful official documentation: https://central.sonatype.org/publish/publish-manual/

Setup:
1. install gpg
2. get the gpg file here: https://keys.openpgp.org/search?q=calderwoodra1113%40gmail.com
3. get the local.properties file from allen

Instructions to upload a release:
0.  Replace the version numbers in each step to match usersneak/build.gradle PUBLISH_VERSION
1.  $ ./gradlew usersneak:publishReleasePublicationToMavenLocal
2.  $ cd usersneak/build/
3.  $ mkdir bundle/
4.  $ cp libs/usersneak-0.1.0-sources.jar bundle/musurveys-android-sdk-0.1.0-sources.jar
5.  $ cp publications/release/pom-default.xml bundle/pom.xml
6.  $ cp outputs/aar/usersneak-release.aar bundle/musurveys-android-sdk-0.1.0.aar
7.  $ cd bundle/
8.  $ gpg -ab pom.xml
9.  $ gpg -ab musurveys-android-sdk-0.1.0-sources.jar
10. $ gpg -ab musurveys-android-sdk-0.1.0.aar
11. $ jar -cvf bundle.jar .
12. $ cp bundle.jar ~/Desktop/
13. $ open https://s01.oss.sonatype.org/#staging-upload
14. Select: Artifact bundle
15. Upload ~/Desktop/bundle.jar
16. Click the release button (automatically drop checkbox)
