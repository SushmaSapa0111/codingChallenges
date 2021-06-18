package com.challenges.snapuniversity;

import java.util.ArrayList;
import java.util.List;

public class KnightChallenge {
    // {(1,2),(1,-2),(2,1),(2,-1),(-2,1),(-2,-1),(-1,2),(-1,-2)}
    public static void main(String[] args) {
        int[][] board = new int[8][8];
        List<String> pos = new ArrayList<String>();
        pos.add("-2,-1");
        pos.add("-2,1");
        pos.add("-1,-2");
        pos.add("-1,2");
        pos.add("1,-2");
        pos.add("1,2");
        pos.add("2,-1");
        pos.add("2,1");
        int count=1;
        for (int i = 0; i < board.length;i++) {
            for (int j = 0; j < board[i].length;j++) {
                System.out.println("Stared For ("+i+","+j+") ");
                count = 1;
                board = new int[8][8];
                board[i][j] = count;
                count = checkMovesRecursively(i,j,pos,count,board);
                if (count == 64){
                    System.out.println("Solution for position ("+i+","+j+") ");
                    printBoard(board);
                    System.out.println("completed");
                } else {
                    System.out.println("No Solution Found for ("+i+","+j+") ");
                }
            }
        }
    }

    private static int checkMovesRecursively(int x, int y, List<String> pos, int count, int[][] board) {
        if (count == 64){
            return count;
        }
        for (int i = 0; i< pos.size(); i++) {
            String[] co = pos.get(i).split(",");
            int newX = x + Integer.parseInt(co[0]);
            int newY = y + Integer.parseInt(co[1]);
            if (newX >= 0 && newY >= 0 && newX < 8 && newY < 8 && board[newX][newY] == 0) {
                count++;
                board[newX][newY] = count;
                count = checkMovesRecursively(newX, newY, pos, count, board);
            }
            if (count == 64) {
                return count;
            }
            if (i == pos.size()-1) {
                count--;
                board[x][y] = 0;
            }
        }
        return count;
    }

    private static void printBoard(int[][] board) {
        for (int[] x : board)
        {
            for (int y : x)
            {
                if (y > 9) {
                    System.out.print(y + " ");
                } else {
                    System.out.print(y + "  ");
                }
            }
            System.out.println();
        }
    }
}

