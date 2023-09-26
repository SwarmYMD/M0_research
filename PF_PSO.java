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
                    agents[j].getAreaNo(agents[j].row, agents[j].col);

                    if(agents[j].state.equals("t")){
                        break;
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

                for(int k=0; k<Variable.N; k++){
                    for(int s=0; s<Variable.M; s++){
                        System.out.print(grid.agent_pos[k][s]);
                    }
                    System.out.println();
                }
                System.out.println();   

                if(i == Variable.maxStep - 1){
                    System.out.printf("Reach the final step\n");
                }

                fw[i].close();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
}