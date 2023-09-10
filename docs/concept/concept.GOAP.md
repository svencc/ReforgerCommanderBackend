Concept of GOAP running AI Commander in RECOM
====================
***(Goal-Oriented Action Planning)***

**Author:** *Me*

# Introduction
In the dynamic world of strategy games, artificial intelligence (AI) is a pivotal factor in crafting an immersive and challenging player experience. 

One of the techniques utilized in the development of AI for strategy games is Goal-Oriented Action Planning (GOAP), a planning system that enables AI agents to respond flexibly and intelligently to the ever-changing conditions in the game world.

GOAP is based on three core components: **goals**, **actions** and **world state**.
Let's delve into these elements in detail.

## Goals
**Goals** represent the specific intentions or states that an AI agent seeks to achieve. In a strategy game, the goals could be diverse, ranging from gathering certain resources to conquering strategic points on the map. 

Defining clear and reachable goals is the first step in developing an effective GOAP system, as they dictate the direction and focus of the AI agents.

## Actions
**Actions** are the concrete steps that an AI agent can take to reach its goals. 

Each action has certain prerequisites and produces specific effects in the game world. 

For instance, a "gather" action might require the agent to be near a resource source and have the effect of increasing the agent's resources. 

Actions are the building blocks of GOAP plans, and their careful definition allows AI agents to devise complex and intelligent strategies.

## World-State
The **World-State** is a representation of the current state of the game world, encompassing all relevant information that the AI agents need to make decisions. 

The world-state could include information about the position and health status of units, the state of technology research, control over various territories, and other vital data. 

The world-state serves as the foundation for the AI agents' planning, enabling them to understand and anticipate the impacts of their actions on the game world.

## Agents
In the context of Goal-Oriented Action Planning (GOAP) and artificial intelligence (AI) more broadly, an "agent" refers to an entity that can perceive its environment through **sensors** and act upon that environment through **actuators** based on a set of rules, a plan, or a policy derived from an AI algorithm.

In the domain of strategy games, an agent could be a character or unit controlled by the game's AI system. 

These agents have the following characteristics:
1. **Autonomy**: Agents have a degree of autonomy, which allows them to perform tasks without constant guidance from the player or the developer.
2. **Reactivity**: Agents can perceive changes in their environment (the game world) and react to them in real-time, adjusting their behavior and strategies based on the current state of the world.
3. **Proactivity**: Agents not only react to the environment but also take the initiative to achieve their goals, which are defined in the GOAP system.
4. **Goal-Oriented Behavior**: Agents have specific goals they are trying to achieve, and their actions are directed towards achieving these goals.
5. **Learning and Adaptability**: Although not always a requirement, in more advanced AI systems, agents can learn from their experiences and adapt their behavior over time to become more effective at achieving their goals.

In summary, an agent in the context of GOAP is a virtual entity that can perceive its environment and take actions to achieve its goals based on its understanding of the world state and the set of available actions. 

It is the central actor in the GOAP system, utilizing the planning system to navigate and interact with the game world dynamically and intelligently.

In the context of artificial intelligence and robotics, "sensors" and "actuators" are critical components that enables interaction with the environment. 

Let's delve into each term individually.

### Sensors
Sensors are devices or mechanisms that allow an agent (like a robot or a virtual entity in a video game) to perceive its surroundings or environment. 

They collect data from the environment, which the agent can then use to make informed decisions. 

In the context of video games, sensors might be virtual constructs that allow an AI agent to gather information about the game world. 

Here are some examples:
1. **Visual Sensors**: These could be used to perceive the environment visually, identifying objects, obstacles, and other entities in the game world.
2. **Audio Sensors**: These might allow the agent to perceive sound cues in the game environment.
3. **Proximity Sensors**: These could be used to detect nearby entities or objects, helping the agent to avoid collisions or to find targets.
4. **Data Sensors**: These could be used to gather data on various game metrics, such as the health status of units, resource levels, etc.

### Actuators
Actuators are devices or mechanisms that allow an agent to interact with its environment by performing actions based on the data received from the sensors. 

