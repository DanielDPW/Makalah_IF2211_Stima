# IF2211 - Algorithm Strategies

<a href="https://github.com/DanielDPW/Makalah_IF2211_Stima/tree/main/docs"><strong>Documentation »</strong></a>
<br />
</p>

This repository contains an implementation made in Java for finding optimal route between two points in Minecraft

<br/>
<br/>
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
    </li>
    <li>
      <a href="#project-structure">Project Structure</a>
    </li>
    <li>
      <a href="#how-to-use">How To Use</a>
    </li>
  </ol>
</details>
<br/>

## About The Project

This project explores the application of the A* search algorithm, to solve complex pathfinding problems within the three-dimensional voxel world of Minecraft. The core principle involves modeling the game environment as a dynamically generated, weighted graph from the player's perspective. In this graph, nodes represent reachable positions, while the weighted edges correspond to the full spectrum of player actions, such as walking, sprinting, falling, and complex jumping maneuvers. The weight of each edge is determined by the traversal cost—calculated from the game's movement physics—which allows the model to account for the time and effort of each action. By utilizing the A* algorithm to find the path of least cost on this graph, the system can identify a route that is not merely the shortest geometrically, but is instead optimal in terms of efficient, human-like movement. This gives a more accurate way of mimicking the strategies employed by expert players to navigate difficult terrain.



## Project Structure
```ssh

```

## How To Use

1. Make sure the dependencies are installed (Gradle)

2. Clone the repository
    ```sh
    git clone https://github.com/DanielDPW/Makalah_IF2211.git
    ```

3. Change to repository's directory
    ```sh
    cd Makalah_IF2211_Stima
    ```

4. Build
    ```sh
    .\gradlew build
    ```

