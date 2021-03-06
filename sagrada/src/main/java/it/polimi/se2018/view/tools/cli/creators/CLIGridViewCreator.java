package it.polimi.se2018.view.tools.cli.creators;

import it.polimi.se2018.model.ColorModel;
import it.polimi.se2018.model.IntColorPair;
import it.polimi.se2018.view.tools.GridViewCreator;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to create and to handle grid in CLI
 *
 * @author Mathyas Giudici
 */

public class CLIGridViewCreator extends GridViewCreator<List<String>> {


    /**
     * Basic Class constructor that initializes elements at default value
     *
     */
    public CLIGridViewCreator() {
        super();
        this.dieViewCreator = new CLIDieViewCreator();
    }

    /**
     * Class constructor
     *  @param grid        contains user grid
     * @param gridPattern contains user pattern
     */
    public CLIGridViewCreator(IntColorPair[][] grid, IntColorPair[][] gridPattern) {
        super(grid, gridPattern);
        this.dieViewCreator = new CLIDieViewCreator();
    }

    @Override
    public List<String> display() {
        ArrayList<String> strings;

        //Start to create Pattern
        strings = gridHelper(this.gridPattern);
        strings.add(0, "Pattern\n");

        if (this.grid != null) {
            strings.add("\nGriglia\n");
            strings.addAll(gridHelper(this.grid));
        }

        return strings;
    }

    /**
     * Fixes string spaces for a correct indentation
     *
     * @param toFix contains the string to fix
     * @return fixed string
     */
    private String fixPrintSpace(String toFix) {
        //Check length
        if (toFix.length() < 8) {
            //Create String Builder
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(toFix);

            //Fix String to 8 characters
            while (stringBuilder.toString().length() < 8) {
                stringBuilder.append(" ");
            }
            return stringBuilder.toString();
        }
        //Return
        return toFix;
    }

    /**
     * Creates a dash string for grid row separator
     *
     * @param value contains the number of dash
     * @return the dash string
     */
    private String lineCreator(int value) {
        //Create a String Builder
        StringBuilder string = new StringBuilder();

        //Make line
        for (int i = 0; i < value; i++) {
            string.append("-");
        }
        return string.toString();
    }

    /**
     * Creates a generic grid to print
     *
     * @param toPrint contains the grid matrix
     * @return string that represents the grid
     */
    private ArrayList<String> gridHelper(IntColorPair[][] toPrint) {
        ArrayList<String> returnArray = new ArrayList<>();

        //Start to create grid
        for (int i = 0; i < toPrint.length; i++) {
            StringBuilder currentString = new StringBuilder();
            for (int j = 0; j < toPrint[i].length; j++) {
                currentString.append("|");
                if (toPrint[i][j] == null) {
                    currentString.append(fixPrintSpace(""));
                } else {
                    if (toPrint[i][j].getFirst() == 0 && toPrint[i][j].getSecond() == ColorModel.WHITE) {
                        currentString.append(fixPrintSpace(""));
                    } else {
                        currentString.append(fixPrintSpace((String) dieViewCreator.makeDie(toPrint[i][j])));
                    }
                }
                currentString.append("|");
            }
            returnArray.add(i, currentString.toString());
        }

        //Fix line separator on grid
        for (int i = 0; i < returnArray.size(); i = i + 2) {
            returnArray.add(i + 1, lineCreator(returnArray.get(i).length()));
        }

        returnArray.add(0, lineCreator(returnArray.get(0).length()));

        return returnArray;
    }
}
