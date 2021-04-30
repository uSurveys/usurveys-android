## HOW-TO: bash publish.sh 1.0.0
## Be sure to update 1.0.0 to the same version number in each build.gradle file.
## TODO(allen): centralize the version number to the master gradle file

generate() {(
  version=$1
  name=$2
  cd "${name}/build/" &> /dev/null \
   || { echo "#### Build folder missing"; exit; }

  mkdir bundle/ &> /dev/null || rm -r bundle/* &> /dev/null \
    || { echo "#### failed to create bundle"; exit; }

  cp "libs/${name}-${version}-sources.jar".* "bundle/" \
    || { echo "#### Failed to copy sources"; exit; }

  cp "outputs/aar/${name}-release.aar" "bundle/${name}-${version}.aar" \
    || { echo "#### Failed to copy aar"; exit; }

  cp publications/release/pom-default.xml bundle/pom.xml \
    || { echo "#### Failed to copy pom.xml"; exit; }

  cd bundle/ || { echo "#### bundle directory missing"; exit; }

  gpg -ab pom.xml
  gpg -ab "${name}-${version}.aar"
  jar -cvf "${name}-${version}.jar" .

  cp "${name}-${version}.jar" "$HOME/Desktop/publish/${version}"
)}

version=$1
if [ -z "$version" ];
  then echo "Missing version" && exit
fi

# Create our local aars and jars
./gradlew publishToMavenLocal || exit

# Create a destination for all our compiled artifacts
mkdir "$HOME/Desktop/public" &> /dev/null \
 || mkdir "$HOME/Desktop/publish/${version}" &> /dev/null \
 || rm "$HOME/Desktop/publish/${version}/*" &> /dev/null

# Generate everything
echo "### musurveys-android-sdk ###"
generate "${version}" "musurveys-android-sdk"

echo "### musurveys-android-sdk-api ###"
generate "${version}" "musurveys-android-sdk-api"

echo "### musurveys-android-sdk-internal ###"
generate "${version}" "musurveys-android-sdk-internal"

echo "######"
echo ""
echo "Artifacts are located at: ${HOME}/Desktop/publish/${version}"
echo ""
ls "$HOME/Desktop/publish/${version}/"
echo ""
echo "######"
open "https://s01.oss.sonatype.org/#staging-upload"