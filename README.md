<!--suppress HtmlDeprecatedTag, XmlDeprecatedElement -->
<center><img alt="mod preview" src="https://cdn.modrinth.com/data/MAd0Z2MD/images/bd6c08ac189a07ee4ce15a19e7a0983ff85a9b51.png"/></center>

<center>
Would you be a doll? I certainly would, so I made this collection of toyful tweaks for #dollgamers
</center>

---

### Doll Facts

- dolls are small!
- dolls don't always feel like speaking up (sorta like a mixed team + proximity chat)
- dolls can be used to play dress up!
- dolls can be made of a variety(5) of materials
- dolls can be secured to a player's shoulders with ribbon (shift+rclick ribbon to force dolls off)
- dolls can ride entities friendly to them with ribbon (must be tamed and big enough to hold the doll)
- dolls can be renamed (only in world) with a nametag (dolls can see their own in f5 too) (this is cleared when you become not-doll)
- dolls can also rename themselves by throwing the nametag into position (look straight up)
- dolls are immune to drowning and freezing
- dolls are sustained by repairs instead of food (use your dollcraft item with sticks/clay/string/resin brick/gold nugget in inventory)
- dolls can also be repaired by others with the right tool and material

there's also two potion effects you may see along the way - one's effect is reasonably obvious, and the other makes you hurt more.

---

### How To:

you'll need to craft one of the five dollcraft items to become a doll!
* carving knife - like an iron sword but with iron nuggets
* modeling tool - like a carving knife but with two more nuggets on the sides
* sewing needle - like a fishing rod without the string. and made of iron nuggets
* flush cutter - a cross of sticks but with iron nuggets on the top and one of the sides
* watchmaker's screwdriver - a stick with an iron nugget above and to the right

once you've got that ready, just grab a fragment of your self's essence (conveniently works like a bundle - right click yourself!)
and combine your dollcraft item with the essence fragment in crafting - congrats, you're crafting your essence now!
now pop that right back in with the rest of your self's essence (again, much like a bundle - left click yourself!)
and voila, you're a doll!

ah, if you wish to return to being a player and not a doll, do the same thing but with a pickaxe instead of a dollcraft item.

other players can play with you like an armor stand when they crouch,
and if they craft a ribbon (four strings around a stick),
they can tie you onto their shoulder with it!
(right click a doll to tie on, shift+rclick to untie a doll)

oh, and uhh.. as a doll, if you want others to hear you in chat, you may need to SPEAK UP a bit.
(or bypass this system with a \ in front of your message, but that's less fun and makes me sad.
there's also config to customize or disable this, which makes me happy!)

---

### Tweaks:

launching with the mod will generate a config at `<minecraft folder>/config/be_a_doll.toml`, which can be used to tweak
the chat interference that comes with being a doll.

you can also modify a few of the tags:

`.../tags/item/<material>_doll_care_materials` controls which items are usable as consumable care materials
for dolls of that <material> type

`.../tags/damage_type/doll_immune` makes dolls immune to all damage types within (by default just freezing and drowning)

`.../tags/damage_type/doll_modifies_message` will add new death message translation keys for when dolls die
of that damage type - if you add more, be sure to also add translations in your lang file.
this only works on damage types of the format `death.attack.<more...>`, 
and their dollified copies will follow the format `death.attack.doll.<more...>`

---

### [![Made for ModFest: Toybox](https://raw.githubusercontent.com/ModFest/art/v2/badge/svg/toybox/compact.svg)](https://modfest.net/toybox)
