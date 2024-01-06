package environment;

import java.util.*;

import environment.Variable;

public class Agent_proposal{
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

    int stop = 0;

    int flag = 0;
    int pre_r = row;
    int pre_c = col;

    public int time_count;

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
    public double delta_Q;

    boolean modeChange = false;

    double beta = 0.5;

    Random random = new Random();

    // new variable
    int second_m = Variable.m/5;
    int second_n = Variable.n/5;

    int second_H = Variable.M/second_m;
    int second_W = Variable.N/second_n;

    public double[][] secondPherMatrix = new double[second_n][second_m];
    public double[][] secondExpIndicMatrix = new double[second_n][second_m];
    public double[][] secondSubPherMatrix = new double[second_H][second_W];
    public double[][] secondDisIndicMatrix = new double[second_H][second_W];

    public Agent_proposal(int c, int r){
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

        this.time_count = 0;

        this.c_1 = 1.0;
        this.c_2 = 1.0;
        this.w = 0.6;
        this.rand = 0.0;

        this.delta_tau = 0.0;
        this.sum_pher = 0.0;
        this.delta_Q = 0.0;

        this.v_col = 0;
        this.v_row = 0;
        this.x_col = 0;
        this.x_row = 0;
        this.length_move = 0;

        this.count = 0;

        // new part
        for(int i=0; i<second_n; i++){
            for(int j=0; j<second_m; j++){
                secondPherMatrix[i][j] = 0;
                secondExpIndicMatrix[i][j] = 0;
            }
        }
        for(int i=0; i<second_H; i++){
            for(int j=0; j<second_W; j++){
                secondSubPherMatrix[i][j] = 0;
                secondDisIndicMatrix[i][j] = 0;
            }
        }
    }

    public int getAreaNo(int row, int col){
        int x = col / Variable.W;
        int y = row / Variable.H;
        return x + y * Variable.m;
    }

