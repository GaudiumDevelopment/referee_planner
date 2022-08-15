package me.superbiebel.tests;

import io.quarkus.test.junit.QuarkusTest;
import me.superbiebel.referee_planner.domain.data.TimeTableGenerator;
import org.junit.jupiter.api.*;
import org.optaplanner.benchmark.api.PlannerBenchmark;
import org.optaplanner.benchmark.api.PlannerBenchmarkFactory;

@SuppressWarnings("NewClassNamingConvention")
@QuarkusTest
@Disabled //broken by the drools compiler
class Benchmarks {
    
    @Test
    @Disabled
    void benchmark_weakest_fit_hill_climbing() {
        Assertions.assertDoesNotThrow(() -> {
            PlannerBenchmarkFactory benchmarkFactory = PlannerBenchmarkFactory.createFromXmlResource("benchmarkConfigs/benchmark-weakest_fit-hill_climbing.xml");
            PlannerBenchmark benchmark = benchmarkFactory.buildPlannerBenchmark();
            benchmark.benchmarkAndShowReportInBrowser();
        });
    }
    @Test
    @Disabled
    void benchmark_weakest_fit_hill_climbing_SINGLE_VS_MULTI() {
        Assertions.assertDoesNotThrow(() -> {
            PlannerBenchmarkFactory benchmarkFactory = PlannerBenchmarkFactory.createFromXmlResource("benchmarkConfigs/benchmark-weakest_fit-hill_climbing-single_vs_multi.xml");
            PlannerBenchmark benchmark = benchmarkFactory.buildPlannerBenchmark();
            benchmark.benchmarkAndShowReportInBrowser();
        });
    }
    
    @Test
    @Disabled
    void benchmark_weakest_fit_tabu_search() {
        Assertions.assertDoesNotThrow(() -> {
            PlannerBenchmarkFactory benchmarkFactory = PlannerBenchmarkFactory.createFromXmlResource("benchmarkConfigs/benchmark-WEAKEST_FIT-TABU_SEARCH-HILL_CLIMBING.xml");
            PlannerBenchmark benchmark = benchmarkFactory.buildPlannerBenchmark();
            benchmark.benchmarkAndShowReportInBrowser();
        });
    }
    
    @Test
    void benchmarkSolver() {
        Assertions.assertDoesNotThrow(() -> {
            PlannerBenchmarkFactory benchmarkFactory = PlannerBenchmarkFactory.createFromSolverConfigXmlResource("solverConfig.xml");
            PlannerBenchmark benchmark = benchmarkFactory.buildPlannerBenchmark(
                    new TimeTableGenerator().amountOfGames(300).amountOfReferees(900).build());
            benchmark.benchmarkAndShowReportInBrowser();
        });
    }
    
    @Test
    @Disabled
    void benchmarkALL() {
        Assertions.assertDoesNotThrow(() -> {
            PlannerBenchmarkFactory benchmarkFactory = PlannerBenchmarkFactory.createFromXmlResource("benchmarkConfigs/benchmark_ALL.xml");
            PlannerBenchmark benchmark = benchmarkFactory.buildPlannerBenchmark();
            benchmark.benchmarkAndShowReportInBrowser();
        });
    }
}
