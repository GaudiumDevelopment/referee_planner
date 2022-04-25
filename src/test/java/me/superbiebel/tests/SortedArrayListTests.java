package me.superbiebel.tests;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class SortedArrayListTests {
    
    /*@Execution(ExecutionMode.CONCURRENT)
    @Test
    void orderTestWithInt() {
        ArrayList<Integer> list = new ArrayList<>(Comparator.<Integer>naturalOrder());
        list.add(5);
        list.add(1);
        list.add(10);
        list.add(0);
        assertEquals(0, (int) list.get(0));
        assertEquals(1, (int) list.get(1));
        assertEquals(5, (int) list.get(2));
        assertEquals(10, (int) list.get(3));
    }
    @Execution(ExecutionMode.CONCURRENT)
    @Test
    void orderTestWithGameAssignment() {
        ArrayList<GameAssignment> list = new ArrayList<>(new GameAssignmentComparator());
    
        GameAssignment G1 = new GameAssignment(null, Game.builder().gamePeriod(new TimePeriod(LocalDateTime.now().minusDays(5), LocalDateTime.now().minusDays(4))).build(), 0);
        GameAssignment G2 = new GameAssignment(null, Game.builder().gamePeriod(new TimePeriod(LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(4))).build(), 0);
        GameAssignment G3 = new GameAssignment(null, Game.builder().gamePeriod(new TimePeriod(LocalDateTime.now().minusDays(10), LocalDateTime.now().minusDays(4))).build(), 0);
        GameAssignment G4 = new GameAssignment(null, Game.builder().gamePeriod(new TimePeriod(LocalDateTime.now().minusDays(1), LocalDateTime.now().minusDays(4))).build(), 0);
    
        list.add(G1);
        list.add(G2);
        list.add(G3);
        list.add(G4);
        assertEquals(G3, list.get(0));
        assertEquals(G1, list.get(1));
        assertEquals(G2, list.get(2));
        assertEquals(G4, list.get(3));
    }*/
}
