# jcesd
Simple Java CES-D (Center for Epidemiologic Studies Depression Scale CES-D) screen test

## Information
The Center for Epidemiologic Studies Depression Scale (CES-D) is one of the most common screening tests for helping an individual to determine his or her depression quotient [1]. This simple Java 7 - Netbeans 7.0, Swing app was part of the _"Research Methodologies in Neuroscience"_ of _"Advanced Computing and Communication Systems - Intelligent Systems"_ MSc programm ran by Aristotle University Thessaloniki - Dept. Electrical & Computer Engineering. This app scientific guidance, translation and (scale) standardization was held by Konstantinos N. Fountoulakis, MD, Professor of Psychiatry (Aristotle University of Thessaloniki, AHEPA University Hospital Thessaloniki, Greece) [2].  

## Exceptions
App was developed in 2010, as such technology stack is deprecated. 

## Purpose
This app's main objective was to collect and save questionnaire data from subjects. The research's outcomes are presented here [3] 

***CES-D test is in Greek. In order to use it follow the steps below***

## Setup with ant
1. Download JDK 1.7, set JAVA_HOME and in your path [4]
2. Download Apache Ant 1.7.0 and set it your path [5]
3. Download Netbeans 7.0 - 8.2 (optional)
4. Checkout sources
5. clean, compile and run

`$ ant -buildfile ./jbuild.xml run`

## Setup with Netbeans (optional)
Netbeans' ant scripts will use build.xml and ./nbproject folder
1. Open Netbeans
2. File -> Open Project -> /the/path/you/checked/out/git/sources/ -> choose jcesd -> Open Project
3. Run -> choose main class -> com.dimipet.jcesd.JCESDApp 

# References
1. http://counsellingresource.com/quizzes/cesd/index.html, access 04-12-2010
2. https://www.med.auth.gr/en/users/fountoul, access 04-12-2020 
3. Kampakis S., Mihailidis N., Petridis D., Siatra V., Terzopoulou A., Tsironis T., Fountoulakis K., (2011), Validity and reliability of the electronic versions of the CES-D questionnaire and the Theory of mind - Picture stories test, 2nd International Congress on Neurobiology, Psychopharmacology & Treatment Guidance
4. https://www.oracle.com/java/technologies/javase/javase7-archive-downloads.html Oracle JDK 7, access 17-06-2022
5. https://archive.apache.org/dist/ant/binaries/ Apache Ant old binary releases, access 17-06-2022
