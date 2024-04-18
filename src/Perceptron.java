public class Perceptron {
    private double[] weights;
    private Double[] entries;
    private double net;
    private double threshold;

    public Perceptron(double threshold, double[] weights){
        this.threshold = threshold;
        this.weights = weights;
    }

    public boolean classify(Double[] entries) {
        net = 0;
        this.entries = entries;
        for(int i = 0; i < weights.length; i++) {
            net += weights[i] * entries[i];
        }
        return net >= threshold;
    }

    public boolean train(Double[] entries, int expectedResult, double alpha) {
        boolean output = classify(entries);
        double delta = expectedResult - (output ? 1 : 0);
        if (delta != 0) {
            for (int j = 0; j < weights.length; j++) {
                weights[j] += alpha * delta * entries[j];
            }

            threshold -= alpha * delta; //+=??
        }

        return delta == 0;
    }

    public double getThreshold() {
        return threshold;
    }

    public double[] getWeights() {
        return weights;
    }

    public double getNet() {
        return net;
    }

    public Double[] getEntries() {
        return entries;
    }
}