package it.polimi.se2018.events.messages;

import it.polimi.se2018.events.ServerMessageHandler;
import it.polimi.se2018.model.cards.ActiveObjectives;
import it.polimi.se2018.model.cards.ActiveTools;

/**
 * Info abouts the cards in game
 * @author Alì El Wahsh
 */
public class CardInfo extends ServerMessage {

    private final int[] tools = new int[3];
    private final int[] toolCost = new int[3];
    private final int[] objectives = new int[3];

    /**
     * Class' constructor
     * @param t tools in game
     * @param o objectives in game
     */
    public CardInfo(ActiveTools t, ActiveObjectives o)
    {
        for(int i = 0; i<3;i++) {
            tools[i] = t.getTool(i).getId();
            if(t.getTool(i).isUsed())
                toolCost[i] = 2;
            else toolCost[i] = 1 ;
            objectives[i] = o.getObjective(i).getId();
        }
        this.description = "CardInfo";
    }

    /**
     * Getter for public objectives IDs
     * @return public objectives IDs
     */
    public int[] getObjectives() {
        return objectives;
    }

    /**
     * Getter for tools IDs
     * @return tools IDs
     */
    public int[] getTools() {
        return tools;
    }

    /**
     * Getter for all tool cards cost
     * @return array of costs
     */
    public int[] getToolCost() {
        return toolCost;
    }

    @Override
    public void visit(ServerMessageHandler handler) {
        handler.handle(this);
    }
}
