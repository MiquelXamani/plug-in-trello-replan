package web.domain.operation_classes;

import web.domain.Card;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Miquel on 02/06/2017.
 */
public class CardDependency {
    public CardDependency(){}

    public boolean stillDependsOnAnotherCard(Card card, Card[] cardsDone){
        String description, dependsOnText, dependsOnCards;
        dependsOnText = "**Depends on:** ";
        int startIndex, textSize, endIndex, count;
        textSize = dependsOnText.length();
        List<String> dependsOnList;

        boolean found;
        description = card.getDesc();
        startIndex = description.indexOf(dependsOnText) + textSize;
        endIndex = description.length();
        dependsOnCards = description.substring(startIndex,endIndex);
        System.out.println("substring: " + dependsOnCards);
        dependsOnList = Arrays.asList(dependsOnCards.split("\\s*,\\s*"));

        System.out.println("depends list size: " + dependsOnList.size());
        for(int i = 0; i < dependsOnList.size(); i++){
            System.out.println(dependsOnList.get(i));
        }

        System.out.println("cards in done list size: " + cardsDone.length);
        /*for(int j = 0; j < cardsDone.length; j++){
            System.out.println(cardsDone[j].getName());
        }*/

        boolean depends = true;
        if(dependsOnList.size() == 1 && dependsOnList.get(0).equals("-")){
            System.out.println("No dependencies");
            depends = false;
        }
        else{
            count = 0;
            found = false;
            for(int i = 0; i < dependsOnList.size(); i++){
                for(int j = 0; !found && j < cardsDone.length; j++){
                    if(cardsDone[j].getName().equals(dependsOnList.get(i))){
                        found = true;
                        count++;
                    }
                }
            }
            System.out.println("Count: " + count + " dependsOnList size: "+dependsOnList.size());
            if(count == dependsOnList.size()){
                depends = false;
                System.out.println("Yellow label removed!");
            }
        }
        if(depends){
            System.out.println("CARD "+card.getName()+" DEPENDS");
        }
        else{
            System.out.println("CARD "+card.getName()+" NO DEPENDS");
        }
        return depends;
    }
}
