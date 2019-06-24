package scroll.persistence.Util;

/**
 * Ermittelt Klassenspezifische Informationen
 */
public class BasicClassInformation {

    public Class class_;
    public String className;
    public String classPackage;

    public BasicClassInformation(Object o){
        if(o instanceof Class){
            Class c = (Class) o;
            this.class_ = c;
        } else {
//            this.class_ = o.getClass();
            this.class_ = BasicClassInformation.getClass(o);
        }
        this.className = this.class_.getSimpleName();
//        this.classPackage = this.class_.getCanonicalName();
        this.classPackage = this.class_.getName();
    }

//    public BasicClassInformation(Object o) throws Exception {
//        this.class_ = BasicClassInformation.getClass(o);
//        this.className = this.class_.getSimpleName();
////            this.classPackage = this.class_.getCanonicalName();
//        this.classPackage = this.class_.getName();
//    }

    public static Class getClass(Object o){
        // innere Anonyme Klassen müssen über die Superklasse gehen
        if(o.getClass().toString().contains("$anon$")) // Ein `$anon$` weißt auf eine Anonyme Innere Klasse hin
            return o.getClass().getSuperclass();
        return o.getClass();
    }

}
