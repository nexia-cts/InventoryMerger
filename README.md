# InventoryMerger
A server-side Fabric library mod to easily merge, save and load inventories.  
  
Allows you to add items to an inventory while following a specified layout. Can be used to let players create custom inventory layouts for kits that might change in the future.

## Merging

<img src="https://i.imgur.com/7Hwm7mS.png" width="400">

The example above was created with `InventoryMerger::merge`.  
  
- **Layout** represents the specified layout which the kit tries to follow, most likely something a player on your server can create. Items are different from **Result**, but the layout has been followed. 
- **Kit** contains all the given items. Therefore items in **Kit** are the same as items in **Result**.
- **Result** is what the player's inventory will look like after the "merge".
