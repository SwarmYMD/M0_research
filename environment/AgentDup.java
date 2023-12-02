package environment;

import java.util.*;

import environment.Variable;

public class AgentDup{
    public int col;
    public int row;
    //int pos;
    public String state;
    public int range;  // capable distance of communicating among agents
    public double[][] PherMatrix = new double[Variable.n][Variable.m];
    public double[][] expIndicMatrix = new double[Variable.n][Variable.m];
    public double[][] subPherMatrix = new double[Variable.H][Variable.W];
    public double[][] disIndicMatrix = new double[Variable.H][Variable.W];
    public int areaNo;

    int d_exp;
    int d_dis;
    int pld_col;
    int pld_row;
    int pgd_col;
    int pgd_row;

    int next_area;

    double c_1;
    double c_2;
    double w;
    double rand;

    double v_col;
    double v_row;

    double x_col;
    double x_row;

    int length_move;

    int count;

    public double delta_tau;
    public double sum_pher;

    boolean modeChange = false;

    Random random = new Random();

    public AgentDup(int c, int r){
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

        this.next_area = -1;

        this.c_1 = 1.0;
        this.c_2 = 1.0;
        this.w = 0.6;
        this.rand = 0.0;

        this.delta_tau = 0.0;
        this.sum_pher = 0.0;

        this.v_col = 0;
        this.v_row = 0;
        this.x_col = 0;
        this.x_row = 0;
        this.length_move = 0;

        this.count = 0;
    }

    public int getAreaNo(int row, int col){
        int x = col / Variable.W;
        int y = row / Variable.H;
        return x + y * Variable.m;
    }

    public void dispersion(GridDup grid){
        int leftEnd, rightEnd;
        int upperEnd, lowerEnd;
        leftEnd = (areaNo % Variable.m) * Variable.W;
        rightEnd = leftEnd + Variable.W - 1;
        upperEnd = (areaNo / Variable.m) * Variable.H;
        lowerEnd = upperEnd + Variable.H - 1;

        //System.out.printf("dispersion mode.\n");

        if(grid.table[row][col] == 1){
            //System.out.printf("this agent already reached goal.\n");
            state = "t";
        }

        if(state.equals("t")){
            int h = areaNo/Variable.m;
            int w = areaNo%Variable.m;
            grid.pherData[row][col] = Variable.alpha * grid.pherData[row][col] + Variable.c * sum_pher;
            if(grid.pherData[row][col] > Variable.max_tau){
                grid.pherData[row][col] = Variable.max_tau;
            //} else if (grid.pherData[row][col] < 100){
                //grid.pherData[row][col] = 100;
            }
            grid.areaPherData[h][w] =  Variable.alpha * grid.areaPherData[h][w] + Variable.c * sum_pher;
            if(grid.areaPherData[h][w] > Variable.max_tau){
                grid.areaPherData[h][w] = Variable.max_tau;
            //} else if (grid.areaPherData[h][w] < 100){
                //grid.areaPherData[h][w] = 100;
            }
            sum_pher = 0;
            grid.alreadyUpdateDis[row][col] = true;
            grid.alreadyUpdateExp[h][w] = true;
            if(grid.agent_pos[row][col] == 1){
                grid.recordPos(this);
            }
            return;
        }

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
            state = "e";
            grid.vacant[areaNo] = true;
            v_col = 0;
            v_row = 0;
            x_col = 0;
            x_row = 0;
            //System.out.printf("mode changed to exploration.\n");
        } else if(grid.table[row][col] != 1){
            move_dis(grid, leftEnd, rightEnd, upperEnd, lowerEnd);
            // pheromone update will be written here?
        }

