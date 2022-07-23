package me.corxl.CorxlTeleport.Commands;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class TeleportRequest {
    private Player requestReceiver, requestSender, to, from;
    private boolean isTpaHere;
    private BukkitTask task;
    private Location fromPlayerLocation;
    public TeleportRequest(Player requestSender, Player requestReceiver, boolean isTpaHere) {
        this.requestSender = requestSender;
        this.requestReceiver = requestReceiver;
        this.isTpaHere = isTpaHere;
        if (isTpaHere) {
            this.to = requestSender;
            this.from = requestReceiver;
        } else {
            this.to = requestReceiver;
            this.from = requestSender;
        }
        this.fromPlayerLocation = this.from.getLocation();
    }

    public Player getTo() {
        return to;
    }

    public Player getFrom() {
        return from;
    }

    public boolean isTpaHere() {
        return isTpaHere;
    }

    public Player getRequestReceiver() {
        return requestReceiver;
    }

    public Player getRequestSender() {
        return requestSender;
    }

    public BukkitTask getTask() {
        return this.task;
    }

    public void setTask(BukkitTask task) {
        this.task = task;
    }

    public Location getFromPlayerLocation() {
        return fromPlayerLocation;
    }
}
