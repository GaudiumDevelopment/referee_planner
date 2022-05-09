package me.superbiebel.tests;

import io.quarkus.test.junit.QuarkusTest;
import me.superbiebel.referee_planner.domain.data.TimeTableGenerator;
import org.junit.jupiter.api.*;
import org.optaplanner.benchmark.api.PlannerBenchmark;
import org.optaplanner.benchmark.api.PlannerBenchmarkFactory;

@SuppressWarnings("NewClassNamingConvention")
@QuarkusTest
class Benchmarks {
    
    @Test
    void benchmarkEveryAlgorithm() {
        PlannerBenchmarkFactory benchmarkFactory = PlannerBenchmarkFactory.createFromXmlResource("benchmark.xml");
        PlannerBenchmark benchmark = benchmarkFactory.buildPlannerBenchmark(
                new TimeTableGenerator().amountOfGames(550).amountOfReferees(2000).build());
        benchmark.benchmarkAndShowReportInBrowser();
    }
}