In video games, actuators might represent the set of possible actions that an AI agent can perform in the game world. 

Here are some examples:
1. **Movement Actuators**: These would allow the agent to move within the game world, navigating towards targets or away from threats.
2. **Manipulation Actuators**: These might allow the agent to interact with objects in the game world, such as picking up items or using tools.
3. **Communication Actuators**: These could allow communication with other agents or entities in the game world, allowing for coordinated actions or strategies.
4. **Combat Actuators**: In strategy games, these might allow the agent to engage in combat, using weapons or abilities to attack enemies.

In summary, **sensors** and **actuators** work in tandem to realize intelligent behavior in AI agents, with **sensors** providing the data needed to make decisions and **actuators** allowing the agent to carry out actions based on those decisions. 

They are fundamental to the functioning of any AI system, enabling the perception-action cycle that is central to AI behavior.

## Conclusion
Through the adept combination of goals, actions, and understanding of the world-state, the GOAP system empowers AI agents in strategy games to create adaptive and realistic plans. 

Implementing GOAP requires careful consideration and elaboration of these three elements to create an AI that is both challenging and convincing.

# Goals
Goals describe the primary objectives AI agents aim to achieve in strategy games, steering their actions and strategies throughout the gameplay.

They are central in guiding the AI to success, providing clear targets to work towards. 

Here are some common goals utilized in GOAP systems:
- **Maximize Resources**: A goal could be to collect a certain amount of resources.
- **Base Expansion**: A goal could be to expand the base by constructing new buildings and structures.
- **Eliminate enemy units**: One goal could be to eliminate all enemy units.
- **Area Securing**: A goal could be to secure or control a specific area on the map.
- **Technological Superiority**: A goal could be to research a specific technology to gain an advantage over the opponent.
- **Survival**: A basic goal could simply be to survive and protect your base from enemy attacks.

# Actions
Actions are the specific steps AI agents take to achieve their goals in strategy games. 

They define the capabilities of the agents, dictating how they interact with the game environment to fulfill their objectives. 

Below, we outline a range of actions commonly integrated into GOAP systems:
- **Collect/Mine Resources**: Units could have actions to collect resources such as wood, stone or food.
- **Build**: Units could construct buildings or structures.
- **Attack/Defend**: Units could have actions to attack enemy units or defend themselves or a base.
- **Explore**: Units could explore new areas of the map or research technologies to improve their abilities.
- **Heal/Repair**: Units could heal other units or repair damaged structures.
- **Trade**: In some games, units may perform trade actions to exchange or sell resources.

# WorldStates
Various **world states** and **effects** can be used to control the dynamics of the game and the decisions of the AI. 

Here are some examples:
- **Resource Levels**: The current amounts of available resources such as wood, food, gold, etc.
- **Health Status**: The health status of units and structures.
- **Technology Level**: The current state of technology research and development.
- **Territorial Control**: Which areas are under the control of which faction.
- **Threat Level**: The current threat from enemy units or natural disasters.
- **Population Size**: The number of units a player controls

# Effects - The Bridge Between Actions and Goals
**Effects** in a GOAP strategy game system refer to the effects that certain actions have on the world states. 

They are the concrete results or changes caused by performing an action in the game world.

For example, the action of collecting resources could have the effect of increasing the resource level. 

Effects are an integral part of the planning process because they allow the GOAP Planner to evaluate the potential impact of various actions and create a plan that changes the World-States in a way that achieves a specific goal.

Effects are therefore central to the functioning of a GOAP system, as they form the bridge between actions and goals by defining how actions change world states and thus influence the achievability of goals.

Here are some examples of effects that could be used in a GOAP system:
- **Resource Change**: Changes in resource levels through gathering, consuming, or trading.
- **Health Change**: Changes in health status through healing, damage, or natural regeneration.
- **Technology Upgrades**: Improvements in the technology level through research and development.
- **Territorial Gain/Loss**: Changes in territorial control through conquest or loss of territories.
- **Threat Level Change**: Changes in the threat level through enemy actions or natural events.
- **Population Change**: Changes in the population size through birth, death, or recruitment of units.

