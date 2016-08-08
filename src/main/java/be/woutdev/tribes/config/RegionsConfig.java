package be.woutdev.tribes.config;

import be.woutdev.tribes.enums.RegionShape;
import be.woutdev.tribes.model.Level;
import be.woutdev.tribes.model.Rank;
import be.woutdev.tribes.model.RegionBlock;
import com.google.common.collect.Lists;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Wout on 3-8-2016.
 */
public class RegionsConfig extends Config
{
    public RegionsConfig(Plugin plugin)
    {
        super(plugin, "config.yml");
    }

    /**
     * This loads all the RegionBlocks from the configuration.
     *
     * @return A list with all RegionBlocks from the configuration.
     */
    public List<RegionBlock> getRegionBlocks()
    {
        List<RegionBlock> regions = Lists.newArrayList();

        for (String region : getFileConfiguration().getKeys(false))
        {
            ConfigurationSection regionSection = getFileConfiguration().getConfigurationSection(region);

            int id = Integer.parseInt(region.replaceAll("\\D+", ""));
            String name = regionSection.getString("region-name");
            String parent = regionSection.getString("parent");

            ItemStack stack = new ItemStack(regionSection.getInt("flag.type"), 1);
            ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', regionSection.getString("flag.name")));
            List<String> lore = regionSection.getStringList("flag.lore");
            for (int i = 0; i < lore.size(); i++)
            {
                lore.set(i, ChatColor.translateAlternateColorCodes('&', lore.get(i)));
            }
            meta.setLore(lore);
            stack.setItemMeta(meta);

            int regionReset = regionSection.getInt("region-reset");

            RegionShape shape = RegionShape.valueOf(regionSection.getString("shape"));

            int bottomY = regionSection.getInt("area-bottom");

            int topY = regionSection.getInt("area-top");

            int externalRadius = regionSection.getInt("external-radius");

            List<Level> levels = new ArrayList<>();

            for (String level : regionSection.getConfigurationSection("levels").getKeys(false))
            {
                ConfigurationSection levelSection = regionSection.getConfigurationSection("levels." + level);

                int levelId = Integer.parseInt(level.replaceAll("\\D+", ""));
                String levelName = levelSection.getString("name");
                int exp = levelSection.getInt("exp");
                int internalRadius = levelSection.getInt("internal-radius");

                Set<Integer> restrictedBlocks = new HashSet<>();

                levelSection.getIntegerList("restricted-block-list").forEach(restrictedBlocks::add);

                levels.add(new Level(levelId, levelName, exp, internalRadius, restrictedBlocks));
            }

            List<Rank> ranks = new ArrayList<>();

            for (String rank : regionSection.getConfigurationSection("ranks").getKeys(false))
            {
                ConfigurationSection rankSection = regionSection.getConfigurationSection("ranks." + rank);

                int rankId = Integer.parseInt(rank.substring(rank.length() - 1));
                String rankName = rankSection.getString("name");
                double healDelay = rankSection.getDouble("heal-buff");
                double feedDelay = rankSection.getDouble("food-buff");

                ranks.add(new Rank(rankId, rankName, healDelay, feedDelay, rankSection.getBoolean("protector"),
                                   rankSection.getBoolean("cook"), rankSection.getBoolean("gardener"),
                                   rankSection.getBoolean("warrior"), rankSection.getBoolean("builder"),
                                   rankSection.getBoolean("engineer"), rankSection.getBoolean("chemist"),
                                   rankSection.getBoolean("wizard"), rankSection.getBoolean("chiefs-aide")));
            }

            regions.add(
                    new RegionBlock(id, name, parent, stack, regionReset, shape, bottomY, topY, externalRadius, levels,
                                    ranks));
        }

        return regions;
    }
}