    public void dispersion(Grid_proposal grid, Agent_proposal[] agents){
        int leftEnd, rightEnd;
        int upperEnd, lowerEnd;
        leftEnd = (areaNo % Variable.m) * Variable.W;
        rightEnd = leftEnd + Variable.W - 1;
        upperEnd = (areaNo / Variable.m) * Variable.H;
        lowerEnd = upperEnd + Variable.H - 1;

        if(pre_c == col && pre_r == row){
            time_count++;
        } else {
            time_count = 0;
        }
        pre_c = col;
        pre_r = row;  

        ArrayList<Integer> candidate = new ArrayList<Integer>();
        int change_pos;

        //System.out.printf("dispersion mode.\n");

        if(grid.table[row][col] == 1){
            //System.out.printf("this agent already reached goal.\n");
            state = "t";
            if(grid.table[row][col] == 1){
                grid.occupied[row][col] = 1;
            }
        }

        if(state.equals("t")){
            int h = areaNo/Variable.m;
            int w = areaNo%Variable.m;          
            
            //if(grid.vacant[areaNo] == false){
                //if(time_count == 5){
                    for(int j = 0; j < 4; j++){
                        int r = row + Variable.dir_row[j];
                        int c = col + Variable.dir_col[j];
                        if(r >= 0 && r < Variable.N && c >= 0 && c < Variable.M){
                            if(grid.agent_pos[r][c] == 0 && grid.table[r][c] == 1){
                                for(int p = row - range; p <= row + range; p++){
                                    for(int q = col - range; q <= col + range; q++){
                                        if(p >= 0 && p < Variable.N && q >= 0 && q < Variable.M){
                                            if(GetDist(row, col, p, q) <= range){
                                                delta_Q += Variable.Q;
                                            }
                                        }
                                    }
                                }
                                grid.pherData[r][c] = grid.pherData[r][c] + Variable.c * sum_pher;
                                //grid.pherData[r][c] = grid.pherData[r][c] + Variable.c * delta_Q;
                                if(grid.pherData[r][c] > Variable.max_tau){
                                    grid.pherData[r][c] = Variable.max_tau;
                                }
                                //grid.alreadyUpdateDis[r][c] = true;
                                delta_Q = 0;
                            }
                        }
                    }
                    //time_count = 0;
                //}
            //}
            
            
            
            

            if(grid.alreadyUpdateDis[row][col] == false){
                //grid.pherData[row][col] = Variable.alpha * grid.pherData[row][col] + Variable.c * sum_pher;
                grid.pherData[row][col] = Variable.alpha * grid.pherData[row][col];
                if(grid.pherData[row][col] > Variable.max_tau){
                    grid.pherData[row][col] = Variable.max_tau;
                //} else if (grid.pherData[row][col] < 100){
                    //grid.pherData[row][col] = 100;
                }
                grid.alreadyUpdateDis[row][col] = true;
            }
            if(grid.alreadyUpdateExp[h][w] == false){
            grid.areaPherData[h][w] =  Variable.alpha * grid.areaPherData[h][w] + Variable.c * sum_pher;
            if(grid.areaPherData[h][w] > Variable.max_tau){
                grid.areaPherData[h][w] = Variable.max_tau;
            //} else if (grid.areaPherData[h][w] < 100){
                //grid.areaPherData[h][w] = 100;
            }
                grid.alreadyUpdateExp[h][w] = true;
            }
            sum_pher = 0;
            
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
                    if(grid.agent_pos[i][j] == 1){
                        count--;
                        //move_dis(grid, leftEnd, rightEnd, upperEnd, lowerEnd, agents);
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
            move_dis(grid, leftEnd, rightEnd, upperEnd, lowerEnd, agents);
            grid.vacant[areaNo] = false;
            // pheromone update will be written here?
        } else{
            grid.vacant[areaNo] = false;
        }

        if(length_move != 0){
            delta_tau = Variable.Q / length_move;
        }
        count = 0;
        sum_pher = 0;
    }

    public void move_dis(Grid_proposal grid, int leftEnd, int rightEnd, int upperEnd, int lowerEnd, Agent_proposal[] agents){
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
            if(grid.agent_pos[pld_row][pld_col] == 0){
            //if(grid.agent_pos[pld_row][pld_col] == 0 && grid.alreadyUpdateDis[pld_row][pld_col] == false){
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

        

        check_dis(grid, leftEnd, rightEnd, upperEnd, lowerEnd, agents, move_row, move_col);

        if(grid.table[row][col] == 1){
            //System.out.printf("this agent already reached goal.\n");
            state = "t";
            if(grid.table[row][col] == 1){
                grid.occupied[row][col] = 1;
            }
        }
    }

    public void check_dis(Grid_proposal grid, int leftEnd, int rightEnd, int upperEnd, int lowerEnd, Agent_proposal[] agents, int mr, int mc){
        ArrayList<Integer> candiList = new ArrayList<Integer>();
        int change_pos;
        for(int i = 0; i < Variable.AGENT_NUM; i++){
            if(this != agents[i]){
            //System.out.printf("agents[%d] pos: (%d, %d)\n", i, agents[i].row, agents[i].col);
                if(mr == agents[i].row && mc == agents[i].col){
                    flag = 1;
                    for(int j = 0; j < 4; j++){
                        int r = agents[i].row + Variable.dir_row[j];
                        int c = agents[i].col + Variable.dir_col[j];
                        if(r >= 0 && r < Variable.N && c >= 0 && c < Variable.M){
                            if(grid.agent_pos[r][c] == 0){
                                if(grid.table[r][c] == 1){
                                    candiList.add(r*Variable.M+c);
                                }
                            }
                        }
                    }
                    if(candiList.size() != 0){
                        Collections.shuffle(candiList);
                        change_pos = candiList.get(0);
                        int a = change_pos % Variable.M;
                        int b = change_pos / Variable.M;
                        grid.agent_pos[agents[i].row][agents[i].col] = 0;
                        grid.occupied[agents[i].row][agents[i].col] = 0;
                        agents[i].length_move = agents[i].length_move + 1;
                        agents[i].col = a;
                        agents[i].row = b;
                        agents[i].v_col = 0;
                        agents[i].v_row = 0;
                        if(grid.agent_pos[b][a] == 1){
                            System.out.println("Invalid in check_dis");
                        }
                        grid.agent_pos[b][a] = 1;

                        if(grid.table[b][a] == 1){
                            grid.occupied[b][a] = 1;
                        }

                        grid.occupied[row][col] = 0;
                        length_move = length_move + Math.abs(col - mc) + Math.abs(row - mr);

                        grid.deletePos(this);
                        col = mc;
                        row = mr;

                        stop = 0;

                        
                        if(grid.table[row][col] == 1){
                            grid.occupied[row][col] = 1;
                        }
                        grid.recordPos(this);

                    }else if(grid.table[agents[i].row][agents[i].col] == 0){
                        for(int j = 0; j < 4; j++){
                            int r = agents[i].row + Variable.dir_row[j];
                            int c = agents[i].col + Variable.dir_col[j];
                            if(r >= 0 && r < Variable.N && c >= 0 && c < Variable.M){
                                if(grid.agent_pos[r][c] != 1){
                                    candiList.add(r*Variable.M+c);
                                }
                            }
                        }
                        if(candiList.size() != 0){
                            change_pos = candiList.get(0);
                            int a = change_pos % Variable.M;
                            int b = change_pos / Variable.M;
                            grid.agent_pos[agents[i].row][agents[i].col] = 0;
                            agents[i].length_move = agents[i].length_move + 1;
                            agents[i].col = a;
                            agents[i].row = b;
                            agents[i].v_col = 0;
                            agents[i].v_row = 0;
                            grid.agent_pos[b][a] = 1;

                            grid.occupied[row][col] = 0;
                            length_move = length_move + Math.abs(col - mc) + Math.abs(row - mr);
                            grid.deletePos(this);
                            col = mc;
                            row = mr;

                            stop = 0;

                            
                            if(grid.table[row][col] == 1){
                                grid.occupied[row][col] = 1;
                            }
                            grid.recordPos(this);
                        }
                    }
                } else {
                    stop = 1;

                    /*
                    length_move = length_move + Math.abs(col - mc) + Math.abs(row - mr);
                    grid.deletePos(this);
                    col = mc;
                    row = mr;

                    if(grid.table[row][col] == 1 && grid.occupied[row][col] == 0){
                        grid.occupied[row][col] = 1;
                    }
                    grid.recordPos(this);
                    */
                }
            }
        }

        candiList.clear();

        if(flag == 0){
            length_move = length_move + Math.abs(col - mc) + Math.abs(row - mr);
            grid.deletePos(this);
            col = mc;
            row = mr;

            
            if(grid.table[row][col] == 1){
                grid.occupied[row][col] = 1;
            }
            grid.recordPos(this);
        } else  if(col != mc && row != mr){
            v_col = 0;
            v_row = 0;
        }

        flag = 0;
        
        /*
        //grid.deletePos(this);

        length_move = length_move + Math.abs(col - move_col) + Math.abs(row - move_row);

        col = move_col;
        row = move_row;

        
        if(grid.table[row][col] == 1 && grid.occupied[row][col] == 0){
            grid.occupied[row][col] = 1;
        }
        */
        

        //grid.recordPos(this);

        // checking behavior of this function

        //System.out.printf("now: (%d, %d)\n", row, col);

        //System.out.print(areaNo);
        //System.out.println();

        /*
        for(int i=0; i<Variable.H; i++){
            for(int j=0; j<Variable.W; j++){
                //System.out.printf("%f,", disIndicMatrix[i][j]);
            }
            //System.out.println();
        }
        
        System.out.printf("x: (%.1f, %.1f), ", x_row, x_col);
        System.out.printf("areaNo: %d, ", areaNo);
        System.out.printf("left, right: (%d, %d)\n", leftEnd, rightEnd);
        */
    }

    public void exploration(Grid_proposal grid, Agent_proposal[] agents){
        int leftEnd, rightEnd;
        int upperEnd, lowerEnd;
        leftEnd = (areaNo % Variable.m) * Variable.W;
        rightEnd = leftEnd + Variable.W - 1;
        upperEnd = (areaNo / Variable.m) * Variable.H;
        lowerEnd = upperEnd + Variable.H - 1;

        if(pre_c == col && pre_r == row){
            time_count++;
        } else {
            time_count = 0;
        }
        pre_c = col;
        pre_r = row;  

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
                    if(grid.agent_pos[i][j] == 1){
                        count--;
                        //move_dis(grid, leftEnd, rightEnd, upperEnd, lowerEnd, agents);
                    }
                }
            }
        }

