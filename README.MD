# Introduction

A handy UML diagram generator that helps you:
- Understand your code by turning it into an UML class diagram or sequence diagram
- Write better code by detecting violations of [Dependency Inversion Principle](https://en.wikipedia.org/wiki/Dependency_inversion_principle)
- Find design patterns by highlighting them in the generated class diagram (feature in progress)

Architecture pattern:
Three-tier architecture pattern (i.e., presentation, bussiness logic, and database layer)

Design patterns used:
1. Strategy pattern
2. Factory Method pattern
3. Abstract Factory pattern (refactoring in progess)
4. Facade pattern (refactoring in progress)
5. Visitor pattern

Documentation generator:
- Javadoc

# Usage

Suppose you are using Eclipse,

* Clone this project from GitHub
* Include both jar files in the lib folder (ASMTreeTutorial-all.jar and plantuml.jar) into the Project Libaray in Run Configuration
* Add the target project into Project in Run Configuration
* Add a class name as the entry point of the parser in run configuration (Use fully qualified names, e.g. java.lang.String)
* After you run the parser, a png file named "happy_path.svg" will appear under the project root directory. Open it with your choice of image viewer
* Get insights into your project now!

# Documentation

# Contribute

# Credits

Developers:
Jerry Zheng, Timothy Ren, Joseph Zou, Jinhao Shen

School:
Rose-Hulman Institute of Technology