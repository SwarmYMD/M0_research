import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;


class Variable{
    private Variable(){};
    public static final int M = 20;
    public static final int N = 20;
    public static final int m = 4;
    public static final int n = 4;
    public static final int W = M/m;
    public static final int H = N/n;
    public static final int AGENT_NUM = 100;
}

class Grid{
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

    int h;
    int w;
    int[][] table = new int[Variable.N][Variable.M];
    int[][] agent_pos = new int[Variable.N][Variable.M];

    Grid(){
        this.h = grid.length;
        this.w = grid[0].length();
        for(int i=0; i<h; i++){
            for(int j=0; j<w; j++){
                table[i][j] = Character.getNumericValue(grid[i].charAt(j));
            }
        }
        for(int i=0; i<h; i++){
            for(int j=0; j<w; j++){
                agent_pos[i][j] = 0;
            }
        }
        
    }

    void recordPos(Agent agent){
        agent_pos[agent.row][agent.col] = 1;
    }

}

class Agent{
    int col;
    int row;
    //int pos;
    String state;
    int range;  // capable distance of communicating among agents
    double[] PherMatrix = new double[Variable.m * Variable.n];
    double[] subPherMatrix = new double[Variable.W * Variable.H];
    int areaNo;

    Agent(int c, int r){
        this.col = c;
        this.row = r;
        this.state = "d";
        this.range = 3;
        Arrays.fill(PherMatrix, 0);
        Arrays.fill(subPherMatrix, 0);
        this.areaNo = 0;
    }

    void getAreaNo(){
        int x = col / Variable.W;
        int y = row / Variable.H;
        areaNo = x + y * Variable.m;
    }

    void move(){

    }
}

public class PF_PSO{
    public static void main(String[] args) {
        Grid grid = new Grid();

        int initial_pos;

        ArrayList<Integer> randomList = new ArrayList<Integer>();

        for(int i = 0 ; i < Variable.M * Variable.N ; i++) {
            randomList.add(i);
        }
        Collections.shuffle(randomList);

        //System.out.println(randomList);
        Agent[] agents = new Agent[Variable.AGENT_NUM];
        for (int i=0; i<Variable.AGENT_NUM; i++){
            initial_pos = randomList.get(i);
            agents[i] = new Agent(initial_pos/Variable.M, initial_pos%Variable.M);
            grid.recordPos(agents[i]);
            //System.out.printf("(%d, %d)\n", agents[i].row, agents[i].col);
        }

        for(int i=0; i<Variable.N; i++){
            for(int j=0; j<Variable.M; j++){
                System.out.print(grid.agent_pos[i][j]);
            }
            System.out.println();
        }
    }
}