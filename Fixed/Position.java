import environment.Variable;
import environment.Grid;
import environment.Agent;

import java.util.*;

import java.io.FileWriter;
import java.io.IOException;

public class Position{
    public static void main(String[] args) {
        Grid grid = new Grid();

        int initial_pos;

        double achieved_count = 0.0;
        double achieve_percent = 0.0;
        double agent_percent = 0.0;

        int pattern_num = 0;

        ArrayList<Integer> randomList = new ArrayList<Integer>();

        FileWriter[] fw = new FileWriter[Variable.maxStep];

        for(int i = 0 ; i < Variable.M * Variable.N ; i++) {
            
            if(grid.table[i/Variable.M][i%Variable.M] == 0){
                
                //if((i%Variable.M > 30 && i%Variable.M < 173) && (i/Variable.M > 30 && i/Variable.M < 173)){
                //} else {
                
                    randomList.add(i);
                //}
            } else {
                pattern_num++;
            }
            
            //randomList.add(i);
        }
        Collections.shuffle(randomList);

        Agent[] agents = new Agent[Variable.AGENT_NUM];
        for (int i=0; i<Variable.AGENT_NUM; i++){
            initial_pos = randomList.get(i);
            agents[i] = new Agent(initial_pos%Variable.M, initial_pos/Variable.M);
            agents[i].areaNo = agents[i].getAreaNo(agents[i].row, agents[i].col);
            grid.recordPos(agents[i]);
            //System.out.printf("(%d, %d)\n", agents[i].row, agents[i].col);
        }

        try{
            FileWriter f = new FileWriter("./initial_pos.csv");
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
    }
}