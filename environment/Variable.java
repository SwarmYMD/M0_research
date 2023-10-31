package environment;

import java.util.ArrayList;

public class Variable{
    private Variable(){};
    public static final int M = 200;
    public static final int N = 200;
    public static final int m = 20;
    public static final int n = 20;
    public static final int W = M/m;
    public static final int H = N/n;
    public static final int AGENT_NUM = 11050;
    public static final double alpha = 0.9;
    public static final double c = 0.8;
    public static final double Q = 1.0;
    public static final double max_tau = 3000;
    public static final int maxStep = 100000;
    public static final int[] dir_row = {-1, 1, 0, 0};
    public static final int[] dir_col = {0, 0, -1, 1};

    public static ArrayList<Integer> candidate = new ArrayList<Integer>();
}