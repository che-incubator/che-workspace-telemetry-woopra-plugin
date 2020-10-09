[![Master Build Status](https://ci.centos.org/buildStatus/icon?subject=master&job=devtools-che-workspace-telemetry-woopra-plugin-build-master/)](https://ci.centos.org/view/Devtools/job/devtools-che-workspace-telemetry-woopra-plugin-build-master/)
[![Nightly Build Status](https://ci.centos.org/buildStatus/icon?subject=nightly&job=devtools-che-workspace-telemetry-woopra-plugin-che-nightly/)](https://ci.centos.org/view/Devtools/job/devtools-che-workspace-telemetry-woopra-plugin-che-nightly/)

[![Contribute](https://camo.githubusercontent.com/7ca4f6be43fb5eb61a73ba6d40b3481d93ef5813/68747470733a2f2f6368652e6f70656e73686966742e696f2f666163746f72792f7265736f75726365732f666163746f72792d636f6e747269627574652e737667)](https://che.openshift.io/f?url=https://github.com/redhat-developer/che-workspace-telemetry-woopra-plugin)

# che-workspace-telemetry-woopra-plugin

## Prerequisites

This repo depends on packages in the [GitHub maven package registry](https://github.com/features/packages).
A [personal access token](https://github.com/settings/tokens) with `read:packages` access is required to pull down
dependencies from GitHub.

To compile a native image, you also need [GraalVM 19.2.1](https://www.graalvm.org/) and `native-image`
installed.  For now, this repository uses quarkus release `1.0.0.Final` and only works with GraalVM 19.2.1.  This will eventually
be upgraded allowing for later versions of GraalVM.

Add a repository entry in `$HOME/.m2/settings.xml`:

```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                      http://maven.apache.org/xsd/settings-1.0.0.xsd">
   <servers>
      <server>
         <id>che-incubator</id>
         <username>YOUR GITHUB USERNAME</username>
         <password>YOUR PERSONAL ACCESS TOKEN</password>
      </server>
   </servers>

   <profiles>
      <profile>
         <id>github</id>
         <activation>
            <activeByDefault>true</activeByDefault>
         </activation>
         <repositories>
            <repository>
               <id>central</id>
               <url>https://repo1.maven.org/maven2</url>
               <releases><enabled>true</enabled></releases>
               <snapshots><enabled>false</enabled></snapshots>
            </repository>
            <repository>
               <id>che-incubator</id>
               <name>GitHub navikt Apache Maven Packages</name>
               <url>https://maven.pkg.github.com/che-incubator/che-workspace-telemetry-client</url>
            </repository>
         </repositories>
      </profile>
   </profiles>
</settings>
```

## Building

`mvn package -Pnative`

## Building the Docker Image

### If you have previously built the native binary with `mvn package -Pnative`:

`docker build -f src/main/docker/Dockerfile.native -t image-name:tag .`

### To use the multistage Dockerfile to build the GraalVM native image and docker image

The `Dockerfile` needs two build arguments:

+ `GITHUB_USERNAME` - the GitHub username you intend to use to build the native image.  This user should have an access token with `read:packages` permissions associated with it.
+ `GITHUB_TOKEN` - a GitHub Personal Access Token with `read:packages` permissions to pull down the necessary Maven dependencies.

Then pass in the build arguments:

`docker build --build-arg GITHUB_USERNAME=<github username> --build-arg GITHUB_TOKEN=<personal access token> -f src/main/docker/Dockerfile.multi -t image-name:tag .`

## Creating a Development Image and Testing on Che

The Full flow to test a new version of this plugin on Eclipse Che is:

- Build a new version of the image with any additional TLS certificates added to the trust store
- Build a custom version of the [`che-plugin-registry`](https://github.com/eclipse/che-plugin-registry) referencing your development version of the image
- Set the `spec.server.pluginRegistryImage` and `spec.server.pluginRegistryPullPolicy`  fields in the `cheCluster` CR.

Eclipse Che by default secures the Che API with TLS certificates.  In order to build your own development image to test on Che,
you need to add the TLS certificate of the Che API to your Java truststore, and you need to tell Quarkus where that trust store is.
Since native compilation behaves differently than running the application on a JVM, you need to pass in the trust store at build time
through the `quarkus.native.additional-build-args` property.  This repo has a Dockerfile that will build an image and set the trustStore
at build-time.  The Dockerfile expects a modified trustStore at the root of this repository with the name `cacerts`.

To get the eclipse che certificate:

```shell
openssl s_client -connect <eclipse che url>:443
# Copy the certificate that is sent back and save it as che.crt
keytool -import -alias development-che-cert -keystore $JAVA_HOME/lib/security/cacerts -file che.crt
# Default keystore password is 'changeit'
# cp $JAVA_HOME/lib/security/cacerts .
```

To build the image, add an additional property to `quarkus.native.additional-build-args` to `pom.xml`, adding the location of the trust store in the docker image (`/tmp/cacerts`):


```xml
        <properties>                                                                                                                                                                                                                          
          <quarkus.package.type>native</quarkus.package.type>                                                                                                                                                                                 
          <quarkus.native.additional-build-args>-J-Djavax.net.ssl.trustStore=/tmp/cacerts,-H:DynamicProxyConfigurationResources=dynamic-proxies.json,--enable-all-security-services</quarkus.native.additional-build-args>                                                              
        </properties>     
```
 
 
Build the image using the development Dockerfile:

```shell
docker run --build-arg GITHUB_USERNAME <username to download packages from the GitHub registries> --build-arg GITHUB_TOKEN <personal access token> -f src/main/docker/Dockerfile.dev -t $YOUR_DOCKER_REGISTRY/$YOUR_ORG/che-workspace-telemetry-woopra-plugin .
```

Then clone the [`che-plugin-registry`](https://github.com/eclipse/che-plugin-registry) repository, and add the image you just built
to the plugin registry:

```shell
mkdir -p v3/plugins/eclipse/che-workspace-telemetry-woopra-plugin
cd v3/plugins/eclipse/che-workspace-telemetry-woopra-plugin
mkdir -p 0.0.1/
echo "0.0.1" > latest.txt
# copy the meta yaml from this file to 0.0.1/ and change the image field to refernce your image
./build.sh
# re-tag and push the che-plugin-registry image
```

Then edit your `CheCluster`:

```shell
kubectl edit checluster eclipse-che -n eclipse-che
set pluginRegistryImage to the development image your created and pluginRegistryPullPolicy to Always
```

Delete the plugin registry pod to cause the pod to restart.  When you check the 'Plugins' tab of the Che dashboard you should
see the new plugin.  Enable the plugin in a workspace by setting the slider.

## Running Tests

`mvn verify`


## Publishing a new version of the plugin `meta.yaml` file

The plugin `meta.yaml` is hosted on a CDN at [static.developers.redhat.com](https://static.developers.redhat.com).  In order to push a new version, you will need the appropriate Akamai credential file, with the following layout:

```
[default]
key = key = <Secret key for the Akamai NetStorage account>
id = <NetStorage account ID>
group = <NetStorage storage group>
host = <NetStorage host>
cpcode = <NetStorage CPCode>
```

Save this file as `akamai-auth.conf`.

In the root of this repository, run:

```shell
docker run -w /root/app -v $(pwd):/root/app -v \
  /path/to/akamai-auth.conf:/root/.akamai-cli/.netstorage/auth \
  akamai/cli netstorage upload \
  --directory rh-che/plugins/che-workspace-telemetry-woopra-backend/0.0.1 \
  meta.yaml
```
