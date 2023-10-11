package environment;

public class Variable{
    private Variable(){};
    public static final int M = 200;
    public static final int N = 200;
    public static final int m = 20;
    public static final int n = 20;
    public static final int W = M/m;
    public static final int H = N/n;
    public static final int AGENT_NUM = 100;
    public static final double alpha = 0.9;
    public static final double c = 0.8;
    public static final double Q = 1.0;
    public static final double max_tau = 3000;
    public static final int maxStep = 10000;
}