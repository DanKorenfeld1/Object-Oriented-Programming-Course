# Object-Oriented Programming Course Projects

This repository showcases the projects I completed during my Object-Oriented Programming course. Each project demonstrates different aspects of OOP and software design principles.

## Project 1: PEPSE (Precise Environmental Procedural Simulator Extraordinaire)

PEPSE is a 2D world simulation that demonstrates complex object interactions and environmental modeling.

### Key Features
- Day-night cycle with dynamic lighting
- Controllable avatar with running and jumping capabilities
- Procedurally generated terrain
- Trees with leaves that respond to wind
- Energy-giving fruits

### Technical Highlights
- **Observer Pattern**: Implemented for notifying objects about avatar actions, particularly jumping.
- **Facade Pattern**: Used the `Flora` class to simplify the complex process of forest creation.
- **Callback Mechanisms**: Extensively used for flexible behavior implementation, especially in environmental interactions.

### Lessons Learned
- Modular design principles allowing for easy system extensions
- Effective use of design patterns in a practical scenario
- Balancing performance with complex, real-time simulations

## Project 2: ASCII Art Generator

An application that converts image files to ASCII art, showcasing file I/O, image processing, and user interface design.

### Key Components
- Image to ASCII conversion algorithm
- Adjustable output resolution
- Command-line interface for user interaction

### Technical Highlights
- **Strategy Pattern**: Implemented for different ASCII character selection methods.
- **Factory Method**: Used for creating various types of outputs (Console/HTML).
- Efficient algorithms for character brightness calculation and mapping.

### Lessons Learned
- Importance of choosing appropriate data structures for performance optimization
- Designing flexible systems that can easily accommodate new features (e.g., new output formats)
- Balancing code reusability with performance considerations

## Project 3: Bricker Game

A Breakout-style game that demonstrates core OOP concepts and game development principles.

### Key Elements
- Player-controlled paddle
- Physics-based ball movement
- Variety of brick types with special effects

### Technical Highlights
- **Strategy Pattern**: Implemented for different collision behaviors.
- **Decorator Pattern**: Used to combine multiple collision strategies dynamically.
- **Observer Pattern**: Utilized for updating the game state and UI elements.

### Lessons Learned
- Practical application of design patterns in game development
- Importance of clean, maintainable code in an interactive, real-time system
- Balancing object-oriented design with performance requirements in gaming contexts

## Reflections on OOP and Software Design

Throughout these projects, I gained hands-on experience with:

- Applying SOLID principles, particularly the Single Responsibility and Open/Closed principles
- Creating UML diagrams to plan and communicate system architecture
- Implementing custom exceptions for better error handling and code robustness
- Utilizing generics for type-safe collections and algorithms
- Balancing theoretical OOP concepts with practical implementation concerns

These projects not only enhanced my programming skills but also deepened my understanding of software architecture and design principles. They provided valuable experience in creating modular, extensible, and maintainable code - skills that are crucial in real-world software development.