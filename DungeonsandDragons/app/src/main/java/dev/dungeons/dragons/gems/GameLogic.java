package dev.dungeons.dragons.gems;

import java.util.Random;

public class GameLogic {

    private final int[] slotsX = {1, 1, 1};
    private final int[] slots = {1, 1, 1};
    private final int[] slotsY = {1, 1, 1};
    private int theCoins;
    private int lines = 1;
    private int bet;
    private int jackpot;
    private int prize = 0;
    private boolean hasWon = false;
    private boolean hasDiagonalWon = false;
    private final Random rand = new Random();

    public int getRandomInt() {
        return rand.nextInt(7) + 1;
    }

    public int getPosition(int i) {
        return slots[i] + 5;
    }

    public int checkNumber(int x) {
        if (x == 0) {
            x = 7;
        } else if (x == 8) {
            x = 1;
        }
        return x;
    }

    public void getSpinResults() {
        hasWon = true;
        hasDiagonalWon = true;
        prize = 0;

        for (int i = 0; i < slots.length; i++) {
            slots[i] = getRandomInt();
            int x = slots[i] - 1;
            slotsX[i] = checkNumber(x);
            int y = slots[i] + 1;
            slotsY[i] = checkNumber(y);
        }

        if (slots[0] == 7 || slots[1] == 7 || slots[2] == 7) {
            hasDiagonalWon = false;
            int i = 0;

            for (int a : slots) {
                if (a == 7) {
                    i++;
                }
            }

            switch (i) {
                case 1:
                    prize = bet * 5;
                    break;
                case 2:
                    prize = bet * 10;
                    break;
                case 3:
                    // Increase the uncertainty for winning the jackpot
                    if (rand.nextInt(20000) == 0) {
                        prize = jackpot;
                        jackpot = 0;
                    }
                    break;
                default:
                    break;
            }

            theCoins += prize;
        } else if (slots[0] == slots[1] && slots[1] == slots[2]) {
            hasDiagonalWon = false;
            win(slots);
        } else if (slotsX[0] == slots[1] && slots[1] == slotsY[2]) {
            hasWon = false;
            win(slotsX);
        } else if (slotsX[2] == slots[1] && slots[1] == slotsY[0]) {
            hasWon = false;
            win(slotsY);
        } else {
            hasWon = false;
            hasDiagonalWon = false;
            theCoins -= bet;
            jackpot += bet;
        }
    }


    private void win(int[] slots) {
        switch (slots[0]) {
            case 1:
                prize = bet * 2;
                break;
            case 2:
                prize = bet * 3;
                break;
            case 3:
                prize = bet * 5;
                break;
            case 4:
                prize = bet * 7;
                break;
            case 5:
                prize = bet * 10;
                break;
            case 6:
                prize = bet * 15;
                break;
            default:
                break;
        }

        theCoins += prize;
    }

    public int getWinningPattern() {
        if (hasDiagonalWon) {
            // Diagonal win logic for combination_7
            if (slots[0] == 1 && slots[1] == 5 && slots[2] == 9) {
                return 1;
            } else if (slots[0] == 3 && slots[1] == 5 && slots[2] == 7) {
                return 2;
            }
        } else {
            // Horizontal win logic for combination_7
            if (slots[0] == 7 && slots[1] == 7 && slots[2] == 7) {
                return slots[0];
            }

            // Horizontal, vertical, and diagonal win logic for combination_1 to combination_6
            int row = -1;
            int col = -1;

            if (slots[0] >= 1 && slots[0] <= 6) {
                row = (slots[0] - 1) / 3;
                col = (slots[0] - 1) % 3;
            }

            if (row != -1 && col != -1 && slots[0] == slots[1] && slots[1] == slots[2]) {
                // Check for horizontal win
                if (row == (slots[1] - 1) / 3) {
                    return row + 1; // Return the row for combination_1 to combination_6
                }
                // Check for vertical win
                if (col == (slots[1] - 1) % 3) {
                    return col + 4; // Return the column for combination_1 to combination_6
                }
                // Check for diagonal win
                if (row == col && row == (slots[1] - 1) / 3 && col == (slots[1] - 1) % 3) {
                    return 7; // Return 7 for diagonal win for combination_1 to combination_6
                }
            }
        }

        // Default: return the first slot
        return slots[0];
    }




    public void betUp() {
        if (bet < 100) {
            bet += 5;
            theCoins -= 5;
        }
    }

    public void betDown() {
        if (bet > 5) {
            bet -= 5;
            theCoins += 5;
        }
    }

    // Getters
    public String getTheCoins() {
        return Integer.toString(theCoins);
    }

    // Setters
    public void setTheCoins(int theCoins) {
        this.theCoins = theCoins;
    }

    public String getLines() {
        return Integer.toString(lines);
    }

    public void setLines(int lines) {
        this.lines = lines;
    }

    public String getBet() {
        return Integer.toString(bet);
    }

    public void setBet(int bet) {
        this.bet = bet;
    }

    public String getJackpot() {
        return Integer.toString(jackpot);
    }

    public void setJackpot(int jackpot) {
        this.jackpot = jackpot;
    }

    public boolean getHasWon() {
        return hasWon;
    }

    public void setHasWon(boolean hasWon) {
        this.hasWon = hasWon;
    }

    public boolean getDiagonalWon() {
        return hasDiagonalWon;
    }

    public String getPrize() {
        return Integer.toString(prize);
    }

    public void setPrize(int prize) {
        this.prize = prize;
    }
}