        if(count == 0){
            grid.vacant[areaNo] = true;
        }else{
            grid.vacant[areaNo] = false;
        }
        
        if(areaNo == next_area){
            if(grid.vacant[areaNo] == false){
                state = "d";
                v_col = 0;
                v_row = 0;
                x_col = 0;
                x_row = 0;
            } else {
                move_exp(grid, agents);
            }
            //System.out.printf("mode changed to dispersion.\n");
        } else if(grid.vacant[areaNo] == false) {
            state = "d";
            v_col = 0;
            v_row = 0;
            x_col = 0;
            x_row = 0;
        /*
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

            check_exp(grid, agents, move_row, move_col);
            */
        } else {
            move_exp(grid, agents);
        }
        if(length_move != 0){
            delta_tau = Variable.Q / length_move;
        }
        count = 0;
        sum_pher = 0;
    }

    public void move_exp(Grid_proposal grid, Agent_proposal[] agents){
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

        
        //modify part 
        int expIndex = maxIndex(expIndicMatrix);
        int area_c = expIndex % Variable.m;
        int area_r = expIndex / Variable.m;

        next_area = getAreaNo(area_r, area_c);

        int leftNext, rightNext;
        int upperNext, lowerNext;
        leftNext = (next_area % Variable.m) * Variable.W;
        rightNext = leftNext + Variable.W - 1;
        upperNext = (next_area / Variable.m) * Variable.H;
        lowerNext = upperNext + Variable.H - 1;

        for(int i=upperNext; i<=lowerNext; i++){
            for(int j=leftNext; j<=rightNext; j++){
                subPherMatrix[i-upperNext][j-leftNext] = grid.pherData[i][j];
            }
        }
        
        for(int i=0; i<Variable.H; i++){
            for(int j=0; j<Variable.W; j++){
                d_dis = Math.abs(row - i - upperNext) + Math.abs(col - j - leftNext);
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
        
        // determine pgd
        pgd_col = area_c*Variable.W + a;
        pgd_row = area_r*Variable.H + b;
        

        /*
        int maxIndex = maxIndex(expIndicMatrix);
        int a = maxIndex % Variable.m;
        int b = maxIndex / Variable.m;

        // determine pgd
        pgd_col = a*Variable.W + Variable.W/2;
        pgd_row = b*Variable.H + Variable.H/2;
        */

        
        //System.out.printf("now: (%d, %d)\n", row, col);
        //System.out.printf("pgd: (%d, %d)\n", pgd_row, pgd_col);
        //System.out.printf("areaNo: %d\n", areaNo);
        //System.out.println();

        //next_area = getAreaNo(pgd_row, pgd_col);
        
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

        /*
        grid.deletePos(this);

        length_move = length_move + Math.abs(col - move_col) + Math.abs(row - move_row);

        col = move_col;
        row = move_row;

        //System.out.printf("now: (%d, %d)\n", row, col);
        //System.out.printf("pgd: (%d, %d)\n", pgd_row, pgd_col);

        grid.recordPos(this);
        */
        check_exp(grid, agents, move_row, move_col);

        if(grid.table[row][col] == 1){
            //System.out.printf("this agent already reached goal.\n");
            state = "t";
            if(grid.table[row][col] == 1){
                grid.occupied[row][col] = 1;
            }
        }
    }

    public void check_exp(Grid_proposal grid, Agent_proposal[] agents, int mr, int mc){
        ArrayList<Integer> candiList = new ArrayList<Integer>();
        int change_pos;
        //int unyo = 0;
        for(int i = 0; i < Variable.AGENT_NUM; i++){
            if(this != agents[i]){
            //System.out.printf("agents[%d] pos: (%d, %d)\n", i, agents[i].row, agents[i].col);
                if(mr == agents[i].row && mc == agents[i].col){
                    flag = 1;
                    for(int j = 0; j < 4; j++){
                        int r = agents[i].row + Variable.dir_row[j];
                        int c = agents[i].col + Variable.dir_col[j];
                        if(r >= 0 && r < Variable.N && c >= 0 && c < Variable.M){
                            if(grid.agent_pos[r][c] != 1){
                                if(grid.table[r][c] == 1){
                                    candiList.add(r*Variable.M+c);
                                }
                            }
                        }
                    }
                    if(candiList.size() != 0){
                        Collections.shuffle(candiList);
                        change_pos = candiList.get(0);
                        int a = change_pos % Variable.M;
                        int b = change_pos / Variable.M;
                        grid.agent_pos[agents[i].row][agents[i].col] = 0;
                        grid.occupied[agents[i].row][agents[i].col] = 0;
                        agents[i].length_move = agents[i].length_move + 1;
                        agents[i].col = a;
                        agents[i].row = b;
                        agents[i].v_col = 0;
                        agents[i].v_row = 0;
                        if(grid.agent_pos[b][a] == 1){
                            System.out.println("Invalid in check_exp");
                        }
                        grid.agent_pos[b][a] = 1;

                        if(grid.table[b][a] == 1){
                            grid.occupied[b][a] = 1;
                        }

                        grid.occupied[row][col] = 0;
                        length_move = length_move + Math.abs(col - mc) + Math.abs(row - mr);

                        grid.deletePos(this);
                        col = mc;
                        row = mr;

                        stop = 0;

                        
                        if(grid.table[row][col] == 1){
                            grid.occupied[row][col] = 1;
                        }
                        grid.recordPos(this);

                    }else if(grid.table[agents[i].row][agents[i].col] == 0){
                        for(int j = 0; j < 4; j++){
                            int r = agents[i].row + Variable.dir_row[j];
                            int c = agents[i].col + Variable.dir_col[j];
                            if(r >= 0 && r < Variable.N && c >= 0 && c < Variable.M){
                                if(grid.agent_pos[r][c] != 1){
                                    candiList.add(r*Variable.M+c);
                                }
                            }
                        }
                        if(candiList.size() != 0){
                            change_pos = candiList.get(0);
                            int a = change_pos % Variable.M;
                            int b = change_pos / Variable.M;
                            grid.agent_pos[agents[i].row][agents[i].col] = 0;
                            grid.occupied[agents[i].row][agents[i].col] = 0;
                            agents[i].length_move = agents[i].length_move + 1;
                            agents[i].col = a;
                            agents[i].row = b;
                            agents[i].v_col = 0;
                            agents[i].v_row = 0;
                            grid.agent_pos[b][a] = 1;

                            if(grid.table[b][a] == 1){
                                grid.occupied[b][a] = 1;
                            }

                            grid.occupied[row][col] = 0;
                            length_move = length_move + Math.abs(col - mc) + Math.abs(row - mr);
                            grid.deletePos(this);
                            col = mc;
                            row = mr;

                            stop = 0;

                            
                            if(grid.table[row][col] == 1){
                                grid.occupied[row][col] = 1;
                            }
                            grid.recordPos(this);

                            //System.out.println("pass the check_exp");
                        }
                    }
                } else {
                    stop = 1;
                    /*
                    length_move = length_move + Math.abs(col - mc) + Math.abs(row - mr);
                    grid.deletePos(this);
                    col = mc;
                    row = mr;

                    if(grid.table[row][col] == 1 && grid.occupied[row][col] == 0){
                        grid.occupied[row][col] = 1;
                    }
                    grid.recordPos(this);
                    */
                }
            }
            //unyo = i;
        }

        //System.out.println(unyo);

        candiList.clear();

        if(flag == 0){
            length_move = length_move + Math.abs(col - mc) + Math.abs(row - mr);
            grid.deletePos(this);
            col = mc;
            row = mr;

            
            if(grid.table[row][col] == 1 && grid.occupied[row][col] == 0){
                grid.occupied[row][col] = 1;
            }
            grid.recordPos(this);
        } else if(col != mc && row != mr){
            v_col = 0;
            v_row = 0;
        }

        flag = 0;
        
        /*
        //grid.deletePos(this);

        length_move = length_move + Math.abs(col - move_col) + Math.abs(row - move_row);

        col = move_col;
        row = move_row;

        
        if(grid.table[row][col] == 1 && grid.occupied[row][col] == 0){
            grid.occupied[row][col] = 1;
        }
        */
        

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

        if(maxIndexList.size() == 0){
            for(int i=0; i<indic.length; i++){
                for(int j=0; j<indic[i].length; j++){
                    System.out.print(indic[i][j]);
                }
                System.out.println();
            }
            System.out.println();
        }

        return maxIndexList.get(0);
    }





    // new part
    public int getAreaNo2(int row, int col){
        int x = col / second_W;
        int y = row / second_H;
        return x + y * second_m;
    }

    public void dis2(Grid_proposal grid, Agent_proposal[] agents){
        int leftEnd, rightEnd;
        int upperEnd, lowerEnd;
        leftEnd = (areaNo % second_m) * second_W;
        rightEnd = leftEnd + second_W - 1;
        upperEnd = (areaNo / second_m) * second_H;
        lowerEnd = upperEnd + second_H - 1;

        if(pre_c == col && pre_r == row){
            time_count++;
        } else {
            time_count = 0;
        }
        pre_c = col;
        pre_r = row;  

        ArrayList<Integer> candidate = new ArrayList<Integer>();
        int change_pos;

        //System.out.printf("dispersion mode.\n");

        if(grid.table[row][col] == 1){
            //System.out.printf("this agent already reached goal.\n");
            state = "t";
            if(grid.table[row][col] == 1){
                grid.occupied[row][col] = 1;
            }
        }

        if(state.equals("t")){
            int h = areaNo/second_m;
            int w = areaNo%second_m;        
            
            //if(grid.vacant[areaNo] == false){
                //if(time_count == 5){
                    for(int j = 0; j < 4; j++){
                        int r = row + Variable.dir_row[j];
                        int c = col + Variable.dir_col[j];
                        if(r >= 0 && r < Variable.N && c >= 0 && c < Variable.M){
                            if(grid.agent_pos[r][c] == 0 && grid.table[r][c] == 1){
                                for(int p = row - range; p <= row + range; p++){
                                    for(int q = col - range; q <= col + range; q++){
                                        if(p >= 0 && p < Variable.N && q >= 0 && q < Variable.M){
                                            if(GetDist(row, col, p, q) <= range){
                                                delta_Q += Variable.Q;
                                            }
                                        }
                                    }
                                }
                                grid.pherData[r][c] = grid.pherData[r][c] + Variable.c * sum_pher;
                                //grid.pherData[r][c] = Variable.alpha * grid.pherData[r][c] + Variable.c * delta_Q;
                                if(grid.pherData[r][c] > Variable.max_tau){
                                    grid.pherData[r][c] = Variable.max_tau;
                                }
                                //grid.alreadyUpdateDis[r][c] = true;
                                delta_Q = 0;
                                if(grid.pherData[r][c] > Variable.max_tau){
                                    grid.pherData[r][c] = Variable.max_tau;
                                }
                                grid.alreadyUpdateDis[r][c] = true;
                            }
                        }
                    }
                    //time_count = 0;
                //}
            //}
            
            
            
            

            if(grid.alreadyUpdateDis[row][col] == false){
                //grid.pherData[row][col] = Variable.alpha * grid.pherData[row][col] + Variable.c * sum_pher;
                grid.pherData[row][col] = beta * grid.pherData[row][col];
                if(grid.pherData[row][col] > Variable.max_tau){
                    grid.pherData[row][col] = Variable.max_tau;
                //} else if (grid.pherData[row][col] < 100){
                    //grid.pherData[row][col] = 100;
                }
                grid.alreadyUpdateDis[row][col] = true;
            }
            if(grid.alreadyUpdateExp2[h][w] == false){
            grid.areaPherData2[h][w] =  Variable.alpha * grid.areaPherData2[h][w] + Variable.c * sum_pher;
            if(grid.areaPherData2[h][w] > Variable.max_tau){
                grid.areaPherData2[h][w] = Variable.max_tau;
            //} else if (grid.areaPherData2[h][w] < 100){
                //grid.areaPherData2[h][w] = 100;
            }
                grid.alreadyUpdateExp2[h][w] = true;
            }
            sum_pher = 0;
            
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
                    if(grid.agent_pos[i][j] == 1){
                        count--;
                        //move_dis(grid, leftEnd, rightEnd, upperEnd, lowerEnd, agents);
                    }
                }
            }
        }

        // if all pattern grids are occupied, changing mode(otherwise, continuing dispersion mode)
        if(count == 0){
            state = "e";
            grid.vacant2[areaNo] = true;
            v_col = 0;
            v_row = 0;
            x_col = 0;
            x_row = 0;
            //System.out.printf("mode changed to exploration.\n");
        } else if(grid.table[row][col] != 1){
            move_dis2(grid, leftEnd, rightEnd, upperEnd, lowerEnd, agents);
            grid.vacant2[areaNo] = false;
            // pheromone update will be written here?
        } else{
            grid.vacant2[areaNo] = false;
        }

        if(length_move != 0){
            delta_tau = Variable.Q / length_move;
        }
        count = 0;
        sum_pher = 0;
    }

    public void move_dis2(Grid_proposal grid, int leftEnd, int rightEnd, int upperEnd, int lowerEnd, Agent_proposal[] agents){
        // copy pheromone data of the area which this agent exists
        for(int i=upperEnd; i<=lowerEnd; i++){
            for(int j=leftEnd; j<=rightEnd; j++){
                secondSubPherMatrix[i-upperEnd][j-leftEnd] = grid.pherData[i][j];
            }
        }
        
        for(int i=0; i<second_H; i++){
            for(int j=0; j<second_W; j++){
                d_dis = Math.abs(row - i - upperEnd) + Math.abs(col - j - leftEnd);
                if(d_dis != 0){
                    secondDisIndicMatrix[i][j] = Math.exp(secondSubPherMatrix[i][j]) / d_dis;
                } else {
                    secondDisIndicMatrix[i][j] = 0;
                }
            }
        }

        int maxIndex = maxIndex(secondDisIndicMatrix);
        int a = maxIndex % second_W;
        int b = maxIndex / second_W;

        // determine pld
        pld_col = leftEnd + a;
        pld_row = upperEnd + b;

        //System.out.printf("pld: (%d, %d)\n", pld_row, pld_col);

        // pheromone update (calc of sum_pher is written in PF_PSO.java)
        if(grid.table[pld_row][pld_col] == 1){
            if(grid.agent_pos[pld_row][pld_col] == 0){
            //if(grid.agent_pos[pld_row][pld_col] == 0 && grid.alreadyUpdateDis[pld_row][pld_col] == false){
                int h = areaNo/second_m;
                int w = areaNo%second_m;
                grid.pherData[pld_row][pld_col] = Variable.alpha * grid.pherData[pld_row][pld_col] + Variable.c * sum_pher;
                if(grid.pherData[pld_row][pld_col] > Variable.max_tau){
                    grid.pherData[pld_row][pld_col] = Variable.max_tau;
                }
                grid.areaPherData2[h][w] =  Variable.alpha * grid.areaPherData2[h][w] + Variable.c * sum_pher;
                if(grid.areaPherData2[h][w] > Variable.max_tau){
                    grid.areaPherData2[h][w] = Variable.max_tau;
                }
                sum_pher = 0;
                grid.alreadyUpdateDis[pld_row][pld_col] = true;
                grid.alreadyUpdateExp2[h][w] = true;
            }
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

        

        check_dis2(grid, leftEnd, rightEnd, upperEnd, lowerEnd, agents, move_row, move_col);

        if(grid.table[row][col] == 1){
            //System.out.printf("this agent already reached goal.\n");
            state = "t";
            if(grid.table[row][col] == 1){
                grid.occupied[row][col] = 1;
            }
        }
    }

    public void check_dis2(Grid_proposal grid, int leftEnd, int rightEnd, int upperEnd, int lowerEnd, Agent_proposal[] agents, int mr, int mc){
        ArrayList<Integer> candiList = new ArrayList<Integer>();
        int change_pos;
        for(int i = 0; i < Variable.AGENT_NUM; i++){
            if(this != agents[i]){
            //System.out.printf("agents[%d] pos: (%d, %d)\n", i, agents[i].row, agents[i].col);
                if(mr == agents[i].row && mc == agents[i].col){
                    flag = 1;
                    for(int j = 0; j < 4; j++){
                        int r = agents[i].row + Variable.dir_row[j];
                        int c = agents[i].col + Variable.dir_col[j];
                        if(r >= 0 && r < Variable.N && c >= 0 && c < Variable.M){
                            if(grid.agent_pos[r][c] == 0){
                                if(grid.table[r][c] == 1){
                                    candiList.add(r*Variable.M+c);
                                }
                            }
                        }
                    }
                    if(candiList.size() != 0){
                        Collections.shuffle(candiList);
                        change_pos = candiList.get(0);
                        int a = change_pos % Variable.M;
                        int b = change_pos / Variable.M;
                        grid.agent_pos[agents[i].row][agents[i].col] = 0;
                        grid.occupied[agents[i].row][agents[i].col] = 0;
                        agents[i].length_move = agents[i].length_move + 1;
                        agents[i].col = a;
                        agents[i].row = b;
                        agents[i].v_col = 0;
                        agents[i].v_row = 0;
                        if(grid.agent_pos[b][a] == 1){
                            System.out.println("Invalid in check_dis");
                        }
                        grid.agent_pos[b][a] = 1;

                        if(grid.table[b][a] == 1){
                            grid.occupied[b][a] = 1;
                        }

                        grid.occupied[row][col] = 0;
                        length_move = length_move + Math.abs(col - mc) + Math.abs(row - mr);

                        grid.deletePos(this);
                        col = mc;
                        row = mr;

                        stop = 0;

                        
                        if(grid.table[row][col] == 1){
                            grid.occupied[row][col] = 1;
                        }
                        grid.recordPos(this);

                    }else if(grid.table[agents[i].row][agents[i].col] == 0){
                        for(int j = 0; j < 4; j++){
                            int r = agents[i].row + Variable.dir_row[j];
                            int c = agents[i].col + Variable.dir_col[j];
                            if(r >= 0 && r < Variable.N && c >= 0 && c < Variable.M){
                                if(grid.agent_pos[r][c] != 1){
                                    candiList.add(r*Variable.M+c);
                                }
                            }
                        }
                        if(candiList.size() != 0){
                            change_pos = candiList.get(0);
                            int a = change_pos % Variable.M;
                            int b = change_pos / Variable.M;
                            grid.agent_pos[agents[i].row][agents[i].col] = 0;
                            agents[i].length_move = agents[i].length_move + 1;
                            agents[i].col = a;
                            agents[i].row = b;
                            agents[i].v_col = 0;
                            agents[i].v_row = 0;
                            grid.agent_pos[b][a] = 1;

                            grid.occupied[row][col] = 0;
                            length_move = length_move + Math.abs(col - mc) + Math.abs(row - mr);
                            grid.deletePos(this);
                            col = mc;
                            row = mr;

                            stop = 0;

                            
                            if(grid.table[row][col] == 1){
                                grid.occupied[row][col] = 1;
                            }
                            grid.recordPos(this);
                        }
                    }
                } else {
                    stop = 1;

                    /*
                    length_move = length_move + Math.abs(col - mc) + Math.abs(row - mr);
                    grid.deletePos(this);
                    col = mc;
                    row = mr;

                    if(grid.table[row][col] == 1 && grid.occupied[row][col] == 0){
                        grid.occupied[row][col] = 1;
                    }
                    grid.recordPos(this);
                    */
                }
            }
        }

        candiList.clear();

        if(flag == 0){
            length_move = length_move + Math.abs(col - mc) + Math.abs(row - mr);
            grid.deletePos(this);
            col = mc;
            row = mr;

            
            if(grid.table[row][col] == 1){
                grid.occupied[row][col] = 1;
            }
            grid.recordPos(this);
        } else  if(col != mc && row != mr){
            v_col = 0;
            v_row = 0;
        }

        flag = 0;
        
        /*
        //grid.deletePos(this);

        length_move = length_move + Math.abs(col - move_col) + Math.abs(row - move_row);

        col = move_col;
        row = move_row;

        
        if(grid.table[row][col] == 1 && grid.occupied[row][col] == 0){
            grid.occupied[row][col] = 1;
        }
        */
        

        //grid.recordPos(this);

        // checking behavior of this function

        //System.out.printf("now: (%d, %d)\n", row, col);

        //System.out.print(areaNo);
        //System.out.println();

        /*
        for(int i=0; i<Variable.H; i++){
            for(int j=0; j<Variable.W; j++){
                //System.out.printf("%f,", disIndicMatrix[i][j]);
            }
            //System.out.println();
        }
        
        System.out.printf("x: (%.1f, %.1f), ", x_row, x_col);
        System.out.printf("areaNo: %d, ", areaNo);
        System.out.printf("left, right: (%d, %d)\n", leftEnd, rightEnd);
        */
    }

    public void exp2(Grid_proposal grid, Agent_proposal[] agents){
        int leftEnd, rightEnd;
        int upperEnd, lowerEnd;
        leftEnd = (areaNo % second_m) * second_W;
        rightEnd = leftEnd + second_W - 1;
        upperEnd = (areaNo / second_m) * second_H;
        lowerEnd = upperEnd + second_H - 1;


        if(pre_c == col && pre_r == row){
            time_count++;
        } else {
            time_count = 0;
        }
        pre_c = col;
        pre_r = row;  

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
                    if(grid.agent_pos[i][j] == 1){
                        count--;
                        //move_dis(grid, leftEnd, rightEnd, upperEnd, lowerEnd, agents);
                    }
                }
            }
        }

        if(count == 0){
            grid.vacant2[areaNo] = true;
        }else{
            grid.vacant2[areaNo] = false;
        }
        
        if(areaNo == next_area){
            if(grid.vacant2[areaNo] == false){
                state = "d";
                v_col = 0;
                v_row = 0;
                x_col = 0;
                x_row = 0;
            } else {
                move_exp2(grid, agents);
            }
            //System.out.printf("mode changed to dispersion.\n");
        } else if(grid.vacant2[areaNo] == false) {
            state = "d";
            v_col = 0;
            v_row = 0;
            x_col = 0;
            x_row = 0;
        /*
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

            check_exp2(grid, agents, move_row, move_col);
            */
        } else {
            move_exp2(grid, agents);
        }
        if(length_move != 0){
            delta_tau = Variable.Q / length_move;
        }
        count = 0;
        sum_pher = 0;
    }

    public void move_exp2(Grid_proposal grid, Agent_proposal[] agents){
        for(int i=0; i<second_n; i++){
            for(int j=0; j<second_m; j++){
               secondPherMatrix[i][j] = grid.areaPherData2[i][j];
            }
        }

        for(int i=0; i<second_n; i++){
            for(int j=0; j<second_m; j++){
                d_exp = Math.abs(row - (i*second_H + second_H/2 )) + Math.abs(col - (j*second_W + second_W/2 ));
                if (grid.vacant[i*second_m+j] == true){
                    secondExpIndicMatrix[i][j] = 0;
                } else if(d_exp != 0){
                    secondExpIndicMatrix[i][j] = Math.exp(0-secondPherMatrix[i][j]) / d_exp;
                } else {
                    secondExpIndicMatrix[i][j] = 0;
                }
            }
        }

        int expIndex = maxIndex(secondExpIndicMatrix);
        int area_c = expIndex % second_m;
        int area_r = expIndex / second_m;

        next_area = getAreaNo2(area_r, area_c);

        int leftNext, rightNext;
        int upperNext, lowerNext;
        leftNext = (next_area % second_m) * second_W;
        rightNext = leftNext + second_W - 1;
        upperNext = (next_area / second_m) * second_H;
        lowerNext = upperNext + second_H - 1;

        for(int i=upperNext; i<=lowerNext; i++){
            for(int j=leftNext; j<=rightNext; j++){
                secondSubPherMatrix[i-upperNext][j-leftNext] = grid.pherData[i][j];
            }
        }
        
        for(int i=0; i<second_H; i++){
            for(int j=0; j<second_W; j++){
                d_dis = Math.abs(row - i - upperNext) + Math.abs(col - j - leftNext);
                if(d_dis != 0){
                    secondDisIndicMatrix[i][j] = Math.exp(secondSubPherMatrix[i][j]) / d_dis;
                } else {
                    secondDisIndicMatrix[i][j] = 0;
                }
            }
        }

        int maxIndex = maxIndex(secondDisIndicMatrix);
        int a = maxIndex % second_W;
        int b = maxIndex / second_W;
        
        // determine pgd
        pgd_col = area_c*second_W + a;
        pgd_row = area_r*second_H + b;

        /*
        int maxIndex = maxIndex(secondExpIndicMatrix);
        int a = maxIndex % second_m;
        int b = maxIndex / second_m;

        // determine pgd
        pgd_col = a*second_W + second_W/2;
        pgd_row = b*second_H + second_H/2;
        */
        
        //System.out.printf("now: (%d, %d)\n", row, col);
        //System.out.printf("pgd: (%d, %d)\n", pgd_row, pgd_col);
        //System.out.printf("areaNo: %d\n", areaNo);
        //System.out.println();

        //next_area = getAreaNo2(pgd_row, pgd_col);
        
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

        /*
        grid.deletePos(this);

        length_move = length_move + Math.abs(col - move_col) + Math.abs(row - move_row);

        col = move_col;
        row = move_row;

        //System.out.printf("now: (%d, %d)\n", row, col);
        //System.out.printf("pgd: (%d, %d)\n", pgd_row, pgd_col);

        grid.recordPos(this);
        */
        check_exp2(grid, agents, move_row, move_col);

        if(grid.table[row][col] == 1){
            //System.out.printf("this agent already reached goal.\n");
            state = "t";
            if(grid.table[row][col] == 1){
                grid.occupied[row][col] = 1;
            }
        }
    }

    public void check_exp2(Grid_proposal grid, Agent_proposal[] agents, int mr, int mc){
        ArrayList<Integer> candiList = new ArrayList<Integer>();
        int change_pos;
        //int unyo = 0;
        for(int i = 0; i < Variable.AGENT_NUM; i++){
            if(this != agents[i]){
            //System.out.printf("agents[%d] pos: (%d, %d)\n", i, agents[i].row, agents[i].col);
                if(mr == agents[i].row && mc == agents[i].col){
                    flag = 1;
                    for(int j = 0; j < 4; j++){
                        int r = agents[i].row + Variable.dir_row[j];
                        int c = agents[i].col + Variable.dir_col[j];
                        if(r >= 0 && r < Variable.N && c >= 0 && c < Variable.M){
                            if(grid.agent_pos[r][c] != 1){
                                if(grid.table[r][c] == 1){
                                    candiList.add(r*Variable.M+c);
                                }
                            }
                        }
                    }
                    if(candiList.size() != 0){
                        Collections.shuffle(candiList);
                        change_pos = candiList.get(0);
                        int a = change_pos % Variable.M;
                        int b = change_pos / Variable.M;
                        grid.agent_pos[agents[i].row][agents[i].col] = 0;
                        grid.occupied[agents[i].row][agents[i].col] = 0;
                        agents[i].length_move = agents[i].length_move + 1;
                        agents[i].col = a;
                        agents[i].row = b;
                        agents[i].v_col = 0;
                        agents[i].v_row = 0;
                        if(grid.agent_pos[b][a] == 1){
                            System.out.println("Invalid in check_exp");
                        }
                        grid.agent_pos[b][a] = 1;

                        if(grid.table[b][a] == 1){
                            grid.occupied[b][a] = 1;
                        }

                        grid.occupied[row][col] = 0;
                        length_move = length_move + Math.abs(col - mc) + Math.abs(row - mr);

                        grid.deletePos(this);
                        col = mc;
                        row = mr;

                        stop = 0;

                        
                        if(grid.table[row][col] == 1){
                            grid.occupied[row][col] = 1;
                        }
                        grid.recordPos(this);

                    }else if(grid.table[agents[i].row][agents[i].col] == 0){
                        for(int j = 0; j < 4; j++){
                            int r = agents[i].row + Variable.dir_row[j];
                            int c = agents[i].col + Variable.dir_col[j];
                            if(r >= 0 && r < Variable.N && c >= 0 && c < Variable.M){
                                if(grid.agent_pos[r][c] != 1){
                                    candiList.add(r*Variable.M+c);
                                }
                            }
                        }
                        if(candiList.size() != 0){
                            change_pos = candiList.get(0);
                            int a = change_pos % Variable.M;
                            int b = change_pos / Variable.M;
                            grid.agent_pos[agents[i].row][agents[i].col] = 0;
                            grid.occupied[agents[i].row][agents[i].col] = 0;
                            agents[i].length_move = agents[i].length_move + 1;
                            agents[i].col = a;
                            agents[i].row = b;
                            agents[i].v_col = 0;
                            agents[i].v_row = 0;
                            grid.agent_pos[b][a] = 1;

                            if(grid.table[b][a] == 1){
                                grid.occupied[b][a] = 1;
                            }

                            grid.occupied[row][col] = 0;
                            length_move = length_move + Math.abs(col - mc) + Math.abs(row - mr);
                            grid.deletePos(this);
                            col = mc;
                            row = mr;

                            stop = 0;

                            
                            if(grid.table[row][col] == 1){
                                grid.occupied[row][col] = 1;
                            }
                            grid.recordPos(this);

                            //System.out.println("pass the check_exp");
                        }
                    }
                } else {
                    stop = 1;
                    /*
                    length_move = length_move + Math.abs(col - mc) + Math.abs(row - mr);
                    grid.deletePos(this);
                    col = mc;
                    row = mr;

                    if(grid.table[row][col] == 1 && grid.occupied[row][col] == 0){
                        grid.occupied[row][col] = 1;
                    }
                    grid.recordPos(this);
                    */
                }
            }
            //unyo = i;
        }

        //System.out.println(unyo);

        candiList.clear();

        if(flag == 0){
            length_move = length_move + Math.abs(col - mc) + Math.abs(row - mr);
            grid.deletePos(this);
            col = mc;
            row = mr;

            
            if(grid.table[row][col] == 1 && grid.occupied[row][col] == 0){
                grid.occupied[row][col] = 1;
            }
            grid.recordPos(this);
        } else if(col != mc && row != mr){
            v_col = 0;
            v_row = 0;
        }

        flag = 0;
    }

    public double GetDist (int r1, int c1, int r2, int c2) {
    	double d = Math.sqrt((r2-r1)*(r2-r1)+(c2-c1)*(c2-c1));
	    return d;
    }
}
