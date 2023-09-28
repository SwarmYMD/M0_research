package environment;

import java.util.*;

public class Grid{
    public static String[] grid = {
        "00000000000000000000",
        "00000000000000000000",
        "00001111111111110000",
        "00001111111111110000",
        "00001111111111110000",
        "00001111111111110000",
        "00001110000000000000",
        "00001110000000000000",
        "00001111111111110000",
        "00001111111111110000",
        "00001111111111110000",
        "00001111111111110000",
        "00001110000000000000",
        "00001110000000000000",
        "00001111111111110000",
        "00001111111111110000",
        "00001111111111110000",
        "00001111111111110000",
        "00000000000000000000",
        "00000000000000000000",
    };

    public int h;
    public int w;
    public int[][] table = new int[Variable.N][Variable.M];
    public int[][] occupied = new int[Variable.N][Variable.M];
    public int[][] agent_pos = new int[Variable.N][Variable.M];
    public double[][] pherData = new double[Variable.N][Variable.M];
    public double[][] areaPherData = new double[Variable.n][Variable.m];
    public boolean[] vacant = new boolean[Variable.n * Variable.m];
    public boolean[][] alreadyUpdateDis = new boolean[Variable.N][Variable.M];
    public boolean[][] alreadyUpdateExp = new boolean[Variable.n][Variable.m];

    public Grid(){
        this.h = grid.length;
        this.w = grid[0].length();
        for(int i=0; i<h; i++){
            for(int j=0; j<w; j++){
                table[i][j] = Character.getNumericValue(grid[i].charAt(j));
                occupied[i][j] = 0;
            }
        }
        for(int i=0; i<h; i++){
            for(int j=0; j<w; j++){
                agent_pos[i][j] = 0;
            }
        }
        for(int i=0; i<Variable.N; i++){
            for(int j=0; j<Variable.M; j++){
                pherData[i][j] = 0;
                alreadyUpdateDis[i][j] = false;
                // pherData[i][j] = i * Variable.M + j;
            }
        }
        for(int i=0; i<Variable.n; i++){
            for(int j=0; j<Variable.m; j++){
                areaPherData[i][j] = 0;
                alreadyUpdateExp[i][j] = false;
            }
        }
        Arrays.fill(vacant, false);
    }

    public void recordPos(Agent agent){
        agent_pos[agent.row][agent.col] = 1;
    }

    public void deletePos(Agent agent){
        agent_pos[agent.row][agent.col] = 0;
    }

}
