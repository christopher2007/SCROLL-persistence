package scroll.persistence.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import scroll.persistence.Database;
import scroll.persistence.Inheritance.MetaPersistenceNt;
import scroll.persistence.tests.ressources.UniversityExample_large_copy;

import java.util.Date;
import java.util.List;

public class NT {

    // Beispiel-Instanzen
    static UniversityExample_large_copy.Concepts exampleConcepts; // Einmalig am Anfang geladen, nur lesen, Referenz

    //@BeforeAll
    @BeforeEach
    void beforeEach(){
        exampleConcepts = UniversityExample_large_copy.getInstanceExample();
    }

    @Test
    void create() {
        Assertions.assertDoesNotThrow( // darf keinen Fehler geben
                () -> {

                    // Vorbereitung
                    MetaPersistenceNt nt = exampleConcepts.nts().get(0);
                    UniversityExample_large_copy.Person p1 = (UniversityExample_large_copy.Person) nt;
                    p1.name_$eq("kas/(&gudgklui7t");

                    // create
                    Database.nt().createOrUpdate(p1);

                }
        );
    }

    @Test
    void createSelect() {
        Assertions.assertDoesNotThrow( // darf keinen Fehler geben
                () -> {

                    // Vorbereitung
                    MetaPersistenceNt nt = exampleConcepts.nts().get(0);
                    UniversityExample_large_copy.Person p1 = (UniversityExample_large_copy.Person) nt;
                    p1.name_$eq("iklugo8A/&GSdolkj");

                    // create
                    Database.nt().createOrUpdate(p1);

                    // select
                    List<UniversityExample_large_copy.Person> o1 =
                            (List<UniversityExample_large_copy.Person>) Database.nt().select(p1.getClass(), "name", p1.name());
                    Assertions.assertEquals(1, o1.size()); // Länge Prüfen
                    Assertions.assertEquals(p1.name(), o1.get(0).name()); // Genaues Objekt prüfen
                    Assertions.assertEquals(p1.uuid_(), o1.get(0).uuid_()); // Genaues Objekt prüfen

                }
        );
    }

    @Test
    void createSelectUpdateSelect() {
        Assertions.assertDoesNotThrow( // darf keinen Fehler geben
                () -> {

                    // Vorbereitung
                    MetaPersistenceNt nt = exampleConcepts.nts().get(0);
                    UniversityExample_large_copy.Person p1 = (UniversityExample_large_copy.Person) nt;
                    p1.name_$eq("lkigO(/AS&tgdohaslsödljk");

                    // create
                    Database.nt().createOrUpdate(p1);

                    // select 1
                    List<UniversityExample_large_copy.Person> o1 =
                            (List<UniversityExample_large_copy.Person>) Database.nt().select(p1.getClass(), "name", p1.name());
                    Assertions.assertEquals(1, o1.size()); // Länge Prüfen
                    Assertions.assertEquals(p1.name(), o1.get(0).name()); // Genaues Objekt prüfen
                    Assertions.assertEquals(p1.uuid_(), o1.get(0).uuid_()); // Genaues Objekt prüfen

                    // update
                    p1.name_$eq("ihgIASu76dt9azsgdjkl");
                    Database.nt().createOrUpdate(p1);

                    // select 2
                    List<UniversityExample_large_copy.Person> o2 =
                            (List<UniversityExample_large_copy.Person>) Database.nt().select(p1.getClass(), "name", p1.name());
                    Assertions.assertEquals(1, o2.size()); // Länge Prüfen
                    Assertions.assertEquals(p1.name(), o2.get(0).name()); // Genaues Objekt prüfen
                    Assertions.assertEquals(p1.uuid_(), o2.get(0).uuid_()); // Genaues Objekt prüfen

                }
        );
    }

    @Test
    void createSelectDeleteSelect() {
        Assertions.assertDoesNotThrow( // darf keinen Fehler geben
                () -> {

                    // Vorbereitung
                    MetaPersistenceNt nt = exampleConcepts.nts().get(0);
                    UniversityExample_large_copy.Person p1 = (UniversityExample_large_copy.Person) nt;
                    p1.name_$eq("ioualjhkajksudkljah");

                    // create
                    Database.nt().createOrUpdate(p1);

                    // select 1
                    List<UniversityExample_large_copy.Person> o1 =
                            (List<UniversityExample_large_copy.Person>) Database.nt().select(p1.getClass(), "name", p1.name());
                    Assertions.assertEquals(1, o1.size()); // Länge Prüfen
                    Assertions.assertEquals(p1.name(), o1.get(0).name()); // Genaues Objekt prüfen
                    Assertions.assertEquals(p1.uuid_(), o1.get(0).uuid_()); // Genaues Objekt prüfen

                    // delete
                    Database.nt().delete(p1);

                    // select 2
                    List<UniversityExample_large_copy.Person> o2 =
                            (List<UniversityExample_large_copy.Person>) Database.nt().select(p1.getClass(), "name", p1.name());
                    Assertions.assertEquals(0, o2.size()); // Länge Prüfen

                }
        );
    }

}
