import environment.Variable;
import environment.Grid;
import environment.Agent;

import java.util.*;

import java.io.*;
import java.nio.charset.Charset;

public class PF_PSO_test{
    public static void main(String[] args) {
        Grid grid = new Grid();

        int initial_pos;
        int finish_agent = 0;
        boolean half_flag = false;
        int count = 0;

        double achieved_count = 0.0;
        double achieve_percent = 0.0;
        double agent_percent = 0.0;

        int pattern_num = 0;

        FileWriter[] fw = new FileWriter[Variable.maxStep];

        for(int i = 0 ; i < Variable.M * Variable.N ; i++) {
            
            if(grid.table[i/Variable.M][i%Variable.M] == 0){
                
                //if((i%Variable.M > 30 && i%Variable.M < 173) && (i/Variable.M > 30 && i/Variable.M < 173)){
                //} else {
                
                    //randomList.add(i);
                //}
            } else {
                pattern_num++;
            }
            
            //randomList.add(i);
        }
        //Collections.shuffle(randomList);

        //System.out.println(randomList);

        Agent[] agents = new Agent[Variable.AGENT_NUM];

        // Initial setting of agents.
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream("initial_pos.csv"), Charset.forName("Shift-JIS")))) {
        
            String line;
            int index = 0;
            while ((line = reader.readLine()) != null) {
                if (index >= 0) {
                    String[] data = line.split(",");
                    
                    agents[index] = new Agent(Integer.parseInt(data[0]), Integer.parseInt(data[1]));
                    agents[index].areaNo = agents[index].getAreaNo(agents[index].row, agents[index].col);
                    grid.recordPos(agents[index]);
                }
                index++;
            }
        } catch (IOException e) {
            e.printStackTrace();
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

        /*
        for(int i=0; i<Variable.N; i++){
            for(int j=0; j<Variable.M; j++){
                System.out.printf("%d", grid.table[i][j]);
            }
            System.out.println();
        }
        System.out.println();
        */
        
        /*
        if(agents[0].state.equals("d")){
            agents[0].dispersion(grid); 
        }
        */
        
        try{
            FileWriter f = new FileWriter("./csv_test/step0.csv");
            for (int j=0; j<Variable.AGENT_NUM; j++){
                f.append(String.valueOf(agents[j].col));
                f.append(",");
                f.append(String.valueOf(agents[j].row));
                f.append("\n");
            }
            f.close();

            for(int k=0; k<Variable.N; k++){
                for(int s=0; s<Variable.M; s++){
                    if(grid.table[k][s] == 1){
                        if(grid.agent_pos[k][s] == 1){
                            achieved_count += 1;
                        }
                    }
                }
            }

            FileWriter percent_recorder = new FileWriter("./percent/percent.csv");
            FileWriter agent_recorder = new FileWriter("./percent/agent.csv");

            achieve_percent = achieved_count / Variable.AGENT_NUM * 100;
            agent_percent = (double)finish_agent / Variable.AGENT_NUM * 100;
            percent_recorder.append(String.valueOf(0));
            percent_recorder.append(",");
            percent_recorder.append(String.valueOf(achieve_percent));
            percent_recorder.append("\n");

            agent_recorder.append(String.valueOf(0));
            agent_recorder.append(",");
            agent_recorder.append(String.valueOf(agent_percent));
            agent_recorder.append("\n");

            achieved_count = 0;
            
            for(int i=0; i<Variable.maxStep; i++){
            //for(int i=0; ; i++){
            //for(int i=0; i<10; i++){
                //fw[i] = new FileWriter("./csv_test/step"+String.valueOf(i+1)+".csv");
                
                System.out.printf("Now: step %d\n", i+1);
                
                for (int j=0; j<Variable.AGENT_NUM; j++){
                    agents[j].areaNo = agents[j].getAreaNo(agents[j].row, agents[j].col);
                    calc_sum_pher(agents, agents[j], grid);

                    if(agents[j].state.equals("t")){
                        agents[j].dispersion(grid, agents); 
                    }else if(agents[j].state.equals("d")){
                    // dispersion mode
                        agents[j].dispersion(grid, agents); 
                        //System.out.printf("agent[%d]'s now: (%d, %d)\n", j, agents[j].row, agents[j].col);
                    }else if(agents[j].state.equals("e")){
                    // exploration mode
                        agents[j].exploration(grid, agents); 
                    }
                    
                    /*
                    fw[i].append(String.valueOf(agents[j].col));
                    fw[i].append(",");
                    fw[i].append(String.valueOf(agents[j].row));
                    fw[i].append("\n");
                    */
                }

                for (int j=0; j<Variable.AGENT_NUM; j++){
                    if(agents[j].state.equals("t")){
                        if(grid.table[agents[j].row][agents[j].col] == 0){
                            agents[j].state = "e";
                        }
                    }
                }

                finish_agent = 0;
                for (int j=0; j<Variable.AGENT_NUM; j++){
                    if(agents[j].state.equals("t")){
                        finish_agent += 1;
                    }
                }

                for(int k=0; k<Variable.N; k++){
                    for(int s=0; s<Variable.M; s++){
                        if(grid.table[k][s] == 1){
                            if(grid.agent_pos[k][s] == 1){
                                achieved_count += 1;
                            }
                        }
                    }
                }

                achieve_percent = achieved_count / pattern_num * 100;
                agent_percent = (double)finish_agent / Variable.AGENT_NUM * 100;
                percent_recorder.append(String.valueOf(i+1));
                percent_recorder.append(",");
                percent_recorder.append(String.valueOf(achieve_percent));
                percent_recorder.append("\n");

                agent_recorder.append(String.valueOf(i+1));
                agent_recorder.append(",");
                agent_recorder.append(String.valueOf(agent_percent));
                agent_recorder.append("\n");

                /*
                if(agents[0].state.equals(("d"))){
                    for(int k=0; k<Variable.H; k++){
                        for(int s=0; s<Variable.W; s++){
                            System.out.printf("%f,", agents[0].subPherMatrix[k][s]);
                        }
                        System.out.println();
                    }
                    System.out.println();
                }
                */

                
                fw[i] = new FileWriter("./csv_test/step"+String.valueOf(i+1)+".csv");
                for (int j=0; j<Variable.AGENT_NUM; j++){
                    fw[i].append(String.valueOf(agents[j].col));
                    fw[i].append(",");
                    fw[i].append(String.valueOf(agents[j].row));
                    fw[i].append("\n");
                }
                fw[i].close();
                
                
                for(int k=0; k<Variable.N; k++){
                    for(int s=0; s<Variable.M; s++){
                        if(grid.agent_pos[k][s] == 1){
                            count++;
                        }
                    }
                }
                
                if(count != Variable.AGENT_NUM){
                    System.out.printf("Duplicate occured in step %d !\n", i+1);
                }

                count = 0;

                if(finish_agent == Variable.AGENT_NUM){
                    System.out.printf("PF completes.\n");
                    System.out.printf("total steps: %d\n", i+1);
                    for (int j=0; j<Variable.AGENT_NUM; j++){
                        System.out.print(agents[j].state);
                    }
                    System.out.printf("\n");

                    /*
                    for (int j=0; j<Variable.AGENT_NUM; j++){
                        grid.recordPos(agents[j]);
                    }
                    */

                    for(int k=0; k<Variable.N; k++){
                        for(int s=0; s<Variable.M; s++){
                            System.out.print(grid.agent_pos[k][s]);
                        }
                        System.out.println();
                    }
                    System.out.println();

                    for (int j=0; j<Variable.AGENT_NUM; j++){
                        if(grid.agent_pos[agents[j].row][agents[j].col] != 1){
                            System.out.printf("Strange pos: (%d, %d)\n", agents[j].row, agents[j].col);
                        }
                    }
                    
                    /*
                    fw[i] = new FileWriter("./csv_test/step"+String.valueOf(i+1)+".csv");
                    for (int j=0; j<Variable.AGENT_NUM; j++){
                        fw[i].append(String.valueOf(agents[j].col));
                        fw[i].append(",");
                        fw[i].append(String.valueOf(agents[j].row));
                        fw[i].append("\n");
                    }
                    fw[i].close();
                    */

                    break;
                }

                /*
                for (int j=0; j<Variable.AGENT_NUM; j++){
                    grid.recordPos(agents[j]);
                }
                */

                /*
                //if((i+1)%100 == 0){
                if(i>9995 && ((i+1) != Variable.maxStep)){
                    
                    for(int k=0; k<Variable.N; k++){
                        for(int s=0; s<Variable.M; s++){
                            System.out.print(grid.agent_pos[k][s]);
                        }
                        System.out.println();
                    }
                    System.out.println();

                    for (int j=0; j<Variable.AGENT_NUM; j++){
                        System.out.print(agents[j].state);
                    }

                    System.out.println();
                    
                    for (int j=0; j<Variable.AGENT_NUM; j++){
                        if(agents[j].state.equals("d")) {
                            //System.out.printf("agent[%d]'s x: (%.1f, %.1f) ", j, agents[j].x_row, agents[j].x_col);
                            System.out.printf("agent[%d]'s now: (%d, %d)\n", j, agents[j].row, agents[j].col);
                        }
                    }
                    for (int j=0; j<Variable.AGENT_NUM; j++){
                        if(agents[j].state.equals("e")) {
                            for(int k=0; k<Variable.n; k++){
                                for(int s=0; s<Variable.m; s++){
                                    System.out.print(agents[j].expIndicMatrix[k][s]);
                                }
                                System.out.println();
                            }
                        }
                    }
                }
                */

                /*
                for (int j=0; j<Variable.AGENT_NUM; j++){
                    System.out.print(agents[j].state);
                }
                System.out.println();
                */

                
                achieved_count = 0;
                

                //fw[i].close();

                for(int k=0; k<Variable.N; k++){
                    for(int s=0; s<Variable.M; s++){
                        if(grid.alreadyUpdateDis[k][s] == false){
                            grid.pherData[k][s] = Variable.alpha * grid.pherData[k][s];
                            if(grid.pherData[k][s] > Variable.max_tau){
                                grid.pherData[k][s] = Variable.max_tau;
                            }
                        }else{
                            grid.alreadyUpdateDis[k][s] = false;
                        }
                    }
                }

                for(int k=0; k<Variable.n; k++){
                    for(int s=0; s<Variable.m; s++){
                        if(grid.alreadyUpdateExp[k][s] == false){
                            grid.areaPherData[k][s] = Variable.alpha * grid.areaPherData[k][s];
                            if(grid.areaPherData[k][s] > Variable.max_tau){
                                grid.areaPherData[k][s] = Variable.max_tau;
                            }
                        }else{
                            grid.alreadyUpdateExp[k][s] = false;
                        }
                    }
                }

            }
            int not_count = 0;
            int out_count = 0;
            for (int j=0; j<Variable.AGENT_NUM; j++){
                if(grid.table[agents[j].row][agents[j].col] == 0){
                    if(grid.agent_pos[agents[j].row][agents[j].col] == 1){
                        out_count += 1;
                    }
                }
            }
            for(int k=0; k<Variable.N; k++){
                for(int s=0; s<Variable.M; s++){
                    if(grid.table[k][s] == 1){
                        if(grid.agent_pos[k][s] == 0){
                            not_count += 1;
                        }
                    }
                }
            }
            FileWriter critic_recorder = new FileWriter("./percent/critic.csv");
            critic_recorder.append("not count");
            critic_recorder.append(",");
            critic_recorder.append(String.valueOf(not_count));
            critic_recorder.append("\n");
            critic_recorder.append("out count");
            critic_recorder.append(",");
            critic_recorder.append(String.valueOf(out_count));
            critic_recorder.append("\n");
            critic_recorder.append("critic value");
            critic_recorder.append(",");
            critic_recorder.append(String.valueOf(not_count + out_count));
            critic_recorder.append("\n");
            critic_recorder.close();
            percent_recorder.close();
            agent_recorder.close();
            System.out.printf("achieved percent : %.2f%%\n", achieve_percent);
            System.out.printf("agent percent : %.2f%%\n", agent_percent);
            System.out.printf("critic : %d\n", not_count + out_count);

            
                            for(int k=0; k<Variable.n; k++){
                                for(int s=0; s<Variable.m; s++){
                                    System.out.printf("%f\t",grid.areaPherData[k][s]);
                                }
                                System.out.println();
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