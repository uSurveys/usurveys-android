# HOLY SHIT THIS IS COMPLEX

Followed a very nice guide here: https://getstream.io/blog/publishing-libraries-to-mavencentral-2021/
Some useful official documentation: https://central.sonatype.org/publish/publish-manual/

Setup:
1. install gpg
2. get the gpg file here: https://keys.openpgp.org/search?q=calderwoodra1113%40gmail.com
3. get the local.properties file from allen
4. mkdir musurveys-android-sdk/build/bundle/
5. mkdir ~/Desktop/publish

Deploy:
1. Update all instances of the current version of the library (ex. 0.3.0) to the next version.
2. $ bash publish <next-version>
3. Select: Artifact bundle
4. Upload all of the contents of ~/Desktop/publish/<next-version>/
5. Go to staging repositories
6. Select all, then click the release button (check the automatically drop checkbox)