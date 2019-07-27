package com.songoda.ultimateclaims.gui;

import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.claim.Claim;
import com.songoda.ultimateclaims.member.ClaimMember;
import com.songoda.ultimateclaims.member.ClaimRole;
import com.songoda.ultimateclaims.utils.Methods;
import com.songoda.ultimateclaims.utils.ServerVersion;
import com.songoda.ultimateclaims.utils.gui.AbstractGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class GUIMembers extends AbstractGUI {

    private UltimateClaims plugin;
    private Player player;
    private Claim claim;
    private ClaimRole displayedRole = ClaimRole.OWNER;
    private int page = 1;
    private List<ClaimMember> allMembers;
    private List<ClaimMember> members;
    private List<ClaimMember> visitors;


    public GUIMembers(Player player, Claim claim) {
        super(player);
        this.player = player;
        this.claim = claim;
        plugin = UltimateClaims.getInstance();

        for (ClaimMember claimMember : claim.getMembers()) {
            if (claimMember.getRole() == ClaimRole.MEMBER) members.add(claimMember);
            if (claimMember.getRole() == ClaimRole.VISITOR) visitors.add(claimMember);
            allMembers.add(claimMember);
        }

        init(Methods.formatTitle(plugin.getLocale().getMessage("interface.members.title").getMessage()), 54);
    }

    @Override
    protected void constructGUI() {
        inventory.clear();
        resetClickables();
        registerClickables();

        inventory.setItem(1, Methods.getBackgroundGlass(true));
        inventory.setItem(9, Methods.getBackgroundGlass(true));

        inventory.setItem(7, Methods.getBackgroundGlass(true));
        inventory.setItem(17, Methods.getBackgroundGlass(true));

        inventory.setItem(36, Methods.getBackgroundGlass(true));
        inventory.setItem(45, Methods.getBackgroundGlass(true));
        inventory.setItem(46, Methods.getBackgroundGlass(true));

        inventory.setItem(44, Methods.getBackgroundGlass(true));
        inventory.setItem(52, Methods.getBackgroundGlass(true));
        inventory.setItem(53, Methods.getBackgroundGlass(true));

        inventory.setItem(2, Methods.getBackgroundGlass(false));
        inventory.setItem(6, Methods.getBackgroundGlass(false));
        inventory.setItem(18, Methods.getBackgroundGlass(false));
        inventory.setItem(26, Methods.getBackgroundGlass(false));
        inventory.setItem(27, Methods.getBackgroundGlass(false));
        inventory.setItem(35, Methods.getBackgroundGlass(false));
        inventory.setItem(47, Methods.getBackgroundGlass(false));
        inventory.setItem(48, Methods.getBackgroundGlass(false));
        inventory.setItem(49, Methods.getBackgroundGlass(false));
        inventory.setItem(50, Methods.getBackgroundGlass(false));
        inventory.setItem(51, Methods.getBackgroundGlass(false));

        ItemStack exit = new ItemStack(plugin.isServerVersionAtLeast(ServerVersion.V1_13) ? Material.OAK_FENCE_GATE : Material.valueOf("FENCE_GATE"));
        ItemMeta exitMeta = exit.getItemMeta();
        exitMeta.setDisplayName(plugin.getLocale().getMessage("interface.members.exittitle").getMessage());
        List<String> exitLore = new ArrayList<>();
        String[] exitSplit = plugin.getLocale().getMessage("interface.members.exitlore").getMessage().split("\\|");
        for (String line : exitSplit) exitLore.add(line);
        exitMeta.setLore(exitLore);
        exit.setItemMeta(exitMeta);

        ItemStack type = new ItemStack(Material.HOPPER);
        ItemMeta typeMeta = type.getItemMeta();
        typeMeta.setDisplayName(plugin.getLocale().getMessage("interface.members.changetypetitle").getMessage());
        List<String> typeLore = new ArrayList<>();
        String[] typeSplit = plugin.getLocale().getMessage("interface.members.typelore").getMessage().split("\\|");
        for (String line : typeSplit) typeLore.add(line);
        typeMeta.setLore(typeLore);
        type.setItemMeta(typeMeta);

        ItemStack sort = new ItemStack(Material.HOPPER);
        ItemMeta sortMeta = sort.getItemMeta();
        sortMeta.setDisplayName(plugin.getLocale().getMessage("interface.members.changesorttitle").getMessage());
        List<String> sortLore = new ArrayList<>();
        String[] sortSplit = plugin.getLocale().getMessage("interface.members.sortlore").getMessage().split("\\|");
        for (String line : sortSplit) sortLore.add(line);
        sortMeta.setLore(sortLore);
        sort.setItemMeta(sortMeta);

        ItemStack stats = new ItemStack(Material.PAINTING);
        ItemMeta statsMeta = stats.getItemMeta();
        statsMeta.setDisplayName(plugin.getLocale().getMessage("interface.members.statstitle").getMessage());
        List<String> statsLore = new ArrayList<>();
        String[] statsSplit = plugin.getLocale().getMessage("interface.members.statslore").getMessage().split("\\|");
        for (String line : statsSplit) statsLore.add(line);
        statsMeta.setLore(statsLore);
        stats.setItemMeta(statsMeta);

        ItemStack visitor = new ItemStack(plugin.isServerVersionAtLeast(ServerVersion.V1_13) ? Material.OAK_SIGN : Material.valueOf("SIGN"));
        ItemMeta visitorMeta = visitor.getItemMeta();
        visitorMeta.setDisplayName(plugin.getLocale().getMessage("interface.members.visitorsettingstitle").getMessage());
        List<String> visitorLore = new ArrayList<>();
        String[] visitorSplit = plugin.getLocale().getMessage("interface.members.visitorsettingslore").getMessage().split("\\|");
        for (String line : visitorSplit) visitorLore.add(line);
        visitorMeta.setLore(visitorLore);
        visitor.setItemMeta(visitorMeta);

        ItemStack member = new ItemStack(Material.PAINTING);
        ItemMeta memberMeta = member.getItemMeta();
        memberMeta.setDisplayName(plugin.getLocale().getMessage("interface.members.membersettingstitle").getMessage());
        List<String> memberLore = new ArrayList<>();
        String[] memberSplit = plugin.getLocale().getMessage("interface.members.membersettingslore").getMessage().split("\\|");
        for (String line : memberSplit) memberLore.add(line);
        memberMeta.setLore(memberLore);
        member.setItemMeta(memberMeta);

        ItemStack previous = new ItemStack(Material.MAP);
        ItemMeta previousMeta = previous.getItemMeta();
        previousMeta.setDisplayName(plugin.getLocale().getMessage("interface.members.previoustitle").getMessage());
        List<String> previousLore = new ArrayList<>();
        String[] previousSplit = plugin.getLocale().getMessage("interface.members.previouslore").getMessage().split("\\|");
        for (String line : previousSplit) previousLore.add(line);
        previousMeta.setLore(previousLore);
        previous.setItemMeta(previousMeta);

        ItemStack next = new ItemStack(Material.PAPER);
        ItemMeta nextMeta = next.getItemMeta();
        nextMeta.setDisplayName(plugin.getLocale().getMessage("interface.members.nexttitle").getMessage());
        List<String> nextLore = new ArrayList<>();
        String[] nextSplit = plugin.getLocale().getMessage("interface.members.nextlore").getMessage().split("\\|");
        for (String line : nextSplit) nextLore.add(line);
        nextMeta.setLore(nextLore);
        next.setItemMeta(nextMeta);

        inventory.setItem(0, exit);
        inventory.setItem(8, exit);
        inventory.setItem(3, type);
        inventory.setItem(4, stats);
        inventory.setItem(5, sort);
        if (page > 1) inventory.setItem(37, previous);
        inventory.setItem(39, visitor);
        inventory.setItem(41, member);

        if (allMembers == null || allMembers.isEmpty()) return;

        List<ClaimMember> toDisplay = null;
        if (displayedRole == ClaimRole.OWNER) toDisplay = allMembers;
        if (displayedRole == ClaimRole.MEMBER) toDisplay = members;
        if (displayedRole == ClaimRole.VISITOR) toDisplay = visitors;

        if (page < Math.ceil(toDisplay.size() / 21)) inventory.setItem(43, next);

        // I think this should work?

        int currentMember = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = currentMember; j < currentMember+8; j++) {
                if (toDisplay.size() < j) return;

                OfflinePlayer skullPlayer = Bukkit.getOfflinePlayer(toDisplay.get(currentMember).getUniqueId());

                ItemStack skull = new ItemStack(plugin.isServerVersionAtLeast(ServerVersion.V1_13) ?
                        Material.PLAYER_HEAD : Material.valueOf("SKULL"));
                if (!plugin.isServerVersionAtLeast(ServerVersion.V1_13)) skull.setDurability((short) 3);
                SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
                skullMeta.setOwningPlayer(skullPlayer);
                skullMeta.setDisplayName(Methods.formatText("&b") + skullPlayer.getName());
                List<String> lore = new ArrayList<>();
                String[] skullSplit = plugin.getLocale().getMessage("interface.members.skull")
                        .processPlaceholder("role",
                                Methods.formatText(toDisplay.get(currentMember).getRole().toString().toLowerCase(), true))
                        .getMessage().split("\\|");
                skull.setItemMeta(skullMeta);

                inventory.setItem(((i + 1) * 7) + j, skull);

                currentMember++;
            }
        }
    }

    @Override
    protected void registerClickables() {
        registerClickable(0, (player, inventory, cursor, slot, type) -> {
            player.closeInventory();
        });

        registerClickable(8, (player, inventory, cursor, slot, type) -> {
            player.closeInventory();
        });

        registerClickable(3, (player, inventory, cursor, slot, type) -> {
            if (displayedRole == ClaimRole.MEMBER) displayedRole = ClaimRole.OWNER;
            if (displayedRole == ClaimRole.OWNER) displayedRole = ClaimRole.VISITOR;
            if (displayedRole == ClaimRole.VISITOR) displayedRole = ClaimRole.MEMBER;
            constructGUI();
        });

        registerClickable(5, (player, inventory, cursor, slot, type) -> {
            // Change Sort
        });

        registerClickable(37, (player, inventory, cursor, slot, type) -> {
            page = page == 1 ? 1 : page--;
        });

        registerClickable(39, (player, inventory, cursor, slot, type) -> {
            new GUISettings(player, claim, ClaimRole.VISITOR);
        });

        registerClickable(41, (player, inventory, cursor, slot, type) -> {
            new GUISettings(player, claim, ClaimRole.MEMBER);
        });

        registerClickable(43, (player, inventory, cursor, slot, type) -> page++);
    }

    @Override
    protected void registerOnCloses() {

    }
}