These world states and effects would serve as the basis for the AI's planning and decision-making. 

Actions in a GOAP system would be defined in terms of their impacts on these world states and effects, and the planner would try to find a sequence of actions that alters the world states in a way that achieves a specific goal.

# Actions and Preconditions
In the context of GOAP (Goal-Oriented Action Planning), "actions" refer to the specific tasks or operations that an AI agent can perform in the game environment. 

Each action is defined not just by its outcome but also by its "preconditions," which are the specific circumstances or states that must be met for the action to be carried out.

Example: Let's consider a simple action in a strategy game: "**Gather Wood**."
- **Action**: Gather Wood
- **Preconditions**: 
  - The agent must be near a forest.
  - The agent must have a tool for gathering wood (e.g., an axe).
- **Effect**: Increase in the agent's wood resource.

In this scenario, the action cannot be initiated unless all the preconditions are satisfied. 

Once the action is successfully completed, it will have a defined effect on the world state, which, in this case, is an increase in the agent's wood resource.

# Introducing the GOAP Planner
The GOAP planner is a sophisticated tool in AI programming that enables dynamic decision-making for AI agents based on the current world state and a set of predefined goals. 

Here is a step-by-step breakdown of the tasks executed by a GOAP planner:
- **1. Goal Setting:**
    - The first step involves defining the goals that the AI agent aims to achieve. 
    - These goals are based on the current world state and the overarching objectives of the agent in the game context.
- **2. Action Selection:**
  - Based on the defined goals, the planner selects a set of potential actions that can be performed by the agent. 
  - This set includes all actions that are theoretically possible given the current world state.
- **3. Checking Preconditions:**
  - For each potential action, the planner checks the preconditions associated with that action to determine if it can be executed in the current world state.
- **4. Plan Formation:**
  - Once the feasible actions are identified, the planner forms a plan by arranging a sequence of actions that lead to the achievement of the defined goal. 
  - This is often done using a planning algorithm that considers the effects of each action on the world state to find the most efficient path to the goal.
- **5. Execution:**
  - With a plan in place, the AI agent then executes the actions one after the other, constantly monitoring the world state and adjusting the plan as necessary based on the changing conditions in the game environment.
- **6. Feedback and Adjustment:**
  - After the execution of the plan, the planner receives feedback on the outcome, which is used to make adjustments for future planning, enabling a learning and adaptive AI system.
  - Through this process, the GOAP planner enables AI agents to navigate complex game environments with a high degree of autonomy and intelligence, creating a dynamic and immersive gaming experience.

# GOAP FSM
In AI development for video games and other simulations, Goal-Oriented Action Planning (GOAP) often works in conjunction with Finite State Machines (FSM) to create intelligent and adaptable agent behaviors. 

Let's delve into how GOAP utilizes FSM.

## Finite State Machines (FSM)
Before we get into how GOAP uses FSM, it's essential to understand what an FSM is. 

A Finite State Machine is a mathematical model of computation used to design algorithms. 

In the context of AI in video games, an FSM would represent different states that an AI agent can be in, with transitions between these states being triggered by events or conditions.

## Integration with GOAP
In a GOAP system, FSM can be used to manage the high-level states of an AI agent, such as "Idle," "Gathering Resources," "Building," or "Attacking." 

Each of these states can then leverage the GOAP system to dynamically generate a plan of actions to achieve the goals associated with that state.

Here is how GOAP integrates with FSM:

- **State Representation:** Each state in the FSM represents a different behavior or objective for the AI agent. The GOAP system can be invoked to generate a plan of actions to achieve the goal associated with the current state.
- **Dynamic Transition:** FSM allows for dynamic transitions between different states based on the outcomes of the actions executed through the GOAP plans. For instance, if the "Gathering Resources" plan is completed successfully, the FSM might transition the agent to a "Building" state.
- **Real-Time Adaptability:** The combination of GOAP and FSM allows for real-time adaptability. If the world state changes in a way that makes the current plan unachievable, the GOAP system can generate a new plan, and the FSM can potentially transition the agent to a different state to adapt to the new circumstances.
- **Hierarchical Planning:** By using FSM and GOAP together, developers can create a hierarchical planning system where the FSM manages high-level goals and states, while the GOAP system handles the detailed planning of the actions needed to achieve those goals.

