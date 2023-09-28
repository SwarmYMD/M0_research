import environment.Variable;
import environment.Grid;
import environment.Agent;

import java.util.*;

import java.io.FileWriter;
import java.io.IOException;

public class PF_PSO{
    public static void main(String[] args) {
        Grid grid = new Grid();

        int initial_pos;
        int finish_agent = 0;

        ArrayList<Integer> randomList = new ArrayList<Integer>();

        FileWriter[] fw = new FileWriter[Variable.maxStep];

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
            agents[i].areaNo = agents[i].getAreaNo(agents[i].row, agents[i].col);
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

        for(int i=0; i<Variable.N; i++){
            for(int j=0; j<Variable.M; j++){
                System.out.printf("%d", grid.table[i][j]);
            }
            System.out.println();
        }
        System.out.println();

        
        /*
        if(agents[0].state.equals("d")){
            agents[0].dispersion(grid); 
        }
        */
        
        try{
            for(int i=0; i<Variable.maxStep; i++){
                fw[i] = new FileWriter("./csv/step"+String.valueOf(i+1)+".csv");
                for (int j=0; j<Variable.AGENT_NUM; j++){
                    agents[j].areaNo = agents[j].getAreaNo(agents[j].row, agents[j].col);
                    calc_sum_pher(agents, agents[j], grid);

                    if(agents[j].state.equals("t")){
                        agents[j].dispersion(grid); 
                    }
                    
                    // dispersion mode
                    if(agents[j].state.equals("d")){
                        agents[j].dispersion(grid); 
                    }

                    // exploration mode
                    if(agents[j].state.equals("e")){
                        agents[j].exploration(grid); 
                    }
                    
                    fw[i].append(String.valueOf(agents[j].col));
                    fw[i].append(",");
                    fw[i].append(String.valueOf(agents[j].row));
                    fw[i].append("\n");
                    
                }

                finish_agent = 0;
                for (int j=0; j<Variable.AGENT_NUM; j++){
                    if(agents[j].state.equals("t")){
                        finish_agent += 1;
                    }
                }

                if(finish_agent == Variable.AGENT_NUM){
                    System.out.printf("PF completes.\n");
                    break;
                }

                
                for(int k=0; k<Variable.N; k++){
                    for(int s=0; s<Variable.M; s++){
                        System.out.print(grid.agent_pos[k][s]);
                    }
                    System.out.println();
                }
                System.out.println();
                

                if(i == Variable.maxStep - 1 && finish_agent < Variable.AGENT_NUM){
                    System.out.printf("Reach the final step and FAILURE...\n\n");

                    for (int j=0; j<Variable.AGENT_NUM; j++){
                        System.out.print(agents[j].state);
                    }
                }

                fw[i].close();

                for(int k=0; k<Variable.N; k++){
                    for(int s=0; s<Variable.M; s++){
                        if(grid.alreadyUpdateDis[k][s] == false){
                            grid.pherData[k][s] = Variable.alpha * grid.pherData[k][s];
                        }else{
                            grid.alreadyUpdateDis[k][s] = false;
                        }
                    }
                }

                for(int k=0; k<Variable.n; k++){
                    for(int s=0; s<Variable.m; s++){
                        if(grid.alreadyUpdateExp[k][s] == false){
                            grid.areaPherData[k][s] = Variable.alpha * grid.areaPherData[k][s];
                        }else{
                            grid.alreadyUpdateExp[k][s] = false;
                        }
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    public static void calc_sum_pher(Agent[] agents, Agent r, Grid grid) {
        r.sum_pher = 0;
    
        for(int i = r.row - r.range; i <= r.row + r.range; i++){
            for(int j = r.col - r.range; j <= r.col + r.range; j++){
                for(int num = 0; num < Variable.AGENT_NUM; num++){
                    if(i >= 0 && i < Variable.N && j >= 0 && j < Variable.M && agents[num] != r){
                        if(agents[num].row == i && agents[num].col == j){
                            r.sum_pher += agents[num].delta_tau;
                        }
                    }
                }
            }
        }
    }
}