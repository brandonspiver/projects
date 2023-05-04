# Set the name of the project
PROJECT_NAME = group7

# Set the os type

OS_TYPE := $(shell uname)

# Set the path to the JavaFX runtime library


ifeq ($(OS_TYPE), Linux)
JAVA_FX_LIB = javaFX_lib/linux
else ifeq ($(OS_TYPE), Darwin)
JAVA_FX_LIB = javaFX_lib/macos
else ifeq ($(OS_TYPE), Windows_NT)
JAVA_FX_LIB = javaFX_lib/windows
else
$(error Unsupported OS: $(OS_TYPE))
endif

# Set the Maven executable
MVN = mvn

# Set the options for the JVM
JVM_OPTIONS = --module-path $(JAVA_FX_LIB) --add-modules javafx.controls,javafx.fxml

# Default target: compile and build the application
.PHONY: all
all: compile build
	

# Target to compile the Java files
.PHONY: compile
compile:
	mvn install
	$(MVN) clean compile

# Target to build the application
.PHONY: build
build:
	$(MVN) package

# Target to run the ReceiverGUI application
.PHONY: server
server:
	java $(JVM_OPTIONS) -cp target/classes:target/$(PROJECT_NAME)-1.0-SNAPSHOT.jar:fontawesomefx-8.1.jar com.group7.server.Server javax.jar

# Target to run the Sender application
.PHONY: client
client:
	java $(JVM_OPTIONS) -cp target/classes:target/$(PROJECT_NAME)-1.0-SNAPSHOT.jar:fontawesomefx-8.1.jar com.group7.client.Client javax.jar
