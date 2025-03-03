import environment.Variable;
import environment.Grid_proposal;
import environment.Agent_proposal;

import java.util.*;

import java.io.FileWriter;
import java.io.IOException;

public class Position{
    public static void main(String[] args) {
        Grid_proposal grid = new Grid_proposal();

        int initial_pos;

        ArrayList<Integer> randomList = new ArrayList<Integer>();

        for(int n = 0; n < 10 ; n++){
            for(int i = 0 ; i < Variable.M * Variable.N ; i++) {
                
                if(grid.table[i/Variable.M][i%Variable.M] == 0){
                    
                    //if((i%Variable.M > 30 && i%Variable.M < 173) && (i/Variable.M > 30 && i/Variable.M < 173)){
                    //} else {
                    
                        randomList.add(i);
                    //}
                }
                
                //randomList.add(i);
            }
            Collections.shuffle(randomList);

            Agent_proposal[] agents = new Agent_proposal[Variable.AGENT_NUM];
            for (int i=0; i<Variable.AGENT_NUM; i++){
                initial_pos = randomList.get(i);
                agents[i] = new Agent_proposal(initial_pos%Variable.M, initial_pos/Variable.M);
                agents[i].areaNo = agents[i].getAreaNo(agents[i].row, agents[i].col);
                grid.recordPos(agents[i]);
                //System.out.printf("(%d, %d)\n", agents[i].row, agents[i].col);
            }

            try{
                FileWriter f = new FileWriter("./initial_pos"+String.valueOf(n+1)+".csv");
                for (int j=0; j<Variable.AGENT_NUM; j++){
                    f.append(String.valueOf(agents[j].col));
                    f.append(",");
                    f.append(String.valueOf(agents[j].row));
                    f.append("\n");
                }
                f.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            randomList.clear();
        }
    }
}