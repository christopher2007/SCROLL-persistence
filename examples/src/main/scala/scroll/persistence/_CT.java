package scroll.persistence;

public class _CT {

    // Singelton Pattern
    private static _CT instance;
    private _CT() {}
    // protected, nur für aktuelles Package
    protected static _CT getInstance () {
        if (_CT.instance == null)
            _CT.instance = new _CT();
        return _CT.instance;
    }

}
