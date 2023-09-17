import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


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
    int[][] occupied = new int[Variable.N][Variable.M];
    int[][] agent_pos = new int[Variable.N][Variable.M];
    double[][] pherData = new double[Variable.N][Variable.M];
    double[][] areaPherData = new double[Variable.n][Variable.m];

    Grid(){
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
                // pherData[i][j] = i * Variable.M + j;
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

    void deletePos(Agent agent){
        agent_pos[agent.row][agent.col] = 0;
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
    int pld_col;
    int pld_row;

    double c_1;
    double c_2;
    double w;
    double rand;

    int v_col;
    int v_row;

    int x_col;
    int x_row;

    boolean modeChange = false;

    Random random = new Random();

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
        this.pld_col = 0;
        this.pld_row = 0;

        this.c_1 = 1.0;
        this.c_2 = 1.0;
        this.w = 0.6;
        this.rand = 0.0;

        this.v_col = 0;
        this.v_row = 0;
        this.x_col = 0;
        this.x_row = 0;
    }

    void getAreaNo(){
        int x = col / Variable.W;
        int y = row / Variable.H;
        areaNo = x + y * Variable.m;
    }

    void dispersion(Grid grid){
        int count = 0;
        int leftEnd, rightEnd;
        int upperEnd, lowerEnd;
        leftEnd = (areaNo % Variable.m) * Variable.W;
        rightEnd = leftEnd + Variable.W - 1;
        upperEnd = (areaNo / Variable.m) * Variable.H;
        lowerEnd = upperEnd + Variable.H - 1;

        System.out.printf("dispersion mode.\n");

        // count the number of pattern on grid
        for(int i=upperEnd; i<=lowerEnd; i++){
            for(int j=leftEnd; j<=rightEnd; j++){
                if(grid.table[i][j] == 1){
                    count++;
                }
            }
        }

        // check the number of occupied grids
        for(int i=upperEnd; i<=lowerEnd; i++){
            for(int j=leftEnd; j<=rightEnd; j++){
                if(grid.table[i][j] == 1){
                    if(grid.occupied[i][j] == 1){
                        count--;
                        //move_dis(grid, leftEnd, rightEnd, upperEnd, lowerEnd);
                    }
                }
            }
        }

        // if all pattern grids are occupied, changing mode(otherwise, continuing dispersion mode)
        if(count == 0){
            state.replace("d", "e");
            System.out.printf("mode changed.\n");
        } else if(grid.table[row][col] != 1){
            move_dis(grid, leftEnd, rightEnd, upperEnd, lowerEnd);
            // pheromone update will be written here?
        } else{
            grid.occupied[row][col] = 1;
            System.out.printf("this agent already reached goal.\n");
            // pheromone update will be written here?
        }
    }

    void move_dis(Grid grid, int leftEnd, int rightEnd, int upperEnd, int lowerEnd){
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

        int maxIndex = maxIndex(disIndicMatrix);
        int a = maxIndex % Variable.W;
        int b = maxIndex / Variable.W;

        // determine pld
        pld_col = leftEnd + a;
        pld_row = upperEnd + b;

        /*
        System.out.printf("now: (%d, %d)\n\n", row, col);

        System.out.printf("upper, left: (%d, %d)\n", upperEnd, leftEnd);
        System.out.printf("b, a: (%d, %d)\n", b, a);
        */
        System.out.printf("pld: (%d, %d)\n", pld_row, pld_col);
        
        // move to the next position
        rand = random.nextDouble();
        v_col = (int)(w * v_col + c_2 * rand * (pld_col - col));
        v_row = (int)(w * v_row + c_2 * rand * (pld_row - row));
        x_col = col + v_col;
        x_row = row + v_row;

        System.out.printf("x: (%d, %d)\n", x_row, x_col);
        
        grid.deletePos(this);

        if(x_col >= leftEnd && x_col <= rightEnd){
            col = x_col;
        } else if(x_col < leftEnd){
            col = leftEnd;
        } else{
            col = rightEnd;
        }

        if(x_row >= upperEnd && x_row <= lowerEnd){
            row = x_row;
        } else if(x_row < upperEnd){
            row = upperEnd;
        } else{
            row = lowerEnd;
        }

        /*
        col = pld_col;
        row = pld_row;
        */

        grid.recordPos(this);

        // checking behavior of this function

        System.out.printf("now: (%d, %d)\n", row, col);

        System.out.print(areaNo);
        System.out.println();

        /*

        for(int i=0; i<Variable.H; i++){
            for(int j=0; j<Variable.W; j++){
                System.out.printf("%f,", subPherMatrix[i][j]);
            }
            System.out.println();
        }
        */

        for(int i=0; i<Variable.H; i++){
            for(int j=0; j<Variable.W; j++){
                System.out.printf("%f,", disIndicMatrix[i][j]);
            }
            System.out.println();
        }

        // System.out.printf("pld: (%d, %d)\n", pld_row, pld_col);
    }

    int maxIndex(double[][] indic){
        double max = 0;
        List<Integer> maxIndexList = new ArrayList<Integer>();
        for(int i=0; i<indic.length; i++){
            for(int j=0; j<indic[i].length; j++){
                if(max < indic[i][j]){
                    max = indic[i][j];
                } 
            }
        }

        for(int i=0; i<indic.length; i++){
            for(int j=0; j<indic[i].length; j++){
                if(max == indic[i][j]){
                    maxIndexList.add(i*indic[i].length + j);
                } 
            }
        }

        Collections.shuffle(maxIndexList);

        return maxIndexList.get(0);
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

        System.out.printf("now: (%d, %d)\n", agents[0].row, agents[0].col);

        System.out.print(agents[0].areaNo);
        System.out.println();

        System.out.println();

        if(agents[0].state.equals("d")){
            agents[0].dispersion(grid); 
            
            for(int i=0; i<Variable.N; i++){
            for(int j=0; j<Variable.M; j++){
                System.out.print(grid.agent_pos[i][j]);
            }
            System.out.println();
        }
        }

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
            }
        }
        */
    }
}