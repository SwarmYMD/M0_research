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
    public static final double alpha = 0.9;
    public static final double c = 0.8;
    public static final int maxStep = 10000;
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
    double[][] pherData = new double[Variable.N][Variable.M];
    double[][] areaPherData = new double[Variable.n][Variable.m];

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
        for(int i=0; i<Variable.N; i++){
            for(int j=0; j<Variable.M; j++){
                pherData[i][j] = 0;
            }
        }
        for(int i=0; i<Variable.n; i++){
            for(int j=0; j<Variable.m; j++){
                areaPherData[i][j] = 0;
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
    double[][] PherMatrix = new double[Variable.n][Variable.m];
    double[][] expIndicMatrix = new double[Variable.n][Variable.m];
    double[][] subPherMatrix = new double[Variable.H][Variable.W];
    double[][] disIndicMatrix = new double[Variable.H][Variable.W];
    int areaNo;
    int d_exp;
    int d_dis;

    Agent(int c, int r){
        this.col = c;
        this.row = r;
        this.state = "d";
        this.range = 3;
        for(int i=0; i<Variable.n; i++){
            for(int j=0; j<Variable.m; j++){
                PherMatrix[i][j] = 0;
                expIndicMatrix[i][j] = 0;
            }
        }
        for(int i=0; i<Variable.H; i++){
            for(int j=0; j<Variable.W; j++){
                subPherMatrix[i][j] = 0;
                disIndicMatrix[i][j] = 0;
            }
        }
        this.areaNo = 0;
        this.d_exp = 0;
        this.d_dis = 0;
    }

    void getAreaNo(){
        int x = col / Variable.W;
        int y = row / Variable.H;
        areaNo = x + y * Variable.m;
    }

    void move_dis(Grid grid){
        int leftEnd, rightEnd;
        int upperEnd, lowerEnd;
        leftEnd = (areaNo % Variable.m) * Variable.W;
        rightEnd = leftEnd + Variable.W - 1;
        upperEnd = (areaNo / Variable.m) * Variable.H;
        lowerEnd = upperEnd + Variable.H - 1;
        // copy pheromone data of the area which this agent exists
        for(int i=upperEnd; i<=lowerEnd; i++){
            for(int j=leftEnd; j<=rightEnd; j++){
                subPherMatrix[i-upperEnd][j-leftEnd] = grid.pherData[i][j];
            }
        }
        for(int i=0; i<Variable.H; i++){
            for(int j=0; j<Variable.W; j++){
                d_dis = Math.abs(row - i - upperEnd) + Math.abs(col - j - leftEnd);
                if(d_dis != 0){
                    disIndicMatrix[i][j] = Math.exp(subPherMatrix[i][j]) / d_dis;
                } else {
                    disIndicMatrix[i][j] = 0;
                }
            }
        }

        // checking behavior of this function

        System.out.printf("(%d, %d)\n", row, col);

        System.out.print(areaNo);
        System.out.println();

        for(int i=0; i<Variable.H; i++){
            for(int j=0; j<Variable.W; j++){
                System.out.printf("%f,", disIndicMatrix[i][j]);
            }
            System.out.println();
        }
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

        // Initial setting of agents.
        Agent[] agents = new Agent[Variable.AGENT_NUM];
        for (int i=0; i<Variable.AGENT_NUM; i++){
            initial_pos = randomList.get(i);
            agents[i] = new Agent(initial_pos/Variable.M, initial_pos%Variable.M);
            agents[i].getAreaNo();
            grid.recordPos(agents[i]);
            //System.out.printf("(%d, %d)\n", agents[i].row, agents[i].col);
        }

        for(int i=0; i<Variable.N; i++){
            for(int j=0; j<Variable.M; j++){
                System.out.print(grid.agent_pos[i][j]);
            }
            System.out.println();
        }

        agents[0].move_dis(grid);

        /*

        for(int i=0; i<Variable.maxStep; i++){
            for (int j=0; j<Variable.AGENT_NUM; j++){
                agents[i].getAreaNo();
                // dispersion mode
                if(agents[i].state.equals("d")){
                    
                }

                // exploration mode
                if(agents[i].state.equals("e")){
                    
                }
                grid.recordPos(agents[i]);
            }
        }
        */
    }
}