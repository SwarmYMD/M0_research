import java.util.Arrays;

public class Variable{
    private Variable(){};
    public static final int M = 20;
    public static final int N = 20;
    public static final int m = 4;
    public static final int n = 4;
    public static final int W = M/m;
    public static final int H = N/n;
}

public class Grid{
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
}