## Example

Let's consider a simple example in a strategy game:
- **Idle State:** The agent starts in an idle state with no specific goal.
- **Resource Gathering State:** Based on the world state, the FSM transitions the agent to a resource-gathering state. The GOAP system then generates a plan of actions to gather the necessary resources.
- **Building State:** Once the resources are gathered, the FSM transitions the agent to a building state, where the GOAP system generates a new plan to construct a building using the gathered resources.

## Conclusion

By integrating GOAP with FSM, developers can create AI agents that have both the high-level strategic decision-making capabilities provided by FSM and the dynamic, goal-oriented planning capabilities provided by GOAP, resulting in AI agents that can behave intelligently and adaptively in a complex, changing environment.

# Glossary
| Term                | Description                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     |
|---------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **GOAP (Goal-Oriented Action Planning)** | A planning system utilized in AI development to facilitate dynamic decision-making for AI agents based on predefined goals and the existing state of the environment.                                                                                                                                                                                                                                                                                                                         |
| **Agent**           | An entity capable of perceiving its environment and executing actions to meet specific goals. Agents are equipped with a list of actions they can perform.                                                                                                                                                                                                                                                                                                                                     |
| **Goals**           | The objectives or targets that AI agents strive to achieve. Goals dictate the actions and strategies agents undertake throughout the gameplay.                                                                                                                                                                                                                                                                                                                                                 |
| **Actions**         | Specific tasks or operations that agents can perform in the environment. Each action is characterized by preconditions and effects, and is a fundamental step within a plan that influences the game world in some way. Actions are aware of their validity and the effects they will have on the world state.                                                                                                                                                                                |
| **Preconditions**   | The set of conditions or states that must be satisfied for an action to be initiated. Preconditions ensure actions are undertaken in the appropriate context and are essential for determining the viability of an action at a given point in time.                                                                                                                                                                                                                                             |
| **Effects**         | The outcomes or changes in the world state that result from the execution of an action. Effects depict how actions influence the environment and are the changes to the state post the execution of an action.                                                                                                                                                                                                                                                                               |
| **World State**     | A representation of the current state of the environment, encompassing all the pertinent information that agents require to make informed decisions.                                                                                                                                                                                                                                                                                                                                           |
| **Planner**         | A component of the GOAP system responsible for creating a plan of action for the agents. It selects and sequences actions based on their preconditions and effects to achieve the defined goals.                                                                                                                                                                                                                                                                                                |
| **Sensors**         | Virtual constructs or devices that facilitate agents in perceiving the environment and gathering the necessary information for decision-making.                                                                                                                                                                                                                                                                                                                                                |
| **Actuators**       | Mechanisms that enable agents to interact with the environment by executing specific actions based on the data acquired through sensors.                                                                                                                                                                                                                                                                                                                                                       |
| **Plan**            | A sequence of actions devised by the planner to achieve a specific goal, considering the current world state and the preconditions and effects of individual actions. A plan takes the agent from a starting state to a state that satisfies a goal.                                                                                                                                                                                                                                             |
| **Feedback Loop**   | A system where the outcomes of actions are fed back into the planning process, allowing for adjustments and adaptations in response to the changing environment.                                                                                                                                                                                                                                                                                                                              |
| **Dynamic Decision Making** | The process of making decisions in real-time, adapting to the changing conditions of the environment to work towards achieving the goals effectively.                                                                                                                                                                                                                                                                                                                                         |
| **Cost**            | Each action has an associated cost value, which the GOAP planner utilizes to calculate the plan and determine which plan entails less effort, thereby choosing the most efficient pathway to achieve the goal.                                                                                                                                                                                                                                                                                 |
