package scroll.persistence.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import scroll.persistence.Database;
import scroll.persistence.Inheritance.MetaPersistenceNt;
import scroll.persistence.Inheritance.MetaPersistenceRt;
import scroll.persistence.Util.ReturnRT;
import scroll.persistence.tests.ressources.UniversityExample_large_copy;

import java.util.List;

public class NTplayingRT {

    // Beispiel-Instanzen
    static UniversityExample_large_copy.Concepts exampleConcepts; // Einmalig am Anfang geladen, nur lesen, Referenz

    //@BeforeAll
    @BeforeEach
    void beforeEach(){
        exampleConcepts = UniversityExample_large_copy.getInstanceExample();
    }

    @Test
    @Order(1)
    void create() {
        Assertions.assertDoesNotThrow( // darf keinen Fehler geben
                () -> {

                    // Vorbereitung NT
                    MetaPersistenceNt nt = exampleConcepts.nts().get(0);
                    UniversityExample_large_copy.Person p1 = (UniversityExample_large_copy.Person) nt;
                    p1.name_$eq("klagso9d87GAosidhb");

                    // Vorbereitung RT
                    MetaPersistenceRt rt = exampleConcepts.rts().get(0);

                    // create
                    Database.rt().createOrUpdate(rt, true, true);

                }
        );
    }

    @Test
    @Order(2)
    void createSelect() {
        Assertions.assertDoesNotThrow( // darf keinen Fehler geben
                () -> {

                    // Vorbereitung NT
                    MetaPersistenceNt nt = exampleConcepts.nts().get(0);
                    UniversityExample_large_copy.Person p1 = (UniversityExample_large_copy.Person) nt;
                    p1.name_$eq("öoUHAlskdjasd");

                    // Vorbereitung RT
                    MetaPersistenceRt rt = exampleConcepts.rts().get(0);

                    // select 1
                    List<ReturnRT> o1 =
                            (List<ReturnRT>) Database.rt().select(rt.getClass(), "matNr", 123, true, exampleConcepts.cts().get(0));
                    int length = o1.size();

                    // create
                    Database.rt().createOrUpdate(rt, true, true);

                    // select 2
                    List<ReturnRT> o2 =
                            (List<ReturnRT>) Database.rt().select(rt.getClass(), "matNr", 123, true, exampleConcepts.cts().get(0));
                    Assertions.assertEquals((length+1), o2.size()); // Länge Prüfen

                }
        );
    }

    @Test
    @Order(3)
    void createSelectDeleteSelect() {
        Assertions.assertDoesNotThrow( // darf keinen Fehler geben
                () -> {

                    // Vorbereitung NT
                    MetaPersistenceNt nt = exampleConcepts.nts().get(0);
                    UniversityExample_large_copy.Person p1 = (UniversityExample_large_copy.Person) nt;
                    p1.name_$eq("ohaLSKDJHlasdas");

                    // Vorbereitung RT
                    MetaPersistenceRt rt = exampleConcepts.rts().get(0);

                    // create
                    Database.rt().createOrUpdate(rt, true, true);

                    // select 1
                    List<ReturnRT> o1 =
                            (List<ReturnRT>) Database.rt().select(rt.getClass(), "matNr", 123, true, exampleConcepts.cts().get(0));
                    int length = o1.size();

                    // delete
                    Database.rt().delete(rt);

                    // select 2
                    List<ReturnRT> o2 =
                            (List<ReturnRT>) Database.rt().select(rt.getClass(), "matNr", 123, true, exampleConcepts.cts().get(0));
                    Assertions.assertEquals((length-1), o2.size()); // Länge Prüfen

                }
        );
    }

}
