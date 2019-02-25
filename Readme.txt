To run the pre-compiled program simply type the following command from this directory:

java -jar ETL-OALP.jar

You can compile a version of the executable yourself with gradle!
(This will not work on bluenose as gradle seems to lack permissions! Run on local environment ):

git clone git@github.com:aianta/data_mining_assignment.git
cd data_mining_assignment

chmod +x ./gradlew      //Set the gradle wrapper to be executable
dos2unix gradlew        //Run if you're getting a 'Cannot find file or folder error' and running on linux
./gradlew shadowJar     //Tell gradle to pull dependencies and compile sources into an executable jar

Then you can run the freshly built executable with:
java -jar build\libs\assignment2-1.0-SNAPSHOT-fat.jar

The only dependency I used was slf4j, a logging tool I used for debugging. This can be confirmed by checking the gradle.build file after
cloning the repository from github.



