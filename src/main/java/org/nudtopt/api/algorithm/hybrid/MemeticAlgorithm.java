package org.nudtopt.api.algorithm.hybrid;

import org.nudtopt.api.algorithm.evolution.GeneticAlgorithm;

public class MemeticAlgorithm extends GeneticAlgorithm {

    private String name         = "模因算法(MA)";
    private String type         = "混合算法";
    private long iteration      = 100;
    private int populationSize  = 10;
    private double selectionPro = 0.9;
    private double crossoverPro = 0.9;
    private double mutationPro  = 0.9;
    private int mutationNum     = 500;


    @Override
    public void run() {
        run(solution, null, iteration, selectionPro, crossoverPro, mutationPro, mutationNum);
    }


    // getter & setter
    @Override
    public String getName() {
        return name;
    }
    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getType() {
        return type;
    }
    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public long getIteration() {
        return iteration;
    }
    @Override
    public void setIteration(long iteration) {
        this.iteration = iteration;
    }

    @Override
    public int getPopulationSize() {
        return populationSize;
    }
    @Override
    public void setPopulationSize(int populationSize) {
        this.populationSize = populationSize;
    }

    @Override
    public double getSelectionPro() {
        return selectionPro;
    }
    @Override
    public void setSelectionPro(double selectionPro) {
        this.selectionPro = selectionPro;
    }

    @Override
    public double getCrossoverPro() {
        return crossoverPro;
    }
    @Override
    public void setCrossoverPro(double crossoverPro) {
        this.crossoverPro = crossoverPro;
    }

    @Override
    public double getMutationPro() {
        return mutationPro;
    }
    @Override
    public void setMutationPro(double mutationPro) {
        this.mutationPro = mutationPro;
    }

    @Override
    public int getMutationNum() {
        return mutationNum;
    }
    @Override
    public void setMutationNum(int mutationNum) {
        this.mutationNum = mutationNum;
    }

}