        if(length_move != 0){
            delta_tau = Variable.Q / length_move;
        }
        count = 0;
    }

    public void move_dis(GridDup grid, int leftEnd, int rightEnd, int upperEnd, int lowerEnd){
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

        //System.out.printf("pld: (%d, %d)\n", pld_row, pld_col);

        // pheromone update (calc of sum_pher is written in PF_PSO.java)
        if(grid.table[pld_row][pld_col] == 1){
            int h = areaNo/Variable.m;
            int w = areaNo%Variable.m;
            grid.pherData[pld_row][pld_col] = Variable.alpha * grid.pherData[pld_row][pld_col] + Variable.c * sum_pher;
            if(grid.pherData[pld_row][pld_col] > Variable.max_tau){
                grid.pherData[pld_row][pld_col] = Variable.max_tau;
            //} else if (grid.pherData[pld_row][pld_col] < 100){
                //grid.pherData[pld_row][pld_col] = 100;
            }
            grid.areaPherData[h][w] =  Variable.alpha * grid.areaPherData[h][w] + Variable.c * sum_pher;
            if(grid.areaPherData[h][w] > Variable.max_tau){
                grid.areaPherData[h][w] = Variable.max_tau;
            //} else if (grid.areaPherData[h][w] < 100){
                //grid.areaPherData[h][w] = 100;
            }
            sum_pher = 0;
            grid.alreadyUpdateDis[pld_row][pld_col] = true;
            grid.alreadyUpdateExp[h][w] = true;
        }
        
        // move to the next position
        rand = random.nextDouble();
        v_col = ((w * v_col + c_2 * rand * (pld_col - col)));
        v_row = ((w * v_row + c_2 * rand * (pld_row - row)));
        x_col = col + v_col;
        x_row = row + v_row;

        int move_col = (int)(x_col);
        int move_row = (int)(x_row);

        //System.out.printf("x: (%d, %d)\n", x_row, x_col);
        
        // prevent going over other area
        if(move_col < leftEnd){
            move_col = leftEnd;
            x_col = move_col;
        } else if(move_col > rightEnd){
            move_col = rightEnd;
            x_col = move_col;
        }

        if(move_row < upperEnd){
            move_row = upperEnd;
            x_row = move_row;
        } else if(move_row > lowerEnd){
            move_row = lowerEnd;
            x_row = move_row;
        }

        /*
        // check the next position that is unoccupied pattern grid
        List<List<Integer>> neighbors = new ArrayList<List<Integer>>(); 
        System.out.printf("pattern grid?: %d\n", grid.table[x_row][x_col]);
        while(grid.table[x_row][x_col] == 1){
            if(grid.occupied[x_row][x_col] == 1){
                neighbors.add(Arrays.asList(x_row,x_col-1));
                neighbors.add(Arrays.asList(x_row,x_col+1));
                neighbors.add(Arrays.asList(x_row-1,x_col));
                neighbors.add(Arrays.asList(x_row+1,x_col));
                Collections.shuffle(neighbors);

                x_row = neighbors.get(0).get(0);
                x_col = neighbors.get(0).get(1);

                neighbors.clear();

                // prevent going over other area
                if(x_col < leftEnd){
                    x_col = leftEnd;
                } else{
                    x_col = rightEnd;
                }

                if(x_row < upperEnd){
                    x_row = upperEnd;
                } else{
                    x_row = lowerEnd;
                }
            }
        }

        */

        //System.out.printf("x2: (%d, %d)\n", x_row, x_col);

        //grid.deletePos(this);

        length_move = length_move + Math.abs(col - move_col) + Math.abs(row - move_row);

        col = move_col;
        row = move_row;

        if(grid.table[row][col] == 1 && grid.occupied[row][col] == 0){
            grid.occupied[row][col] = 1;
        }

        //grid.recordPos(this);

        // checking behavior of this function

        //System.out.printf("now: (%d, %d)\n", row, col);

        //System.out.print(areaNo);
        //System.out.println();

        for(int i=0; i<Variable.H; i++){
            for(int j=0; j<Variable.W; j++){
                //System.out.printf("%f,", disIndicMatrix[i][j]);
            }
            //System.out.println();
        }
        /*
        System.out.printf("x: (%.1f, %.1f), ", x_row, x_col);
        System.out.printf("areaNo: %d, ", areaNo);
        System.out.printf("left, right: (%d, %d)\n", leftEnd, rightEnd);
        */
    }

    public void exploration(GridDup grid){
        int leftEnd, rightEnd;
        int upperEnd, lowerEnd;
        leftEnd = (areaNo % Variable.m) * Variable.W;
        rightEnd = leftEnd + Variable.W - 1;
        upperEnd = (areaNo / Variable.m) * Variable.H;
        lowerEnd = upperEnd + Variable.H - 1;

        if(grid.table[row][col] == 1){
            //System.out.printf("this agent already reached goal.\n");
            state = "t";
            if(grid.table[row][col] == 1){
                grid.occupied[row][col] = 1;
            }
            return;
        }

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

        if(count == 0){
            grid.vacant[areaNo] = true;
        }
        
        if(areaNo == next_area){
            if(grid.vacant[areaNo] == false){
                state = "d";
                v_col = 0;
                v_row = 0;
                x_col = 0;
                x_row = 0;
            } else {
                move_exp(grid);
            }
            //System.out.printf("mode changed to dispersion.\n");
        } else if(grid.vacant[areaNo] == false) {
            state = "d";
            v_col = 0;
            v_row = 0;
            x_col = 0;
            x_row = 0;
        } else if(next_area != -1){
            rand = random.nextDouble();
            v_col = ((w * v_col + c_2 * rand * (pgd_col - col)));
            v_row = ((w * v_row + c_2 * rand * (pgd_row - row)));
            x_col = col + v_col;
            x_row = row + v_row;

            int move_col = (int)(x_col);
            int move_row = (int)(x_row);

            if(move_col < 0){
                move_col = 0;
                x_col = move_col;
            } else if(move_col > Variable.M-1){
                move_col = Variable.M-1;
                x_col = move_col;
            }

            if(move_row < 0){
                move_row = 0;
                x_row = move_row;
            } else if(move_row > Variable.N-1){
                move_row = Variable.N-1;
                x_row = move_row;
            }

            //System.out.printf("x: (%d, %d)\n", x_row, x_col);

            //grid.deletePos(this);

            length_move = length_move + Math.abs(col - move_col) + Math.abs(row - move_row);

            col = move_col;
            row = move_row;
        } else {
            move_exp(grid);
        }
        if(length_move != 0){
            delta_tau = Variable.Q / length_move;
        }
        count = 0;
    }

    public void move_exp(GridDup grid){
        for(int i=0; i<Variable.n; i++){
            for(int j=0; j<Variable.m; j++){
               PherMatrix[i][j] = grid.areaPherData[i][j];
            }
        }

        for(int i=0; i<Variable.n; i++){
            for(int j=0; j<Variable.m; j++){
                d_exp = Math.abs(row - (i*Variable.H + Variable.H/2 )) + Math.abs(col - (j*Variable.W + Variable.W/2 ));
                if (grid.vacant[i*Variable.m+j] == true){
                    expIndicMatrix[i][j] = 0;
                } else if(d_exp != 0){
                    expIndicMatrix[i][j] = Math.exp(0-PherMatrix[i][j]) / d_exp;
                } else {
                    expIndicMatrix[i][j] = 0;
                }
            }
        }

        int maxIndex = maxIndex(expIndicMatrix);
        int a = maxIndex % Variable.m;
        int b = maxIndex / Variable.m;

        // determine pgd
        pgd_col = a*Variable.W + Variable.W/2;
        pgd_row = b*Variable.H + Variable.H/2;

        
        //System.out.printf("now: (%d, %d)\n", row, col);
        //System.out.printf("pgd: (%d, %d)\n", pgd_row, pgd_col);
        //System.out.printf("areaNo: %d\n", areaNo);
        //System.out.println();

        next_area = getAreaNo(pgd_row, pgd_col);
        
        // move to the next position
        rand = random.nextDouble();
        v_col = ((w * v_col + c_2 * rand * (pgd_col - col)));
        v_row = ((w * v_row + c_2 * rand * (pgd_row - row)));
        x_col = col + v_col;
        x_row = row + v_row;

        int move_col = (int)(x_col);
        int move_row = (int)(x_row);

        if(move_col < 0){
            move_col = 0;
            x_col = move_col;
        } else if(move_col > Variable.M-1){
            move_col = Variable.M-1;
            x_col = move_col;
        }

        if(move_row < 0){
            move_row = 0;
            x_row = move_row;
        } else if(move_row > Variable.N-1){
            move_row = Variable.N-1;
            x_row = move_row;
        }

        //System.out.printf("x: (%d, %d)\n", x_row, x_col);

        //grid.deletePos(this);

        length_move = length_move + Math.abs(col - move_col) + Math.abs(row - move_row);

        col = move_col;
        row = move_row;

        //System.out.printf("now: (%d, %d)\n", row, col);
        //System.out.printf("pgd: (%d, %d)\n", pgd_row, pgd_col);

        //grid.recordPos(this);
    }

    public int maxIndex(double[][] indic){
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
