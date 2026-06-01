![Banner](https://cdn.modrinth.com/data/cached_images/e7092321d1886a474d2a3b07a6210ce970db6cb2.webp)
**Ultimate Warp Pad | Inspired by Steven Universe**

Bring the magic of the Crystal Gems to your Minecraft server! Ultimate Warp Pad is a unique, lightweight, and highly interactive teleportation plugin inspired by the iconic warp pads from the animated series Steven Universe. Travel across your world in style with immersive particle effects, visualization, sounds, and GUI!

![feature](https://proxy.spigotmc.org/ec548d429cd1b8889e7786fa8eb2ff2e6c11ab38/68747470733a2f2f70726f78792e737069676f746d632e6f72672f623833613539623139333463613163343935623236376562656232613533666332353865653062632f3638373437343730373333613266326636393265363936643637373537323265363336663664326634643639373134333438333835383265373036653637)

- **Steven Universe Inspired:** Experience teleportation just like the Crystal Gems! Includes custom particle effects and sound cues when warping.
- **Interactive GUI Menu:** Players don't need to type long commands to travel. Simply step on a warp pad and open the beautifully designed Warp Selection GUI to choose your destination!
- **Instant & Seamless Travel:** Connect multiple locations across your server instantly. Perfect for survival servers, hubs, or RPG worlds.
- **Easy Setup:** Creating and linking warp pads is incredibly simple and intuitive for admins.
- **Highly Customizable:** Tweak messages, GUI layouts, Craft item, and warp settings via the config.
- **Lightweight & Optimized:** Designed to have minimal impact on your server's performance.

### Demo Video

**Ultimate Warp Pad - Steven Universe Style**

<iframe width="100%" height="450" 
        src="https://www.youtube-nocookie.com/embed/jtLe_bDiM8Q" 
        title="Ultimate Warp Pad Demo" 
        frameborder="0" 
        allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" 
        allowfullscreen>
</iframe>

<iframe width="100%" height="450" 
        src="https://www.youtube-nocookie.com/embed/7McfmQKyr7Q" 
        title="Ultimate Warp Pad Demo 2" 
        frameborder="0" 
        allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" 
        allowfullscreen>
</iframe>

**How It Works**
- Set the Warp: Simple just one command. Plugin will create a real block warp structure for you. and add it into Warp System.
- Travel: Players simply step onto the center of the pad, Press Shift and the Warp Selection GUI will appear, allowing them to travel to any other discovered/public warp pads!

![command](https://proxy.spigotmc.org/75b09d13783d69b2444074c9ac45caa60ce78b16/68747470733a2f2f70726f78792e737069676f746d632e6f72672f626239376137313236366139626361356631393938313039303963646465663533663435326230332f3638373437343730373333613266326636393265363936643637373537323265363336663664326633303633333333303434343437303265373036653637)

**Admin Command:** `uwp.admin`
- **/wpa create <warp_id>** - Creates a new warp pad at your current location.
- **/wpa delete <warp_id>** - Deletes an existing warp pad.
- **/wpa setting [warp_id]** - Setting your warp pad you standing on it.
- **/wpa give <player> [amount]** - Give a player warp creator item.
- **/wpa reload** - Reloads the plugin configuration.

**Player Command:** `uwp.user.*`
- **/wpp create <warp_id>** - Creates a new player warp pad at your current location.
- **/wpp delete <warp_id>** - Deletes an existing warp pad.
- **/wpp setting [warp_id]** - Setting your warp pad you standing on it.
- **/wpp trust <warp_id> <player>** - Add a player into your private warp network.

**Other Permisisons:**
- Add `uwp.user.create.<n>` Override max warps per player. Replace `<n>` with a number.
- `Add uwp.user.range.<n>` Add a custom range option to the range cycle. Replace `<n>` with a number.
