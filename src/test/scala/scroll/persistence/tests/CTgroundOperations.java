package scroll.persistence.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import scroll.persistence.Database;
import scroll.persistence.Inheritance.MetaPersistenceCt;
import scroll.persistence.Inheritance.MetaPersistenceNt;
import scroll.persistence.tests.ressources.UniversityExample_large_copy;

import java.util.List;

public class CTgroundOperations {

    // Beispiel-Instanzen
    static UniversityExample_large_copy.Concepts exampleConcepts; // Einmalig am Anfang geladen, nur lesen, Referenz

    //@BeforeAll
    @BeforeEach
    void beforeEach(){
        exampleConcepts = UniversityExample_large_copy.getInstanceExample();
    }

//    @Test
//    void recursiveEverything() {
//        Assertions.assertDoesNotThrow( // darf keinen Fehler geben
//                () -> {
//
//                    // select
//                    List<MetaPersistenceCt> ctList1 = Database.groundOperations().selectRecursive();
//
//                    // create or update
//                    Database.groundOperations().createOrUpdateRecursive(exampleConcepts.cts().get(0)); // vom ersten CT ausgehend
//
//                    // select
//                    List<MetaPersistenceCt> ctList2 = Database.groundOperations().selectRecursive();
//
//                    // Vergleich
//                    Assertions.assertEquals(ctList1.size()+1, ctList2.size());
//
//                    // Einer der ausgewählten CTs muss der übergebene CT sein
//                    boolean found = false;
//                    for(MetaPersistenceCt ct : ctList2){
//                        if(ct.uuid_() == exampleConcepts.cts().get(0).uuid_())
//                            found = true;
//                    }
//                    Assertions.assertTrue(found);
//
//                }
//        );
//    }

    @Test
    void createOrUpdateRecursive() {
        Assertions.assertDoesNotThrow( // darf keinen Fehler geben
                () -> {

                    // create or update
                    Database.groundOperations().createOrUpdateRecursive(exampleConcepts.cts().get(0)); // vom ersten CT ausgehend

                }
        );
    }

//    @Test
//    void selectRecursive() {
//        Assertions.assertDoesNotThrow( // darf keinen Fehler geben
//                () -> {
//
//                    // select
//                    List<MetaPersistenceCt> ctList1 = Database.groundOperations().selectRecursive();
//
//                }
//        );
//    }

}
