//package com.bongbong.ace.velocity.staff.punishments.menus;
//
//import com.bongbong.ace.shared.utils.DateFormatter;
//import com.bongbong.ace.velocity.staff.punishments.Punishment;
//import com.bongbong.ace.velocity.utils.Colors;
//import com.bongbong.ace.velocity.utils.OfflinePlayer;
//import com.bongbong.ace.velocity.utils.PlayerFinder;
//import dev.simplix.cirrus.item.CirrusItem;
//import dev.simplix.cirrus.menu.MenuRow;
//import dev.simplix.cirrus.menus.AbstractBrowser;
//import dev.simplix.cirrus.model.Click;
//import dev.simplix.protocolize.data.ItemType;
//import dev.simplix.protocolize.data.inventory.InventoryType;
//
//import java.util.*;
//
//public class HistoryMenu extends AbstractBrowser<ItemType> {
//
//    private final PlayerFinder playerFinder;
//    private final TreeMap<Date, Punishment> map;
//    private int position = 0;
//
//    public HistoryMenu(PlayerFinder playerFinder, TreeMap<Date, Punishment> map) {
//        this.playerFinder = playerFinder;
//        this.map = map;
//
//        fixedSize(InventoryType.GENERIC_9X2);
//        title(Colors.translate("&eHistory Menu (Total: " + map.size() + ")"));
//    }
//
//    @Override
//    protected void interceptBottomRow(MenuRow bottomRow) {
//        bottomRow.get(0).set(CirrusItem.of(ItemType.ARROW,
//                hasPreviousPage() ? Colors.translate("&aPrevious page") : Colors.translate("&cPrevious Page"),
//                hasPreviousPage() ? Colors.translate("&7Click to view the previous page.") : Colors.translate("&7This is the first page.")
//        ).actionHandler("previousPage"));
//
//        bottomRow.get(8).set(CirrusItem.of(ItemType.ARROW,
//                hasNextPage() ? Colors.translate("&aNext page") : Colors.translate("&cNext Page"),
//                hasNextPage() ? Colors.translate("&7Click to view the next page.") : Colors.translate("&7This is the last page.")
//        ).actionHandler("nextPage"));
//    }
//
//    @Override
//    protected void handleClick(Click click, ItemType value) {
//        click.player().sendMessage(Colors.translate("&aPunishment info dump coming soon"));
//    }
//
//    @Override
//    protected Collection<ItemType> elements() {
//        Collection<ItemType> collection = new ArrayList<>();
//        for (Punishment punishment : map.descendingMap().values()) {
//            switch (punishment.getType()) {
//                case BAN: { collection.add(ItemType.DIAMOND_SWORD); continue; }
//                case BLACKLIST: { collection.add(ItemType.BEDROCK); continue; }
//                case MUTE: { collection.add(ItemType.WRITABLE_BOOK); continue; }
//                case KICK: { collection.add(ItemType.GOLDEN_BOOTS); continue; }
//                default: { collection.add(ItemType.PAPER); }
//            }
//        }
//
//        return collection;
//    }
//
//    @Override
//    protected CirrusItem map(ItemType element) {
//        Punishment punishment = null;
//        int count = 0;
//
//        for (Punishment punishment1 : map.descendingMap().values()) {
//            if (position == count) {
//                punishment = punishment1;
//                break;
//            }
//
//            count++;
//        }
//        position++;
//
//        if (punishment == null) return null;
//
//        String issuerName = null;
//        if (punishment.getIssuer() == null) issuerName = "Console";
//        else {
//            OfflinePlayer issuerPlayer = playerFinder.getOPlayerFromUUID(punishment.getIssuer());
//            if (issuerPlayer != null) issuerName = issuerPlayer.getUsername();
//        }
//
//        String victimName = null;
//        OfflinePlayer victimPlayer = playerFinder.getOPlayerFromUUID(punishment.getVictim());
//        if (victimPlayer != null) victimName = victimPlayer.getUsername();
//
//        List<String> lore = new ArrayList<>();
//
//        lore.add("");
//        lore.add(Colors.translate("&eTarget: &f" + victimName));
//        lore.add(Colors.translate("&eDuration: &f" + punishment.originalDuration()));
//        lore.add("");
//        lore.add(Colors.translate("&eIssued By: &f" + issuerName));
//        lore.add(Colors.translate("&eIssued Date: &f" + DateFormatter.format(punishment.getIssued())));
//        lore.add(Colors.translate("&eIssued Reason: &f" + punishment.getIssueReason()));
//
//        if (!punishment.isActive()) {
//            String pardonerName = "Console";
//            Date pardonDate = punishment.getExpires();
//            String pardonReason = "Expired";
//
//            if (punishment.getPardoned() != null) {
//                pardonReason = punishment.getPardonReason();
//                pardonDate = punishment.getPardoned();
//
//                if (punishment.getPardoner() != null) {
//                    OfflinePlayer pardonerPlayer = playerFinder.getOPlayerFromUUID(punishment.getPardoner());
//                    if (pardonerPlayer != null) pardonerName = pardonerPlayer.getUsername();
//                }
//            }
//
//            lore.add("");
//            lore.add(Colors.translate("&eRemoved By: &f" + pardonerName));
//            lore.add(Colors.translate("&eRemoved Date: &f" + DateFormatter.format(pardonDate)));
//            lore.add(Colors.translate("&eRemoved Reason: &f" + pardonReason));
//        }
//
//        return CirrusItem.of(element)
//                .displayName(Colors.translate("&7" + punishment.getType().toString().toUpperCase() + " (#" + punishment.getId() + ")"))
//                .lore(lore);
//    }
//}
