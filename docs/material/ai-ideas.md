# Strategic Algorithms:
Strategic Decision Making involves a group of characters/agents in order to achieve goals.

# Tactical Algorithms:
- Decision Making involves characters/agents; More limited to one individual!
- Tactical algorithms analyze the threats and opportunities in the game environment  and those can be used to make decisions for single characters/agents or the strategy of the group.







## Threads:
- Enemy Detection
- unknown presence of enemy 
  - in hidden positions
  - on top of higher ground
  - presence behind walls, hills, higher ground
## Opportunities:
- Hidden Positions (e.g. in a forest, behind a hill/wall)
- superior position (e.g. higher ground)
- overwatched terrain and positions

# AI Structure
- Level is loaded into AI Engine
- AI behavior is created from level data and registered with the ai engine
- Game Engine regularly calls update on the AI Engine
  - ai engine updates behavior 
  - getting info from world via interface
  - ai engine applies output to game data