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
            this.className = c.getSimpleName();
//            this.classPackage = c.getCanonicalName();
            this.classPackage = c.getName();
        }else {
            this.class_ = o.getClass();
            this.className = o.getClass().getSimpleName();
//            this.classPackage = o.getClass().getCanonicalName();
            this.classPackage = o.getClass().getName();
        }
    }

}
