# <img src="https://github.com/ADHDMC/.github/blob/main/pictures/nerf-farms-logo.png?raw=true" width="75" height="75"> NerfFarms

[![Ko-Fi Support Link](https://img.shields.io/badge/Ko--fi-donate-FF5E5B?logo=ko-fi)](https://ko-fi.com/illogicalrhythmic)
[![wakatime](https://wakatime.com/badge/user/bf4f6f62-0e88-4b6f-8363-aac43611fe08/project/bcf0c9a5-da39-4756-898e-74ac8876a921.svg?style=flat)](https://wakatime.com/badge/user/bf4f6f62-0e88-4b6f-8363-aac43611fe08/project/bcf0c9a5-da39-4756-898e-74ac8876a921)

This plugin lets you have greater control over what farms can be functionally used on your server, without needing to limit spawn numbers.
You can 'nerf' mob kills based on many different checks such as:

- Spawn reason
- Damage reason
- If there is a valid path to the player
- If they have a line-of-sight to the player
- If they are too far away from the player

You can also control 
- how sensitive or lenient these rules are with `max-blacklisted-damage-percent`
- whether 'nerfed' mobs should drop only xp, only drops, or nothing at all
- whether these rules should only apply to hostile mobs, or all mobs

## Roadmap
- [ ] Add config for a message that is sent to the player when the mob they kill is nerfed
- [ ] Add a toggle command for that message
- [ ] Add a config option for allowing non-player kills (checkDamager method in MobDamageListener)
- [ ] Add configuration for max height diff between mob and attacker
- [ ] Move permissions to an Enum
- [ ] Move messages to an Enum

> **Warning**
> 
> This plugin is currently in a development state, you will need to delete and re-set your config on each update.
> 
> This plugin only works on Paper and forks of paper, it will not work on Spigot alone.

### Special Thanks

[Wuffeh](https://github.com/Wuffeh) for testing and working with us to develop this plugin.
