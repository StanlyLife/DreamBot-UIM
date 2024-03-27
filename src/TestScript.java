import org.dreambot.api.input.Mouse;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widget;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.widgets.WidgetChild;

import java.awt.*;
import java.util.List;
import java.util.Optional;

import Gui.GUI;

@ScriptManifest(name = "UIM developer", description = "No description", author = "ImLife",
        version = 1.0, category = Category.WOODCUTTING, image = "")
public class TestScript extends AbstractScript{
    public enum State {
        WALKING, CHOPPING, FIREMAKING, GOBLIN, FINDING_TREE,FLETCHING
    }
    private State currentState;
    private boolean configShouldFletch = true;
    Area workArea = new Area(3183, 3240, 3155, 3230);

    @Override
    public void onPaint(Graphics g) {
        GUI myGui = new GUI();
        myGui.paint(g);
    }
    private State getState() {
        if(Players.getLocal().isMoving()) currentState = State.WALKING;
        if(!Players.getLocal().isMoving() && Inventory.isFull()) currentState = configShouldFletch ? State.FLETCHING : State.FIREMAKING;
        if (!Inventory.isFull() && !Players.getLocal().isAnimating() && workArea.contains(Players.getLocal().getTile())) currentState = State.FINDING_TREE;
        if (!Inventory.isFull() && Players.getLocal().isAnimating() && workArea.contains(Players.getLocal().getTile()))  currentState = State.CHOPPING;
        if(currentState == null) currentState = State.CHOPPING;
        return currentState;
    }
    @Override
    public int onLoop() {
        Logger.log("State: " + getState());
        if(currentState == State.FINDING_TREE) {
            if (!Players.getLocal().isAnimating() && !Players.getLocal().isMoving()) {
                GameObject normalTree = GameObjects.closest(t -> t.getName().equalsIgnoreCase("tree") && workArea.contains(t.getTile()));
                if (normalTree != null && normalTree.interact("Chop down")) {
                    Sleep.sleepUntil(() -> !Players.getLocal().isAnimating(), 4000);
                }
            }
        }
        if(currentState == State.CHOPPING) {
            GameObject normalTree = GameObjects.closest(t -> t.getName().equalsIgnoreCase("tree") && workArea.contains(t.getTile()));
            if (!Players.getLocal().isAnimating() && normalTree != null && normalTree.interact("Chop down")) {
                Sleep.sleepUntil(() -> Players.getLocal().isAnimating(), 4000);
            }
        }
        if(currentState == State.FLETCHING) {
            Fletch();
        }
        if(currentState == State.FIREMAKING) {
            boolean hasLogs = Inventory.contains("Logs");
            Logger.log("hasLogs and wants to firemake: " + hasLogs);
            Firemake();
        }

        return Calculations.random(1000, 1500);
    }
    private boolean StartFletching() {
        Logger.log("!IsPlayerAnimatingOrWalking()");
        Inventory.interact("Knife");
        sleep(500, 854);
        Inventory.interact("Logs");
        List<org.dreambot.api.methods.widget.Widget> allWidgets = Widgets.getAllWidgets();
        Logger.log(allWidgets);
        boolean containsWidget = allWidgets.stream().anyMatch(widget -> widget.getID() == 270);
        if(containsWidget) {
            Optional<Widget> foundWidget = allWidgets.stream()
                    .filter(widget -> widget.getID() == 270)
                    .findFirst();
            Widget retrievedWidget = foundWidget.get();
            Logger.log("FOUND WIDGET");
            Logger.log(retrievedWidget);
            List<WidgetChild> myWidgetChildren = retrievedWidget.getChildren();
            Logger.log(myWidgetChildren);

            //CHECK IF CHILD WIDGET 14 EXISTS
            Optional<WidgetChild> foundChildWidget = myWidgetChildren.stream()
                    .filter(widget -> widget.getID() == 14)
                    .findFirst();
            WidgetChild childWidget = foundChildWidget.get();
            childWidget.interact();
        }else {
            Logger.log("Does not contain widget 270");
            return false;
        }
        return true;
    }
    private boolean Fletch() {
        Logger.log("Started fletching");
        StartFletching();
        sleep(1000, 5000);
        while(IsFiremakingOrFletching()) {
            Logger.log("IsFiremakingOrFletching()");
            if(!IsPlayerAnimatingOrWalking()) {

                sleep(3000, 5000);
            }else {
                Logger.log("Player is animating or walking");
            }
        }
        return true;
    }

    private boolean Firemake() {
        while(IsFiremakingOrFletching()) {
            if(!IsPlayerAnimatingOrWalking()) {
                Inventory.interact("Logs");
                Inventory.interact("Tinderbox");
                Sleep.sleepUntil(this::IsPlayerAnimating, 2000);
                if(!IsPlayerAnimatingOrWalking() && Inventory.contains("Logs")) {
                    Walking.walk(workArea.getRandomTile());
                }
            }
            boolean finalIsFireMaking = IsFiremakingOrFletching();
            Sleep.sleepUntil(() -> !finalIsFireMaking, 3000);
        }
        return true;
    }
    private boolean IsPlayerAnimatingOrWalking() {
        return Players.getLocal().isAnimating() || Players.getLocal().isMoving();
    }
    private boolean IsPlayerAnimating() {
        return Players.getLocal().isAnimating();
    }
    private boolean IsFiremakingOrFletching() {
        return Players.getLocal().isAnimating() && Inventory.contains("Logs");
    }
}
