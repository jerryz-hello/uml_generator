# Introduction

A handy UML diagram generator that helps you:
- Understand your code by turning it into an UML class diagram or sequence diagram
- Write better code by detecting violations of [Dependency Inversion Principle](https://en.wikipedia.org/wiki/Dependency_inversion_principle)
- Find design patterns by highlighting them in the generated class diagram (feature in progress)

## Architecture pattern:
Three-tier architecture pattern (i.e., presentation, bussiness logic, and database layer)

## Design patterns used:
1. Strategy pattern
2. Factory Method pattern
3. Abstract Factory pattern (refactoring in progess)
4. Facade pattern (refactoring in progress)
5. Visitor pattern

## Documentation generator:
- Javadoc (in progress, a work sample could be found [here](docs/javadoc/DataSource/ASMClassDiagramParser.html))

# Usage

Suppose you are using Eclipse,

* Clone this project from GitHub
* Include both jar files in the lib folder (ASMTreeTutorial-all.jar and plantuml.jar) into the Project Libaray in Run Configuration
* Add the target project into Project in Run Configuration
* Add a class name as the entry point of the parser in run configuration (Use fully qualified names, e.g. java.lang.String)
* After you run the parser, a png file named "happy_path.svg" will appear under the project root directory. Open it with your choice of image viewer
* Get insights into your project now!

# Documentation

[UML Diagram Generator Documentation v1.0](docs/javadoc/index.html)

# Contribute

0. Star this project
1. Create a issue explaining what feature you plan to add
1. Fork a feature branch (e.g., detecting-design-pattterns)
2. Work on the feature 
3. When you are done, submit a PR and we'll respond ASAP
4. Once adopted, we'll add your name into the credits section below and let your name shines!

# Credits

Special thanks:
(We'd love your contribution!)

Developers:
Jerry Zheng, Timothy Ren, Joseph Zou, Jinhao Shen

School:
Rose-Hulman Institute of Technology